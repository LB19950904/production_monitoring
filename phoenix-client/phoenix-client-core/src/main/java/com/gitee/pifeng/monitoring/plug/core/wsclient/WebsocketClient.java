package com.gitee.pifeng.monitoring.plug.core.wsclient;

import com.gitee.pifeng.monitoring.plug.constant.WebSocketCloseReasonEnums;
import com.gitee.pifeng.monitoring.plug.core.ThreadPoolAcquirer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * <p>
 * Websocket 客户端
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/2/12 14:36
 */
@Slf4j
@ClientEndpoint
public class WebsocketClient {

    /**
     * 全局共享的调度线程池，用于执行所有实例的重连任务，
     * 使用 {@link ThreadPoolAcquirer#getWebsocketClientReconnectScheduledThreadPoolExecutor()} 获取，
     * 避免每个实例创建独立线程，节省系统资源
     */
    private static final ScheduledExecutorService GLOBAL_SCHEDULER = ThreadPoolAcquirer.getWebsocketClientReconnectScheduledThreadPoolExecutor();

    /**
     * 全局共享的 Tyrus 客户端实例，用于复用底层连接资源，
     * 避免每次连接都创建新的 ClientManager，提升性能并减少内存开销
     */
    private static final ClientManager SHARED_CLIENT = ClientManager.createClient();

    /**
     * WebSocket 服务端地址（如 ws://example.com/monitor），在构造时确定且不可变
     */
    private final String serverUri;

    /**
     * 连接超时时间（秒），必须大于 0
     */
    private final int connectTimeoutSeconds;

    /**
     * 自动重连间隔时间（秒），必须大于 0，连接断开后，延迟此时间再尝试重连
     */
    private final int reconnectDelaySeconds;

    /**
     * 是否启用自动重连功能，该值在构造时确定，为 {@code final}，不可运行时修改
     */
    private final boolean autoReconnect;

    /**
     * 当前 WebSocket 会话对象。由 Tyrus 框架在 {@link #onOpen(Session)} 中设置，使用 {@code volatile} 保证多线程可见性
     */
    private volatile Session session;

    /**
     * 标记当前客户端是否已成功连接并处于活跃状态
     */
    private volatile boolean connected = false;

    /**
     * 连接门闩，用于同步等待连接结果，每次调用 {@link #connect()} 前会新建一个实例，避免重连干扰首次连接
     */
    private volatile CountDownLatch connectLatch;

    /**
     * 消息处理器，用于处理从服务端接收到的文本消息，通过 {@link #setMessageHandler(Consumer)} 设置，支持 Lambda 表达式
     */
    @Setter
    private volatile Consumer<String> messageHandler;

    /**
     * 并发连接保护标志，防止多个线程同时发起连接请求，使用 {@link AtomicBoolean#compareAndSet(boolean, boolean)} 实现无锁并发控制
     */
    private final AtomicBoolean connectionPending = new AtomicBoolean(false);

    /**
     * 运行时重连开关，
     * 默认为 {@code true}，调用 {@link #close()} 或 {@link #disableReconnect()} 会将其设为 {@code false}，
     * 从而永久禁止后续自动重连，即使 {@link #autoReconnect} 为 {@code true}
     */
    private volatile boolean enableReconnect = true;

    /**
     * <p>
     * 数构造器，默认配置：连接超时5秒，重连间隔5秒，开启自动重连
     * </p>
     *
     * @param serverUri 服务端地址 (ws:// 或 wss://)
     * @author 皮锋
     * @custom.date 2026/2/12 16:37
     */
    public WebsocketClient(String serverUri) {
        this(serverUri, 5, 5, true);
    }

    /**
     * <p>
     * 全参数构造器
     * </p>
     *
     * @param serverUri             服务端地址 (ws:// 或 wss://)
     * @param connectTimeoutSeconds 连接超时秒数 (>0)
     * @param reconnectDelaySeconds 重连间隔秒数 (>0)
     * @param autoReconnect         是否自动重连
     * @author 皮锋
     * @custom.date 2026/2/12 16:36
     */
    public WebsocketClient(String serverUri, int connectTimeoutSeconds, int reconnectDelaySeconds, boolean autoReconnect) {
        // 1. 地址严格校验
        Objects.requireNonNull(serverUri, "serverUri不能为空！");
        String trimmed = serverUri.trim();
        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme();
            if (!"ws".equals(scheme) && !"wss".equals(scheme)) {
                throw new IllegalArgumentException("WebSocket协议必须为ws或wss！");
            }
            if (uri.getHost() == null || uri.getHost().isEmpty()) {
                throw new IllegalArgumentException("WebSocket地址缺少Host！");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("WebSocket地址格式错误！", e);
        }
        this.serverUri = trimmed;
        // 2. 参数校验并赋值
        this.connectTimeoutSeconds = connectTimeoutSeconds > 0 ? connectTimeoutSeconds : 5;
        this.reconnectDelaySeconds = reconnectDelaySeconds > 0 ? reconnectDelaySeconds : 5;
        this.autoReconnect = autoReconnect;
    }

    /**
     * <p>
     * WebSocket 连接成功回调，由 Tyrus 框架在握手完成后调用，更新内部状态，并释放 {@link #connectLatch} 以唤醒等待线程
     * </p>
     *
     * @param session 新建立的 WebSocket 会话
     * @author 皮锋
     * @custom.date 2026/2/12 17:22
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        this.connected = true;
        // 释放等待中的 connect() 调用
        CountDownLatch latch = this.connectLatch;
        if (latch != null) {
            latch.countDown();
        }
        log.info("成功建立WebSocket连接[URI：{}]！", this.serverUri);
    }

    /**
     * <p>
     * 接收到文本消息回调，由 Tyrus 框架在收到服务端消息时调用，若设置了 {@link #messageHandler}，则委托其处理；否则仅记录日志
     * </p>
     *
     * @param message 从服务端接收到的文本消息
     * @author 皮锋
     * @custom.date 2026/2/12 17:23
     */
    @OnMessage
    public void onMessage(String message) {
        if (log.isDebugEnabled()) {
            log.debug("从WebSocket服务端[URI：{}]收到消息：{}", this.serverUri, message);
        }
        Consumer<String> handler = this.messageHandler;
        if (handler != null) {
            try {
                handler.accept(message);
            } catch (Exception e) {
                // 隔离异常，防止连接中断
                log.error("处理来自WebSocket服务端[URI：{}]的消息时发生异常：{}", this.serverUri, e.getMessage());
            }
        }
    }

    /**
     * <p>
     * WebSocket 连接关闭回调，由 Tyrus 框架在连接正常或异常关闭时调用，清理状态，并根据配置决定是否启动自动重连
     * </p>
     *
     * @param reason 关闭原因，包含状态码和描述信息
     * @author 皮锋
     * @custom.date 2026/2/12 17:26
     */
    @OnClose
    public void onClose(CloseReason reason) {
        this.connected = false;
        this.session = null;
        String closeReasonStr = (reason != null) ? reason.getReasonPhrase() : "Unknown (no close handshake)";
        String msg = StringUtils.isBlank(closeReasonStr) ? "WebSocket连接已关闭[URI：" + this.serverUri + "]！" : "WebSocket连接已关闭[URI：" + this.serverUri + "]，原因：" + closeReasonStr;
        log.warn(msg);
        // 重复连接，直接结束，不再重连
        if (reason != null && reason.getCloseCode().getCode() == WebSocketCloseReasonEnums.DUPLICATE_CONNECTION.getCode()) {
            log.warn("WebSocket服务端检测到已有活跃会话，拒绝重复连接！");
            return;
        }
        // 满足条件则调度重连任务
        if (this.enableReconnect && this.autoReconnect) {
            log.info("WebSocket连接关闭，将在 {} 秒后尝试重新连接WebSocket服务端[URI：{}]...", this.reconnectDelaySeconds, this.serverUri);
            GLOBAL_SCHEDULER.schedule(() -> this.attemptReconnect(), this.reconnectDelaySeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * <p>
     * WebSocket 发生错误回调，通常由底层网络异常、协议错误等触发，行为与 {@link #onClose(CloseReason)} 一致：清理状态 + 可选重连
     * </p>
     *
     * @param throwable 导致错误的异常对象
     * @author 皮锋
     * @custom.date 2026/2/12 17:29
     */
    @OnError
    public void onError(Throwable throwable) {
        this.connected = false;
        this.session = null;
        log.error("WebSocket连接异常[URI：{}]，原因：{}", this.serverUri, throwable.getMessage());
        // 不在此处调度重连，onClose 会随后被调用并统一处理重连
    }

    /**
     * <p>
     * 同步连接到 WebSocket 服务端，若已连接，则直接返回；若失败，则进行重试
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/2/27 08:13
     */
    public void connectWithRetry() {
        try {
            this.connect();
        } catch (Exception e) {
            // 满足条件则调度重连任务
            if (this.enableReconnect && this.autoReconnect) {
                log.info("WebSocket连接失败，将在 {} 秒后尝试重新连接WebSocket服务端[URI：{}]...", this.reconnectDelaySeconds, this.serverUri);
                GLOBAL_SCHEDULER.schedule(() -> this.attemptReconnect(), this.reconnectDelaySeconds, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * <p>
     * 同步连接到 WebSocket 服务端，若已连接，则直接返回；
     * 若正在连接中，则跳过本次请求（防并发重复连接），连接过程最多等待 {@link #connectTimeoutSeconds} 秒，超时将抛出异常
     * </p>
     *
     * @throws IOException          网络 I/O 错误、连接超时或底层异常
     * @throws DeploymentException  WebSocket 端点部署失败（如类路径问题）
     * @throws InterruptedException 当前线程在等待连接时被中断
     * @author 皮锋
     * @custom.date 2026/2/12 16:54
     */
    public void connect() throws IOException, DeploymentException, InterruptedException {
        // 已连接则快速返回
        if (this.isConnected()) {
            if (log.isDebugEnabled()) {
                log.debug("已连接到WebSocket服务端[URI：{}]，跳过重复连接！", this.serverUri);
            }
            return;
        }
        // 防止并发连接：仅允许一个线程进入连接流程
        if (!this.connectionPending.compareAndSet(false, true)) {
            if (log.isDebugEnabled()) {
                log.debug("已有连接请求正在处理中，跳过重复连接[URI：{}]！", this.serverUri);
            }
            return;
        }
        // 局部变量，保存本次创建的会话
        Session newSession = null;
        try {
            // 重置门闩：确保每次 connect 使用新的同步点
            this.connectLatch = new CountDownLatch(1);
            // 发起连接（Tyrus 内部处理）
            // 1. 建立连接，捕获返回的 Session（抑制资源警告）
            newSession = SHARED_CLIENT.connectToServer(this, URI.create(this.serverUri));
            // 2. 等待 onOpen 确认（或超时）
            boolean success = this.connectLatch.await(this.connectTimeoutSeconds, TimeUnit.SECONDS);
            if (!success) {
                // 超时：立即关闭已建立的会话，避免泄漏
                this.safeCloseSession(newSession);
                throw new IOException(String.format("连接WebSocket服务端[URI：%s]超时(%d秒)！", this.serverUri, this.connectTimeoutSeconds));
            }
            // 连接成功，状态已在 onOpen 中更新
        } catch (IOException | DeploymentException | InterruptedException e) {
            // 发生异常时，关闭可能已创建的会话
            this.safeCloseSession(newSession);
            // 已知异常直接抛出
            throw e;
        } catch (Exception e) {
            this.safeCloseSession(newSession);
            // 其他未知异常包装为 IOException
            throw new IOException(String.format("连接WebSocket服务端[URI：%s]失败，原因：%s", this.serverUri, e.getMessage()), e);
        } finally {
            // 释放连接占位，允许下次连接尝试
            this.connectionPending.set(false);
        }
    }

    /**
     * <p>
     * 发送文本消息到 WebSocket 服务端
     * </p>
     *
     * @param message 要发送的文本消息，不能为 {@code null}
     * @author 皮锋
     * @custom.date 2026/2/12 17:00
     */
    public void sendMessage(String message) {
        Session sess = this.session;
        if (sess == null || !this.connected || !sess.isOpen()) {
            if (log.isDebugEnabled()) {
                log.debug("未连接到WebSocket服务端[URI：{}]，消息已丢弃！", this.serverUri);
            }
            return;
        }
        try {
            sess.getBasicRemote().sendText(message);
            if (log.isDebugEnabled()) {
                log.debug("已向WebSocket服务端[URI：{}]发送消息：{}", this.serverUri, message);
            }
        } catch (IOException e) {
            log.error("向WebSocket服务端[URI：{}]发送消息失败：{}", this.serverUri, e.getMessage());
            this.connected = false;
            this.safeCloseSession(sess);
        }
    }

    /**
     * <p>
     * 主动关闭当前 WebSocket 连接，并禁用后续自动重连，调用后，即使 {@link #autoReconnect} 为 {@code true}，也不会再尝试重连
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/2/12 17:07
     */
    public void close() {
        // 永久禁用重连（优先级高于 autoReconnect）
        this.enableReconnect = false;
        // 防止 connect() 永久阻塞
        CountDownLatch latch = this.connectLatch;
        if (latch != null) {
            latch.countDown();
        }
        // 安全关闭会话
        log.info("正在主动关闭WebSocket连接[URI：{}]...", this.serverUri);
        this.safeCloseSession(this.session);
        this.connected = false;
        this.session = null;
    }

    /**
     * <p>
     * 检查当前客户端是否处于已连接且会话活跃状态
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/2/12 17:21
     */
    public boolean isConnected() {
        // 快照引用，避免并发问题
        Session sess = this.session;
        return this.connected && sess != null && sess.isOpen();
    }

    /**
     * <p>
     * 启动带指数退避的重连流程，首次调用时使用默认重试次数 1
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/2/13 11:08
     */
    private void attemptReconnect() {
        this.attemptReconnect(1);
    }

    /**
     * <p>
     * 尝试重新连接到服务端，
     * 内部方法，由重连调度器调用，若当前已连接、重连被禁用或自动重连未开启，则直接返回，重连失败会递归调度下一次尝试
     * </p>
     *
     * @param attempt 当前重试次数，用于计算退避延迟时间
     * @author 皮锋
     * @custom.date 2026/2/12 17:32
     */
    private void attemptReconnect(int attempt) {
        // 三重防护：运行时开关 + 配置开关 + 当前状态
        if (!this.enableReconnect || !this.autoReconnect || this.isConnected()) {
            return;
        }
        try {
            log.info("正在尝试重新连接WebSocket服务端[URI：{}]，第{}次...", this.serverUri, attempt);
            // 重用主连接逻辑
            this.connect();
        } catch (Exception e) {
            log.error("重连WebSocket服务端[URI：{}] 第{}次 失败，原因：{}", this.serverUri, attempt, e.getMessage());
            // 必须再次检查，避免禁用后仍无限重试
            if (this.enableReconnect && this.autoReconnect) {
                // 退避，上限5分钟
                long delay = Math.min(300, this.reconnectDelaySeconds * (long) Math.pow(1.5, attempt - 1));
                GLOBAL_SCHEDULER.schedule(() -> this.attemptReconnect(attempt + 1), delay, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * <p>
     * 彻底禁止自动重连功能，
     * 与 {@link #close()} 配合使用，可用于优雅停机，调用后，即使连接断开，也不会再尝试重连
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/2/12 17:34
     */
    public void disableReconnect() {
        this.enableReconnect = false;
    }

    /**
     * <p>
     * 安全关闭 WebSocket 会话
     * </p>
     *
     * @param session 要关闭的 WebSocket 会话，如果为 {@code null} 或已关闭，则方法直接返回
     * @author 皮锋
     * @custom.date 2026/2/13 10:56
     */
    private void safeCloseSession(Session session) {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("关闭WebSocket连接[URI：{}]时发生异常：{}", this.serverUri, e.getMessage());
            }
        }
    }

}
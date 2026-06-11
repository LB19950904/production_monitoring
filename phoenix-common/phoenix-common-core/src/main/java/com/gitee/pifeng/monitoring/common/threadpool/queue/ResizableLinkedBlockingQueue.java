package com.gitee.pifeng.monitoring.common.threadpool.queue;

import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 可动态修改容量的链式阻塞队列。
 * </p>
 * 基于 {@link LinkedBlockingQueue} 改造，将 {@code capacity} 字段从 {@code final}
 * 改为可变，并增加 {@link #setCapacity(int)} 方法，允许在运行时动态调整队列容量。<br>
 * 其设计源自 RabbitMQ 的可变容量队列实现。
 *
 * @param <E> 队列中元素的类型
 * @author 皮锋
 * @custom.date 2026/3/24 00:00
 */
public class ResizableLinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {

    private static final long serialVersionUID = -6903933977591709194L;

    /**
     * <p>
     * 链表节点
     * </p>
     *
     * @param <E> 节点持有的元素类型
     */
    static class Node<E> {

        /**
         * 节点持有的元素，volatile 保证读写的可见性
         */
        volatile E item;

        /**
         * 后继节点指针
         */
        Node<E> next;

        /**
         * 构造一个持有指定元素的节点
         *
         * @param x 节点持有的元素
         */
        Node(E x) {
            item = x;
        }
    }

    /**
     * 队列容量，非 final，可动态修改
     */
    @Getter
    private int capacity;

    /**
     * 当前元素数量
     */
    private final AtomicInteger count = new AtomicInteger(0);

    /**
     * 链表头节点（哨兵节点，{@code head.item} 始终为 {@code null}）
     */
    private transient Node<E> head;

    /**
     * 链表尾节点
     */
    private transient Node<E> last;

    /**
     * take、poll 等出队操作持有的锁
     */
    private final ReentrantLock takeLock = new ReentrantLock();

    /**
     * 等待队列非空的条件（与 {@link #takeLock} 关联）
     */
    private final Condition notEmpty = takeLock.newCondition();

    /**
     * put、offer 等入队操作持有的锁
     */
    private final ReentrantLock putLock = new ReentrantLock();

    /**
     * 等待队列非满的条件（与 {@link #putLock} 关联）
     */
    private final Condition notFull = putLock.newCondition();

    /**
     * <p>
     * 通知一个正在等待取元素的线程：队列已非空。
     * </p>
     * 仅从 put/offer 方法中调用。
     *
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    /**
     * <p>
     * 通知一个正在等待放元素的线程：队列已非满。
     * </p>
     * 仅从 take/poll 方法中调用。
     *
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

    /**
     * <p>
     * 在链表尾部插入一个新节点。
     * </p>
     * 调用者必须持有 {@link #putLock}。
     *
     * @param x 待插入的元素
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    private void insert(E x) {
        last = last.next = new Node<>(x);
    }

    /**
     * <p>
     * 从链表头部移除并返回一个元素。
     * </p>
     * 调用者必须持有 {@link #takeLock}。
     *
     * @return 被移除的元素
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    private E extract() {
        Node<E> first = head.next;
        head = first;
        E x = first.item;
        first.item = null;
        return x;
    }

    /**
     * <p>
     * 同时获取 {@link #putLock} 和 {@link #takeLock}，用于需要原子性遍历或修改整个队列的操作。
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    private void fullyLock() {
        putLock.lock();
        takeLock.lock();
    }

    /**
     * <p>
     * 释放 {@link #putLock} 和 {@link #takeLock}，与 {@link #fullyLock()} 配对使用。
     * </p>
     * 解锁顺序与加锁顺序相反。
     *
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    private void fullyUnlock() {
        takeLock.unlock();
        putLock.unlock();
    }

    /**
     * 创建一个容量为 {@link Integer#MAX_VALUE} 的队列
     *
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    public ResizableLinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    /**
     * 创建一个指定容量的队列
     *
     * @param capacity 队列容量
     * @throws IllegalArgumentException 如果容量不大于 0
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    public ResizableLinkedBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        last = head = new Node<>(null);
    }

    /**
     * 创建一个容量为 {@link Integer#MAX_VALUE} 的队列，并初始化包含给定集合中的所有元素
     *
     * @param c 初始元素集合
     * @throws NullPointerException 如果集合或其中任何元素为 {@code null}
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    public ResizableLinkedBlockingQueue(Collection<? extends E> c) {
        this(Integer.MAX_VALUE);
        addAll(c);
    }

    /**
     * <p>
     * 将指定元素插入队列尾部，如果队列未满则插入成功并返回 {@code true}，否则抛出 {@link IllegalStateException}。
     * </p>
     *
     * @param e 待插入的元素
     * @return {@code true}（如 {@link Collection#add} 所规定）
     * @throws IllegalStateException 如果队列已满
     * @throws NullPointerException  如果指定元素为 {@code null}
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public boolean add(@NonNull E e) {
        return super.add(e);
    }

    /**
     * <p>
     * 返回队列中当前元素的数量。
     * </p>
     *
     * @return 队列中的元素数量
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public int size() {
        return count.get();
    }

    /**
     * <p>
     * 动态设置队列容量。
     * </p>
     * 如果新容量大于当前元素数量，且之前队列已满，则唤醒等待入队的线程。
     *
     * @param capacity 新的队列容量
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    public void setCapacity(int capacity) {
        final int oldCapacity = this.capacity;
        this.capacity = capacity;
        final int size = count.get();
        if (capacity > size && size >= oldCapacity) {
            signalNotFull();
        }
    }

    /**
     * <p>
     * 返回队列在不阻塞的情况下还能接受的元素数量。
     * </p>
     * 等于当前容量减去当前元素数量。
     *
     * @return 队列的剩余容量
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public int remainingCapacity() {
        return capacity - count.get();
    }

    /**
     * <p>
     * 将指定元素插入队列尾部，如果队列已满则阻塞等待直到有空间可用。
     * </p>
     *
     * @param e 待插入的元素
     * @throws InterruptedException 如果在等待过程中被中断
     * @throws NullPointerException 如果指定元素为 {@code null}
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public void put(@NonNull E e) throws InterruptedException {
        int c;
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while (count.get() >= capacity) {
                notFull.await();
            }
            insert(e);
            c = count.getAndIncrement();
            if (c + 1 < capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
    }

    /**
     * <p>
     * 将指定元素插入队列尾部，如果队列已满则等待指定的超时时间。
     * </p>
     *
     * @param e       待插入的元素
     * @param timeout 超时时间
     * @param unit    超时时间单位
     * @return 如果成功插入返回 {@code true}，超时返回 {@code false}
     * @throws InterruptedException 如果在等待过程中被中断
     * @throws NullPointerException 如果指定元素为 {@code null}
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public boolean offer(@NonNull E e, long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        int c;
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while (count.get() >= capacity) {
                if (nanos <= 0) {
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            insert(e);
            c = count.getAndIncrement();
            if (c + 1 < capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return true;
    }

    /**
     * <p>
     * 将指定元素插入队列尾部（非阻塞），如果队列已满则立即返回 {@code false}。
     * </p>
     *
     * @param e 待插入的元素
     * @return 如果成功插入返回 {@code true}，队列已满返回 {@code false}
     * @throws NullPointerException 如果指定元素为 {@code null}
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public boolean offer(@NonNull E e) {
        final AtomicInteger count = this.count;
        if (count.get() >= capacity) {
            return false;
        }
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            if (count.get() < capacity) {
                insert(e);
                c = count.getAndIncrement();
                if (c + 1 < capacity) {
                    notFull.signal();
                }
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return c >= 0;
    }

    /**
     * <p>
     * 从队列头部移除并返回一个元素，如果队列为空则阻塞等待直到有元素可用。
     * </p>
     *
     * @return 被移除的队列头部元素
     * @throws InterruptedException 如果在等待过程中被中断
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @NonNull
    @Override
    public E take() throws InterruptedException {
        E x;
        int c;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                notEmpty.await();
            }
            x = extract();
            c = count.getAndDecrement();
            if (c > 1) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return x;
    }

    /**
     * <p>
     * 从队列头部移除并返回一个元素，如果队列为空则等待指定的超时时间。
     * </p>
     *
     * @param timeout 超时时间
     * @param unit    超时时间单位
     * @return 被移除的队列头部元素，超时则返回 {@code null}
     * @throws InterruptedException 如果在等待过程中被中断
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public E poll(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        E x;
        int c;
        long nanos = unit.toNanos(timeout);
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                if (nanos <= 0) {
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            x = extract();
            c = count.getAndDecrement();
            if (c > 1) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return x;
    }

    /**
     * <p>
     * 从队列头部移除并返回一个元素（非阻塞），如果队列为空则立即返回 {@code null}。
     * </p>
     *
     * @return 被移除的队列头部元素，队列为空返回 {@code null}
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public E poll() {
        final AtomicInteger count = this.count;
        if (count.get() == 0) {
            return null;
        }
        E x = null;
        int c = -1;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            if (count.get() > 0) {
                x = extract();
                c = count.getAndDecrement();
                if (c > 1) {
                    notEmpty.signal();
                }
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return x;
    }

    /**
     * <p>
     * 查看但不移除队列头部的元素，如果队列为空则返回 {@code null}。
     * </p>
     *
     * @return 队列头部元素，队列为空返回 {@code null}
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public E peek() {
        if (count.get() == 0) {
            return null;
        }
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            Node<E> first = head.next;
            if (first == null) {
                return null;
            } else {
                return first.item;
            }
        } finally {
            takeLock.unlock();
        }
    }

    /**
     * <p>
     * 从队列中移除指定元素的第一个匹配项（如果存在）。
     * </p>
     *
     * @param o 要从队列中移除的元素
     * @return 如果队列中包含指定元素并成功移除则返回 {@code true}，否则返回 {@code false}
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        fullyLock();
        try {
            for (Node<E> trail = head, p = trail.next; p != null; trail = p, p = p.next) {
                if (o.equals(p.item)) {
                    p.item = null;
                    trail.next = p.next;
                    if (last == p) {
                        last = trail;
                    }
                    if (count.getAndDecrement() == capacity) {
                        notFull.signal();
                    }
                    return true;
                }
            }
            return false;
        } finally {
            fullyUnlock();
        }
    }

    /**
     * <p>
     * 返回包含队列中所有元素的数组（按入队顺序排列）。
     * </p>
     *
     * @return 包含队列中所有元素的 {@code Object} 数组
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public Object[] toArray() {
        fullyLock();
        try {
            int size = count.get();
            Object[] a = new Object[size];
            int k = 0;
            for (Node<E> p = head.next; p != null; p = p.next) {
                a[k++] = p.item;
            }
            return a;
        } finally {
            fullyUnlock();
        }
    }

    /**
     * <p>
     * 返回包含队列中所有元素的数组；返回数组的运行时类型与指定数组的运行时类型相同。
     * </p>
     *
     * @param a   用于存储队列元素的数组（如果足够大）；否则分配一个相同运行时类型的新数组
     * @param <T> 数组元素的类型
     * @return 包含队列中所有元素的数组
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    @SuppressWarnings({"unchecked", "NullableProblems"})
    public <T> T[] toArray(T[] a) {
        fullyLock();
        try {
            int size = count.get();
            if (a.length < size) {
                a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
            }
            int k = 0;
            for (Node<E> p = head.next; p != null; p = p.next) {
                a[k++] = (T) p.item;
            }
            if (a.length > k) {
                a[k] = null;
            }
            return a;
        } finally {
            fullyUnlock();
        }
    }

    /**
     * <p>
     * 移除队列中的所有元素。此方法返回后队列将为空。
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public void clear() {
        fullyLock();
        try {
            head.next = null;
            last = head;
            if (count.getAndSet(0) == capacity) {
                notFull.signal();
            }
        } finally {
            fullyUnlock();
        }
    }

    /**
     * <p>
     * 将队列中所有可用元素移除并添加到给定的集合中。
     * </p>
     *
     * @param c 接收元素的目标集合
     * @return 实际转移的元素数量
     * @throws NullPointerException     如果目标集合为 {@code null}
     * @throws IllegalArgumentException 如果目标集合是队列自身
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public int drainTo(@NonNull Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    /**
     * <p>
     * 将队列中最多 {@code maxElements} 个可用元素移除并添加到给定的集合中。
     * </p>
     *
     * @param c           接收元素的目标集合
     * @param maxElements 最多转移的元素数量
     * @return 实际转移的元素数量
     * @throws NullPointerException     如果目标集合为 {@code null}
     * @throws IllegalArgumentException 如果目标集合是队列自身
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Override
    public int drainTo(@NonNull Collection<? super E> c, int maxElements) {
        if (c == this) {
            throw new IllegalArgumentException();
        }
        boolean signalNotFull = false;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            int n = Math.min(maxElements, count.get());
            int i = 0;
            try {
                while (i < n) {
                    E x = extract();
                    c.add(x);
                    ++i;
                }
                return n;
            } finally {
                if (i > 0) {
                    signalNotFull = (count.getAndAdd(-i) == capacity);
                }
            }
        } finally {
            takeLock.unlock();
            if (signalNotFull) {
                signalNotFull();
            }
        }
    }

    /**
     * <p>
     * 返回此队列中元素的迭代器（按入队顺序）。
     * </p>
     *
     * @return 此队列中元素的迭代器
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @NonNull
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * <p>
     * 队列的迭代器实现。
     * </p>
     * 遍历时需要获取双锁以保证一致性快照。
     *
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    private class Itr implements Iterator<E> {

        /**
         * 当前迭代位置的节点
         */
        private Node<E> current;

        /**
         * 上一次 {@link #next()} 返回的节点，用于 {@link #remove()} 操作
         */
        private Node<E> lastRet;

        /**
         * 当前节点持有的元素（预读缓存，避免持锁期间返回已被修改的值）
         */
        private E currentElement;

        /**
         * 构造迭代器，在双锁保护下获取队列头部的快照
         *
         * @author 皮锋
         * @custom.date 2026/3/24 00:00
         */
        Itr() {
            fullyLock();
            try {
                current = head.next;
                if (current != null) {
                    currentElement = current.item;
                }
            } finally {
                fullyUnlock();
            }
        }

        /**
         * <p>
         * 判断是否还有下一个元素。
         * </p>
         *
         * @return 如果迭代器还有更多元素则返回 {@code true}
         * @author 皮锋
         * @custom.date 2026/3/24 00:00
         */
        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * <p>
         * 返回迭代中的下一个元素。
         * </p>
         *
         * @return 下一个元素
         * @throws NoSuchElementException 如果没有更多元素
         * @author 皮锋
         * @custom.date 2026/3/24 00:00
         */
        @Override
        public E next() {
            fullyLock();
            try {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                E x = currentElement;
                lastRet = current;
                current = current.next;
                if (current != null) {
                    currentElement = current.item;
                }
                return x;
            } finally {
                fullyUnlock();
            }
        }

        /**
         * <p>
         * 从队列中移除迭代器上一次返回的元素。
         * </p>
         * 每次调用 {@link #next()} 后只能调用一次此方法。
         *
         * @throws IllegalStateException 如果尚未调用 {@link #next()} 或已经调用过 {@code remove()}
         * @author 皮锋
         * @custom.date 2026/3/24 00:00
         */
        @Override
        public void remove() {
            if (lastRet == null) {
                throw new IllegalStateException();
            }
            fullyLock();
            try {
                Node<E> node = lastRet;
                lastRet = null;
                for (Node<E> trail = head, p = trail.next; p != null; trail = p, p = p.next) {
                    if (p == node) {
                        p.item = null;
                        trail.next = p.next;
                        if (last == p) {
                            last = trail;
                        }
                        if (count.getAndDecrement() == capacity) {
                            notFull.signal();
                        }
                        break;
                    }
                }
            } finally {
                fullyUnlock();
            }
        }
    }

}

package com.gitee.pifeng.monitoring.common.threadpool.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.*;

/**
 * <p>
 * 队列类型枚举：定义线程池支持的所有队列类型，并提供根据名称和容量创建队列实例的能力。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/24 00:00
 */
@Getter
@AllArgsConstructor
public enum QueueTypeEnum {

    /**
     * {@link ArrayBlockingQueue}：有界阻塞队列，FIFO
     */
    ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue") {
        @Override
        public <T> BlockingQueue<T> create(int capacity) {
            return new ArrayBlockingQueue<>(capacity);
        }
    },

    /**
     * {@link LinkedBlockingQueue}：基于链表的可选有界阻塞队列
     */
    LINKED_BLOCKING_QUEUE("LinkedBlockingQueue") {
        @Override
        public <T> BlockingQueue<T> create(int capacity) {
            return new LinkedBlockingQueue<>(capacity);
        }
    },

    /**
     * {@link LinkedBlockingDeque}：基于链表的双端阻塞队列
     */
    LINKED_BLOCKING_DEQUE("LinkedBlockingDeque") {
        @Override
        public <T> BlockingQueue<T> create(int capacity) {
            return new LinkedBlockingDeque<>(capacity);
        }
    },

    /**
     * {@link SynchronousQueue}：不存储元素的阻塞队列，每个插入操作必须等待一个移除操作
     */
    SYNCHRONOUS_QUEUE("SynchronousQueue") {
        @Override
        public <T> BlockingQueue<T> create(int capacity) {
            return new SynchronousQueue<>();
        }
    },

    /**
     * {@link LinkedTransferQueue}：基于链表的无界传输队列
     */
    LINKED_TRANSFER_QUEUE("LinkedTransferQueue") {
        @Override
        public <T> BlockingQueue<T> create(int capacity) {
            return new LinkedTransferQueue<>();
        }
    },

    /**
     * {@link PriorityBlockingQueue}：支持优先级排序的无界阻塞队列
     */
    PRIORITY_BLOCKING_QUEUE("PriorityBlockingQueue") {
        @Override
        public <T> BlockingQueue<T> create(int capacity) {
            return new PriorityBlockingQueue<>(capacity);
        }
    },

    /**
     * {@link ResizableLinkedBlockingQueue}：可动态修改容量的链式阻塞队列
     */
    RESIZABLE_LINKED_BLOCKING_QUEUE("ResizableLinkedBlockingQueue") {
        @Override
        public <T> BlockingQueue<T> create(int capacity) {
            return new ResizableLinkedBlockingQueue<>(capacity);
        }
    };

    /**
     * 队列类型名称（与队列 Class 的 SimpleName 对应）
     */
    private final String name;

    /**
     * 根据容量创建对应类型的阻塞队列实例
     *
     * @param capacity 队列容量
     * @param <T>      队列中元素的类型
     * @return {@link BlockingQueue} 实例
     */
    public abstract <T> BlockingQueue<T> create(int capacity);

    /**
     * 默认队列容量
     */
    private static final int DEFAULT_CAPACITY = 1024;

    /**
     * <p>
     * 根据队列类型名称和容量创建对应的 {@link BlockingQueue} 实例
     * </p>
     *
     * @param queueTypeName 队列类型名称
     * @param capacity      队列容量，若 {@code null} 或小于等于 0，则使用默认容量 {@value DEFAULT_CAPACITY}
     * @param <T>           队列中元素的类型
     * @return {@link BlockingQueue} 实例，无法识别队列类型时返回 {@code null}
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    public static <T> BlockingQueue<T> createBlockingQueue(String queueTypeName, Integer capacity) {
        int resolvedCapacity = (capacity != null && capacity > 0) ? capacity : DEFAULT_CAPACITY;
        for (QueueTypeEnum queueType : values()) {
            if (queueType.name.equals(queueTypeName)) {
                return queueType.create(resolvedCapacity);
            }
        }
        return null;
    }

}

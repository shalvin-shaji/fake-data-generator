package com.fdg.generator;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Bounded ring buffer of generated values. Used to back REFERENCE fields:
 * the parent stream pushes its key values here, and the child stream picks
 * from them at random.
 *
 * Eviction is FIFO once capacity is reached. Thread-safe for concurrent
 * push (parent stream) and pickRandom (child streams).
 */
public class ReferencePool {

    private final Object[] buffer;
    private final int capacity;
    private int writeIdx = 0;
    private int size = 0;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ReferencePool(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    public void push(Object value) {
        lock.writeLock().lock();
        try {
            buffer[writeIdx] = value;
            writeIdx = (writeIdx + 1) % capacity;
            if (size < capacity) {
                size++;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Returns null if empty. */
    public Object pickRandom() {
        lock.readLock().lock();
        try {
            if (size == 0) {
                return null;
            }
            int idx = ThreadLocalRandom.current().nextInt(size);
            return buffer[idx];
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }
}

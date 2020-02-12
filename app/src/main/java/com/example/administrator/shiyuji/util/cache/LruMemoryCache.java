package com.example.administrator.shiyuji.util.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/1.
 */


public class LruMemoryCache<K, V> {
    private final LinkedHashMap<K, V> map;
    private int size;
    private int maxSize;
    private int putCount;
    private int createCount;
    private int evictionCount;
    private int hitCount;
    private int missCount;

    public LruMemoryCache(int maxSize) {
        if(maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else {
            this.maxSize = maxSize;
            this.map = new LinkedHashMap(0, 0.75F, true);
        }
    }

    public final V get(K key) {
        if(key == null) {
            throw new NullPointerException("key == null");
        } else {
            Object mapValue;
            synchronized(this) {
                mapValue = this.map.get(key);
                if(mapValue != null) {
                    ++this.hitCount;
                    return (V) mapValue;
                }

                ++this.missCount;
            }

            Object createdValue = this.create(key);
            if(createdValue == null) {
                return null;
            } else {
                synchronized(this) {
                    ++this.createCount;
                    mapValue = this.map.put(key, (V) createdValue);
                    if(mapValue != null) {
                        this.map.put(key, (V) mapValue);
                    } else {
                        this.size += this.safeSizeOf(key, (V) createdValue);
                    }
                }

                if(mapValue != null) {
                    this.entryRemoved(false, key, (V)createdValue,(V) mapValue);
                    return (V) mapValue;
                } else {
                    this.trimToSize(this.maxSize);
                    return (V) createdValue;
                }
            }
        }
    }

    public final V put(K key, V value) {
        if(key != null && value != null) {
            Object previous;
            synchronized(this) {
                ++this.putCount;
                this.size += this.safeSizeOf(key, value);
                previous = this.map.put(key, value);
                if(previous != null) {
                    this.size -= this.safeSizeOf(key, (V) previous);
                }
            }

            if(previous != null) {
                this.entryRemoved(false, key, (V) previous, value);
            }

            this.trimToSize(this.maxSize);
            return (V) previous;
        } else {
            throw new NullPointerException("key == null || value == null");
        }
    }

    private void trimToSize(int maxSize) {
        while(true) {
            Object key;
            Object value;
            synchronized(this) {
                label47: {
                    if(this.size >= 0 && (!this.map.isEmpty() || this.size == 0)) {
                        if(this.size > maxSize && !this.map.isEmpty()) {
                            Map.Entry toEvict = (Map.Entry)this.map.entrySet().iterator().next();
                            key = toEvict.getKey();
                            value = toEvict.getValue();
                            this.map.remove(key);
                            int recyleSize = this.safeSizeOf((K)key, (V)value);
                            this.size -= recyleSize;
//                            if(value instanceof MyBitmap) {
//                                Logger.d(LruMemoryCache.class.getSimpleName(), "recyle size = " + recyleSize / 1024 + "kb");
//                            }

                            ++this.evictionCount;
                            break label47;
                        }

                        return;
                    }

                    throw new IllegalStateException(this.getClass().getName() + ".sizeOf() is reporting inconsistent results!");
                }
            }

            this.entryRemoved(true, (K)key, (V)value, (V)null);
        }
    }

    public final V remove(K key) {
        if(key == null) {
            throw new NullPointerException("key == null");
        } else {
            Object previous;
            synchronized(this) {
                previous = this.map.remove(key);
                if(previous != null) {
                    this.size -= this.safeSizeOf(key, (V) previous);
                }
            }

            if(previous != null) {
                this.entryRemoved(false, key, (V)previous, (V)null);
            }

            return (V) previous;
        }
    }

    protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
    }

    protected V create(K key) {
        return null;
    }

    private int safeSizeOf(K key, V value) {
        int result = this.sizeOf(key, value);
        if(result < 0) {
            throw new IllegalStateException("Negative size: " + key + "=" + value);
        } else {
            return result;
        }
    }

    protected int sizeOf(K key, V value) {
        return 1;
    }

    public final void evictAll() {
        this.trimToSize(-1);
    }

    public final void evictHalf() {
        this.trimToSize(this.maxSize / 2);
    }

    public final synchronized int size() {
        return this.size;
    }

    public final synchronized int maxSize() {
        return this.maxSize;
    }

    public final synchronized int hitCount() {
        return this.hitCount;
    }

    public final synchronized int missCount() {
        return this.missCount;
    }

    public final synchronized int createCount() {
        return this.createCount;
    }

    public final synchronized int putCount() {
        return this.putCount;
    }

    public final synchronized int evictionCount() {
        return this.evictionCount;
    }

    public final synchronized Map<K, V> snapshot() {
        return new LinkedHashMap(this.map);
    }

    public final synchronized String toString() {
        int accesses = this.hitCount + this.missCount;
        int hitPercent = accesses != 0?100 * this.hitCount / accesses:0;
        return String.format("LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", new Object[]{Integer.valueOf(this.maxSize), Integer.valueOf(this.hitCount), Integer.valueOf(this.missCount), Integer.valueOf(hitPercent)});
    }
}

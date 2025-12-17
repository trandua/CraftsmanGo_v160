package com.microsoft.xbox.toolkit;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;

/* loaded from: classes3.dex */
public class ThreadSafeFixedSizeHashtable<K, V> {
    private final int maxSize;
    private int count = 0;
    private PriorityQueue<ThreadSafeFixedSizeHashtable<K, V>.KeyTuple> fifo = new PriorityQueue<>();
    private Hashtable<K, V> hashtable = new Hashtable<>();
    private Object syncObject = new Object();

    /* loaded from: classes3.dex */
    private class KeyTuple implements Comparable<ThreadSafeFixedSizeHashtable<K, V>.KeyTuple> {
        private int index;
        public K key;
        final ThreadSafeFixedSizeHashtable this$0;

//        @Override // java.lang.Comparable
//        public /* bridge */ /* synthetic */ int compareTo(Object obj) {
//            return compareTo((KeyTuple) ((KeyTuple) obj));
//        }

        public KeyTuple(ThreadSafeFixedSizeHashtable threadSafeFixedSizeHashtable, K k, int i) {
            this.this$0 = threadSafeFixedSizeHashtable;
            this.key = k;
            this.index = i;
        }

        public int compareTo(ThreadSafeFixedSizeHashtable<K, V>.KeyTuple keyTuple) {
            return this.index - keyTuple.index;
        }

        public K getKey() {
            return this.key;
        }
    }

    public ThreadSafeFixedSizeHashtable(int i) {
        this.maxSize = i;
        if (i <= 0) {
            throw new IllegalArgumentException();
        }
    }

    private void cleanupIfNecessary() {
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable.cleanupIfNecessary():void");
    }

    public Enumeration<V> elements() {
        return this.hashtable.elements();
    }

    public V get(K k) {
        V v;
        if (k == null) {
            return null;
        }
        synchronized (this.syncObject) {
            v = this.hashtable.get(k);
        }
        return v;
    }

    public Enumeration<K> keys() {
        return this.hashtable.keys();
    }

    public void put(K k, V v) {
        if (k == null || v == null) {
            return;
        }
        synchronized (this.syncObject) {
            if (!this.hashtable.containsKey(k)) {
                int i = this.count + 1;
                this.count = i;
                this.fifo.add(new KeyTuple(this, k, i));
                this.hashtable.put(k, v);
                cleanupIfNecessary();
            }
        }
    }

    public void remove(K k) {
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable.remove(java.lang.Object):void");
    }
}

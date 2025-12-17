package com.microsoft.xbox.toolkit;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

/* loaded from: classes3.dex */
public class MultiMap<K, V> {
    private Hashtable<K, HashSet<V>> data = new Hashtable<>();
    private Hashtable<V, K> dataInverse = new Hashtable<>();

    private void removeKeyIfEmpty(K k) {
        HashSet<V> hashSet = get(k);
        if (hashSet == null || !hashSet.isEmpty()) {
            return;
        }
        this.data.remove(k);
    }

    public int TESTsizeDegenerate() {
        int i = 0;
        for (K k : this.data.keySet()) {
            if (this.data.get(k).size() == 0) {
                i++;
            }
        }
        return i;
    }

    public void clear() {
        this.data.clear();
        this.dataInverse.clear();
    }

    public boolean containsKey(K k) {
        return this.data.containsKey(k);
    }

    public boolean containsValue(V v) {
        return getKey(v) != null;
    }

    public HashSet<V> get(K k) {
        return this.data.get(k);
    }

    public K getKey(V v) {
        return this.dataInverse.get(v);
    }

    public boolean keyValueMatches(K k, V v) {
        HashSet<V> hashSet = get(k);
        if (hashSet == null) {
            return false;
        }
        return hashSet.contains(v);
    }

    public void put(K k, V v) {
        if (this.data.get(k) == null) {
            this.data.put(k, new HashSet<>());
        }
        XLEAssert.assertTrue(!this.dataInverse.containsKey(v));
        this.data.get(k).add(v);
        this.dataInverse.put(v, k);
    }

    public void removeKey(K k) {
        Iterator<V> it = this.data.get(k).iterator();
        while (it.hasNext()) {
            V next = it.next();
            XLEAssert.assertTrue(this.dataInverse.containsKey(next));
            this.dataInverse.remove(next);
        }
        this.data.remove(k);
    }

    public void removeValue(V v) {
        K key = getKey(v);
        this.data.get(key).remove(v);
        this.dataInverse.remove(v);
        removeKeyIfEmpty(key);
    }

    public int size() {
        return this.data.size();
    }
}

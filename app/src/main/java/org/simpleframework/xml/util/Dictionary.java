package org.simpleframework.xml.util;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import org.simpleframework.xml.util.Entry;

/* loaded from: classes.dex */
public class Dictionary<T extends Entry> extends AbstractSet<T> {
    protected final Table<T> map = new Table<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Table<T> extends HashMap<String, T> {
    }

    /* JADX WARN: Multi-variable type inference failed */
//    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
//    public /* bridge */ /* synthetic */ boolean add(Object x0) {
//        return add((Dictionary<T>) ((Entry) x0));
//    }

    public boolean add(T item) {
        return this.map.put(item.getName(), item) != null;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.map.size();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator<T> iterator() {
        return this.map.values().iterator();
    }

    public T get(String name) {
        return this.map.get(name);
    }

    public T remove(String name) {
        return this.map.remove(name);
    }
}

package org.simpleframework.xml.util;

/* loaded from: classes.dex */
public interface Cache<T> {
    void cache(Object obj, T t);

    boolean contains(Object obj);

    T fetch(Object obj);

    boolean isEmpty();

    T take(Object obj);
}

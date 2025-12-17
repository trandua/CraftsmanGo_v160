package org.simpleframework.xml.core;

/* loaded from: classes.dex */
public class UnionException extends PersistenceException {
    public UnionException(String text, Object... list) {
        super(String.format(text, list), new Object[0]);
    }
}

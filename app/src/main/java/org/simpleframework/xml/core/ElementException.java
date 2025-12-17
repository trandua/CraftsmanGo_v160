package org.simpleframework.xml.core;

/* loaded from: classes.dex */
public class ElementException extends PersistenceException {
    public ElementException(String text, Object... list) {
        super(text, list);
    }

    public ElementException(Throwable cause, String text, Object... list) {
        super(cause, text, list);
    }
}

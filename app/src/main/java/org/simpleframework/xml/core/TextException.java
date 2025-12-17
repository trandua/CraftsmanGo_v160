package org.simpleframework.xml.core;

/* loaded from: classes.dex */
public class TextException extends PersistenceException {
    public TextException(String text, Object... list) {
        super(text, list);
    }

    public TextException(Throwable cause, String text, Object... list) {
        super(cause, text, list);
    }
}

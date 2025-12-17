package org.simpleframework.xml.convert;

/* loaded from: classes.dex */
public class ConvertException extends Exception {
    public ConvertException(String text, Object... list) {
        super(String.format(text, list));
    }
}

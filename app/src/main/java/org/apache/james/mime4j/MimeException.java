package org.apache.james.mime4j;

/* loaded from: classes.dex */
public class MimeException extends Exception {
    private static final long serialVersionUID = 8352821278714188542L;

    public MimeException(String str) {
        super(str);
    }

    public MimeException(String str, Throwable th) {
        super(str);
        initCause(th);
    }

    public MimeException(Throwable th) {
        super(th);
    }
}

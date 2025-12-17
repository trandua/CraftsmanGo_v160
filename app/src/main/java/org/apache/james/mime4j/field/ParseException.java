package org.apache.james.mime4j.field;

import org.apache.james.mime4j.MimeException;

/* loaded from: classes.dex */
public class ParseException extends MimeException {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    public ParseException(String str) {
        super(str);
    }

    protected ParseException(String str, Throwable th) {
        super(str, th);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ParseException(Throwable th) {
        super(th);
    }
}

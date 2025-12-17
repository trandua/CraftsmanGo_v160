package org.apache.james.mime4j;

import java.io.IOException;

/* loaded from: classes.dex */
public class MimeIOException extends IOException {
    private static final long serialVersionUID = 5393613459533735409L;

    public MimeIOException(String str) {
        this(new MimeException(str));
    }

    public MimeIOException(MimeException mimeException) {
        super(mimeException.getMessage());
        initCause(mimeException);
    }
}

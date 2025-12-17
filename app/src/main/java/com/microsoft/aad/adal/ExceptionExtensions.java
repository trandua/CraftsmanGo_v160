package com.microsoft.aad.adal;

import java.io.PrintWriter;
import java.io.StringWriter;

/* loaded from: classes3.dex */
final class ExceptionExtensions {
    private ExceptionExtensions() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getExceptionMessage(Exception exc) {
        if (exc == null) {
            return null;
        }
        String message = exc.getMessage();
        if (message != null) {
            return message;
        }
        StringWriter stringWriter = new StringWriter();
        exc.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}

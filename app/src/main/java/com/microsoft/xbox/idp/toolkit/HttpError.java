package com.microsoft.xbox.idp.toolkit;

import java.io.InputStream;
import java.util.Scanner;

/* loaded from: classes3.dex */
public class HttpError {
    private static final String INPUT_START_TOKEN = "\\A";
    private final int errorCode;
    private final String errorMessage;
    private final int httpStatus;

    public HttpError(int i, int i2, InputStream inputStream) {
        this.errorCode = i;
        this.httpStatus = i2;
        Scanner useDelimiter = new Scanner(inputStream).useDelimiter(INPUT_START_TOKEN);
        this.errorMessage = useDelimiter.hasNext() ? useDelimiter.next() : "";
    }

    public HttpError(int i, int i2, String str) {
        this.errorCode = i;
        this.httpStatus = i2;
        this.errorMessage = str;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("errorCode: ");
        stringBuffer.append(this.errorCode);
        stringBuffer.append(", httpStatus: ");
        stringBuffer.append(this.httpStatus);
        stringBuffer.append(", errorMessage: ");
        stringBuffer.append(this.errorMessage);
        return stringBuffer.toString();
    }
}

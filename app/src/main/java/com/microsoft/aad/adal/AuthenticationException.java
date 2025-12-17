package com.microsoft.aad.adal;

import android.content.Context;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;

/* loaded from: classes3.dex */
public class AuthenticationException extends Exception {
    static final long serialVersionUID = 1;
    private ADALError mCode;
    private HashMap<String, String> mHttpResponseBody;
    private HashMap<String, List<String>> mHttpResponseHeaders;
    private int mServiceStatusCode;

    public AuthenticationException() {
        this.mHttpResponseBody = null;
        this.mServiceStatusCode = -1;
        this.mHttpResponseHeaders = null;
    }

    public AuthenticationException(ADALError aDALError) {
        this.mHttpResponseBody = null;
        this.mServiceStatusCode = -1;
        this.mHttpResponseHeaders = null;
        this.mCode = aDALError;
    }

    public AuthenticationException(ADALError aDALError, String str) {
        super(str);
        this.mHttpResponseBody = null;
        this.mServiceStatusCode = -1;
        this.mHttpResponseHeaders = null;
        this.mCode = aDALError;
    }

    public AuthenticationException(ADALError aDALError, String str, HttpWebResponse httpWebResponse) {
        super(str);
        this.mHttpResponseBody = null;
        this.mServiceStatusCode = -1;
        this.mHttpResponseHeaders = null;
        this.mCode = aDALError;
        setHttpResponse(httpWebResponse);
    }

    public AuthenticationException(ADALError aDALError, String str, HttpWebResponse httpWebResponse, Throwable th) {
        this(aDALError, str, th);
        setHttpResponse(httpWebResponse);
    }

    public AuthenticationException(ADALError aDALError, String str, Throwable th) {
        super(str, th);
        this.mHttpResponseBody = null;
        this.mServiceStatusCode = -1;
        this.mHttpResponseHeaders = null;
        this.mCode = aDALError;
        if (th == null || !(th instanceof AuthenticationException)) {
            return;
        }
        AuthenticationException authenticationException = (AuthenticationException) th;
        this.mServiceStatusCode = authenticationException.getServiceStatusCode();
        if (authenticationException.getHttpResponseBody() != null) {
            this.mHttpResponseBody = new HashMap<>(authenticationException.getHttpResponseBody());
        }
        if (authenticationException.getHttpResponseHeaders() != null) {
            this.mHttpResponseHeaders = new HashMap<>(authenticationException.getHttpResponseHeaders());
        }
    }

    public ADALError getCode() {
        return this.mCode;
    }

    public HashMap<String, String> getHttpResponseBody() {
        return this.mHttpResponseBody;
    }

    public HashMap<String, List<String>> getHttpResponseHeaders() {
        return this.mHttpResponseHeaders;
    }

    public String getLocalizedMessage(Context context) {
        if (!StringExtensions.isNullOrBlank(super.getMessage())) {
            return super.getMessage();
        }
        ADALError aDALError = this.mCode;
        if (aDALError != null) {
            return aDALError.getLocalizedDescription(context);
        }
        return null;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return getLocalizedMessage(null);
    }

    public int getServiceStatusCode() {
        return this.mServiceStatusCode;
    }

    public void setHttpResponse(AuthenticationResult authenticationResult) {
        if (authenticationResult != null) {
            this.mHttpResponseBody = authenticationResult.getHttpResponseBody();
            this.mHttpResponseHeaders = authenticationResult.getHttpResponseHeaders();
            this.mServiceStatusCode = authenticationResult.getServiceStatusCode();
        }
    }

    public void setHttpResponse(HttpWebResponse httpWebResponse) {
        if (httpWebResponse != null) {
            this.mServiceStatusCode = httpWebResponse.getStatusCode();
            if (httpWebResponse.getResponseHeaders() != null) {
                this.mHttpResponseHeaders = new HashMap<>(httpWebResponse.getResponseHeaders());
            }
            if (httpWebResponse.getBody() != null) {
                try {
                    this.mHttpResponseBody = new HashMap<>(HashMapExtensions.getJsonResponse(httpWebResponse));
                } catch (JSONException e) {
                    Logger.m14609e("AuthenticationException", "Json exception", ExceptionExtensions.getExceptionMessage(e), ADALError.SERVER_INVALID_JSON_RESPONSE);
                }
            }
        }
    }

    public void setHttpResponseBody(HashMap<String, String> hashMap) {
        this.mHttpResponseBody = hashMap;
    }

    public void setHttpResponseHeaders(HashMap<String, List<String>> hashMap) {
        this.mHttpResponseHeaders = hashMap;
    }

    public void setServiceStatusCode(int i) {
        this.mServiceStatusCode = i;
    }
}

package com.microsoft.xbox.service.network.managers;

import java.net.URI;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;

/* loaded from: classes3.dex */
public class HttpDeleteWithRequestBody extends HttpPost {
    @Override // org.apache.http.client.methods.HttpPost, org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.methods.HttpUriRequest
    public String getMethod() {
        return HttpDelete.METHOD_NAME;
    }

    public HttpDeleteWithRequestBody(URI uri) {
        super(uri);
    }
}

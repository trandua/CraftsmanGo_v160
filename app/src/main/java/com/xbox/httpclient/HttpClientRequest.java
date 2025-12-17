package com.xbox.httpclient;

import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.http.client.methods.HttpPut;

/* loaded from: classes3.dex */
public class HttpClientRequest {
    private static final byte[] NO_BODY = new byte[0];
    private static final OkHttpClient OK_CLIENT = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();
    private Request.Builder requestBuilder = new Request.Builder();

    public native void OnRequestCompleted(long j, HttpClientResponse httpClientResponse);

    public native void OnRequestFailed(long j, String str, boolean z);

    String _urll_ = "";
    public void doRequestAsync(final long j) {
        if(requestBuilder != null) {
            OK_CLIENT.newCall(this.requestBuilder.build()).enqueue(new Callback() { // from class: com.xbox.httpclient.HttpClientRequest.1
                @Override // okhttp3.Callback
                public void onFailure(Call call, IOException iOException) {
                    HttpClientRequest.this.OnRequestFailed(j, iOException.getClass().getCanonicalName(), iOException instanceof UnknownHostException);
                }

                @Override // okhttp3.Callback
                public void onResponse(Call call, Response response) {
                    HttpClientRequest httpClientRequest = HttpClientRequest.this;
                    long j2 = j;
                    httpClientRequest.OnRequestCompleted(j2, new HttpClientResponse(j2, response));
                }
            });
        }
    }

    public void setHttpHeader(String str, String str2) {
        if(requestBuilder != null)
            this.requestBuilder = this.requestBuilder.addHeader(str, str2);
    }

    public void setHttpMethodAndBody(String str, long j, String str2, long j2) {
        if(requestBuilder != null) {
            RequestBody create;
            if (j2 != 0) {
                create = new HttpClientRequestBody(j, str2, j2);
            } else {
                create = null;
                if ("POST".equals(str) || HttpPut.METHOD_NAME.equals(str)) {
                    create = RequestBody.create(str2 != null ? MediaType.parse(str2) : null, NO_BODY);
                }
            }
            this.requestBuilder.method(str, create);
        }
    }

    public void setHttpUrl(String str) {
        _urll_ = str;
        if(str.equals("https://vortex.data.microsoft.com/collect/v1")){
            _urll_ = "https";
            requestBuilder = null;
        }
        else {
            this.requestBuilder = this.requestBuilder.url(str);
        }
    }
}

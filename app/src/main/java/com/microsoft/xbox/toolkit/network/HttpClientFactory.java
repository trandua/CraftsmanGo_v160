package com.microsoft.xbox.toolkit.network;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/* loaded from: classes3.dex */
public class HttpClientFactory {
    private static final int CONNECTION_PER_ROUTE = 16;
    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 40;
    private static final int MAX_TOTAL_CONNECTIONS = 32;
    public static HttpClientFactory networkOperationsFactory = new HttpClientFactory();
    public static HttpClientFactory noRedirectNetworkOperationsFactory = new HttpClientFactory(false);
    public static HttpClientFactory textureFactory = new HttpClientFactory(true);
    private AbstractXLEHttpClient client;
    private AbstractXLEHttpClient clientWithTimeoutOverride;
    private ClientConnectionManager connectionManager;
    private Object httpSyncObject;
    private HttpParams params;

    public void setHttpClient(AbstractXLEHttpClient abstractXLEHttpClient) {
    }

    public HttpClientFactory() {
        this(false);
    }

    public HttpClientFactory(boolean z) {
        this.connectionManager = null;
        this.httpSyncObject = new Object();
        this.client = null;
        this.clientWithTimeoutOverride = null;
        this.params = new BasicHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        HttpProtocolParams.setVersion(this.params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(this.params, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(this.params, false);
        HttpClientParams.setRedirecting(this.params, z);
        if (XboxLiveEnvironment.Instance().getProxyEnabled()) {
            this.params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("itgproxy.redmond.corp.microsoft.com", 80));
        }
        HttpConnectionParams.setConnectionTimeout(this.params, 40000);
        HttpConnectionParams.setSoTimeout(this.params, 40000);
        HttpConnectionParams.setSocketBufferSize(this.params, 8192);
        ConnManagerParams.setMaxConnectionsPerRoute(this.params, new ConnPerRouteBean(16));
        ConnManagerParams.setMaxTotalConnections(this.params, 32);
        this.connectionManager = new ThreadSafeClientConnManager(this.params, schemeRegistry);
    }

    public ClientConnectionManager getClientConnectionManager() {
        return this.connectionManager;
    }

    public AbstractXLEHttpClient getHttpClient(int i) {
        synchronized (this.httpSyncObject) {
            if (i > 0) {
                AbstractXLEHttpClient abstractXLEHttpClient = this.clientWithTimeoutOverride;
                if (abstractXLEHttpClient != null) {
                    return abstractXLEHttpClient;
                }
                HttpParams copy = this.params.copy();
                int i2 = i * 1000;
                HttpConnectionParams.setConnectionTimeout(copy, i2);
                HttpConnectionParams.setSoTimeout(copy, i2);
                return new XLEHttpClient(this.connectionManager, copy);
            }
            if (this.client == null) {
                this.client = new XLEHttpClient(this.connectionManager, this.params);
            }
            return this.client;
        }
    }

    public HttpParams getHttpParams() {
        return this.params;
    }
}

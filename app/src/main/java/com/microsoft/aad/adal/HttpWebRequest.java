package com.microsoft.aad.adal;

import android.content.Context;
import android.os.Debug;
import android.os.Process;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.protocol.HTTP;

/* loaded from: classes3.dex */
class HttpWebRequest {
    private static final int DEBUG_SIMULATE_DELAY = 0;
    static final String REQUEST_METHOD_GET = "GET";
    static final String REQUEST_METHOD_POST = "POST";
    private static final String TAG = "HttpWebRequest";
    private final byte[] mRequestContent;
    private final String mRequestContentType;
    private final Map<String, String> mRequestHeaders;
    private final String mRequestMethod;
    private final URL mUrl;
    private static final int CONNECT_TIME_OUT = AuthenticationSettings.INSTANCE.getConnectTimeOut();
    private static final int READ_TIME_OUT = AuthenticationSettings.INSTANCE.getReadTimeOut();

    public HttpWebRequest(URL url, String str, Map<String, String> map) {
        this(url, str, map, null, null);
    }

    public HttpWebRequest(URL url, String str, Map<String, String> map, byte[] bArr, String str2) {
        this.mUrl = url;
        this.mRequestMethod = str;
        HashMap hashMap = new HashMap();
        this.mRequestHeaders = hashMap;
        if (url != null) {
            hashMap.put(HTTP.TARGET_HOST, url.getAuthority());
        }
        hashMap.putAll(map);
        this.mRequestContent = bArr;
        this.mRequestContentType = str2;
    }

    public static String convertStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(readLine);
                } else {
                    bufferedReader.close();
                    return sb.toString();
                }
            } catch (Throwable th) {
                bufferedReader.close();
                throw th;
            }
        }
    }

    private static void safeCloseStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Logger.m14610e(TAG, "Failed to close the stream. ", "", ADALError.IO_EXCEPTION, e);
            }
        }
    }

    private static void setRequestBody(HttpURLConnection httpURLConnection, byte[] bArr, String str) throws IOException {
        OutputStream outputStream;
        if (bArr != null) {
            httpURLConnection.setDoOutput(true);
            if (str != null && !str.isEmpty()) {
                httpURLConnection.setRequestProperty("Content-Type", str);
            }
            httpURLConnection.setRequestProperty(HTTP.CONTENT_LEN, Integer.toString(bArr.length));
            httpURLConnection.setFixedLengthStreamingMode(bArr.length);
            try {
                outputStream = httpURLConnection.getOutputStream();
            } catch (Throwable th) {
                th = th;
                outputStream = null;
            }
            try {
                outputStream.write(bArr);
                safeCloseStream(outputStream);
            } catch (Throwable th2) {
//                th = th2;
                safeCloseStream(outputStream);
                throw th2;
            }
        }
    }

    private HttpURLConnection setupConnection() throws IOException {
        Logger.m14615v("HttpWebRequest:setupConnection", "HttpWebRequest setupConnection.", "Thread:" + Process.myTid(), null);
        URL url = this.mUrl;
        if (url == null) {
            throw new IllegalArgumentException("requestURL");
        }
        if (url.getProtocol().equalsIgnoreCase("http")) {
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection createHttpUrlConnection = HttpUrlConnectionFactory.createHttpUrlConnection(this.mUrl);
            createHttpUrlConnection.setConnectTimeout(CONNECT_TIME_OUT);
            createHttpUrlConnection.setRequestProperty(HTTP.CONN_DIRECTIVE, "close");
            for (Map.Entry<String, String> entry : this.mRequestHeaders.entrySet()) {
                Logger.m14615v("HttpWebRequest:setupConnection", "Setting header. ", "Header: " + entry.getKey(), null);
                createHttpUrlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            createHttpUrlConnection.setReadTimeout(READ_TIME_OUT);
            createHttpUrlConnection.setInstanceFollowRedirects(true);
            createHttpUrlConnection.setUseCaches(false);
            createHttpUrlConnection.setRequestMethod(this.mRequestMethod);
            createHttpUrlConnection.setDoInput(true);
            setRequestBody(createHttpUrlConnection, this.mRequestContent, this.mRequestContentType);
            return createHttpUrlConnection;
        }
        throw new IllegalArgumentException("requestURL");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void throwIfNetworkNotAvailable(Context context) throws AuthenticationException {
        DefaultConnectionService defaultConnectionService = new DefaultConnectionService(context);
        if (defaultConnectionService.isConnectionAvailable()) {
            return;
        }
        if (defaultConnectionService.isNetworkDisabledFromOptimizations()) {
            ADALError aDALError = ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION;
            AuthenticationException authenticationException = new AuthenticationException(aDALError, "Connection is not available to refresh token because power optimization is enabled. And the device is in doze mode or the app is standby" + ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION.getDescription());
            Logger.m14617w(TAG, "Connection is not available to refresh token because power optimization is enabled. And the device is in doze mode or the app is standby" + ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION.getDescription(), "", ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION);
            throw authenticationException;
        }
        AuthenticationException authenticationException2 = new AuthenticationException(ADALError.DEVICE_CONNECTION_IS_NOT_AVAILABLE, "Connection is not available to refresh token");
        Logger.m14617w(TAG, "Connection is not available to refresh token", "", ADALError.DEVICE_CONNECTION_IS_NOT_AVAILABLE);
        throw authenticationException2;
    }

    public HttpWebResponse send() throws IOException {
        InputStream inputStream;
        Logger.m14615v("HttpWebRequest:send", "HttpWebRequest send. ", " Thread: " + Process.myTid(), null);
        HttpURLConnection httpURLConnection = setupConnection();
        try {
            inputStream = httpURLConnection.getInputStream();
        } catch (IOException e) {
            Logger.m14609e("HttpWebRequest:send", "IOException is thrown when sending the request. ", e.getMessage(), ADALError.SERVER_ERROR);
            InputStream errorStream = httpURLConnection.getErrorStream();
            if (errorStream == null) {
                throw e;
            }
            inputStream = errorStream;
        } catch (Throwable th) {
            safeCloseStream(null);
            throw th;
        }
        int responseCode = httpURLConnection.getResponseCode();
        String convertStreamToString = convertStreamToString(inputStream);
        Debug.isDebuggerConnected();
        Logger.m14614v("HttpWebRequest:send", "Response is received.");
        HttpWebResponse httpWebResponse = new HttpWebResponse(responseCode, convertStreamToString, httpURLConnection.getHeaderFields());
        safeCloseStream(inputStream);
        return httpWebResponse;
    }
}

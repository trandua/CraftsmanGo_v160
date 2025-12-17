package com.microsoft.aad.adal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.ChallengeResponseBuilder;
import com.microsoft.aad.adal.HttpAuthDialog;
import java.util.HashMap;
import java.util.Locale;

/* loaded from: classes3.dex */
abstract class BasicWebViewClient extends WebViewClient {
    public static final String BLANK_PAGE = "about:blank";
    private static final String INSTALL_URL_KEY = "app_link";
    private static final String TAG = "BasicWebViewClient";
    private final Context mCallingContext;
    private final String mRedirect;
    private final AuthenticationRequest mRequest;
    private final UIEvent mUIEvent;

    public abstract void cancelWebViewRequest();

    public abstract void postRunnable(Runnable runnable);

    public abstract void prepareForBrokerResumeRequest();

    public abstract boolean processInvalidUrl(WebView webView, String str);

    public abstract void processRedirectUrl(WebView webView, String str);

    public abstract void sendResponse(int i, Intent intent);

    public abstract void setPKeyAuthStatus(boolean z);

    public abstract void showSpinner(boolean z);

    public BasicWebViewClient(Context context, String str, AuthenticationRequest authenticationRequest, UIEvent uIEvent) {
        this.mCallingContext = context;
        this.mRedirect = str;
        this.mRequest = authenticationRequest;
        this.mUIEvent = uIEvent;
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedHttpAuthRequest(WebView webView, final HttpAuthHandler httpAuthHandler, String str, String str2) {
        Logger.m14612i("BasicWebViewClient:onReceivedHttpAuthRequest", "Start. ", "Host:" + str);
        this.mUIEvent.setNTLM(true);
        HttpAuthDialog httpAuthDialog = new HttpAuthDialog(this.mCallingContext, str, str2);
        httpAuthDialog.setOkListener(new HttpAuthDialog.OkListener() { // from class: com.microsoft.aad.adal.BasicWebViewClient.1
            @Override // com.microsoft.aad.adal.HttpAuthDialog.OkListener
            public void onOk(String str3, String str4, String str5, String str6) {
                Logger.m14612i("BasicWebViewClient:onReceivedHttpAuthRequest", "Handler proceed. ", "Host: " + str3);
                httpAuthHandler.proceed(str5, str6);
            }
        });
        httpAuthDialog.setCancelListener(new HttpAuthDialog.CancelListener() { // from class: com.microsoft.aad.adal.BasicWebViewClient.2
            @Override // com.microsoft.aad.adal.HttpAuthDialog.CancelListener
            public void onCancel() {
                Logger.m14612i("BasicWebViewClient:onReceivedHttpAuthRequest", "Handler cancelled", "");
                httpAuthHandler.cancel();
                BasicWebViewClient.this.cancelWebViewRequest();
            }
        });
        Logger.m14612i("BasicWebViewClient:onReceivedHttpAuthRequest", "Show dialog. ", "");
        httpAuthDialog.show();
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedError(WebView webView, int i, String str, String str2) {
        super.onReceivedError(webView, i, str, str2);
        showSpinner(false);
        Logger.m14609e(TAG, "Webview received an error. ErrorCode:" + i, str, ADALError.ERROR_WEBVIEW);
        Intent intent = new Intent();
        intent.putExtra(AuthenticationConstants.Browser.RESPONSE_ERROR_CODE, "Error Code:" + i);
        intent.putExtra(AuthenticationConstants.Browser.RESPONSE_ERROR_MESSAGE, str);
        intent.putExtra(AuthenticationConstants.Browser.RESPONSE_REQUEST_INFO, this.mRequest);
        sendResponse(2002, intent);
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        super.onReceivedSslError(webView, sslErrorHandler, sslError);
        showSpinner(false);
        sslErrorHandler.cancel();
        Logger.m14609e(TAG, "Received ssl error. ", "", ADALError.ERROR_FAILED_SSL_HANDSHAKE);
        Intent intent = new Intent();
        intent.putExtra(AuthenticationConstants.Browser.RESPONSE_ERROR_CODE, "Code:-11");
        intent.putExtra(AuthenticationConstants.Browser.RESPONSE_ERROR_MESSAGE, sslError.toString());
        intent.putExtra(AuthenticationConstants.Browser.RESPONSE_REQUEST_INFO, this.mRequest);
        sendResponse(2002, intent);
    }

    @Override // android.webkit.WebViewClient
    public void onPageFinished(WebView webView, String str) {
        super.onPageFinished(webView, str);
        webView.setVisibility(0);
        if (str.startsWith(BLANK_PAGE)) {
            return;
        }
        showSpinner(false);
    }

    @Override // android.webkit.WebViewClient
    public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
        logPageStartLoadingUrl(str);
        super.onPageStarted(webView, str, bitmap);
        showSpinner(true);
    }

    private void logPageStartLoadingUrl(String str) {
        if (TextUtils.isEmpty(str)) {
            Logger.m14614v("BasicWebViewClient:logPageStartLoadingUrl", "onPageStarted: Null url for page to load.");
            return;
        }
        Uri parse = Uri.parse(str);
        if (parse.isOpaque()) {
            Logger.m14615v("BasicWebViewClient:logPageStartLoadingUrl", "onPageStarted: Non-hierarchical loading uri. ", "Url: " + str, null);
        } else if (StringExtensions.isNullOrBlank(parse.getQueryParameter(AuthenticationConstants.OAuth2.CODE))) {
            Logger.m14615v("BasicWebViewClient:logPageStartLoadingUrl", "Webview starts loading. ", " Host: " + parse.getHost() + " Path: " + parse.getPath() + " Full loading url is: " + str, null);
        } else {
            Logger.m14615v("BasicWebViewClient:logPageStartLoadingUrl", "Webview starts loading. ", " Host: " + parse.getHost() + " Path: " + parse.getPath() + " Auth code is returned for the loading url.", null);
        }
    }

    @Override // android.webkit.WebViewClient
    public boolean shouldOverrideUrlLoading(final WebView webView, final String str) {
        Logger.m14614v("BasicWebViewClient:shouldOverrideUrlLoading", "Navigation is detected");
        if (str.startsWith(AuthenticationConstants.Broker.PKEYAUTH_REDIRECT)) {
            Logger.m14614v("BasicWebViewClient:shouldOverrideUrlLoading", "Webview detected request for pkeyauth challenge.");
            webView.stopLoading();
            setPKeyAuthStatus(true);
            new Thread(new Runnable() { // from class: com.microsoft.aad.adal.BasicWebViewClient.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        final ChallengeResponseBuilder.ChallengeResponse challengeResponseFromUri = new ChallengeResponseBuilder(new JWSBuilder()).getChallengeResponseFromUri(str);
                        final HashMap hashMap = new HashMap();
                        hashMap.put("Authorization", challengeResponseFromUri.getAuthorizationHeaderValue());
                        BasicWebViewClient.this.postRunnable(new Runnable() { // from class: com.microsoft.aad.adal.BasicWebViewClient.3.1
                            @Override // java.lang.Runnable
                            public void run() {
                                String submitUrl = challengeResponseFromUri.getSubmitUrl();
                                Logger.m14615v("BasicWebViewClient:shouldOverrideUrlLoading", "Respond to pkeyAuth challenge", "Challenge submit url:" + challengeResponseFromUri.getSubmitUrl(), null);
                                webView.loadUrl(submitUrl, hashMap);
                            }
                        });
                    } catch (AuthenticationServerProtocolException e) {
                        Logger.m14610e("BasicWebViewClient:shouldOverrideUrlLoading", "Argument exception. ", e.getMessage(), ADALError.ARGUMENT_EXCEPTION, e);
                        Intent intent = new Intent();
                        intent.putExtra(AuthenticationConstants.Browser.RESPONSE_AUTHENTICATION_EXCEPTION, e);
                        if (BasicWebViewClient.this.mRequest != null) {
                            intent.putExtra(AuthenticationConstants.Browser.RESPONSE_REQUEST_INFO, BasicWebViewClient.this.mRequest);
                        }
                        BasicWebViewClient.this.sendResponse(2005, intent);
                    } catch (AuthenticationException e2) {
                        Logger.m14610e("BasicWebViewClient:shouldOverrideUrlLoading", "It is failed to create device certificate response", e2.getMessage(), ADALError.DEVICE_CERTIFICATE_RESPONSE_FAILED, e2);
                        Intent intent2 = new Intent();
                        intent2.putExtra(AuthenticationConstants.Browser.RESPONSE_AUTHENTICATION_EXCEPTION, e2);
                        if (BasicWebViewClient.this.mRequest != null) {
                            intent2.putExtra(AuthenticationConstants.Browser.RESPONSE_REQUEST_INFO, BasicWebViewClient.this.mRequest);
                        }
                        BasicWebViewClient.this.sendResponse(2005, intent2);
                    }
                }
            }).start();
            return true;
        } else if (str.toLowerCase(Locale.US).startsWith(this.mRedirect.toLowerCase(Locale.US))) {
            Logger.m14614v("BasicWebViewClient:shouldOverrideUrlLoading", "Navigation starts with the redirect uri.");
            if (hasCancelError(str)) {
                Logger.m14612i("BasicWebViewClient:shouldOverrideUrlLoading", "Sending intent to cancel authentication activity", "");
                webView.stopLoading();
                cancelWebViewRequest();
                return true;
            }
            processRedirectUrl(webView, str);
            return true;
        } else if (str.startsWith(AuthenticationConstants.Broker.BROWSER_EXT_PREFIX)) {
            Logger.m14614v("BasicWebViewClient:shouldOverrideUrlLoading", "It is an external website request");
            openLinkInBrowser(str);
            webView.stopLoading();
            cancelWebViewRequest();
            return true;
        } else if (!str.startsWith(AuthenticationConstants.Broker.BROWSER_EXT_INSTALL_PREFIX)) {
            return processInvalidUrl(webView, str);
        } else {
            Logger.m14614v("BasicWebViewClient:shouldOverrideUrlLoading", "It is an install request");
            HashMap<String, String> urlParameters = StringExtensions.getUrlParameters(str);
            prepareForBrokerResumeRequest();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException unused) {
                Logger.m14614v("BasicWebViewClient:shouldOverrideUrlLoading", "Error occurred when having thread sleeping for 1 second.");
            }
            openLinkInBrowser(urlParameters.get(INSTALL_URL_KEY));
            webView.stopLoading();
            return true;
        }
    }

    public final Context getCallingContext() {
        return this.mCallingContext;
    }

    public void openLinkInBrowser(String str) {
        this.mCallingContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str.replace(AuthenticationConstants.Broker.BROWSER_EXT_PREFIX, AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX))));
    }

    private boolean hasCancelError(String str) {
        HashMap<String, String> urlParameters = StringExtensions.getUrlParameters(str);
        String str2 = urlParameters.get(AuthenticationConstants.OAuth2.ERROR);
        String str3 = urlParameters.get(AuthenticationConstants.OAuth2.ERROR_DESCRIPTION);
        if (StringExtensions.isNullOrBlank(str2)) {
            return false;
        }
        Logger.m14617w(TAG, "Cancel error: " + str2, str3, null);
        return true;
    }
}

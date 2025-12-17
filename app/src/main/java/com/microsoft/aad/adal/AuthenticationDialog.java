package com.microsoft.aad.adal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.UnsupportedEncodingException;
import org.simpleframework.xml.strategy.Name;

/* loaded from: classes3.dex */
public class AuthenticationDialog {
    protected static final String TAG = "AuthenticationDialog";
    private final AcquireTokenRequest mAcquireTokenRequest;
    private final Context mContext;
    private Dialog mDialog;
    private final Handler mHandlerInView;
    private final AuthenticationRequest mRequest;
    private WebView mWebView;

    public AuthenticationDialog(Handler handler, Context context, AcquireTokenRequest acquireTokenRequest, AuthenticationRequest authenticationRequest) {
        this.mHandlerInView = handler;
        this.mContext = context;
        this.mAcquireTokenRequest = acquireTokenRequest;
        this.mRequest = authenticationRequest;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getResourceId(String str, String str2) {
        return this.mContext.getResources().getIdentifier(str, str2, this.mContext.getPackageName());
    }

    public void show() {
        this.mHandlerInView.post(new Runnable() { // from class: com.microsoft.aad.adal.AuthenticationDialog.1
            @Override // java.lang.Runnable
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationDialog.this.mContext);
                View inflate = ((LayoutInflater) AuthenticationDialog.this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(AuthenticationDialog.this.getResourceId("dialog_authentication", "layout"), (ViewGroup) null);
                AuthenticationDialog authenticationDialog = AuthenticationDialog.this;
                authenticationDialog.mWebView = (WebView) inflate.findViewById(authenticationDialog.getResourceId("com_microsoft_aad_adal_webView1", Name.MARK));
                if (AuthenticationDialog.this.mWebView == null) {
                    Logger.m14609e("AuthenticationDialog:show", "Expected resource name for webview is com_microsoft_aad_adal_webView1. It is not in your layout file", "", ADALError.DEVELOPER_DIALOG_LAYOUT_INVALID);
                    Intent intent = new Intent();
                    intent.putExtra(AuthenticationConstants.Browser.REQUEST_ID, AuthenticationDialog.this.mRequest.getRequestId());
                    AuthenticationDialog.this.mAcquireTokenRequest.onActivityResult(1001, 2001, intent);
                    if (AuthenticationDialog.this.mHandlerInView != null) {
                        AuthenticationDialog.this.mHandlerInView.post(new Runnable() { // from class: com.microsoft.aad.adal.AuthenticationDialog.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (AuthenticationDialog.this.mDialog == null || !AuthenticationDialog.this.mDialog.isShowing()) {
                                    return;
                                }
                                AuthenticationDialog.this.mDialog.dismiss();
                            }
                        });
                        return;
                    }
                    return;
                }
                if (!AuthenticationSettings.INSTANCE.getDisableWebViewHardwareAcceleration()) {
                    AuthenticationDialog.this.mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    Logger.m14608d("AuthenticationDialog:show", "Hardware acceleration is disabled in WebView");
                }
                AuthenticationDialog.this.mWebView.getSettings().setJavaScriptEnabled(true);
                AuthenticationDialog.this.mWebView.requestFocus(130);
                String userAgentString = AuthenticationDialog.this.mWebView.getSettings().getUserAgentString();
                WebSettings settings = AuthenticationDialog.this.mWebView.getSettings();
                settings.setUserAgentString(userAgentString + AuthenticationConstants.Broker.CLIENT_TLS_NOT_SUPPORTED);
                String userAgentString2 = AuthenticationDialog.this.mWebView.getSettings().getUserAgentString();
                Logger.m14614v("AuthenticationDialog:show", "UserAgent:" + userAgentString2);
                AuthenticationDialog.this.mWebView.setOnTouchListener(new View.OnTouchListener() { // from class: com.microsoft.aad.adal.AuthenticationDialog.1.2
                    @Override // android.view.View.OnTouchListener
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        int action = motionEvent.getAction();
                        if ((action == 0 || action == 1) && !view.hasFocus()) {
                            view.requestFocus();
                            return false;
                        }
                        return false;
                    }
                });
                AuthenticationDialog.this.mWebView.getSettings().setLoadWithOverviewMode(true);
                AuthenticationDialog.this.mWebView.getSettings().setDomStorageEnabled(true);
                AuthenticationDialog.this.mWebView.getSettings().setUseWideViewPort(true);
                AuthenticationDialog.this.mWebView.getSettings().setBuiltInZoomControls(true);
                try {
                    final String codeRequestUrl = new Oauth2(AuthenticationDialog.this.mRequest).getCodeRequestUrl();
                    String redirectUri = AuthenticationDialog.this.mRequest.getRedirectUri();
                    AuthenticationDialog.this.mWebView.setWebViewClient(new DialogWebViewClient(AuthenticationDialog.this.mContext, redirectUri, AuthenticationDialog.this.mRequest));
                    AuthenticationDialog.this.mWebView.post(new Runnable() { // from class: com.microsoft.aad.adal.AuthenticationDialog.1.3
                        @Override // java.lang.Runnable
                        public void run() {
                            AuthenticationDialog.this.mWebView.loadUrl(BasicWebViewClient.BLANK_PAGE);
                            AuthenticationDialog.this.mWebView.loadUrl(codeRequestUrl);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    Logger.m14610e("AuthenticationDialog:show", "Encoding error", "", ADALError.ENCODING_IS_NOT_SUPPORTED, e);
                }
                builder.setView(inflate).setCancelable(true);
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.microsoft.aad.adal.AuthenticationDialog.1.4
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface dialogInterface) {
                        AuthenticationDialog.this.cancelFlow();
                    }
                });
                AuthenticationDialog.this.mDialog = builder.create();
                Logger.m14612i("AuthenticationDialog:show", "Showing authenticationDialog", "");
                AuthenticationDialog.this.mDialog.show();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelFlow() {
        Logger.m14612i(TAG, "Cancelling dialog", "");
        Intent intent = new Intent();
        intent.putExtra(AuthenticationConstants.Browser.REQUEST_ID, this.mRequest.getRequestId());
        this.mAcquireTokenRequest.onActivityResult(1001, 2001, intent);
        Handler handler = this.mHandlerInView;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.microsoft.aad.adal.AuthenticationDialog.2
                @Override // java.lang.Runnable
                public void run() {
                    if (AuthenticationDialog.this.mDialog == null || !AuthenticationDialog.this.mDialog.isShowing()) {
                        return;
                    }
                    AuthenticationDialog.this.mDialog.dismiss();
                }
            });
        }
    }

    /* loaded from: classes3.dex */
    class DialogWebViewClient extends BasicWebViewClient {
        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void prepareForBrokerResumeRequest() {
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public boolean processInvalidUrl(WebView webView, String str) {
            return false;
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void setPKeyAuthStatus(boolean z) {
        }

        public DialogWebViewClient(Context context, String str, AuthenticationRequest authenticationRequest) {
            super(context, str, authenticationRequest, null);
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void showSpinner(final boolean z) {
            if (AuthenticationDialog.this.mHandlerInView != null) {
                AuthenticationDialog.this.mHandlerInView.post(new Runnable() { // from class: com.microsoft.aad.adal.AuthenticationDialog.DialogWebViewClient.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ProgressBar progressBar;
                        if (AuthenticationDialog.this.mDialog == null || !AuthenticationDialog.this.mDialog.isShowing() || (progressBar = (ProgressBar) AuthenticationDialog.this.mDialog.findViewById(AuthenticationDialog.this.getResourceId("com_microsoft_aad_adal_progressBar", Name.MARK))) == null) {
                            return;
                        }
                        progressBar.setVisibility(z ? View.VISIBLE : View.INVISIBLE);
                    }
                });
            }
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void sendResponse(int i, Intent intent) {
            AuthenticationDialog.this.mDialog.dismiss();
            AuthenticationDialog.this.mAcquireTokenRequest.onActivityResult(1001, i, intent);
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void postRunnable(Runnable runnable) {
            AuthenticationDialog.this.mHandlerInView.post(runnable);
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void processRedirectUrl(WebView webView, String str) {
            Intent intent = new Intent();
            intent.putExtra(AuthenticationConstants.Browser.RESPONSE_FINAL_URL, str);
            intent.putExtra(AuthenticationConstants.Browser.RESPONSE_REQUEST_INFO, AuthenticationDialog.this.mRequest);
            intent.putExtra(AuthenticationConstants.Browser.REQUEST_ID, AuthenticationDialog.this.mRequest.getRequestId());
            sendResponse(2003, intent);
            webView.stopLoading();
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void cancelWebViewRequest() {
            AuthenticationDialog.this.cancelFlow();
        }
    }
}

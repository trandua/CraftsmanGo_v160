package com.microsoft.aad.adal;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.security.KeyChainException;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ClientCertRequest;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.gson.Gson;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.AuthenticationResult;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.UUID;
import org.simpleframework.xml.strategy.Name;

/* loaded from: classes3.dex */
public class AuthenticationActivity extends Activity {
    static final int BACK_PRESSED_CANCEL_DIALOG_STEPS = -2;
    private static final String TAG = "AuthenticationActivity";
    public AuthenticationRequest mAuthRequest;
    public String mCallingPackage;
    public int mCallingUID;
    public String mRedirectUrl;
    private ProgressDialog mSpinner;
    private String mStartUrl;
    public StorageHelper mStorageHelper;
    public int mWaitingRequestId;
    public WebView mWebView;
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mAuthenticatorResultBundle = null;
    public final IJWSBuilder mJWSBuilder = new JWSBuilder();
    public boolean mPkeyAuthRedirect = false;
    private ActivityBroadcastReceiver mReceiver = null;
    private boolean mRegisterReceiver = false;
    public UIEvent mUIEvent = null;
    public final IWebRequestHandler mWebRequestHandler = new WebRequestHandler();

    /* loaded from: classes3.dex */
    private class ActivityBroadcastReceiver extends BroadcastReceiver {
        public int mWaitingRequestId;

        private ActivityBroadcastReceiver() {
            this.mWaitingRequestId = -1;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Logger.m14614v("AuthenticationActivity:onReceive", "ActivityBroadcastReceiver onReceive");
            if (intent.getAction().equalsIgnoreCase(AuthenticationConstants.Browser.ACTION_CANCEL)) {
                Logger.m14614v("AuthenticationActivity:onReceive", "ActivityBroadcastReceiver onReceive action is for cancelling Authentication Activity");
                if (intent.getIntExtra(AuthenticationConstants.Browser.REQUEST_ID, 0) == this.mWaitingRequestId) {
                    Logger.m14614v("AuthenticationActivity:onReceive", "Waiting requestId is same and cancelling this activity");
                    AuthenticationActivity.this.finish();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public class CustomWebViewClient extends BasicWebViewClient {
        final AuthenticationActivity this$0;

        public CustomWebViewClient(AuthenticationActivity authenticationActivity) {
            super(authenticationActivity, authenticationActivity.mRedirectUrl, authenticationActivity.mAuthRequest, authenticationActivity.mUIEvent);
            this.this$0 = authenticationActivity;
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void cancelWebViewRequest() {
            this.this$0.cancelRequest();
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedClientCertRequest(WebView webView, final ClientCertRequest clientCertRequest) {
            Logger.m14614v("AuthenticationActivity:onReceivedClientCertRequest", "Webview receives client TLS request.");
            Principal[] principals = clientCertRequest.getPrincipals();
            if (principals != null) {
                for (Principal principal : principals) {
                    if (principal.getName().contains("CN=MS-Organization-Access")) {
                        Logger.m14614v("AuthenticationActivity:onReceivedClientCertRequest", "Cancelling the TLS request, not respond to TLS challenge triggered by device authentication.");
                        clientCertRequest.cancel();
                        return;
                    }
                }
            }
            KeyChain.choosePrivateKeyAlias(AuthenticationActivity.this, new KeyChainAliasCallback() { // from class: com.microsoft.aad.adal.AuthenticationActivity.CustomWebViewClient.1
                @Override // android.security.KeyChainAliasCallback
                public void alias(String str) {
                    if (str == null) {
                        Logger.m14614v("AuthenticationActivity:onReceivedClientCertRequest", "No certificate chosen by user, cancelling the TLS request.");
                        clientCertRequest.cancel();
                        return;
                    }
                    try {
                        X509Certificate[] certificateChain = KeyChain.getCertificateChain(AuthenticationActivity.this.getApplicationContext(), str);
                        PrivateKey privateKey = KeyChain.getPrivateKey(CustomWebViewClient.this.getCallingContext(), str);
                        Logger.m14614v("AuthenticationActivity:onReceivedClientCertRequest", "Certificate is chosen by user, proceed with TLS request.");
                        clientCertRequest.proceed(privateKey, certificateChain);
                    } catch (KeyChainException e) {
                        Logger.m14611e("AuthenticationActivity:onReceivedClientCertRequest", "KeyChain exception", e);
                        clientCertRequest.cancel();
                    } catch (InterruptedException e2) {
                        Logger.m14611e("AuthenticationActivity:onReceivedClientCertRequest", "InterruptedException exception", e2);
                        clientCertRequest.cancel();
                    }
                }
            }, clientCertRequest.getKeyTypes(), clientCertRequest.getPrincipals(), clientCertRequest.getHost(), clientCertRequest.getPort(), null);
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void postRunnable(Runnable runnable) {
            this.this$0.mWebView.post(runnable);
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void prepareForBrokerResumeRequest() {
            this.this$0.prepareForBrokerResume();
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public boolean processInvalidUrl(WebView webView, String str) {
            AuthenticationActivity authenticationActivity = this.this$0;
            if (authenticationActivity.isBrokerRequest(authenticationActivity.getIntent()) && str.startsWith(AuthenticationConstants.Broker.REDIRECT_PREFIX)) {
                Logger.m14609e("AuthenticationActivity:processInvalidUrl", "The RedirectUri is not as expected.", String.format("Received %s and expected %s", str, this.this$0.mRedirectUrl), ADALError.DEVELOPER_REDIRECTURI_INVALID);
                this.this$0.returnError(ADALError.DEVELOPER_REDIRECTURI_INVALID, String.format("The RedirectUri is not as expected. Received %s and expected %s", str, this.this$0.mRedirectUrl));
                webView.stopLoading();
                return true;
            } else if (str.toLowerCase(Locale.US).equals(BasicWebViewClient.BLANK_PAGE)) {
                Logger.m14614v("AuthenticationActivity:processInvalidUrl", "It is an blank page request");
                return true;
            } else if (str.toLowerCase(Locale.US).startsWith(AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX)) {
                return false;
            } else {
                Logger.m14609e("AuthenticationActivity:processInvalidUrl", "The webview was redirected to an unsafe URL.", "", ADALError.WEBVIEW_REDIRECTURL_NOT_SSL_PROTECTED);
                this.this$0.returnError(ADALError.WEBVIEW_REDIRECTURL_NOT_SSL_PROTECTED, "The webview was redirected to an unsafe URL.");
                webView.stopLoading();
                return true;
            }
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void processRedirectUrl(WebView webView, String str) {
            AuthenticationActivity authenticationActivity = this.this$0;
            if (!authenticationActivity.isBrokerRequest(authenticationActivity.getIntent())) {
                Logger.m14612i("AuthenticationActivity:processRedirectUrl", "It is not a broker request", "");
                Intent intent = new Intent();
                intent.putExtra(AuthenticationConstants.Browser.RESPONSE_FINAL_URL, str);
                intent.putExtra(AuthenticationConstants.Browser.RESPONSE_REQUEST_INFO, this.this$0.mAuthRequest);
                this.this$0.returnToCaller(2003, intent);
                webView.stopLoading();
                return;
            }
            Logger.m14612i("AuthenticationActivity:processRedirectUrl", "It is a broker request", "");
            AuthenticationActivity authenticationActivity2 = this.this$0;
            authenticationActivity2.displaySpinnerWithMessage(authenticationActivity2.getText(authenticationActivity2.getResources().getIdentifier("broker_processing", "string", this.this$0.getPackageName())));
            webView.stopLoading();
            AuthenticationActivity authenticationActivity3 = this.this$0;
            new TokenTask(authenticationActivity3, authenticationActivity3.mWebRequestHandler, this.this$0.mAuthRequest, this.this$0.mCallingPackage, this.this$0.mCallingUID).execute(str);
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void sendResponse(int i, Intent intent) {
            this.this$0.returnToCaller(i, intent);
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void setPKeyAuthStatus(boolean z) {
            this.this$0.mPkeyAuthRedirect = z;
        }

        @Override // com.microsoft.aad.adal.BasicWebViewClient
        public void showSpinner(boolean z) {
            this.this$0.displaySpinner(z);
        }
    }

    /* loaded from: classes3.dex */
    class TokenTask extends AsyncTask<String, String, TokenTaskResult> {
        private AccountManager mAccountManager;
        private int mAppCallingUID;
        private String mPackageName;
        private AuthenticationRequest mRequest;
        private IWebRequestHandler mRequestHandler;
        final AuthenticationActivity this$0;

        public TokenTask(AuthenticationActivity authenticationActivity) {
            this.this$0 = authenticationActivity;
        }

        public TokenTask(AuthenticationActivity authenticationActivity, IWebRequestHandler iWebRequestHandler, AuthenticationRequest authenticationRequest, String str, int i) {
            this.this$0 = authenticationActivity;
            this.mRequestHandler = iWebRequestHandler;
            this.mRequest = authenticationRequest;
            this.mPackageName = str;
            this.mAppCallingUID = i;
            this.mAccountManager = AccountManager.get(authenticationActivity);
        }

        private void appendAppUIDToAccount(Account account) throws GeneralSecurityException, IOException {
            String userData = this.mAccountManager.getUserData(account, AuthenticationConstants.Broker.ACCOUNT_UID_CACHES);
            String str = "";
            if (userData != null) {
                try {
                    str = this.this$0.mStorageHelper.decrypt(userData);
                } catch (IOException | GeneralSecurityException e) {
                    Logger.m14610e("AuthenticationActivity:appendAppUIDToAccount", "appUIDList failed to decrypt", "appIdList:" + userData, ADALError.ENCRYPTION_FAILED, e);
                    Logger.m14612i("AuthenticationActivity:appendAppUIDToAccount", "Reset the appUIDlist", "");
                }
            }
            Logger.m14613i("AuthenticationActivity:appendAppUIDToAccount", "Add calling UID. ", "App UID: " + this.mAppCallingUID + "appIdList:" + str, null);
            if (str.contains(AuthenticationConstants.Broker.USERDATA_UID_KEY + this.mAppCallingUID)) {
                return;
            }
            Logger.m14613i("AuthenticationActivity:appendAppUIDToAccount", "Account has new calling UID. ", "App UID: " + this.mAppCallingUID, null);
            StorageHelper storageHelper = this.this$0.mStorageHelper;
            AccountManager accountManager = this.mAccountManager;
            accountManager.setUserData(account, AuthenticationConstants.Broker.ACCOUNT_UID_CACHES, storageHelper.encrypt(str + AuthenticationConstants.Broker.USERDATA_UID_KEY + this.mAppCallingUID));
        }

        private String getBrokerAppCacheKey(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            String createHash = StringExtensions.createHash(AuthenticationConstants.Broker.USERDATA_UID_KEY + this.mAppCallingUID + str);
            Logger.m14615v(AuthenticationActivity.TAG, "Get broker app cache key.", "Key hash is:" + createHash + " calling app UID:" + this.mAppCallingUID + " Key is: " + str, null);
            return createHash;
        }

        private void saveCacheKey(String str, Account account, int i) {
            Logger.m14614v("AuthenticationActivity:saveCacheKey", "Get CacheKeys for account");
            String userData = this.mAccountManager.getUserData(account, AuthenticationConstants.Broker.USERDATA_CALLER_CACHEKEYS + i);
            if (userData == null) {
                userData = "";
            }
            if (userData.contains(AuthenticationConstants.Broker.CALLER_CACHEKEY_PREFIX + str)) {
                return;
            }
            Logger.m14615v("AuthenticationActivity:saveCacheKey", "Account does not have the cache key. Saving it to account for the caller. ", "callerUID: " + i + "The key to be saved is: " + str, null);
            String str2 = userData + AuthenticationConstants.Broker.CALLER_CACHEKEY_PREFIX + str;
            this.mAccountManager.setUserData(account, AuthenticationConstants.Broker.USERDATA_CALLER_CACHEKEYS + i, str2);
            Logger.m14615v("AuthenticationActivity:saveCacheKey", "Cache key saved into key list for the caller.", "keylist:" + str2, null);
        }

        private void setAccount(TokenTaskResult tokenTaskResult) throws GeneralSecurityException, IOException {
            String brokerAccountName = this.mRequest.getBrokerAccountName();
            Account[] accountsByType = this.mAccountManager.getAccountsByType(AuthenticationConstants.Broker.BROKER_ACCOUNT_TYPE);
            if (accountsByType.length != 1) {
                tokenTaskResult.mTaskResult = null;
                tokenTaskResult.mTaskException = new AuthenticationException(ADALError.BROKER_SINGLE_USER_EXPECTED);
                return;
            }
            Account account = accountsByType[0];
            UserInfo userInfo = tokenTaskResult.mTaskResult.getUserInfo();
            if (userInfo == null || StringExtensions.isNullOrBlank(userInfo.getUserId())) {
                Logger.m14612i("AuthenticationActivity:setAccount", "Set userinfo from account", "");
                tokenTaskResult.mTaskResult.setUserInfo(new UserInfo(brokerAccountName, brokerAccountName, "", "", brokerAccountName));
                this.mRequest.setLoginHint(brokerAccountName);
            } else {
                Logger.m14612i("AuthenticationActivity:setAccount", "Saving userinfo to account", "");
                this.mAccountManager.setUserData(account, AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID, userInfo.getUserId());
                this.mAccountManager.setUserData(account, AuthenticationConstants.Broker.ACCOUNT_USERINFO_GIVEN_NAME, userInfo.getGivenName());
                this.mAccountManager.setUserData(account, AuthenticationConstants.Broker.ACCOUNT_USERINFO_FAMILY_NAME, userInfo.getFamilyName());
                this.mAccountManager.setUserData(account, AuthenticationConstants.Broker.ACCOUNT_USERINFO_IDENTITY_PROVIDER, userInfo.getIdentityProvider());
                this.mAccountManager.setUserData(account, AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID_DISPLAYABLE, userInfo.getDisplayableId());
            }
            tokenTaskResult.mAccountName = brokerAccountName;
            Logger.m14612i("AuthenticationActivity:setAccount", "Setting account in account manager. ", "Package: " + this.mPackageName + " calling app UID:" + this.mAppCallingUID + " Account name: " + brokerAccountName);
            Gson gson = new Gson();
            Logger.m14612i("AuthenticationActivity:setAccount", "app context:" + this.this$0.getApplicationContext().getPackageName() + " context:" + this.this$0.getPackageName() + " calling packagename:" + this.this$0.getCallingPackage(), "");
            if (AuthenticationSettings.INSTANCE.getSecretKeyData() == null) {
                Logger.m14612i("AuthenticationActivity:setAccount", "Calling app doesn't provide the secret key.", "");
            }
            String encrypt = this.this$0.mStorageHelper.encrypt(gson.toJson(TokenCacheItem.createRegularTokenCacheItem(this.mRequest.getAuthority(), this.mRequest.getResource(), this.mRequest.getClientId(), tokenTaskResult.mTaskResult)));
            String createCacheKeyForRTEntry = CacheKey.createCacheKeyForRTEntry(this.this$0.mAuthRequest.getAuthority(), this.this$0.mAuthRequest.getResource(), this.this$0.mAuthRequest.getClientId(), null);
            saveCacheKey(createCacheKeyForRTEntry, account, this.mAppCallingUID);
            this.mAccountManager.setUserData(account, getBrokerAppCacheKey(createCacheKeyForRTEntry), encrypt);
            if (tokenTaskResult.mTaskResult.getIsMultiResourceRefreshToken()) {
                String encrypt2 = this.this$0.mStorageHelper.encrypt(gson.toJson(TokenCacheItem.createMRRTTokenCacheItem(this.mRequest.getAuthority(), this.mRequest.getClientId(), tokenTaskResult.mTaskResult)));
                String createCacheKeyForMRRT = CacheKey.createCacheKeyForMRRT(this.this$0.mAuthRequest.getAuthority(), this.this$0.mAuthRequest.getClientId(), null);
                saveCacheKey(createCacheKeyForMRRT, account, this.mAppCallingUID);
                this.mAccountManager.setUserData(account, getBrokerAppCacheKey(createCacheKeyForMRRT), encrypt2);
            }
            Logger.m14612i("AuthenticationActivity:setAccount", "Set calling uid:" + this.mAppCallingUID, "");
            appendAppUIDToAccount(account);
        }

        @Override // android.os.AsyncTask
        public TokenTaskResult doInBackground(String... strArr) {
            Oauth2 oauth2 = new Oauth2(this.mRequest, this.mRequestHandler, this.this$0.mJWSBuilder);
            TokenTaskResult tokenTaskResult = new TokenTaskResult(this.this$0);
            try {
                tokenTaskResult.mTaskResult = oauth2.getToken(strArr[0]);
                ADALError aDALError = null;
                Logger.m14615v(AuthenticationActivity.TAG, "Process result returned from TokenTask. ", this.mRequest.getLogInfo(), null);
            } catch (AuthenticationException | IOException e) {
                Logger.m14610e(AuthenticationActivity.TAG, "Error in processing code to get a token. ", this.mRequest.getLogInfo(), ADALError.AUTHORIZATION_CODE_NOT_EXCHANGED_FOR_TOKEN, e);
                tokenTaskResult.mTaskException = e;
            }
            if (tokenTaskResult.mTaskResult != null && tokenTaskResult.mTaskResult.getAccessToken() != null) {
                Logger.m14615v(AuthenticationActivity.TAG, "Token task successfully returns access token.", this.mRequest.getLogInfo(), null);
                try {
                    setAccount(tokenTaskResult);
                } catch (IOException | GeneralSecurityException e2) {
                    Logger.m14610e(AuthenticationActivity.TAG, "Error in setting the account", this.mRequest.getLogInfo(), ADALError.BROKER_ACCOUNT_SAVE_FAILED, e2);
                    tokenTaskResult.mTaskException = e2;
                }
            }
            return tokenTaskResult;
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(TokenTaskResult tokenTaskResult) {
            Logger.m14614v(AuthenticationActivity.TAG, "Token task returns the result");
            this.this$0.displaySpinner(false);
            Intent intent = new Intent();
            if (tokenTaskResult.mTaskResult == null) {
                Logger.m14614v(AuthenticationActivity.TAG, "Token task has exception");
                this.this$0.returnError(ADALError.AUTHORIZATION_CODE_NOT_EXCHANGED_FOR_TOKEN, tokenTaskResult.mTaskException.getMessage());
            } else if (tokenTaskResult.mTaskResult.getStatus().equals(AuthenticationResult.AuthenticationStatus.Succeeded)) {
                intent.putExtra(AuthenticationConstants.Browser.REQUEST_ID, this.this$0.mWaitingRequestId);
                intent.putExtra(AuthenticationConstants.Broker.ACCOUNT_ACCESS_TOKEN, tokenTaskResult.mTaskResult.getAccessToken());
                intent.putExtra(AuthenticationConstants.Broker.ACCOUNT_NAME, tokenTaskResult.mAccountName);
                if (tokenTaskResult.mTaskResult.getExpiresOn() != null) {
                    intent.putExtra(AuthenticationConstants.Broker.ACCOUNT_EXPIREDATE, tokenTaskResult.mTaskResult.getExpiresOn().getTime());
                }
                if (tokenTaskResult.mTaskResult.getTenantId() != null) {
                    intent.putExtra(AuthenticationConstants.Broker.ACCOUNT_USERINFO_TENANTID, tokenTaskResult.mTaskResult.getTenantId());
                }
                UserInfo userInfo = tokenTaskResult.mTaskResult.getUserInfo();
                if (userInfo != null) {
                    intent.putExtra(AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID, userInfo.getUserId());
                    intent.putExtra(AuthenticationConstants.Broker.ACCOUNT_USERINFO_GIVEN_NAME, userInfo.getGivenName());
                    intent.putExtra(AuthenticationConstants.Broker.ACCOUNT_USERINFO_FAMILY_NAME, userInfo.getFamilyName());
                    intent.putExtra(AuthenticationConstants.Broker.ACCOUNT_USERINFO_IDENTITY_PROVIDER, userInfo.getIdentityProvider());
                    intent.putExtra(AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID_DISPLAYABLE, userInfo.getDisplayableId());
                }
                this.this$0.returnResult(2004, intent);
            } else {
                this.this$0.returnError(ADALError.AUTHORIZATION_CODE_NOT_EXCHANGED_FOR_TOKEN, tokenTaskResult.mTaskResult.getErrorDescription());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public class TokenTaskResult {
        public String mAccountName;
        public Exception mTaskException;
        public AuthenticationResult mTaskResult;
        final AuthenticationActivity this$0;

        TokenTaskResult(AuthenticationActivity authenticationActivity) {
            this.this$0 = authenticationActivity;
        }
    }

    public void cancelRequest() {
        Logger.m14614v(TAG, "Sending intent to cancel authentication activity");
        Intent intent = new Intent();
        UIEvent uIEvent = this.mUIEvent;
        if (uIEvent != null) {
            uIEvent.setUserCancel();
        }
        returnToCaller(2001, intent);
    }

    public void displaySpinner(boolean z) {
        if (isFinishing() || isChangingConfigurations() || this.mSpinner == null) {
            return;
        }
        Logger.m14614v("AuthenticationActivity:displaySpinner", "DisplaySpinner:" + z + " showing:" + this.mSpinner.isShowing());
        if (z && !this.mSpinner.isShowing()) {
            this.mSpinner.show();
        }
        if (z || !this.mSpinner.isShowing()) {
            return;
        }
        this.mSpinner.dismiss();
    }

    public void displaySpinnerWithMessage(CharSequence charSequence) {
        ProgressDialog progressDialog;
        if (isFinishing() || (progressDialog = this.mSpinner) == null) {
            return;
        }
        progressDialog.show();
        this.mSpinner.setMessage(charSequence);
    }

    private AuthenticationRequest getAuthenticationRequestFromIntent(Intent intent) {
        UUID uuid = null;
        if (isBrokerRequest(intent)) {
            Logger.m14614v("AuthenticationActivity:getAuthenticationRequestFromIntent", "It is a broker request. Get request info from bundle extras.");
            String stringExtra = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_AUTHORITY);
            String stringExtra2 = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_RESOURCE);
            String stringExtra3 = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_REDIRECT);
            String stringExtra4 = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_LOGIN_HINT);
            String stringExtra5 = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_NAME);
            String stringExtra6 = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_CLIENTID_KEY);
            String stringExtra7 = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_CORRELATIONID);
            String stringExtra8 = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_PROMPT);
            PromptBehavior promptBehavior = PromptBehavior.Auto;
            if (!StringExtensions.isNullOrBlank(stringExtra8)) {
                promptBehavior = PromptBehavior.valueOf(stringExtra8);
            }
            PromptBehavior promptBehavior2 = promptBehavior;
            this.mWaitingRequestId = intent.getIntExtra(AuthenticationConstants.Browser.REQUEST_ID, 0);
            if (!StringExtensions.isNullOrBlank(stringExtra7)) {
                try {
                    uuid = UUID.fromString(stringExtra7);
                } catch (IllegalArgumentException unused) {
                    Logger.m14609e("AuthenticationActivity:getAuthenticationRequestFromIntent", "CorrelationId is malformed: " + stringExtra7, "", ADALError.CORRELATION_ID_FORMAT);
                }
            }
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(stringExtra, stringExtra2, stringExtra6, stringExtra3, stringExtra4, uuid, false);
            authenticationRequest.setBrokerAccountName(stringExtra5);
            authenticationRequest.setPrompt(promptBehavior2);
            authenticationRequest.setRequestId(this.mWaitingRequestId);
            return authenticationRequest;
        }
        Serializable serializableExtra = intent.getSerializableExtra(AuthenticationConstants.Browser.REQUEST_MESSAGE);
        if (serializableExtra instanceof AuthenticationRequest) {
            return (AuthenticationRequest) serializableExtra;
        }
        return null;
    }

    private String getBrokerStartUrl(String str, String str2, String str3) {
        if (!StringExtensions.isNullOrBlank(str2) && !StringExtensions.isNullOrBlank(str3)) {
            try {
                return str + "&package_name=" + URLEncoder.encode(str2, "UTF-8") + "&signature=" + URLEncoder.encode(str3, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Logger.m14611e(TAG, "Encoding", e);
            }
        }
        return str;
    }

    private void hideKeyBoard() {
        if (this.mWebView != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.mWebView.getApplicationWindowToken(), 0);
        }
    }

    public boolean isBrokerRequest(Intent intent) {
        return (intent == null || StringExtensions.isNullOrBlank(intent.getStringExtra(AuthenticationConstants.Broker.BROKER_REQUEST))) ? false : true;
    }

    private boolean isCallerBrokerInstaller() {
        PackageHelper packageHelper = new PackageHelper(this);
        String callingPackage = getCallingPackage();
        if (StringExtensions.isNullOrBlank(callingPackage)) {
            return false;
        }
        if (callingPackage.equals(AuthenticationSettings.INSTANCE.getBrokerPackageName())) {
            Logger.m14614v("AuthenticationActivity:isCallerBrokerInstaller", "Same package as broker.");
            return true;
        }
        String currentSignatureForPackage = packageHelper.getCurrentSignatureForPackage(callingPackage);
        Logger.m14615v("AuthenticationActivity:isCallerBrokerInstaller", "Checking broker signature. ", "Check signature for " + callingPackage + " signature:" + currentSignatureForPackage + " brokerSignature:" + AuthenticationSettings.INSTANCE.getBrokerSignature(), null);
        return currentSignatureForPackage.equals(AuthenticationSettings.INSTANCE.getBrokerSignature()) || currentSignatureForPackage.equals(AuthenticationConstants.Broker.AZURE_AUTHENTICATOR_APP_SIGNATURE);
    }

    public void prepareForBrokerResume() {
        Logger.m14614v("AuthenticationActivity:prepareForBrokerResume", "Return to caller with BROKER_REQUEST_RESUME, and waiting for result.");
        returnToCaller(2006, new Intent());
    }

    public void returnError(ADALError aDALError, String str) {
        Logger.m14616w(TAG, "Argument error:" + str);
        Intent intent = new Intent();
        intent.putExtra(AuthenticationConstants.Browser.RESPONSE_ERROR_CODE, aDALError.name());
        intent.putExtra(AuthenticationConstants.Browser.RESPONSE_ERROR_MESSAGE, str);
        if (this.mAuthRequest != null) {
            intent.putExtra(AuthenticationConstants.Browser.REQUEST_ID, this.mWaitingRequestId);
            intent.putExtra(AuthenticationConstants.Browser.RESPONSE_REQUEST_INFO, this.mAuthRequest);
        }
        setResult(2002, intent);
        finish();
    }

    public void returnResult(int i, Intent intent) {
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(i, intent);
        finish();
    }

    public void returnToCaller(int i, Intent intent) {
        Logger.m14614v("AuthenticationActivity:returnToCaller", "Return To Caller:" + i);
        displaySpinner(false);
        if (intent == null) {
            intent = new Intent();
        }
        if (this.mAuthRequest == null) {
            Logger.m14617w("AuthenticationActivity:returnToCaller", "Request object is null", "", ADALError.ACTIVITY_REQUEST_INTENT_DATA_IS_NULL);
        } else {
            Logger.m14614v("AuthenticationActivity:returnToCaller", "Set request id related to response. REQUEST_ID for caller returned to:" + this.mAuthRequest.getRequestId());
            intent.putExtra(AuthenticationConstants.Browser.REQUEST_ID, this.mAuthRequest.getRequestId());
        }
        setResult(i, intent);
        finish();
    }

    private void setAccountAuthenticatorResult(Bundle bundle) {
        this.mAuthenticatorResultBundle = bundle;
    }

    private void setupWebView() {
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.requestFocus(130);
        this.mWebView.setOnTouchListener(new View.OnTouchListener() { // from class: com.microsoft.aad.adal.AuthenticationActivity.1
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
        this.mWebView.getSettings().setLoadWithOverviewMode(true);
        this.mWebView.getSettings().setDomStorageEnabled(true);
        this.mWebView.getSettings().setUseWideViewPort(true);
        this.mWebView.getSettings().setBuiltInZoomControls(true);
        this.mWebView.setWebViewClient(new CustomWebViewClient(this));
        this.mWebView.setVisibility(View.INVISIBLE);
    }

    @Override // android.app.Activity
    public void finish() {
        if (isBrokerRequest(getIntent()) && this.mAccountAuthenticatorResponse != null) {
            Logger.m14614v(TAG, "It is a broker request");
            Bundle bundle = this.mAuthenticatorResultBundle;
            if (bundle == null) {
                this.mAccountAuthenticatorResponse.onError(4, "canceled");
            } else {
                this.mAccountAuthenticatorResponse.onResult(bundle);
            }
            this.mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        Logger.m14614v(TAG, "Back button is pressed");
        if (this.mPkeyAuthRedirect || !this.mWebView.canGoBackOrForward(-2)) {
            cancelRequest();
        } else {
            this.mWebView.goBack();
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        final String str;
        super.onCreate(bundle);
        setContentView(getResources().getIdentifier("activity_authentication", "layout", getPackageName()));
        CookieSyncManager.createInstance(getApplicationContext());
        CookieSyncManager.getInstance().sync();
        CookieManager.getInstance().setAcceptCookie(true);
        Logger.m14614v("AuthenticationActivity:onCreate", "AuthenticationActivity was created.");
        AuthenticationRequest authenticationRequestFromIntent = getAuthenticationRequestFromIntent(getIntent());
        this.mAuthRequest = authenticationRequestFromIntent;
        if (authenticationRequestFromIntent == null) {
            Logger.m14608d("AuthenticationActivity:onCreate", "Intent for Authentication Activity doesn't have the request details, returning to caller");
            Intent intent = new Intent();
            intent.putExtra(AuthenticationConstants.Browser.RESPONSE_ERROR_CODE, AuthenticationConstants.Browser.WEBVIEW_INVALID_REQUEST);
            intent.putExtra(AuthenticationConstants.Browser.RESPONSE_ERROR_MESSAGE, "Intent does not have request details");
            returnToCaller(2002, intent);
        } else if (authenticationRequestFromIntent.getAuthority() == null || this.mAuthRequest.getAuthority().isEmpty()) {
            returnError(ADALError.ARGUMENT_EXCEPTION, AuthenticationConstants.Broker.ACCOUNT_AUTHORITY);
        } else if (this.mAuthRequest.getResource() == null || this.mAuthRequest.getResource().isEmpty()) {
            returnError(ADALError.ARGUMENT_EXCEPTION, AuthenticationConstants.Broker.ACCOUNT_RESOURCE);
        } else if (this.mAuthRequest.getClientId() == null || this.mAuthRequest.getClientId().isEmpty()) {
            returnError(ADALError.ARGUMENT_EXCEPTION, AuthenticationConstants.Broker.ACCOUNT_CLIENTID_KEY);
        } else if (this.mAuthRequest.getRedirectUri() == null || this.mAuthRequest.getRedirectUri().isEmpty()) {
            returnError(ADALError.ARGUMENT_EXCEPTION, AuthenticationConstants.Broker.ACCOUNT_REDIRECT);
        } else {
            this.mRedirectUrl = this.mAuthRequest.getRedirectUri();
            Telemetry.getInstance().startEvent(this.mAuthRequest.getTelemetryRequestId(), "Microsoft.ADAL.ui_event");
            UIEvent uIEvent = new UIEvent("Microsoft.ADAL.ui_event");
            this.mUIEvent = uIEvent;
            uIEvent.setRequestId(this.mAuthRequest.getTelemetryRequestId());
            this.mUIEvent.setCorrelationId(this.mAuthRequest.getCorrelationId().toString());
            this.mWebView = (WebView) findViewById(getResources().getIdentifier("webView1", Name.MARK, getPackageName()));
            if (!AuthenticationSettings.INSTANCE.getDisableWebViewHardwareAcceleration()) {
                this.mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                Logger.m14608d("AuthenticationActivity:onCreate", "Hardware acceleration is disabled in WebView");
            }
            this.mStartUrl = BasicWebViewClient.BLANK_PAGE;
            try {
                this.mStartUrl = new Oauth2(this.mAuthRequest).getCodeRequestUrl();
                ADALError aDALError = null;
                Logger.m14615v("AuthenticationActivity:onCreate", "Init broadcastReceiver with request. RequestId:" + this.mAuthRequest.getRequestId(), this.mAuthRequest.getLogInfo(), null);
                ActivityBroadcastReceiver activityBroadcastReceiver = new ActivityBroadcastReceiver();
                this.mReceiver = activityBroadcastReceiver;
                activityBroadcastReceiver.mWaitingRequestId = this.mAuthRequest.getRequestId();
                LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, new IntentFilter(AuthenticationConstants.Browser.ACTION_CANCEL));
                String userAgentString = this.mWebView.getSettings().getUserAgentString();
                this.mWebView.getSettings().setUserAgentString(userAgentString + AuthenticationConstants.Broker.CLIENT_TLS_NOT_SUPPORTED);
                ADALError aDALError2 = null;
                Logger.m14615v("AuthenticationActivity:onCreate", "", "UserAgent:" + this.mWebView.getSettings().getUserAgentString(), null);
                if (isBrokerRequest(getIntent())) {
                    String callingPackage = getCallingPackage();
                    this.mCallingPackage = callingPackage;
                    if (callingPackage == null) {
                        Logger.m14614v("AuthenticationActivity:onCreate", "Calling package is null, startActivityForResult is not used to call this activity");
                        Intent intent2 = new Intent();
                        intent2.putExtra(AuthenticationConstants.Browser.RESPONSE_ERROR_CODE, AuthenticationConstants.Browser.WEBVIEW_INVALID_REQUEST);
                        intent2.putExtra(AuthenticationConstants.Browser.RESPONSE_ERROR_MESSAGE, "startActivityForResult is not used to call this activity");
                        returnToCaller(2002, intent2);
                        return;
                    }
                    Logger.m14612i("AuthenticationActivity:onCreate", "It is a broker request for package:" + this.mCallingPackage, "");
                    AccountAuthenticatorResponse accountAuthenticatorResponse = (AccountAuthenticatorResponse) getIntent().getParcelableExtra("accountAuthenticatorResponse");
                    this.mAccountAuthenticatorResponse = accountAuthenticatorResponse;
                    if (accountAuthenticatorResponse != null) {
                        accountAuthenticatorResponse.onRequestContinued();
                    }
                    PackageHelper packageHelper = new PackageHelper(this);
                    String callingPackage2 = getCallingPackage();
                    this.mCallingPackage = callingPackage2;
                    this.mCallingUID = packageHelper.getUIDForPackage(callingPackage2);
                    String currentSignatureForPackage = packageHelper.getCurrentSignatureForPackage(this.mCallingPackage);
                    this.mStartUrl = getBrokerStartUrl(this.mStartUrl, this.mCallingPackage, currentSignatureForPackage);
                    if (!isCallerBrokerInstaller()) {
                        Logger.m14614v("AuthenticationActivity:onCreate", "Caller needs to be verified using special redirectUri");
                        this.mRedirectUrl = PackageHelper.getBrokerRedirectUrl(this.mCallingPackage, currentSignatureForPackage);
                    }
                    ADALError aDALError3 = null;
                    Logger.m14615v("AuthenticationActivity:onCreate", "", "Broker redirectUrl: " + this.mRedirectUrl + " The calling package is: " + this.mCallingPackage + " Signature hash for calling package is: " + currentSignatureForPackage + " Current context package: " + getPackageName() + " Start url: " + this.mStartUrl, null);
                } else {
                    ADALError aDALError4 = null;
                    Logger.m14615v("AuthenticationActivity:onCreate", "Non-broker request for package " + getCallingPackage(), " Start url: " + this.mStartUrl, null);
                }
                this.mRegisterReceiver = false;
                Logger.m14612i("AuthenticationActivity:onCreate", "Device info:" + Build.VERSION.RELEASE + " " + Build.MANUFACTURER + Build.MODEL, "");
                this.mStorageHelper = new StorageHelper(getApplicationContext());
                setupWebView();
                if (this.mAuthRequest.getCorrelationId() == null) {
                    str = "Null correlation id in the request.";
                } else {
                    str = "Correlation id for request sent is:" + this.mAuthRequest.getCorrelationId().toString();
                }
                Logger.m14614v("AuthenticationActivity:onCreate", str);
                if (bundle == null) {
                    this.mWebView.post(new Runnable() { // from class: com.microsoft.aad.adal.AuthenticationActivity.2
                        @Override // java.lang.Runnable
                        public void run() {
                            Logger.m14614v("AuthenticationActivity:onCreate", "Launching webview for acquiring auth code.");
                            AuthenticationActivity.this.mWebView.loadUrl(BasicWebViewClient.BLANK_PAGE);
                            AuthenticationActivity.this.mWebView.loadUrl(str);
                        }
                    });
                } else {
                    Logger.m14614v("AuthenticationActivity:onCreate", "Reuse webview");
                }
            } catch (UnsupportedEncodingException e) {
                Logger.m14615v("AuthenticationActivity:onCreate", "Encoding format is not supported. ", e.getMessage(), null);
                Intent intent3 = new Intent();
                intent3.putExtra(AuthenticationConstants.Browser.RESPONSE_REQUEST_INFO, this.mAuthRequest);
                returnToCaller(2002, intent3);
            }
        }
    }

    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (this.mUIEvent != null) {
            Telemetry.getInstance().stopEvent(this.mAuthRequest.getTelemetryRequestId(), this.mUIEvent, "Microsoft.ADAL.ui_event");
        }
    }

    @Override // android.app.Activity
    public void onPause() {
        Logger.m14614v("AuthenticationActivity:onPause", "AuthenticationActivity onPause unregister receiver");
        super.onPause();
        if (this.mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        }
        this.mRegisterReceiver = true;
        if (this.mSpinner != null) {
            Logger.m14614v("AuthenticationActivity:onPause", "Spinner at onPause will dismiss");
            this.mSpinner.dismiss();
        }
        hideKeyBoard();
    }

    @Override // android.app.Activity
    public void onRestart() {
        Logger.m14614v(TAG, "AuthenticationActivity onRestart");
        super.onRestart();
        this.mRegisterReceiver = true;
    }

    @Override // android.app.Activity
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mWebView.restoreState(bundle);
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        if (this.mRegisterReceiver) {
            Logger.m14615v("AuthenticationActivity:onResume", "Webview onResume will register receiver. ", "StartUrl: " + this.mStartUrl, null);
            if (this.mReceiver != null) {
                Logger.m14614v("AuthenticationActivity:onResume", "Webview onResume register broadcast receiver for request. RequestId: " + this.mReceiver.mWaitingRequestId);
                LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, new IntentFilter(AuthenticationConstants.Browser.ACTION_CANCEL));
            }
        }
        this.mRegisterReceiver = false;
        ProgressDialog progressDialog = new ProgressDialog(this);
        this.mSpinner = progressDialog;
        progressDialog.requestWindowFeature(1);
        this.mSpinner.setMessage(getText(getResources().getIdentifier("app_loading", "string", getPackageName())));
    }

    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mWebView.saveState(bundle);
    }
}

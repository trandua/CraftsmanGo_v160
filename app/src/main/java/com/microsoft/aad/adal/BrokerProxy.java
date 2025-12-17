package com.microsoft.aad.adal;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.TelemetryUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class BrokerProxy implements IBrokerProxy {
    private static final int ACCOUNT_MANAGER_ERROR_CODE_BAD_AUTHENTICATION = 9;
    private static final String AUTHENTICATOR_CANCELS_REQUEST = "Authenticator cancels the request";
    public static final String DATA_USER_INFO = "com.microsoft.workaccount.user.info";
    private static final String KEY_ACCOUNT_LIST_DELIM = "|";
    private static final String KEY_APP_ACCOUNTS_FOR_TOKEN_REMOVAL = "AppAccountsForTokenRemoval";
    private static final String KEY_SHARED_PREF_ACCOUNT_LIST = "com.microsoft.aad.adal.account.list";
    private static final String TAG = "BrokerProxy";
    private AccountManager mAcctManager;
    private final String mBrokerTag = AuthenticationSettings.INSTANCE.getBrokerSignature();
    public Context mContext;
    private Handler mHandler;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public enum SwitchToBroker {
        CAN_SWITCH_TO_BROKER,
        CANNOT_SWITCH_TO_BROKER,
        NEED_PERMISSIONS_TO_SWITCH_TO_BROKER
    }

    public BrokerProxy() {
    }

    public BrokerProxy(Context context) {
        this.mContext = context;
        this.mAcctManager = AccountManager.get(context);
        this.mHandler = new Handler(this.mContext.getMainLooper());
    }

    private boolean checkAccount(AccountManager accountManager, String str, String str2) {
        AuthenticatorDescription[] authenticatorTypes;
        for (AuthenticatorDescription authenticatorDescription : accountManager.getAuthenticatorTypes()) {
            if (authenticatorDescription.type.equals(AuthenticationConstants.Broker.BROKER_ACCOUNT_TYPE)) {
                Account[] accountsByType = this.mAcctManager.getAccountsByType(AuthenticationConstants.Broker.BROKER_ACCOUNT_TYPE);
                if (authenticatorDescription.packageName.equalsIgnoreCase(AuthenticationConstants.Broker.AZURE_AUTHENTICATOR_APP_PACKAGE_NAME) || authenticatorDescription.packageName.equalsIgnoreCase(AuthenticationConstants.Broker.COMPANY_PORTAL_APP_PACKAGE_NAME) || authenticatorDescription.packageName.equalsIgnoreCase(AuthenticationSettings.INSTANCE.getBrokerPackageName())) {
                    if (hasSupportToAddUserThroughBroker(authenticatorDescription.packageName)) {
                        return true;
                    }
                    if (accountsByType.length > 0) {
                        return verifyAccount(accountsByType, str, str2);
                    }
                }
            }
        }
        return false;
    }

    private String checkPermission(String str) {
        if (this.mContext.getPackageManager().checkPermission(str, this.mContext.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        Logger.m14617w(TAG, "Broker related permissions are missing for " + str, "", ADALError.DEVELOPER_BROKER_PERMISSIONS_MISSING);
        return str + ' ';
    }

    private Account findAccount(String str, Account[] accountArr) {
        if (accountArr == null) {
            return null;
        }
        for (Account account : accountArr) {
            if (account != null && account.name != null && account.name.equalsIgnoreCase(str)) {
                return account;
            }
        }
        return null;
    }

    private UserInfo findUserInfo(String str, UserInfo[] userInfoArr) {
        if (userInfoArr == null) {
            return null;
        }
        for (UserInfo userInfo : userInfoArr) {
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getUserId()) && userInfo.getUserId().equalsIgnoreCase(str)) {
                return userInfo;
            }
        }
        return null;
    }

    private Bundle getAuthTokenFromAccountManager(AuthenticationRequest authenticationRequest, Bundle bundle) throws AuthenticationException {
        Account targetAccount = getTargetAccount(authenticationRequest);
        if (targetAccount != null) {
            try {
                AccountManagerCallback accountManagerCallback = null;
                AccountManagerFuture<Bundle> authToken = this.mAcctManager.getAuthToken(targetAccount, AuthenticationConstants.Broker.AUTHTOKEN_TYPE, bundle, false, (AccountManagerCallback<Bundle>) null, this.mHandler);
                Logger.m14614v("BrokerProxy:getAuthTokenFromAccountManager", "Received result from broker");
                Bundle result = authToken.getResult();
                Logger.m14614v("BrokerProxy:getAuthTokenFromAccountManager", "Returning result from broker");
                return result;
            } catch (AuthenticatorException e) {
                if (StringExtensions.isNullOrBlank(e.getMessage()) || !e.getMessage().contains("invalid_grant")) {
                    Logger.m14609e("BrokerProxy:getAuthTokenFromAccountManager", AUTHENTICATOR_CANCELS_REQUEST, "", ADALError.BROKER_AUTHENTICATOR_ERROR_GETAUTHTOKEN);
                    throw new AuthenticationException(ADALError.BROKER_AUTHENTICATOR_ERROR_GETAUTHTOKEN, e.getMessage());
                }
                Logger.m14609e("BrokerProxy:getAuthTokenFromAccountManager", AUTHENTICATOR_CANCELS_REQUEST, "Acquire token failed with 'invalid grant' error, cannot proceed with silent request.", ADALError.AUTH_REFRESH_FAILED_PROMPT_NOT_ALLOWED);
                throw new AuthenticationException(ADALError.AUTH_REFRESH_FAILED_PROMPT_NOT_ALLOWED, e.getMessage());
            } catch (OperationCanceledException e2) {
                Logger.m14610e("BrokerProxy:getAuthTokenFromAccountManager", AUTHENTICATOR_CANCELS_REQUEST, "", ADALError.AUTH_FAILED_CANCELLED, e2);
                throw new AuthenticationException(ADALError.AUTH_FAILED_CANCELLED, e2.getMessage(), e2);
            } catch (IOException e3) {
                Logger.m14609e("BrokerProxy:getAuthTokenFromAccountManager", AUTHENTICATOR_CANCELS_REQUEST, "", ADALError.BROKER_AUTHENTICATOR_IO_EXCEPTION);
                if (e3.getMessage() != null && e3.getMessage().contains(ADALError.DEVICE_CONNECTION_IS_NOT_AVAILABLE.getDescription())) {
                    throw new AuthenticationException(ADALError.DEVICE_CONNECTION_IS_NOT_AVAILABLE, "Received error from broker, errorCode: " + e3.getMessage());
                } else if (e3.getMessage() == null || !e3.getMessage().contains(ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION.getDescription())) {
                    throw new AuthenticationException(ADALError.BROKER_AUTHENTICATOR_IO_EXCEPTION, e3.getMessage(), e3);
                } else {
                    throw new AuthenticationException(ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION, "Received error from broker, errorCode: " + e3.getMessage());
                }
            }
        }
        Logger.m14614v("BrokerProxy:getAuthTokenFromAccountManager", "Target account is not found");
        return null;
    }

    private Bundle getBrokerOptions(AuthenticationRequest authenticationRequest) {
        Bundle bundle = new Bundle();
        bundle.putInt(AuthenticationConstants.Browser.REQUEST_ID, authenticationRequest.getRequestId());
        bundle.putInt(AuthenticationConstants.Broker.EXPIRATION_BUFFER, AuthenticationSettings.INSTANCE.getExpirationBuffer());
        bundle.putString(AuthenticationConstants.Broker.ACCOUNT_AUTHORITY, authenticationRequest.getAuthority());
        bundle.putString(AuthenticationConstants.Broker.ACCOUNT_RESOURCE, authenticationRequest.getResource());
        bundle.putString(AuthenticationConstants.Broker.ACCOUNT_REDIRECT, authenticationRequest.getRedirectUri());
        bundle.putString(AuthenticationConstants.Broker.ACCOUNT_CLIENTID_KEY, authenticationRequest.getClientId());
        bundle.putString(AuthenticationConstants.Broker.ADAL_VERSION_KEY, authenticationRequest.getVersion());
        bundle.putString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID, authenticationRequest.getUserId());
        bundle.putString(AuthenticationConstants.Broker.ACCOUNT_EXTRA_QUERY_PARAM, authenticationRequest.getExtraQueryParamsAuthentication());
        if (authenticationRequest.getCorrelationId() != null) {
            bundle.putString(AuthenticationConstants.Broker.ACCOUNT_CORRELATIONID, authenticationRequest.getCorrelationId().toString());
        }
        String brokerAccountName = authenticationRequest.getBrokerAccountName();
        if (StringExtensions.isNullOrBlank(brokerAccountName)) {
            brokerAccountName = authenticationRequest.getLoginHint();
        }
        bundle.putString(AuthenticationConstants.Broker.ACCOUNT_LOGIN_HINT, brokerAccountName);
        bundle.putString(AuthenticationConstants.Broker.ACCOUNT_NAME, brokerAccountName);
        if (authenticationRequest.getPrompt() != null) {
            bundle.putString(AuthenticationConstants.Broker.ACCOUNT_PROMPT, authenticationRequest.getPrompt().name());
        }
        if (Utility.isClaimsChallengePresent(authenticationRequest)) {
            bundle.putString(AuthenticationConstants.Broker.BROKER_SKIP_CACHE, Boolean.toString(true));
            bundle.putString(AuthenticationConstants.Broker.ACCOUNT_CLAIMS, authenticationRequest.getClaimsChallenge());
        }
        return bundle;
    }

    private Intent getIntentForBrokerActivityFromAccountManager(Bundle bundle) {
        try {
            return (Intent) this.mAcctManager.addAccount(AuthenticationConstants.Broker.BROKER_ACCOUNT_TYPE, AuthenticationConstants.Broker.AUTHTOKEN_TYPE, null, bundle, null, null, this.mHandler).getResult().getParcelable("intent");
        } catch (AuthenticatorException e) {
            Logger.m14610e("BrokerProxy:getIntentForBrokerActivityFromAccountManager", AUTHENTICATOR_CANCELS_REQUEST, "", ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, e);
            return null;
        } catch (OperationCanceledException e2) {
            Logger.m14610e("BrokerProxy:getIntentForBrokerActivityFromAccountManager", AUTHENTICATOR_CANCELS_REQUEST, "", ADALError.AUTH_FAILED_CANCELLED, e2);
            return null;
        } catch (IOException e3) {
            Logger.m14610e("BrokerProxy:getIntentForBrokerActivityFromAccountManager", AUTHENTICATOR_CANCELS_REQUEST, "", ADALError.BROKER_AUTHENTICATOR_IO_EXCEPTION, e3);
            return null;
        }
    }

    private AuthenticationResult getResultFromBrokerResponse(Bundle bundle, AuthenticationRequest authenticationRequest) throws AuthenticationException {
        Date date;
        if (bundle != null) {
            int i = bundle.getInt("errorCode");
            String string = bundle.getString("errorMessage");
            String string2 = bundle.getString(AuthenticationConstants.OAuth2.ERROR);
            String string3 = bundle.getString(AuthenticationConstants.OAuth2.ERROR_DESCRIPTION);
            if (!StringExtensions.isNullOrBlank(string)) {
                throw new AuthenticationException(i != 3 ? i != 4 ? i != 6 ? i != 7 ? i != 9 ? ADALError.BROKER_AUTHENTICATOR_ERROR_GETAUTHTOKEN : ADALError.BROKER_AUTHENTICATOR_BAD_AUTHENTICATION : ADALError.BROKER_AUTHENTICATOR_BAD_ARGUMENTS : ADALError.BROKER_AUTHENTICATOR_UNSUPPORTED_OPERATION : ADALError.AUTH_FAILED_CANCELLED : !string.contains(ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION.getDescription()) ? string.contains(ADALError.DEVICE_CONNECTION_IS_NOT_AVAILABLE.getDescription()) ? ADALError.DEVICE_CONNECTION_IS_NOT_AVAILABLE : ADALError.BROKER_AUTHENTICATOR_IO_EXCEPTION : ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION, string);
            } else if (!StringExtensions.isNullOrBlank(string2) && authenticationRequest.isSilent()) {
                ADALError aDALError = ADALError.AUTH_REFRESH_FAILED_PROMPT_NOT_ALLOWED;
                AuthenticationException authenticationException = new AuthenticationException(aDALError, "Received error from broker, errorCode: " + string2 + "; ErrorDescription: " + string3);
                Serializable serializable = bundle.getSerializable(AuthenticationConstants.OAuth2.HTTP_RESPONSE_BODY);
                Serializable serializable2 = bundle.getSerializable(AuthenticationConstants.OAuth2.HTTP_RESPONSE_HEADER);
                if (serializable != null && (serializable instanceof HashMap)) {
                    authenticationException.setHttpResponseBody((HashMap) serializable);
                }
                if (serializable2 != null && (serializable2 instanceof HashMap)) {
                    authenticationException.setHttpResponseHeaders((HashMap) serializable2);
                }
                authenticationException.setServiceStatusCode(bundle.getInt(AuthenticationConstants.OAuth2.HTTP_STATUS_CODE));
                throw authenticationException;
            } else if (bundle.getBoolean(AuthenticationConstants.Broker.ACCOUNT_INITIAL_REQUEST)) {
                return AuthenticationResult.createResultForInitialRequest();
            } else {
                UserInfo userInfoFromBrokerResult = UserInfo.getUserInfoFromBrokerResult(bundle);
                String string4 = bundle.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_TENANTID, "");
                if (bundle.getLong(AuthenticationConstants.Broker.ACCOUNT_EXPIREDATE) == 0) {
                    Logger.m14614v("BrokerProxy:getResultFromBrokerResponse", "Broker doesn't return expire date, set it current date plus one hour");
                    GregorianCalendar gregorianCalendar = new GregorianCalendar();
                    gregorianCalendar.add(13, AuthenticationConstants.DEFAULT_EXPIRATION_TIME_SEC);
                    date = gregorianCalendar.getTime();
                } else {
                    date = new Date(bundle.getLong(AuthenticationConstants.Broker.ACCOUNT_EXPIREDATE));
                }
                AuthenticationResult authenticationResult = new AuthenticationResult(bundle.getString("authtoken"), "", date, false, userInfoFromBrokerResult, string4, "", null);
                TelemetryUtils.CliTelemInfo cliTelemInfo = new TelemetryUtils.CliTelemInfo();
                cliTelemInfo.setServerErrorCode(bundle.getString(AuthenticationConstants.Broker.CliTelemInfo.SERVER_ERROR));
                cliTelemInfo.setServerSubErrorCode(bundle.getString(AuthenticationConstants.Broker.CliTelemInfo.SERVER_SUBERROR));
                cliTelemInfo.setRefreshTokenAge(bundle.getString(AuthenticationConstants.Broker.CliTelemInfo.RT_AGE));
                cliTelemInfo.setSpeRing(bundle.getString(AuthenticationConstants.Broker.CliTelemInfo.SPE_RING));
                authenticationResult.setCliTelemInfo(cliTelemInfo);
                return authenticationResult;
            }
        }
        throw new IllegalArgumentException("bundleResult");
    }

    private X509Certificate getSelfSignedCert(List<X509Certificate> list) throws AuthenticationException {
        int i = 0;
        X509Certificate x509Certificate = null;
        for (X509Certificate x509Certificate2 : list) {
            if (x509Certificate2.getSubjectDN().equals(x509Certificate2.getIssuerDN())) {
                i++;
                x509Certificate = x509Certificate2;
            }
        }
        if (i > 1 || x509Certificate == null) {
            throw new AuthenticationException(ADALError.BROKER_APP_VERIFICATION_FAILED, "Multiple self signed certs found or no self signed cert existed.");
        }
        return x509Certificate;
    }

    private Account getTargetAccount(AuthenticationRequest authenticationRequest) {
        Account[] accountsByType = this.mAcctManager.getAccountsByType(AuthenticationConstants.Broker.BROKER_ACCOUNT_TYPE);
        if (!TextUtils.isEmpty(authenticationRequest.getBrokerAccountName())) {
            return findAccount(authenticationRequest.getBrokerAccountName(), accountsByType);
        }
        try {
            UserInfo findUserInfo = findUserInfo(authenticationRequest.getUserId(), getBrokerUsers());
            if (findUserInfo != null) {
                return findAccount(findUserInfo.getDisplayableId(), accountsByType);
            }
            return null;
        } catch (AuthenticatorException | OperationCanceledException | IOException e) {
            Logger.m14610e("BrokerProxy:getTargetAccount", "Exception is thrown when trying to get target account.", e.getMessage(), ADALError.BROKER_AUTHENTICATOR_IO_EXCEPTION, e);
            return null;
        }
    }

    private UserInfo[] getUserInfoFromAccountManager() throws OperationCanceledException, AuthenticatorException, IOException {
        Account[] accountsByType = this.mAcctManager.getAccountsByType(AuthenticationConstants.Broker.BROKER_ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(DATA_USER_INFO, true);
        Logger.m14614v("BrokerProxy:getUserInfoFromAccountManager", "Retrieve all the accounts from account manager with broker account type, and the account length is: " + accountsByType.length);
        UserInfo[] userInfoArr = new UserInfo[accountsByType.length];
        for (int i = 0; i < accountsByType.length; i++) {
            AccountManagerFuture<Bundle> updateCredentials = this.mAcctManager.updateCredentials(accountsByType[i], AuthenticationConstants.Broker.AUTHTOKEN_TYPE, bundle, null, null, null);
            Logger.m14614v("BrokerProxy:getUserInfoFromAccountManager", "Waiting for userinfo retrieval result from Broker.");
            Bundle result = updateCredentials.getResult();
            userInfoArr[i] = new UserInfo(result.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID), result.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_GIVEN_NAME), result.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_FAMILY_NAME), result.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_IDENTITY_PROVIDER), result.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID_DISPLAYABLE));
        }
        return userInfoArr;
    }

    private boolean hasSupportToAddUserThroughBroker(String str) {
        Intent intent = new Intent();
        intent.setPackage(str);
        intent.setClassName(str, str + ".ui.AccountChooserActivity");
        return this.mContext.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }

    public boolean isBrokerAccountServiceSupported() {
        Context context = this.mContext;
        return isServiceSupported(context, BrokerAccountServiceHandler.getIntentForBrokerAccountService(context));
    }

    private boolean isBrokerWithPRTSupport(Intent intent) {
        if (intent != null) {
            return AuthenticationConstants.Broker.BROKER_PROTOCOL_VERSION.equalsIgnoreCase(intent.getStringExtra(AuthenticationConstants.Broker.BROKER_VERSION));
        }
        throw new IllegalArgumentException();
    }

    private boolean isServiceSupported(Context context, Intent intent) {
        List<ResolveInfo> queryIntentServices;
        return (intent == null || (queryIntentServices = context.getPackageManager().queryIntentServices(intent, 0)) == null || queryIntentServices.size() <= 0) ? false : true;
    }

    private List<X509Certificate> readCertDataForBrokerApp(String str) throws PackageManager.NameNotFoundException, AuthenticationException, IOException, GeneralSecurityException {
        PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(str, 64);
        if (packageInfo == null) {
            throw new AuthenticationException(ADALError.APP_PACKAGE_NAME_NOT_FOUND, "No broker package existed.");
        }
        if (packageInfo.signatures == null || packageInfo.signatures.length == 0) {
            throw new AuthenticationException(ADALError.BROKER_APP_VERIFICATION_FAILED, "No signature associated with the broker package.");
        }
        ArrayList arrayList = new ArrayList(packageInfo.signatures.length);
        for (Signature signature : packageInfo.signatures) {
            try {
                arrayList.add((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(signature.toByteArray())));
            } catch (CertificateException unused) {
                throw new AuthenticationException(ADALError.BROKER_APP_VERIFICATION_FAILED);
            }
        }
        return arrayList;
    }

    public void removeAccountFromAccountManager() {
        Logger.m14614v("BrokerProxy:removeAccountFromAccountManager", "Try to remove account from account manager.");
        Account[] accountsByType = this.mAcctManager.getAccountsByType(AuthenticationConstants.Broker.BROKER_ACCOUNT_TYPE);
        if (accountsByType.length != 0) {
            for (Account account : accountsByType) {
                Logger.m14615v("BrokerProxy:removeAccountFromAccountManager", "Remove tokens for account. ", "Account: " + account.name, null);
                Bundle bundle = new Bundle();
                bundle.putString(AuthenticationConstants.Broker.ACCOUNT_REMOVE_TOKENS, AuthenticationConstants.Broker.ACCOUNT_REMOVE_TOKENS_VALUE);
                this.mAcctManager.getAuthToken(account, AuthenticationConstants.Broker.AUTHTOKEN_TYPE, bundle, false, (AccountManagerCallback<Bundle>) null, this.mHandler);
            }
        }
    }

    private boolean verifyAccount(Account[] accountArr, String str, String str2) {
        if (!StringExtensions.isNullOrBlank(str)) {
            return str.equalsIgnoreCase(accountArr[0].name);
        }
        if (StringExtensions.isNullOrBlank(str2)) {
            return true;
        }
        try {
            return findUserInfo(str2, getBrokerUsers()) != null;
        } catch (AuthenticatorException | OperationCanceledException | IOException e) {
            Logger.m14610e("BrokerProxy:verifyAccount", "Exception thrown when verifying accounts in broker. ", e.getMessage(), ADALError.BROKER_AUTHENTICATOR_EXCEPTION, e);
            Logger.m14614v("BrokerProxy:verifyAccount", "It could not check the uniqueid from broker. It is not using broker");
            return false;
        }
    }

    private boolean verifyAuthenticator(AccountManager accountManager) {
        AuthenticatorDescription[] authenticatorTypes;
        for (AuthenticatorDescription authenticatorDescription : accountManager.getAuthenticatorTypes()) {
            if (authenticatorDescription.type.equals(AuthenticationConstants.Broker.BROKER_ACCOUNT_TYPE) && verifySignature(authenticatorDescription.packageName)) {
                return true;
            }
        }
        return false;
    }

    private void verifyCertificateChain(List<X509Certificate> list) throws GeneralSecurityException, AuthenticationException {
        PKIXParameters pKIXParameters = new PKIXParameters(Collections.singleton(new TrustAnchor(getSelfSignedCert(list), null)));
        pKIXParameters.setRevocationEnabled(false);
        CertPathValidator.getInstance("PKIX").validate(CertificateFactory.getInstance("X.509").generateCertPath(list), pKIXParameters);
    }

    private void verifyNotOnMainThread() {
        Looper myLooper = Looper.myLooper();
        if (myLooper == null || myLooper != this.mContext.getMainLooper()) {
            return;
        }
        IllegalStateException illegalStateException = new IllegalStateException("calling this from your main thread can lead to deadlock");
        Logger.m14610e(TAG, "calling this from your main thread can lead to deadlock and/or ANRs", "", ADALError.DEVELOPER_CALLING_ON_MAIN_THREAD, illegalStateException);
        if (this.mContext.getApplicationInfo().targetSdkVersion >= 8) {
            throw illegalStateException;
        }
    }

    private boolean verifySignature(String str) {
        try {
            List<X509Certificate> readCertDataForBrokerApp = readCertDataForBrokerApp(str);
            verifySignatureHash(readCertDataForBrokerApp);
            if (readCertDataForBrokerApp.size() > 1) {
                verifyCertificateChain(readCertDataForBrokerApp);
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            Logger.m14609e("BrokerProxy:verifySignature", "Broker related package does not exist", "", ADALError.BROKER_PACKAGE_NAME_NOT_FOUND);
            return false;
        } catch (AuthenticationException e) {
//            e = e;
//            Logger.m14610e("BrokerProxy:verifySignature", ADALError.BROKER_VERIFICATION_FAILED.getDescription(), e.getMessage(), ADALError.BROKER_VERIFICATION_FAILED, e);
            return false;
        } catch (IOException e2) {
//            e = e2;
//            Logger.m14610e("BrokerProxy:verifySignature", ADALError.BROKER_VERIFICATION_FAILED.getDescription(), e.getMessage(), ADALError.BROKER_VERIFICATION_FAILED, e);
            return false;
        } catch (NoSuchAlgorithmException unused2) {
//            Logger.m14609e("BrokerProxy:verifySignature", "Digest SHA algorithm does not exists", "", ADALError.DEVICE_NO_SUCH_ALGORITHM);
            return false;
        } catch (GeneralSecurityException e3) {
//            e = e3;
//            Logger.m14610e("BrokerProxy:verifySignature", ADALError.BROKER_VERIFICATION_FAILED.getDescription(), e.getMessage(), ADALError.BROKER_VERIFICATION_FAILED, e);
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x000a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void verifySignatureHash(List<X509Certificate> list) throws NoSuchAlgorithmException, CertificateEncodingException, AuthenticationException {
        for (X509Certificate x509Certificate : list) {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(x509Certificate.getEncoded());
            String encodeToString = Base64.encodeToString(messageDigest.digest(), 0);
            if (this.mBrokerTag.equals(encodeToString) || AuthenticationConstants.Broker.AZURE_AUTHENTICATOR_APP_SIGNATURE.equals(encodeToString)) {
                return;
            }
//            while (r3.hasNext()) {
//            }
        }
        throw new AuthenticationException(ADALError.BROKER_APP_VERIFICATION_FAILED);
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public SwitchToBroker canSwitchToBroker(String str) {
        try {
            URL url = new URL(str);
            String packageName = this.mContext.getPackageName();
            boolean z = true;
            boolean z2 = (!AuthenticationSettings.INSTANCE.getUseBroker() || packageName.equalsIgnoreCase(AuthenticationSettings.INSTANCE.getBrokerPackageName()) || packageName.equalsIgnoreCase(AuthenticationConstants.Broker.AZURE_AUTHENTICATOR_APP_PACKAGE_NAME) || !verifyAuthenticator(this.mAcctManager) || UrlExtensions.isADFSAuthority(url)) ? false : true;
            if (!z2) {
                Logger.m14614v("BrokerProxy:canSwitchToBroker", "Broker auth is turned off or no valid broker is available on the device, cannot switch to broker.");
                return SwitchToBroker.CANNOT_SWITCH_TO_BROKER;
            }
            if (!isBrokerAccountServiceSupported()) {
                if (!z2 || !checkAccount(this.mAcctManager, "", "")) {
                    z = false;
                }
                if (!z) {
                    Logger.m14614v("BrokerProxy:canSwitchToBroker", "No valid account existed in broker, cannot switch to broker for auth.");
                    return SwitchToBroker.CANNOT_SWITCH_TO_BROKER;
                }
                try {
                    verifyBrokerPermissionsAPI23AndHigher();
                } catch (UsageAuthenticationException unused) {
                    Logger.m14614v("BrokerProxy:canSwitchToBroker", "Missing GET_ACCOUNTS permission, cannot switch to broker.");
                    return SwitchToBroker.NEED_PERMISSIONS_TO_SWITCH_TO_BROKER;
                }
            }
            return SwitchToBroker.CAN_SWITCH_TO_BROKER;
        } catch (MalformedURLException unused2) {
            throw new IllegalArgumentException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL.name());
        }
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public boolean canUseLocalCache(String str) {
        String str2;
        if (canSwitchToBroker(str) == SwitchToBroker.CANNOT_SWITCH_TO_BROKER) {
            str2 = "It does not use broker";
        } else if (!verifySignature(this.mContext.getPackageName())) {
            return false;
        } else {
            str2 = "Broker installer can use local cache";
        }
        Logger.m14614v("BrokerProxy:canUseLocalCache", str2);
        return true;
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public AuthenticationResult getAuthTokenInBackground(AuthenticationRequest authenticationRequest, BrokerEvent brokerEvent) throws AuthenticationException {
        verifyNotOnMainThread();
        Bundle brokerOptions = getBrokerOptions(authenticationRequest);
        Bundle authToken = isBrokerAccountServiceSupported() ? BrokerAccountServiceHandler.getInstance().getAuthToken(this.mContext, brokerOptions, brokerEvent) : getAuthTokenFromAccountManager(authenticationRequest, brokerOptions);
        if (authToken != null) {
            return getResultFromBrokerResponse(authToken, authenticationRequest);
        }
        Logger.m14614v(TAG, "No bundle result returned from broker for silent request.");
        return null;
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public String getBrokerAppVersion(String str) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(str, 0);
        return "VersionName=" + packageInfo.versionName + ";VersonCode=" + packageInfo.versionCode + ".";
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public UserInfo[] getBrokerUsers() throws OperationCanceledException, AuthenticatorException, IOException {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return isBrokerAccountServiceSupported() ? BrokerAccountServiceHandler.getInstance().getBrokerUsers(this.mContext) : getUserInfoFromAccountManager();
        }
        throw new IllegalArgumentException("Calling getBrokerUsers on main thread");
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public String getCurrentActiveBrokerPackageName() {
        AuthenticatorDescription[] authenticatorTypes;
        for (AuthenticatorDescription authenticatorDescription : this.mAcctManager.getAuthenticatorTypes()) {
            if (authenticatorDescription.type.equals(AuthenticationConstants.Broker.BROKER_ACCOUNT_TYPE)) {
                return authenticatorDescription.packageName;
            }
        }
        return null;
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public String getCurrentUser() {
        if (isBrokerAccountServiceSupported()) {
            verifyNotOnMainThread();
            try {
                UserInfo[] brokerUsers = BrokerAccountServiceHandler.getInstance().getBrokerUsers(this.mContext);
                if (brokerUsers.length == 0) {
                    return null;
                }
                return brokerUsers[0].getDisplayableId();
            } catch (IOException e) {
                Logger.m14610e("BrokerProxy:getCurrentUser", "No current user could be retrieved.", "", null, e);
                return null;
            }
        }
        Account[] accountsByType = this.mAcctManager.getAccountsByType(AuthenticationConstants.Broker.BROKER_ACCOUNT_TYPE);
        if (accountsByType.length > 0) {
            return accountsByType[0].name;
        }
        return null;
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public Intent getIntentForBrokerActivity(AuthenticationRequest authenticationRequest, BrokerEvent brokerEvent) throws AuthenticationException {
        Intent intentForBrokerActivityFromAccountManager;
        Bundle brokerOptions = getBrokerOptions(authenticationRequest);
        if (isBrokerAccountServiceSupported()) {
            intentForBrokerActivityFromAccountManager = BrokerAccountServiceHandler.getInstance().getIntentForInteractiveRequest(this.mContext, brokerEvent);
            if (intentForBrokerActivityFromAccountManager != null) {
                intentForBrokerActivityFromAccountManager.putExtras(brokerOptions);
            } else {
                Logger.m14609e(TAG, "Received null intent from broker interactive request.", null, ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING);
                throw new AuthenticationException(ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, "Received null intent from broker interactive request.");
            }
        } else {
            intentForBrokerActivityFromAccountManager = getIntentForBrokerActivityFromAccountManager(brokerOptions);
        }
        if (intentForBrokerActivityFromAccountManager != null) {
            intentForBrokerActivityFromAccountManager.putExtra(AuthenticationConstants.Broker.BROKER_REQUEST, AuthenticationConstants.Broker.BROKER_REQUEST);
            if (!isBrokerWithPRTSupport(intentForBrokerActivityFromAccountManager) && PromptBehavior.FORCE_PROMPT == authenticationRequest.getPrompt()) {
                Logger.m14614v("BrokerProxy:getIntentForBrokerActivity", "FORCE_PROMPT is set for broker auth via old version of broker app, reset to ALWAYS.");
                intentForBrokerActivityFromAccountManager.putExtra(AuthenticationConstants.Broker.ACCOUNT_PROMPT, PromptBehavior.Always.name());
            }
        }
        return intentForBrokerActivityFromAccountManager;
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public void removeAccounts() {
        new Thread(new Runnable() { // from class: com.microsoft.aad.adal.BrokerProxy.1
            @Override // java.lang.Runnable
            public void run() {
                if (BrokerProxy.this.isBrokerAccountServiceSupported()) {
                    BrokerAccountServiceHandler.getInstance().removeAccounts(BrokerProxy.this.mContext);
                } else {
                    BrokerProxy.this.removeAccountFromAccountManager();
                }
            }
        }).start();
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public void saveAccount(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(KEY_SHARED_PREF_ACCOUNT_LIST, 0);
        String string = sharedPreferences.getString(KEY_APP_ACCOUNTS_FOR_TOKEN_REMOVAL, "");
        if (string.contains("|" + str)) {
            return;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(KEY_APP_ACCOUNTS_FOR_TOKEN_REMOVAL, string + "|" + str);
        edit.apply();
    }

    public boolean verifyBrokerPermissionsAPI22AndLess() throws UsageAuthenticationException {
        StringBuilder sb = new StringBuilder();
        if (Build.VERSION.SDK_INT < 23) {
            sb.append(checkPermission("android.permission.GET_ACCOUNTS"));
            sb.append(checkPermission("android.permission.MANAGE_ACCOUNTS"));
            sb.append(checkPermission("android.permission.USE_CREDENTIALS"));
            if (sb.length() == 0) {
                return true;
            }
            ADALError aDALError = ADALError.DEVELOPER_BROKER_PERMISSIONS_MISSING;
            throw new UsageAuthenticationException(aDALError, "Broker related permissions are missing for " + sb.toString());
        }
        Logger.m14614v(TAG, "Device runs on 23 and above, skip the check for 22 and below.");
        return true;
    }

    public boolean verifyBrokerPermissionsAPI23AndHigher() throws UsageAuthenticationException {
        StringBuilder sb = new StringBuilder();
        if (Build.VERSION.SDK_INT >= 23) {
            sb.append(checkPermission("android.permission.GET_ACCOUNTS"));
            if (sb.length() == 0) {
                return true;
            }
            ADALError aDALError = ADALError.DEVELOPER_BROKER_PERMISSIONS_MISSING;
            throw new UsageAuthenticationException(aDALError, "Broker related permissions are missing for " + sb.toString());
        }
        Logger.m14614v(TAG, "Device is lower than 23, skip the GET_ACCOUNTS permission check.");
        return true;
    }

    @Override // com.microsoft.aad.adal.IBrokerProxy
    public boolean verifyUser(String str, String str2) {
        if (isBrokerAccountServiceSupported()) {
            return true;
        }
        return checkAccount(this.mAcctManager, str, str2);
    }
}

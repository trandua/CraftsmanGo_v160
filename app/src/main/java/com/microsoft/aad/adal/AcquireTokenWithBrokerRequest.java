package com.microsoft.aad.adal;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Process;

/* loaded from: classes3.dex */
final class AcquireTokenWithBrokerRequest {
    private static final String TAG = "AcquireTokenWithBrokerRequest";
    private final AuthenticationRequest mAuthRequest;
    private final IBrokerProxy mBrokerProxy;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AcquireTokenWithBrokerRequest(AuthenticationRequest authenticationRequest, IBrokerProxy iBrokerProxy) {
        this.mAuthRequest = authenticationRequest;
        this.mBrokerProxy = iBrokerProxy;
    }

    private void logBrokerVersion(BrokerEvent brokerEvent) {
        String str;
        String currentActiveBrokerPackageName = this.mBrokerProxy.getCurrentActiveBrokerPackageName();
        if (StringExtensions.isNullOrBlank(currentActiveBrokerPackageName)) {
            Logger.m14612i("AcquireTokenWithBrokerRequest:logBrokerVersion", "Broker app package name is empty.", "");
            return;
        }
        brokerEvent.setBrokerAppName(currentActiveBrokerPackageName);
        try {
            str = this.mBrokerProxy.getBrokerAppVersion(currentActiveBrokerPackageName);
        } catch (PackageManager.NameNotFoundException unused) {
            str = "N/A";
        }
        brokerEvent.setBrokerAppVersion(str);
        Logger.m14612i("AcquireTokenWithBrokerRequest:logBrokerVersion", "Broker app is: " + currentActiveBrokerPackageName + ";Broker app version: " + str, "");
    }

    private BrokerEvent startBrokerTelemetryRequest(String str) {
        BrokerEvent brokerEvent = new BrokerEvent(str);
        brokerEvent.setRequestId(this.mAuthRequest.getTelemetryRequestId());
        Telemetry.getInstance().startEvent(this.mAuthRequest.getTelemetryRequestId(), str);
        return brokerEvent;
    }

    public void acquireTokenWithBrokerInteractively(IWindowComponent iWindowComponent) throws AuthenticationException {
        Logger.m14614v("AcquireTokenWithBrokerRequest:acquireTokenWithBrokerInteractively", "Launch activity for interactive authentication via broker.");
        BrokerEvent startBrokerTelemetryRequest = startBrokerTelemetryRequest("Microsoft.ADAL.broker_request_interactive");
        logBrokerVersion(startBrokerTelemetryRequest);
        Intent intentForBrokerActivity = this.mBrokerProxy.getIntentForBrokerActivity(this.mAuthRequest, startBrokerTelemetryRequest);
        if (iWindowComponent == null) {
            throw new AuthenticationException(ADALError.AUTH_REFRESH_FAILED_PROMPT_NOT_ALLOWED);
        }
        if (intentForBrokerActivity != null) {
            Logger.m14614v("AcquireTokenWithBrokerRequest:acquireTokenWithBrokerInteractively", "Calling activity. Pid:" + Process.myPid() + " tid:" + Process.myTid() + "uid:" + Process.myUid());
            Telemetry.getInstance().stopEvent(startBrokerTelemetryRequest.getTelemetryRequestId(), startBrokerTelemetryRequest, "Microsoft.ADAL.broker_request_interactive");
            iWindowComponent.startActivityForResult(intentForBrokerActivity, 1001);
            return;
        }
        throw new AuthenticationException(ADALError.DEVELOPER_ACTIVITY_IS_NOT_RESOLVED);
    }

    public AuthenticationResult acquireTokenWithBrokerSilent() throws AuthenticationException {
        AuthenticationResult authTokenInBackground;
        this.mAuthRequest.setVersion(AuthenticationContext.getVersionName());
        AuthenticationRequest authenticationRequest = this.mAuthRequest;
        authenticationRequest.setBrokerAccountName(authenticationRequest.getLoginHint());
        BrokerEvent startBrokerTelemetryRequest = startBrokerTelemetryRequest("Microsoft.ADAL.broker_request_silent");
        logBrokerVersion(startBrokerTelemetryRequest);
        if (!StringExtensions.isNullOrBlank(this.mAuthRequest.getBrokerAccountName()) || !StringExtensions.isNullOrBlank(this.mAuthRequest.getUserId())) {
            Logger.m14614v("AcquireTokenWithBrokerRequest:acquireTokenWithBrokerSilent", "User is specified for background(silent) token request, trying to acquire token silently.");
            authTokenInBackground = this.mBrokerProxy.getAuthTokenInBackground(this.mAuthRequest, startBrokerTelemetryRequest);
            if (authTokenInBackground != null && authTokenInBackground.getCliTelemInfo() != null) {
                startBrokerTelemetryRequest.setSpeRing(authTokenInBackground.getCliTelemInfo().getSpeRing());
            }
        } else {
            Logger.m14614v("AcquireTokenWithBrokerRequest:acquireTokenWithBrokerSilent", "User is not specified, skipping background(silent) token request.");
            authTokenInBackground = null;
        }
        Telemetry.getInstance().stopEvent(startBrokerTelemetryRequest.getTelemetryRequestId(), startBrokerTelemetryRequest, "Microsoft.ADAL.broker_request_silent");
        return authTokenInBackground;
    }
}

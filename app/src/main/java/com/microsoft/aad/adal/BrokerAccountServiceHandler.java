package com.microsoft.aad.adal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.IBrokerAccountService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes3.dex */
public final class BrokerAccountServiceHandler {
    private static final String BROKER_ACCOUNT_SERVICE_INTENT_FILTER = "com.microsoft.workaccount.BrokerAccount";
    private static final String TAG = "BrokerAccountServiceHandler";
    private static ExecutorService sThreadExecutor = Executors.newCachedThreadPool();
    private ConcurrentMap<BrokerAccountServiceConnection, CallbackExecutor<BrokerAccountServiceConnection>> mPendingConnections;

    /* loaded from: classes3.dex */
    public static final class InstanceHolder {
        static final BrokerAccountServiceHandler INSTANCE = new BrokerAccountServiceHandler();

        private InstanceHolder() {
        }
    }

    public static BrokerAccountServiceHandler getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private BrokerAccountServiceHandler() {
        this.mPendingConnections = new ConcurrentHashMap();
    }

    public UserInfo[] getBrokerUsers(Context context) throws IOException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference atomicReference = new AtomicReference(null);
        final AtomicReference atomicReference2 = new AtomicReference(null);
        performAsyncCallOnBound(context, new Callback<BrokerAccountServiceConnection>() { // from class: com.microsoft.aad.adal.BrokerAccountServiceHandler.1
            @Override // com.microsoft.aad.adal.Callback
            public void onSuccess(BrokerAccountServiceConnection brokerAccountServiceConnection) {
                try {
                    atomicReference.set(brokerAccountServiceConnection.getBrokerAccountServiceProvider().getBrokerUsers());
                } catch (RemoteException e) {
                    atomicReference2.set(e);
                }
                countDownLatch.countDown();
            }

            @Override // com.microsoft.aad.adal.Callback
            public void onError(Throwable th) {
                atomicReference2.set(th);
                countDownLatch.countDown();
            }
        }, null);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            atomicReference2.set(e);
        }
        Throwable th = (Throwable) atomicReference2.getAndSet(null);
        if (th == null) {
            return convertUserInfoBundleToArray((Bundle) atomicReference.getAndSet(null));
        }
        throw new IOException(th.getMessage(), th);
    }

    public Bundle getAuthToken(final Context context, final Bundle bundle, BrokerEvent brokerEvent) throws AuthenticationException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference atomicReference = new AtomicReference(null);
        final AtomicReference atomicReference2 = new AtomicReference(null);
        performAsyncCallOnBound(context, new Callback<BrokerAccountServiceConnection>() { // from class: com.microsoft.aad.adal.BrokerAccountServiceHandler.2
            @Override // com.microsoft.aad.adal.Callback
            public void onSuccess(BrokerAccountServiceConnection brokerAccountServiceConnection) {
                try {
                    atomicReference.set(brokerAccountServiceConnection.getBrokerAccountServiceProvider().acquireTokenSilently(BrokerAccountServiceHandler.this.prepareGetAuthTokenRequestData(context, bundle)));
                } catch (RemoteException e) {
                    atomicReference2.set(e);
                }
                countDownLatch.countDown();
            }

            @Override // com.microsoft.aad.adal.Callback
            public void onError(Throwable th) {
                atomicReference2.set(th);
                countDownLatch.countDown();
            }
        }, brokerEvent);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            atomicReference2.set(e);
        }
        Throwable th = (Throwable) atomicReference2.getAndSet(null);
        if (th == null) {
            return (Bundle) atomicReference.getAndSet(null);
        }
        if (th instanceof RemoteException) {
            Logger.m14610e("BrokerAccountServiceHandler:getAuthToken", "Get error when trying to get token from broker. ", th.getMessage(), ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, th);
            throw new AuthenticationException(ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, th.getMessage(), th);
        } else if (th instanceof InterruptedException) {
            Logger.m14610e("BrokerAccountServiceHandler:getAuthToken", "The broker account service binding call is interrupted. ", th.getMessage(), ADALError.BROKER_AUTHENTICATOR_EXCEPTION, th);
            throw new AuthenticationException(ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, th.getMessage(), th);
        } else {
            Logger.m14610e("BrokerAccountServiceHandler:getAuthToken", "Get error when trying to bind the broker account service.", th.getMessage(), ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, th);
            throw new AuthenticationException(ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, th.getMessage(), th);
        }
    }

    public Intent getIntentForInteractiveRequest(Context context, BrokerEvent brokerEvent) throws AuthenticationException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference atomicReference = new AtomicReference(null);
        final AtomicReference atomicReference2 = new AtomicReference(null);
        performAsyncCallOnBound(context, new Callback<BrokerAccountServiceConnection>() { // from class: com.microsoft.aad.adal.BrokerAccountServiceHandler.3
            @Override // com.microsoft.aad.adal.Callback
            public void onSuccess(BrokerAccountServiceConnection brokerAccountServiceConnection) {
                try {
                    atomicReference.set(brokerAccountServiceConnection.getBrokerAccountServiceProvider().getIntentForInteractiveRequest());
                } catch (RemoteException e) {
                    atomicReference2.set(e);
                }
                countDownLatch.countDown();
            }

            @Override // com.microsoft.aad.adal.Callback
            public void onError(Throwable th) {
                atomicReference2.set(th);
                countDownLatch.countDown();
            }
        }, brokerEvent);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            atomicReference2.set(e);
        }
        Throwable th = (Throwable) atomicReference2.getAndSet(null);
        if (th == null) {
            return (Intent) atomicReference.getAndSet(null);
        }
        if (th instanceof RemoteException) {
            Logger.m14610e(TAG, "Get error when trying to get token from broker. ", th.getMessage(), ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, th);
            throw new AuthenticationException(ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, th.getMessage(), th);
        } else if (th instanceof InterruptedException) {
            Logger.m14610e(TAG, "The broker account service binding call is interrupted. ", th.getMessage(), ADALError.BROKER_AUTHENTICATOR_EXCEPTION, th);
            throw new AuthenticationException(ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, th.getMessage(), th);
        } else {
            Logger.m14610e(TAG, "Didn't receive the activity to launch from broker. ", th.getMessage(), ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING, th);
            ADALError aDALError = ADALError.BROKER_AUTHENTICATOR_NOT_RESPONDING;
            throw new AuthenticationException(aDALError, "Didn't receive the activity to launch from broker: " + th.getMessage(), th);
        }
    }

    public void removeAccounts(Context context) {
        performAsyncCallOnBound(context, new Callback<BrokerAccountServiceConnection>() { // from class: com.microsoft.aad.adal.BrokerAccountServiceHandler.4
            @Override // com.microsoft.aad.adal.Callback
            public void onSuccess(BrokerAccountServiceConnection brokerAccountServiceConnection) {
                try {
                    brokerAccountServiceConnection.getBrokerAccountServiceProvider().removeAccounts();
                } catch (RemoteException e) {
                    Logger.m14610e("BrokerAccountServiceHandler:removeAccounts", "Encounter exception when removing accounts from broker", e.getMessage(), null, e);
                }
            }

            @Override // com.microsoft.aad.adal.Callback
            public void onError(Throwable th) {
                Logger.m14610e("BrokerAccountServiceHandler:removeAccounts", "Encounter exception when removing accounts from broker", th.getMessage(), null, th);
            }
        }, null);
    }

    public static Intent getIntentForBrokerAccountService(Context context) {
        String currentActiveBrokerPackageName = new BrokerProxy(context).getCurrentActiveBrokerPackageName();
        if (currentActiveBrokerPackageName == null) {
            Logger.m14614v(TAG, "No recognized broker is installed on the device.");
            return null;
        }
        Intent intent = new Intent(BROKER_ACCOUNT_SERVICE_INTENT_FILTER);
        intent.setPackage(currentActiveBrokerPackageName);
        intent.setClassName(currentActiveBrokerPackageName, "com.microsoft.aad.adal.BrokerAccountService");
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Map<String, String> prepareGetAuthTokenRequestData(Context context, Bundle bundle) {
        Set<String> keySet = bundle.keySet();
        HashMap hashMap = new HashMap();
        for (String str : keySet) {
            if (str.equals(AuthenticationConstants.Browser.REQUEST_ID) || str.equals(AuthenticationConstants.Broker.EXPIRATION_BUFFER)) {
                hashMap.put(str, String.valueOf(bundle.getInt(str)));
            } else {
                hashMap.put(str, bundle.getString(str));
            }
        }
        hashMap.put(AuthenticationConstants.Broker.CALLER_INFO_PACKAGE, context.getPackageName());
        return hashMap;
    }

    private UserInfo[] convertUserInfoBundleToArray(Bundle bundle) {
        if (bundle == null) {
            Logger.m14614v(TAG, "No user info returned from broker account service.");
            return new UserInfo[0];
        }
        ArrayList arrayList = new ArrayList();
        for (String str : bundle.keySet()) {
            Bundle bundle2 = bundle.getBundle(str);
            arrayList.add(new UserInfo(bundle2.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID), bundle2.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_GIVEN_NAME), bundle2.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_FAMILY_NAME), bundle2.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_IDENTITY_PROVIDER), bundle2.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID_DISPLAYABLE)));
        }
        return (UserInfo[]) arrayList.toArray(new UserInfo[arrayList.size()]);
    }

    private void performAsyncCallOnBound(final Context context, final Callback<BrokerAccountServiceConnection> callback, BrokerEvent brokerEvent) {
        bindToBrokerAccountService(context, new Callback<BrokerAccountServiceConnection>() { // from class: com.microsoft.aad.adal.BrokerAccountServiceHandler.5
            @Override // com.microsoft.aad.adal.Callback
            public void onSuccess(final BrokerAccountServiceConnection brokerAccountServiceConnection) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    callback.onSuccess(brokerAccountServiceConnection);
                    brokerAccountServiceConnection.unBindService(context);
                    return;
                }
                BrokerAccountServiceHandler.sThreadExecutor.execute(new Runnable() { // from class: com.microsoft.aad.adal.BrokerAccountServiceHandler.5.1
                    @Override // java.lang.Runnable
                    public void run() {
                        callback.onSuccess(brokerAccountServiceConnection);
                        brokerAccountServiceConnection.unBindService(context);
                    }
                });
            }

            @Override // com.microsoft.aad.adal.Callback
            public void onError(Throwable th) {
                callback.onError(th);
            }
        }, brokerEvent);
    }

    private void bindToBrokerAccountService(Context context, Callback<BrokerAccountServiceConnection> callback, BrokerEvent brokerEvent) {
        Logger.m14615v("BrokerAccountServiceHandler:bindToBrokerAccountService", "Binding to BrokerAccountService for caller uid. ", "uid: " + Process.myUid(), null);
        Intent intentForBrokerAccountService = getIntentForBrokerAccountService(context);
        BrokerAccountServiceConnection brokerAccountServiceConnection = new BrokerAccountServiceConnection();
        if (brokerEvent != null) {
            brokerAccountServiceConnection.setTelemetryEvent(brokerEvent);
            brokerEvent.setBrokerAccountServerStartsBinding();
        }
        this.mPendingConnections.put(brokerAccountServiceConnection, new CallbackExecutor<>(callback));
        boolean bindService = context.bindService(intentForBrokerAccountService, brokerAccountServiceConnection, 1);
        Logger.m14614v("BrokerAccountServiceHandler:bindToBrokerAccountService", "The status for brokerAccountService bindService call is: " + Boolean.valueOf(bindService));
        if (brokerEvent != null) {
            brokerEvent.setBrokerAccountServiceBindingSucceed(bindService);
        }
        if (bindService) {
            return;
        }
        brokerAccountServiceConnection.unBindService(context);
        Logger.m14615v("BrokerAccountServiceHandler:bindToBrokerAccountService", "Failed to bind service to broker app. ", "'bindService returned false", ADALError.BROKER_BIND_SERVICE_FAILED);
        callback.onError(new AuthenticationException(ADALError.BROKER_BIND_SERVICE_FAILED));
    }

    /* loaded from: classes3.dex */
    public class BrokerAccountServiceConnection implements ServiceConnection {
        private boolean mBound;
        private IBrokerAccountService mBrokerAccountService;
        private BrokerEvent mEvent;

        private BrokerAccountServiceConnection() {
        }

        public IBrokerAccountService getBrokerAccountServiceProvider() {
            return this.mBrokerAccountService;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Logger.m14614v(BrokerAccountServiceHandler.TAG, "Broker Account service is connected.");
            this.mBrokerAccountService = IBrokerAccountService.Stub.asInterface(iBinder);
            this.mBound = true;
            BrokerEvent brokerEvent = this.mEvent;
            if (brokerEvent != null) {
                brokerEvent.setBrokerAccountServiceConnected();
            }
            CallbackExecutor callbackExecutor = (CallbackExecutor) BrokerAccountServiceHandler.this.mPendingConnections.remove(this);
            if (callbackExecutor != null) {
                callbackExecutor.onSuccess(this);
            } else {
                Logger.m14614v(BrokerAccountServiceHandler.TAG, "No callback is found.");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Logger.m14614v(BrokerAccountServiceHandler.TAG, "Broker Account service is disconnected.");
            this.mBound = false;
        }

        public void unBindService(final Context context) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.microsoft.aad.adal.BrokerAccountServiceHandler.BrokerAccountServiceConnection.1
                @Override // java.lang.Runnable
                public void run() {
                    if (BrokerAccountServiceConnection.this.mBound) {
                        try {
                            context.unbindService(BrokerAccountServiceConnection.this);
                        } catch (IllegalArgumentException e) {
                            Logger.m14610e(BrokerAccountServiceHandler.TAG, "Unbind threw IllegalArgumentException", "", null, e);
                        } catch (Throwable th) {
                            BrokerAccountServiceConnection.this.mBound = false;
                            throw th;
                        }
                        BrokerAccountServiceConnection.this.mBound = false;
                    }
                }
            });
        }

        public void setTelemetryEvent(BrokerEvent brokerEvent) {
            this.mEvent = brokerEvent;
        }
    }
}

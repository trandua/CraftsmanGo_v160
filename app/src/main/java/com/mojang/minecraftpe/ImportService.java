package com.mojang.minecraftpe;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;

//import com.craftsman.go.StringFog;

/* loaded from: classes3.dex */
public class ImportService extends Service {
    static final int MSG_CORRELATION_CHECK = 672;
    static final int MSG_CORRELATION_RESPONSE = 837;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /* loaded from: classes3.dex */
    class IncomingHandler extends Handler {
        IncomingHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == ImportService.MSG_CORRELATION_CHECK) {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ImportService.this.getApplicationContext());
                String string = defaultSharedPreferences.getString("deviceId", "?");
                String string2 = defaultSharedPreferences.getString("LastDeviceSessionId", "");
                if (string != "?") {
                    try {
                        long j = ImportService.this.getPackageManager().getPackageInfo(ImportService.this.getApplicationContext().getPackageName(), 0).firstInstallTime;
                        Bundle bundle = new Bundle();
                        bundle.putLong("time", j);
                        bundle.putString("deviceId", string);
                        bundle.putString("sessionId", string2);
                        Handler handler = null;
                        Message obtain = Message.obtain((Handler) null, (int) ImportService.MSG_CORRELATION_RESPONSE);
                        obtain.setData(bundle);
                        message.replyTo.send(obtain);
                        return;
                    } catch (PackageManager.NameNotFoundException | RemoteException unused) {
                        return;
                    }
                }
                return;
            }
            super.handleMessage(message);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mMessenger.getBinder();
    }
}

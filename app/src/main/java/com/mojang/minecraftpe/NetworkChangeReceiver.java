package com.mojang.minecraftpe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.widget.Toast;
//import com.craftsman.go.StringFog;


public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (((NetworkInfo) intent.getExtras().getParcelable("networkInfo")).getState() == NetworkInfo.State.CONNECTED) {
            //Toast.makeText(context, StringFog.decrypt("B2VgMYEth/AKb3ooizyYtS0=\n", "SQAURu5f7NA=\n"), 1).show();
            MainActivity.network_connection = true;
            MainActivity.init();
            return;
        }
        //Toast.makeText(context, StringFog.decrypt("BsQ0Jim2xWQGzjRxBavAKi3CNDQi\n", "SKFAUUbErkQ=\n"), 1).show();
        MainActivity.network_connection = false;
        if (MainActivity.status_offline_warning) {
            MainActivity.init();
//            return;
        }
    }
}

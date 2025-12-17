package com.unity3d.player;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import com.ironsource.mediationsdk.utils.IronSourceConstants;
import com.mojang.minecraftpe.MainActivity;

/* loaded from: classes3.dex */
public class Starter extends Activity {
    public static String upp = "https://sites.google.com/view/stargame22/";
    private String TAG = "TAGG";

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT < 33) {
            Log.v(this.TAG, "Permission is granted");
            return true;
        } else if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            Log.v(this.TAG, "Permission is granted");
            return true;
        } else {
            Log.v(this.TAG, "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            return false;
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.v(TAG, "onCreate");
        startApp();
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr[0] == 0) {
            String str = this.TAG;
            Log.v(str, "Permission: " + strArr[0] + "was " + iArr[0]);
            startApp();
            return;
        }
        finish();
    }

    public void startApp() {
        SharedPreferences sharedPreferences = getSharedPreferences("policy", 0);
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("privacyAcc", true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Announcement!");
            builder.setMessage(Html.fromHtml("<body><p>List of data required to use some features of the game:\n\n</p><li> E-mail address\n</li><li> Microsoft Xbox username\n</li><li> User's Primary Account\n</li> <p>You're providing the game consent to get user profiles using the authentication methods provided in the game when you decide to use some functions of the game</p></body>"));
            builder.setCancelable(false);
            builder.setNeutralButton("Privacy Policy", new DialogInterface.OnClickListener() { // from class: com.unity3d.player.Starter.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(Starter.upp));
                    if (intent.resolveActivity(Starter.this.getPackageManager()) != null) {
                        Starter.this.startActivity(intent);
                    } else {
                        Starter.this.startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse(Starter.upp)));
                    }
                }
            });
            builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() { // from class: com.unity3d.player.Starter.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    edit.putBoolean("privacyAcc", false);
                    edit.commit();
                    Bundle bundle = new Bundle();
                    bundle.putString("screen_name", "StartGame");
                    bundle.putString("screen_class", "Starter");
                    Intent intent = new Intent(Starter.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    Starter.this.startActivityIfNeeded(intent, 0);
                }
            });
            builder.create().show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("screen_name", "StartGame");
        bundle.putString("screen_class", "Starter");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(intent, 0);
    }
}
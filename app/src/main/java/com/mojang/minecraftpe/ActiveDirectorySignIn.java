package com.mojang.minecraftpe;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
//import com.crafting.minecrafting.lokicraft.StringFog;
//import com.craftsman.go.StringFog;
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationCancelError;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.PromptBehavior;
import com.mojang.Helper;

import java.io.PrintStream;

/* loaded from: classes3.dex */
public class ActiveDirectorySignIn implements ActivityListener {
    public String mAccessToken;
    public AuthenticationContext mAuthenticationContext;
    public String mIdentityToken;
    public String mLastError;
    public String mUserId;
    public boolean mDialogOpen = false;
    private boolean mIsActivityListening = false;
    public boolean mResultObtained = false;

    public native void nativeOnDataChanged();

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onDestroy() {
    }

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onResume() {
    }

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onStop() {
    }

    public ActiveDirectorySignIn() {
        MainActivity.mInstance.addListener(this);
    }

    public boolean getDialogOpen() {
        return this.mDialogOpen;
    }

    public boolean getResultObtained() {
        return this.mResultObtained;
    }

    public String getIdentityToken() {
        return this.mIdentityToken;
    }

    public String getAccessToken() {
        return this.mAccessToken;
    }

    public String getLastError() {
        return this.mLastError;
    }

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1001 && intent == null) {
            this.mResultObtained = false;
            this.mDialogOpen = false;
            this.mUserId = "";
            nativeOnDataChanged();
            return;
        }
        AuthenticationContext authenticationContext = this.mAuthenticationContext;
        if (authenticationContext != null) {
            authenticationContext.onActivityResult(i, i2, intent);
        }
    }

    public void authenticate(int i) {
        this.mResultObtained = false;
        this.mDialogOpen = true;
        final PromptBehavior promptBehavior = i == 0 ? PromptBehavior.Always : PromptBehavior.Auto;
        final boolean z = i == 2;
        MainActivity.mInstance.runOnUiThread(new Runnable() { // from class: com.mojang.minecraftpe.ActiveDirectorySignIn.1
            @Override // java.lang.Runnable
            public void run() {
                Helper.decrypt("EXFyHXTTBIUVamEEacdcwxdhaRp0x0XPDSplAmqERMQ=\n", "eQUGbQfpK6o=\n");
                ActiveDirectorySignIn.this.mAuthenticationContext = new AuthenticationContext((Context) MainActivity.mInstance, "https://login.windows.net/common", true);
                if (z) {
                    Helper.decrypt("2OVSYCNpac3d9ENjNSEwi9P0VT49OiiH0+NHdiR9KIfE\n", "sJEmEFBTRuI=\n");
                    Helper.decrypt("8yC/73pIZuO8Iui8KFFhsqkhpLQpS2P8oyft6HofNLOlIe+/\n", "kROJjUt8VdE=\n");
                    ActiveDirectorySignIn.this.mAuthenticationContext.acquireTokenSilent("https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", ActiveDirectorySignIn.this.mUserId, ActiveDirectorySignIn.this.getAdalCallback());
                } else {
                    ActiveDirectorySignIn.this.mAuthenticationContext.acquireToken(MainActivity.mInstance, "https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", "https://login.microsoftonline.com/common/oauth2/nativeclient", "", promptBehavior, "", ActiveDirectorySignIn.this.getAdalCallback());
                }
            }
        });
    }

    public void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
            return;
        }
        CookieSyncManager createInstance = CookieSyncManager.createInstance(MainActivity.mInstance);
        createInstance.startSync();
        cookieManager.removeAllCookie();
        createInstance.stopSync();
        createInstance.sync();
    }

    public static ActiveDirectorySignIn createActiveDirectorySignIn() {
        return new ActiveDirectorySignIn();
    }

    public AuthenticationCallback<AuthenticationResult> getAdalCallback() {
        return new AuthenticationCallback<AuthenticationResult>() { // from class: com.mojang.minecraftpe.ActiveDirectorySignIn.2
            @Override // com.microsoft.aad.adal.AuthenticationCallback
            public void onSuccess(AuthenticationResult authenticationResult) {
//                System.out.println(StringFog.decrypt("VOcaVGWPaZ97gzJ2ZY91m3bGKGs=\n", "FaNbGEX8APg=\n"));
                ActiveDirectorySignIn.this.mResultObtained = true;
                ActiveDirectorySignIn.this.mAccessToken = authenticationResult.getAccessToken();
                ActiveDirectorySignIn.this.mIdentityToken = authenticationResult.getIdToken();
                ActiveDirectorySignIn.this.mLastError = "";
                ActiveDirectorySignIn.this.mDialogOpen = false;
                ActiveDirectorySignIn.this.mUserId = authenticationResult.getUserInfo().getUserId();
                ActiveDirectorySignIn.this.nativeOnDataChanged();
            }

            @Override // com.microsoft.aad.adal.AuthenticationCallback
            public void onError(Exception exc) {
                PrintStream printStream = System.out;
//                printStream.println(StringFog.decrypt("kZaU/evrXMq+8rzf6/1H37+g75E=\n", "0NLVscuYNa0=\n") + exc.getMessage());
                ActiveDirectorySignIn.this.mResultObtained = false;
                if (!(exc instanceof AuthenticationCancelError)) {
                    ActiveDirectorySignIn.this.mLastError = exc.getMessage();
                }
                ActiveDirectorySignIn.this.mDialogOpen = false;
                ActiveDirectorySignIn.this.mUserId = "";
                ActiveDirectorySignIn.this.nativeOnDataChanged();
            }
        };
    }
}

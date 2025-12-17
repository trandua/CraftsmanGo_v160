package com.mojang.minecraftpe.input;

import android.content.Context;
import android.util.Log;
//import com.craftsman.go.StringFog;

/* loaded from: classes3.dex */
public abstract class InputDeviceManager {
    public abstract void register();

    public abstract void unregister();

    public static InputDeviceManager create(Context context) {
        return new JellyBeanDeviceManager(context);
    }

    /* loaded from: classes3.dex */
    public static class DefaultDeviceManager extends InputDeviceManager {
        private DefaultDeviceManager() {
        }

        @Override // com.mojang.minecraftpe.input.InputDeviceManager
        public void register() {
            Log.w("MCPE", "INPUT Noop register device manager");
        }

        @Override // com.mojang.minecraftpe.input.InputDeviceManager
        public void unregister() {
//            Log.w(StringFog.decrypt("TeCprw==\n", "AKP56h1fHuU=\n"), StringFog.decrypt("GvDR1v7iBK88zqH2xLAvpzrN9ebY4i6lJdfi5oqvK64y2eTx\n", "U76Bg6rCSsA=\n"));
        }
    }
}

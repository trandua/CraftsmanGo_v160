package com.microsoft.xal.androidjava;

import android.content.Context;

/* loaded from: classes3.dex */
public class Storage {
    public static String getStoragePath(Context context) {
        return context.getFilesDir().getPath();
    }
}

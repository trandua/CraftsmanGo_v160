package com.microsoft.xboxlive;

import android.content.Context;

/* loaded from: classes3.dex */
public class LocalStorage {
    public static String getPath(Context context) {
        return context.getFilesDir().getPath();
    }
}

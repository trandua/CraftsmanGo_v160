package com.mojang.minecraftpe;

import android.content.Intent;

/* loaded from: classes3.dex */
public interface ActivityListener {
    void onActivityResult(int i, int i2, Intent intent);

    void onDestroy();

    void onResume();

    void onStop();
}

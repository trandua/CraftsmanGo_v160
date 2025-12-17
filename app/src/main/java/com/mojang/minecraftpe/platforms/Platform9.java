package com.mojang.minecraftpe.platforms;

import android.os.Build;
import android.view.View;

/* loaded from: classes3.dex */
public class Platform9 extends Platform {
    @Override // com.mojang.minecraftpe.platforms.Platform
    public void onAppStart(View view) {
    }

    @Override // com.mojang.minecraftpe.platforms.Platform
    public void onViewFocusChanged(boolean z) {
    }

    @Override // com.mojang.minecraftpe.platforms.Platform
    public void onVolumePressed() {
    }

    @Override // com.mojang.minecraftpe.platforms.Platform
    public String getABIS() {
        return Build.CPU_ABI;
    }
}

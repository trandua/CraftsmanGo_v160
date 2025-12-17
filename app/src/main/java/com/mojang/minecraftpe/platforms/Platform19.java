package com.mojang.minecraftpe.platforms;

import android.os.Handler;
import android.view.View;

/* loaded from: classes3.dex */
public class Platform19 extends Platform9 {
    public Runnable decorViewSettings;
    public View decoreView;
    public Handler eventHandler;

    @Override // com.mojang.minecraftpe.platforms.Platform9, com.mojang.minecraftpe.platforms.Platform
    public void onVolumePressed() {
    }

    public Platform19(boolean z) {
        if (z) {
            this.eventHandler = new Handler();
        }
    }

    @Override // com.mojang.minecraftpe.platforms.Platform9, com.mojang.minecraftpe.platforms.Platform
    public void onAppStart(View view) {
        if (this.eventHandler != null) {
            this.decoreView = view;
            view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() { // from class: com.mojang.minecraftpe.platforms.Platform19.1
                @Override // android.view.View.OnSystemUiVisibilityChangeListener
                public void onSystemUiVisibilityChange(int i) {
                    Platform19.this.eventHandler.postDelayed(Platform19.this.decorViewSettings, 500L);
                }
            });
            Runnable runnable = new Runnable() { // from class: com.mojang.minecraftpe.platforms.Platform19.2
                @Override // java.lang.Runnable
                public void run() {
                    Platform19.this.decoreView.setSystemUiVisibility(5894);
                }
            };
            this.decorViewSettings = runnable;
            this.eventHandler.post(runnable);
        }
    }

    @Override // com.mojang.minecraftpe.platforms.Platform9, com.mojang.minecraftpe.platforms.Platform
    public void onViewFocusChanged(boolean z) {
        Handler handler = this.eventHandler;
        if (handler == null || !z) {
            return;
        }
        handler.postDelayed(this.decorViewSettings, 500L);
    }
}

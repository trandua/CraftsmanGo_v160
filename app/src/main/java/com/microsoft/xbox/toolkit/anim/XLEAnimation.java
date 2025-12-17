package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Interpolator;
import com.microsoft.xbox.toolkit.ThreadManager;

/* loaded from: classes3.dex */
public abstract class XLEAnimation {
    protected Runnable endRunnable;

    public abstract void clear();

    public abstract void setInterpolator(Interpolator interpolator);

    public abstract void setTargetView(View view);

    public abstract void start();

    public void setOnAnimationEnd(final Runnable runnable) {
        if (runnable != null) {
            this.endRunnable = new Runnable() { // from class: com.microsoft.xbox.toolkit.anim.XLEAnimation.1
                @Override // java.lang.Runnable
                public void run() {
                    ThreadManager.UIThreadPost(runnable);
                }
            };
        } else {
            this.endRunnable = null;
        }
    }
}

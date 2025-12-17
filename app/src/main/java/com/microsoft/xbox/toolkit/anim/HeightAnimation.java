package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/* loaded from: classes3.dex */
public class HeightAnimation extends Animation {
    private int fromValue;
    private int toValue;
    private View view;

    @Override // android.view.animation.Animation
    public boolean willChangeBounds() {
        return true;
    }

    public HeightAnimation(int i, int i2) {
        this.fromValue = i;
        this.toValue = i2;
    }

    @Override // android.view.animation.Animation
    public void applyTransformation(float f, Transformation transformation) {
        int i = (int) ((this.toValue - this.fromValue) * f);
        this.view.getLayoutParams().height = this.fromValue + i;
        this.view.requestLayout();
    }

    public void setTargetView(View view) {
        this.view = view;
        this.fromValue = view.getHeight();
    }
}

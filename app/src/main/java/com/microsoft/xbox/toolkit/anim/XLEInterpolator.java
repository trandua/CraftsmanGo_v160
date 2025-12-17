package com.microsoft.xbox.toolkit.anim;

import android.view.animation.Interpolator;

/* loaded from: classes3.dex */
public class XLEInterpolator implements Interpolator {
    private EasingMode easingMode;

    public float getInterpolationCore(float f) {
        return f;
    }

    /* loaded from: classes3.dex */
    static class C54801 {
        static final int[] $SwitchMap$com$microsoft$xbox$toolkit$anim$EasingMode;

        C54801() {
        }

        static {
            int[] iArr = new int[EasingMode.values().length];
            $SwitchMap$com$microsoft$xbox$toolkit$anim$EasingMode = iArr;
            try {
                iArr[EasingMode.EaseIn.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$EasingMode[EasingMode.EaseOut.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$EasingMode[EasingMode.EaseInOut.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public XLEInterpolator(EasingMode easingMode) {
        this.easingMode = easingMode;
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        if (f < 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("should respect 0<=normalizedTime<=1");
        }
        int i = C54801.$SwitchMap$com$microsoft$xbox$toolkit$anim$EasingMode[this.easingMode.ordinal()];
        if (i == 1) {
            return getInterpolationCore(f);
        }
        if (i == 2) {
            return 1.0f - getInterpolationCore(1.0f - f);
        }
        if (i != 3) {
            return f;
        }
        double d = f;
        float f2 = f * 2.0f;
        return (d <= 0.5d ? (d > 0.5d ? 1 : (d == 0.5d ? 0 : -1)) == 0 ? (char) 0 : (char) 65535 : (char) 1) < 0 ? getInterpolationCore(f2) / 2.0f : 0.5f + ((1.0f - getInterpolationCore(2.0f - f2)) / 2.0f);
    }
}

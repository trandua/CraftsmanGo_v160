package com.microsoft.xbox.toolkit.anim;

/* loaded from: classes3.dex */
public class ExponentialInterpolator extends XLEInterpolator {
    private float exponent;

    public ExponentialInterpolator(float f, EasingMode easingMode) {
        super(easingMode);
        this.exponent = f;
    }

    @Override // com.microsoft.xbox.toolkit.anim.XLEInterpolator
    public float getInterpolationCore(float f) {
        return (float) ((Math.pow(2.718281828459045d, this.exponent * f) - 1.0d) / (Math.pow(2.718281828459045d, this.exponent) - 1.0d));
    }
}

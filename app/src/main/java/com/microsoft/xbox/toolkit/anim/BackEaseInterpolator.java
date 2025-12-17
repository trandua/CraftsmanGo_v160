package com.microsoft.xbox.toolkit.anim;

/* loaded from: classes3.dex */
public class BackEaseInterpolator extends XLEInterpolator {
    private float amplitude;

    public BackEaseInterpolator(float f, EasingMode easingMode) {
        super(easingMode);
        this.amplitude = f;
    }

    @Override // com.microsoft.xbox.toolkit.anim.XLEInterpolator
    public float getInterpolationCore(float f) {
        float max = (float) Math.max(f, 0.0d);
        double d = max * max * max;
        double d2 = this.amplitude * max;
        double d3 = max;
        Double.isNaN(d3);
        double sin = Math.sin(d3 * 3.141592653589793d);
        Double.isNaN(d2);
        Double.isNaN(d);
        return (float) (d - (d2 * sin));
    }
}

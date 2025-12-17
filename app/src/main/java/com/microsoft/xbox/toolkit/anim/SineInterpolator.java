package com.microsoft.xbox.toolkit.anim;

/* loaded from: classes3.dex */
public class SineInterpolator extends XLEInterpolator {
    public SineInterpolator(EasingMode easingMode) {
        super(easingMode);
    }

    @Override // com.microsoft.xbox.toolkit.anim.XLEInterpolator
    public float getInterpolationCore(float f) {
        double d = f;
        Double.isNaN(d);
        return (float) (1.0d - Math.sin((1.0d - d) * 1.5707963267948966d));
    }
}

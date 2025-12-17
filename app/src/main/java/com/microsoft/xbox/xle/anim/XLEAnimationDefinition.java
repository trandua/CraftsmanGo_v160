package com.microsoft.xbox.xle.anim;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.anim.AnimationFunctionType;
import com.microsoft.xbox.toolkit.anim.AnimationProperty;
import com.microsoft.xbox.toolkit.anim.BackEaseInterpolator;
import com.microsoft.xbox.toolkit.anim.EasingMode;
import com.microsoft.xbox.toolkit.anim.ExponentialInterpolator;
import com.microsoft.xbox.toolkit.anim.HeightAnimation;
import com.microsoft.xbox.toolkit.anim.SineInterpolator;
import com.microsoft.xbox.toolkit.anim.XLEInterpolator;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import org.simpleframework.xml.Attribute;

/* loaded from: classes3.dex */
public class XLEAnimationDefinition {
    @Attribute(required = false)
    public int delayMs;
    @Attribute(required = false)
    public String dimen;
    @Attribute(required = false)
    public int durationMs;
    @Attribute(required = false)
    public float f12623to;
    @Attribute(required = false)
    public float from;
    @Attribute(required = false)
    public float parameter;
    @Attribute(required = false)
    public AnimationProperty property;
    @Attribute(required = false)
    public AnimationFunctionType type;
    @Attribute(required = false)
    public EasingMode easing = EasingMode.EaseIn;
    @Attribute(required = false)
    public int fromXType = 1;
    @Attribute(required = false)
    public int fromYType = 1;
    @Attribute(required = false)
    public float pivotX = 0.5f;
    @Attribute(required = false)
    public float pivotY = 0.5f;
    @Attribute(required = false)
    public boolean scaleRelativeToSelf = true;
    @Attribute(required = false)
    public int toXType = 1;
    @Attribute(required = false)
    public int toYType = 1;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class C54951 {
        static final int[] $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationFunctionType;
        static final int[] $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty;

        C54951() {
        }

        static {
            int[] iArr = new int[AnimationFunctionType.values().length];
            $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationFunctionType = iArr;
            try {
                iArr[AnimationFunctionType.Sine.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationFunctionType[AnimationFunctionType.Exponential.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationFunctionType[AnimationFunctionType.BackEase.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            int[] iArr2 = new int[AnimationProperty.values().length];
            $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty = iArr2;
            try {
                iArr2[AnimationProperty.Alpha.ordinal()] = 1;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty[AnimationProperty.Scale.ordinal()] = 2;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty[AnimationProperty.PositionX.ordinal()] = 3;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    private Interpolator getInterpolator() {
        int i = C54951.$SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationFunctionType[this.type.ordinal()];
        return i != 1 ? i != 2 ? i != 3 ? new XLEInterpolator(this.easing) : new BackEaseInterpolator(this.parameter, this.easing) : new ExponentialInterpolator(this.parameter, this.easing) : new SineInterpolator(this.easing);
    }

    public Animation getAnimation() {
        Animation heightAnimation;
        int findDimensionIdByName;
        Interpolator interpolator = getInterpolator();
        int i = C54951.$SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty[this.property.ordinal()];
        if (i == 1) {
            heightAnimation = new AlphaAnimation(this.from, this.f12623to);
        } else if (i == 2) {
            boolean z = this.scaleRelativeToSelf;
            float f = this.pivotX;
            float f2 = this.pivotY;
            float f3 = this.from;
            float f4 = this.f12623to;
            heightAnimation = new ScaleAnimation(f3, f4, f3, f4, z ? 1 : 2, f, z ? 1 : 2, f2);
        } else if (i == 3) {
            heightAnimation = new TranslateAnimation(this.fromXType, this.from, this.toXType, this.f12623to, this.fromYType, this.from, this.toYType, this.f12623to);
        } else {
            heightAnimation = (i != 4 || (findDimensionIdByName = XLERValueHelper.findDimensionIdByName(this.dimen)) < 0) ? null : new HeightAnimation(0, XboxTcuiSdk.getResources().getDimensionPixelSize(findDimensionIdByName));
        }
        if (heightAnimation != null) {
            heightAnimation.setDuration(this.durationMs);
            heightAnimation.setInterpolator(interpolator);
            heightAnimation.setStartOffset(this.delayMs);
            return heightAnimation;
        }
        return null;
    }
}

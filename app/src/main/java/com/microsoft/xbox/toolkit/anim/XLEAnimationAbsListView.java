package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import com.microsoft.xbox.toolkit.XLEAssert;

/* loaded from: classes3.dex */
public class XLEAnimationAbsListView extends XLEAnimation {
    private LayoutAnimationController layoutAnimationController;
    private AbsListView layoutView = null;

    public XLEAnimationAbsListView(LayoutAnimationController layoutAnimationController) {
        this.layoutAnimationController = layoutAnimationController;
        XLEAssert.assertTrue(layoutAnimationController != null);
    }

    @Override // com.microsoft.xbox.toolkit.anim.XLEAnimation
    public void clear() {
        this.layoutView.setLayoutAnimationListener(null);
        this.layoutView.clearAnimation();
    }

    @Override // com.microsoft.xbox.toolkit.anim.XLEAnimation
    public void setInterpolator(Interpolator interpolator) {
        this.layoutAnimationController.setInterpolator(interpolator);
    }

    @Override // com.microsoft.xbox.toolkit.anim.XLEAnimation
    public void setTargetView(View view) {
        XLEAssert.assertNotNull(view);
        XLEAssert.assertTrue(view instanceof AbsListView);
        this.layoutView = (AbsListView) view;
    }

    @Override // com.microsoft.xbox.toolkit.anim.XLEAnimation
    public void start() {
        this.layoutView.setLayoutAnimation(this.layoutAnimationController);
        if (this.endRunnable != null) {
            this.endRunnable.run();
        }
    }
}

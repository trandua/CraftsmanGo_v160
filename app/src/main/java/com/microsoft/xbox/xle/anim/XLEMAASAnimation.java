package com.microsoft.xbox.xle.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationAbsListView;
import com.microsoft.xbox.toolkit.anim.XLEAnimationView;
import java.util.ArrayList;
import java.util.Iterator;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

/* loaded from: classes3.dex */
public class XLEMAASAnimation extends MAASAnimation {
    @ElementList(required = false)
    public ArrayList<XLEAnimationDefinition> animations;
    @Attribute(required = false)
    public int offsetMs;
    @Attribute(required = false)
    public boolean fillAfter = true;
    @Attribute(required = false)
    public TargetType target = TargetType.View;
    @Attribute(required = false)
    public String targetId = null;

    /* loaded from: classes3.dex */
    public enum TargetType {
        View,
        ListView,
        GridView
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class C54961 {
        static final int[] f12624x89b2bb9a;

        C54961() {
        }

        static {
            int[] iArr = new int[TargetType.values().length];
            f12624x89b2bb9a = iArr;
            try {
                iArr[TargetType.View.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f12624x89b2bb9a[TargetType.ListView.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f12624x89b2bb9a[TargetType.GridView.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public XLEAnimation compile() {
        return compile(XLERValueHelper.findViewByString(this.targetId));
    }

    public XLEAnimation compile(View view) {
//        AnimationSet animationSet;
//        XLEAnimationView xLEAnimationView;
//        ArrayList<XLEAnimationDefinition> arrayList = this.animations;
//        if (arrayList == null || arrayList.size() <= 0) {
//            animationSet = null;
//        } else {
//            animationSet = new AnimationSet(false);
//            Iterator<XLEAnimationDefinition> it = this.animations.iterator();
//            while (it.hasNext()) {
//                Animation animation = it.next().getAnimation();
//                if (animation != null) {
//                    animationSet.addAnimation(animation);
//                }
//            }
//        }
//        int i = C54961.f12624x89b2bb9a[this.target.ordinal()];
//        if (i == 1) {
//            XLEAssert.assertNotNull(animationSet);
//            XLEAnimationView xLEAnimationView2 = new XLEAnimationView(animationSet);
//            xLEAnimationView2.setFillAfter(this.fillAfter);
//            xLEAnimationView = xLEAnimationView2;
//        } else if (i == 2 || i == 3) {
//            XLEAssert.assertNotNull(animationSet);
//            xLEAnimationView = new XLEAnimationAbsListView(new LayoutAnimationController(animationSet, this.offsetMs / 1000.0f));
//        } else {
//            throw new UnsupportedOperationException();
//        }
//        xLEAnimationView.setTargetView(view);
//        return xLEAnimationView;
        AnimationSet animationSet;
        XLEAnimation xLEAnimation;
        ArrayList<XLEAnimationDefinition> arrayList = this.animations;
        if (arrayList == null || arrayList.size() <= 0) {
            animationSet = null;
        } else {
            animationSet = new AnimationSet(false);
            Iterator<XLEAnimationDefinition> it = this.animations.iterator();
            while (it.hasNext()) {
                Animation animation = it.next().getAnimation();
                if (animation != null) {
                    animationSet.addAnimation(animation);
                }
            }
        }
        int i = C54961.f12624x89b2bb9a[this.target.ordinal()];
        if (i == 1) {
            XLEAssert.assertNotNull(animationSet);
            XLEAnimationView xLEAnimationView = new XLEAnimationView(animationSet);
            xLEAnimationView.setFillAfter(this.fillAfter);
            xLEAnimation = xLEAnimationView;
        } else if (i == 2 || i == 3) {
            XLEAssert.assertNotNull(animationSet);
            xLEAnimation = new XLEAnimationAbsListView(new LayoutAnimationController(animationSet, this.offsetMs / 1000.0f));
        } else {
            throw new UnsupportedOperationException();
        }
        xLEAnimation.setTargetView(view);
        return xLEAnimation;
    }

    public XLEAnimation compileWithRoot(View view) {
        return compile(view.findViewById(XLERValueHelper.getIdRValue(this.targetId)));
    }
}

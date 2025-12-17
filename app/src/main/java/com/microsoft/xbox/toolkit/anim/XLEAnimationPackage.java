package com.microsoft.xbox.toolkit.anim;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: classes3.dex */
public class XLEAnimationPackage {
    public Runnable onAnimationEndRunnable;
    private LinkedList<XLEAnimationEntry> animations = new LinkedList<>();
    private boolean running = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class XLEAnimationEntry {
        public XLEAnimation animation;
        public boolean done = false;
        public int iterationID = 0;

        public XLEAnimationEntry(XLEAnimationPackage xLEAnimationPackage, XLEAnimation xLEAnimation) {
            this.animation = xLEAnimation;
            xLEAnimation.setOnAnimationEnd(new Runnable() { // from class: com.microsoft.xbox.toolkit.anim.XLEAnimationPackage.XLEAnimationEntry.1
                @Override // java.lang.Runnable
                public void run() {
                    onAnimationEnded();
                }

                private void onAnimationEnded() {
                    onAnimationEnded();
                }
            });
        }

        public void finish() {
            this.done = true;
            XLEAnimationPackage.this.tryFinishAll();
        }

        public void onAnimationEnded() {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            XLEAssert.assertTrue(XLEAnimationPackage.this.onAnimationEndRunnable != null);
            final int i = this.iterationID;
            ThreadManager.UIThreadPost(new Runnable() { // from class: com.microsoft.xbox.toolkit.anim.XLEAnimationPackage.XLEAnimationEntry.2
                @Override // java.lang.Runnable
                public void run() {
                    if (i == XLEAnimationEntry.this.iterationID) {
                        XLEAnimationEntry.this.finish();
                    }
                }
            });
        }

        public void clearAnimation() {
            this.iterationID++;
            this.animation.clear();
        }

        public void startAnimation() {
            this.animation.start();
        }
    }

    private int getRemainingAnimations() {
        Iterator<XLEAnimationEntry> it = this.animations.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (!it.next().done) {
                i++;
            }
        }
        return i;
    }

    public void tryFinishAll() {
        if (getRemainingAnimations() == 0) {
            XLEAssert.assertTrue(this.running);
            this.running = false;
            this.onAnimationEndRunnable.run();
        }
    }

    public XLEAnimationPackage add(XLEAnimationPackage xLEAnimationPackage) {
        if (xLEAnimationPackage != null) {
            Iterator<XLEAnimationEntry> it = xLEAnimationPackage.animations.iterator();
            while (it.hasNext()) {
                add(it.next().animation);
            }
        }
        return this;
    }

    public void add(XLEAnimation xLEAnimation) {
        this.animations.add(new XLEAnimationEntry(this, xLEAnimation));
    }

    public void clearAnimation() {
        Iterator<XLEAnimationEntry> it = this.animations.iterator();
        while (it.hasNext()) {
            it.next().clearAnimation();
        }
    }

    public void setOnAnimationEndRunnable(Runnable runnable) {
        this.onAnimationEndRunnable = runnable;
    }

    public void startAnimation() {
        XLEAssert.assertTrue(!this.running);
        this.running = true;
        Iterator<XLEAnimationEntry> it = this.animations.iterator();
        while (it.hasNext()) {
            it.next().startAnimation();
        }
    }
}

package com.microsoft.xbox.toolkit.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.XLERValueHelper;

/* loaded from: classes3.dex */
public class SwitchPanel extends LinearLayout {
    private static final int LAYOUT_BLOCK_TIMEOUT_MS = 150;
    private AnimatorListenerAdapter AnimateInListener;
    private AnimatorListenerAdapter AnimateOutListener;
    private final int INVALID_STATE_ID;
    private final int VALID_CONTENT_STATE;
    private boolean active;
    private boolean blocking;
    private View newView;
    private View oldView;
    private int selectedState;
    private boolean shouldAnimate;

    /* loaded from: classes3.dex */
    public interface SwitchPanelChild {
        int getState();
    }

    public SwitchPanel(Context context) {
        super(context);
        this.AnimateInListener = new AnimatorListenerAdapter() { // from class: com.microsoft.xbox.toolkit.ui.SwitchPanel.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                SwitchPanel.this.onAnimateInEnd();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SwitchPanel.this.onAnimateInEnd();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                SwitchPanel.this.onAnimateInStart();
            }
        };
        this.AnimateOutListener = new AnimatorListenerAdapter() { // from class: com.microsoft.xbox.toolkit.ui.SwitchPanel.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                SwitchPanel.this.onAnimateOutEnd();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SwitchPanel.this.onAnimateOutEnd();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                SwitchPanel.this.onAnimateOutStart();
            }
        };
        this.INVALID_STATE_ID = -1;
        this.VALID_CONTENT_STATE = 0;
        this.active = false;
        this.blocking = false;
        this.newView = null;
        this.oldView = null;
        this.shouldAnimate = true;
        throw new UnsupportedOperationException();
    }

    public SwitchPanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.AnimateInListener = new AnimatorListenerAdapter() { // from class: com.microsoft.xbox.toolkit.ui.SwitchPanel.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                SwitchPanel.this.onAnimateInEnd();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SwitchPanel.this.onAnimateInEnd();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                SwitchPanel.this.onAnimateInStart();
            }
        };
        this.AnimateOutListener = new AnimatorListenerAdapter() { // from class: com.microsoft.xbox.toolkit.ui.SwitchPanel.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                SwitchPanel.this.onAnimateOutEnd();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SwitchPanel.this.onAnimateOutEnd();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                SwitchPanel.this.onAnimateOutStart();
            }
        };
        this.INVALID_STATE_ID = -1;
        this.VALID_CONTENT_STATE = 0;
        this.active = false;
        this.blocking = false;
        this.newView = null;
        this.oldView = null;
        this.shouldAnimate = true;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("SwitchPanel"));
        this.selectedState = obtainStyledAttributes.getInteger(XLERValueHelper.getStyleableRValue("SwitchPanel_selectedState"), -1);
        obtainStyledAttributes.recycle();
        if (this.selectedState >= 0) {
            setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
            return;
        }
        throw new IllegalArgumentException("You must specify the selectedState attribute in the xml, and the value must be positive.");
    }

    public void onAnimateInEnd() {
        setBlocking(false);
        View view = this.newView;
        if (view != null) {
            view.setLayerType(0, null);
        }
    }

    public void onAnimateInStart() {
        View view = this.newView;
        if (view != null) {
            view.setLayerType(2, null);
            setBlocking(true);
        }
    }

    public void onAnimateOutEnd() {
        View view = this.oldView;
        if (view != null) {
            view.setVisibility(8);
            this.oldView.setLayerType(0, null);
        }
    }

    public void onAnimateOutStart() {
        View view = this.oldView;
        if (view != null) {
            view.setLayerType(2, null);
            setBlocking(true);
        }
    }

    private void updateVisibility(int i, int i2) {
        View view;
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt instanceof SwitchPanelChild) {
                int state = ((SwitchPanelChild) childAt).getState();
                if (state == i) {
                    this.oldView = childAt;
                } else if (state == i2) {
                    this.newView = childAt;
                } else {
                    childAt.setVisibility(8);
                }
            } else {
                throw new UnsupportedOperationException("All children of SwitchPanel must implement the SwitchPanelChild interface. All other types are not supported and should be removed.");
            }
        }
        if (!this.shouldAnimate || i2 != 0 || (view = this.newView) == null) {
            View view2 = this.oldView;
            if (view2 != null) {
                view2.setVisibility(8);
            }
            View view3 = this.newView;
            if (view3 != null) {
                view3.setAlpha(1.0f);
                this.newView.setVisibility(0);
            }
            requestLayout();
            return;
        }
        view.setAlpha(0.0f);
        this.newView.setVisibility(0);
        requestLayout();
        View view4 = this.oldView;
        if (view4 != null) {
            view4.animate().alpha(0.0f).setDuration(150L).setListener(this.AnimateOutListener);
        }
        this.newView.animate().alpha(1.0f).setDuration(150L).setListener(this.AnimateInListener);
    }

    public int getState() {
        return this.selectedState;
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        updateVisibility(-1, this.selectedState);
    }

    public void setActive(boolean z) {
        this.active = z;
    }

    public void setBlocking(boolean z) {
        if (this.blocking != z) {
            this.blocking = z;
            if (z) {
                BackgroundThreadWaitor.getInstance().setBlocking(BackgroundThreadWaitor.WaitType.ListLayout, 150);
            } else {
                BackgroundThreadWaitor.getInstance().clearBlocking(BackgroundThreadWaitor.WaitType.ListLayout);
            }
        }
    }

    public void setShouldAnimate(boolean z) {
        this.shouldAnimate = z;
    }

    public void setState(int i) {
        if (i >= 0) {
            int i2 = this.selectedState;
            if (i2 != i) {
                this.selectedState = i;
                updateVisibility(i2, i);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("New state must be a positive value.");
    }
}

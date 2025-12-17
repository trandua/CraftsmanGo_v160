package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/* loaded from: classes3.dex */
public class FastProgressBar extends ProgressBar {
    private boolean isEnabled;
    private int visibility;

    public FastProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEnabled(true);
        setVisibility(0);
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onDraw(Canvas canvas) {
        synchronized (this) {
            super.onDraw(canvas);
            postInvalidateDelayed(33L);
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        int i;
        if (this.isEnabled != z) {
            this.isEnabled = z;
            if (!z) {
                this.visibility = getVisibility();
                i = 8;
            } else {
                i = this.visibility;
            }
            super.setVisibility(i);
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        if (this.isEnabled) {
            super.setVisibility(i);
        } else {
            this.visibility = i;
        }
    }
}

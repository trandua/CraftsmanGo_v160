package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xboxtcui.C5528R;

/* loaded from: classes3.dex */
public class XLERootView extends RelativeLayout {
    private static final int UNASSIGNED_ACTIVITY_BODY_ID = -1;
    private View activityBody;
    private int activityBodyIndex;
    private String headerName;
    private boolean isTopLevel;
    private long lastFps;
    private long lastMs;
    private int origPaddingBottom;
    private boolean showTitleBar;

    public XLERootView(Context context) {
        super(context);
        this.isTopLevel = false;
        this.lastFps = 0L;
        this.lastMs = 0L;
        this.showTitleBar = true;
        throw new UnsupportedOperationException();
    }

    public XLERootView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isTopLevel = false;
        this.lastFps = 0L;
        this.lastMs = 0L;
        this.showTitleBar = true;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C5528R.styleable.XLERootView);
        if (obtainStyledAttributes != null) {
            try {
                this.activityBodyIndex = obtainStyledAttributes.getResourceId(0, -1);
                this.isTopLevel = obtainStyledAttributes.getBoolean(2, false);
                this.showTitleBar = obtainStyledAttributes.getBoolean(4, true);
                int i = obtainStyledAttributes.getInt(3, Integer.MIN_VALUE);
                if (i != Integer.MIN_VALUE) {
                    setMinimumWidth((Math.max(0, i) * SystemUtil.getScreenWidth()) / 100);
                }
                this.headerName = obtainStyledAttributes.getString(1);
            } finally {
                obtainStyledAttributes.recycle();
            }
        }
    }

    private void initialize() {
        int i = this.activityBodyIndex;
        if (i != -1) {
            this.activityBody = findViewById(i);
        } else {
            this.activityBody = this;
        }
        this.origPaddingBottom = getPaddingBottom();
        View view = this.activityBody;
        if (view == null || view == this) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(layoutParams);
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.addRule(10);
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            setPadding(getPaddingLeft() + marginLayoutParams.leftMargin, getPaddingTop() + marginLayoutParams.topMargin, getPaddingRight() + marginLayoutParams.rightMargin, this.origPaddingBottom + marginLayoutParams.bottomMargin);
            layoutParams2.setMargins(0, 0, 0, 0);
        }
        removeView(this.activityBody);
        addView(this.activityBody, layoutParams2);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public String getHeaderName() {
        return this.headerName;
    }

    public boolean getIsTopLevel() {
        return this.isTopLevel;
    }

    public boolean getShowTitleBar() {
        return this.showTitleBar;
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        initialize();
    }

    public void setBottomMargin(int i) {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), this.origPaddingBottom + i);
    }
}

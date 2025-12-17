package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;

/* loaded from: classes3.dex */
public class SwitchPanelItem extends FrameLayout implements SwitchPanel.SwitchPanelChild {
    private final int INVALID_STATE_ID;
    private int state;

    public SwitchPanelItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.INVALID_STATE_ID = -1;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("SwitchPanelItem"));
        this.state = obtainStyledAttributes.getInteger(XLERValueHelper.getStyleableRValue("SwitchPanelItem_state"), -1);
        obtainStyledAttributes.recycle();
        if (this.state >= 0) {
            setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
            return;
        }
        throw new IllegalArgumentException("You must specify the state attribute in the xml, and the value must be positive.");
    }

    @Override // com.microsoft.xbox.toolkit.ui.SwitchPanel.SwitchPanelChild
    public int getState() {
        return this.state;
    }
}

package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.microsoft.xboxtcui.C5528R;

/* loaded from: classes3.dex */
public class CustomTypefaceTextView extends TextView {
    public CustomTypefaceTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (isInEditMode()) {
            return;
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C5528R.styleable.CustomTypeface);
        String string = obtainStyledAttributes.getString(1);
        String string2 = obtainStyledAttributes.getString(2);
        if (string2 != null) {
            setText(string2.toUpperCase());
        }
        applyCustomTypeface(context, string);
        obtainStyledAttributes.recycle();
    }

    public CustomTypefaceTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        if (isInEditMode()) {
            return;
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C5528R.styleable.CustomTypeface);
        applyCustomTypeface(context, obtainStyledAttributes.getString(1));
        obtainStyledAttributes.recycle();
    }

    public CustomTypefaceTextView(Context context, String str) {
        super(context);
        applyCustomTypeface(context, str);
    }

    private void applyCustomTypeface(Context context, String str) {
        if (str != null) {
            setTypeface(FontManager.Instance().getTypeface(getContext(), str));
        }
        setCursorVisible(false);
    }

    @Override // android.view.View
    public void setClickable(boolean z) {
        if (z) {
            throw new UnsupportedOperationException("If you want CustomTypefaceTextView to be clickable, use XLEButton instead.");
        }
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        throw new UnsupportedOperationException("If you want CustomTypefaceTextView to be clickable, use XLEButton instead.");
    }
}

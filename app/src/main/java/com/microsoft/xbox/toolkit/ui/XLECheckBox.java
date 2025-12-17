package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
//import com.crafting.minecrafting.lokicraft.R;
import com.craftsman.go.R;
import com.microsoft.xbox.toolkit.ui.util.LibCompat;
import com.microsoft.xboxtcui.C5528R;

/* loaded from: classes3.dex */
public class XLECheckBox extends ViewGroup {
    public final CheckBox checkBox;
    private final TextView subText;
    private final TextView text;

    public XLECheckBox(Context context) {
        super(context);
        this.checkBox = new CheckBox(context);
        this.text = new TextView(context);
        this.subText = new TextView(context);
    }

    public XLECheckBox(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.checkBox = new CheckBox(context, attributeSet);
        this.text = new TextView(context, attributeSet);
        this.subText = new TextView(context, attributeSet);
        initialize(context, attributeSet);
    }

    public XLECheckBox(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.checkBox = new CheckBox(context, attributeSet);
        this.text = new TextView(context, attributeSet);
        this.subText = new TextView(context, attributeSet);
        initialize(context, attributeSet);
    }

    private void initialize(Context context, AttributeSet attributeSet) {
//        this.checkBox.setButtonDrawable(R.color.XboxGray6);
//        addView(this.checkBox, new ViewGroup.LayoutParams(-2, -2));
//        this.text.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.toolkit.ui.XLECheckBox.1
//            @Override // android.view.View.OnClickListener
//            public void onClick(View view) {
//                XLECheckBox.this.checkBox.toggle();
//            }
//        });
//        addView(this.text, new ViewGroup.LayoutParams(-2, -2));
//        addView(this.subText, new ViewGroup.LayoutParams(-2, -2));
//        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C5528R.styleable.XLECheckBox);
//        try {
//            if (!isInEditMode()) {
//                LibCompat.setTextAppearance(this.text, obtainStyledAttributes.getResourceId(4, -1));
//                this.text.setTypeface(FontManager.Instance().getTypeface(context, obtainStyledAttributes.getString(5)));
//                LibCompat.setTextAppearance(this.subText, obtainStyledAttributes.getResourceId(1, -1));
//                this.subText.setTypeface(FontManager.Instance().getTypeface(context, obtainStyledAttributes.getString(2)));
//            }
//            this.text.setText(obtainStyledAttributes.getString(3));
//            this.subText.setText(obtainStyledAttributes.getString(0));
//        } finally {
//            obtainStyledAttributes.recycle();
//        }
        this.checkBox.setButtonDrawable(R.drawable.apptheme_btn_check_holo_light);
        addView(this.checkBox, new ViewGroup.LayoutParams(-2, -2));
        this.text.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.toolkit.ui.XLECheckBox.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                XLECheckBox.this.checkBox.toggle();
            }
        });
        addView(this.text, new ViewGroup.LayoutParams(-2, -2));
        addView(this.subText, new ViewGroup.LayoutParams(-2, -2));
    }

    public CharSequence getSubText() {
        return this.subText.getText();
    }

    public CharSequence getText() {
        return this.text.getText();
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop() + Math.max(this.checkBox.getMeasuredHeight() / 2, this.text.getMeasuredHeight() / 2);
        int measuredWidth = paddingTop - (this.checkBox.getMeasuredWidth() / 2);
        CheckBox checkBox = this.checkBox;
        checkBox.layout(paddingLeft, measuredWidth, checkBox.getMeasuredWidth() + paddingLeft, this.checkBox.getMeasuredHeight() + measuredWidth);
        int measuredWidth2 = paddingLeft + this.checkBox.getMeasuredWidth();
        int measuredHeight = paddingTop - (this.text.getMeasuredHeight() / 2);
        TextView textView = this.text;
        textView.layout(measuredWidth2, measuredHeight, textView.getMeasuredWidth() + measuredWidth2, this.text.getMeasuredHeight() + measuredHeight);
        int measuredHeight2 = measuredHeight + this.text.getMeasuredHeight();
        TextView textView2 = this.subText;
        textView2.layout(measuredWidth2, measuredHeight2, textView2.getMeasuredWidth() + measuredWidth2, this.subText.getMeasuredHeight() + measuredHeight2);
    }

    @Override // android.view.View
    public void onMeasure(int i, int i2) {
//        int size = View.MeasureSpec.getSize(i);
//        int mode = View.MeasureSpec.getMode(i);
//        int i3 = mode == 0 ? 0 : Integer.MIN_VALUE;
//        int size2 = View.MeasureSpec.getSize(i2);
//        int mode2 = View.MeasureSpec.getMode(i2);
//        int i4 = mode2 == 0 ? 0 : Integer.MIN_VALUE;
//        int paddingLeft = getPaddingLeft();
//        int paddingTop = getPaddingTop();
//        int i5 = size2 - paddingTop;
//        this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(Math.max((size - paddingLeft) - getPaddingRight(), 0), i3), View.MeasureSpec.makeMeasureSpec(Math.max(i5 - getPaddingBottom(), 0), i4));
//        int measuredWidth = paddingLeft + this.checkBox.getMeasuredWidth();
//        int i6 = size - measuredWidth;
//        this.text.measure(View.MeasureSpec.makeMeasureSpec(Math.max(i6 - getPaddingRight(), 0), i3), View.MeasureSpec.makeMeasureSpec(Math.max(i5 - getPaddingBottom(), 0), i4));
//        int max = paddingTop + Math.max(this.checkBox.getMeasuredHeight(), this.text.getMeasuredHeight());
//        this.subText.measure(View.MeasureSpec.makeMeasureSpec(Math.max(i6 - getPaddingRight(), 0), i3), View.MeasureSpec.makeMeasureSpec(Math.max((size2 - max) - getPaddingBottom(), 0), i4));
//        int max2 = Math.max(this.text.getMeasuredWidth(), this.subText.getMeasuredWidth());
//        int measuredHeight = this.subText.getMeasuredHeight();
//        int paddingRight = measuredWidth + max2 + getPaddingRight();
//        int paddingBottom = max + measuredHeight + getPaddingBottom();
//        if (mode != 0) {
//            paddingRight = Math.min(paddingRight, size);
//        }
//        if (mode2 != 0) {
//            paddingBottom = Math.min(paddingBottom, size2);
//        }
//        setMeasuredDimension(paddingRight, paddingBottom);
        int size = View.MeasureSpec.getSize(i);
        int mode = View.MeasureSpec.getMode(i);
        int i3 = Integer.MIN_VALUE;
        int i4 = mode == 0 ? 0 : Integer.MIN_VALUE;
        int size2 = View.MeasureSpec.getSize(i2);
        int mode2 = View.MeasureSpec.getMode(i2);
        if (mode2 == 0) {
            i3 = 0;
        }
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int i5 = size2 - paddingTop;
        this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(Math.max((size - paddingLeft) - getPaddingRight(), 0), MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(Math.max(i5 - getPaddingBottom(), 0), MeasureSpec.UNSPECIFIED));
        int measuredWidth = paddingLeft + this.checkBox.getMeasuredWidth();
        int i6 = size - measuredWidth;
        this.text.measure(View.MeasureSpec.makeMeasureSpec(Math.max(i6 - getPaddingRight(), 0), MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(Math.max(i5 - getPaddingBottom(), 0), MeasureSpec.UNSPECIFIED));
        int max = paddingTop + Math.max(this.checkBox.getMeasuredHeight(), this.text.getMeasuredHeight());
        this.subText.measure(View.MeasureSpec.makeMeasureSpec(Math.max(i6 - getPaddingRight(), 0), MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(Math.max((size2 - max) - getPaddingBottom(), 0), MeasureSpec.UNSPECIFIED));
        int max2 = measuredWidth + Math.max(this.text.getMeasuredWidth(), this.subText.getMeasuredWidth());
        int measuredHeight = max + this.subText.getMeasuredHeight();
        int paddingRight = max2 + getPaddingRight();
        int paddingBottom = measuredHeight + getPaddingBottom();
        if (mode != 0) {
            paddingRight = Math.min(paddingRight, size);
        }
        if (mode2 != 0) {
            paddingBottom = Math.min(paddingBottom, size2);
        }
        setMeasuredDimension(paddingRight, paddingBottom);
    }

    public void setChecked(boolean z) {
        this.checkBox.setChecked(z);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.checkBox.setEnabled(z);
        this.text.setEnabled(z);
        this.subText.setEnabled(z);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void setSubText(CharSequence charSequence) {
        this.subText.setText(charSequence);
    }

    public void setText(CharSequence charSequence) {
        this.text.setText(charSequence);
    }

    public void toggle() {
        this.checkBox.toggle();
    }
}

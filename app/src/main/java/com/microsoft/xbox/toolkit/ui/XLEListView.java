package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

/* loaded from: classes3.dex */
public class XLEListView extends ListView {
    public XLEListView(Context context) {
        super(context);
    }

    public XLEListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public XLEListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (IndexOutOfBoundsException unused) {
        }
    }
}

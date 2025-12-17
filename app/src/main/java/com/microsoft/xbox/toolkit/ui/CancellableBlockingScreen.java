package com.microsoft.xbox.toolkit.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.XLERValueHelper;

/* loaded from: classes3.dex */
public class CancellableBlockingScreen extends Dialog {
    private XLEButton cancelButton;
    private View container;
    private TextView statusText;

    public CancellableBlockingScreen(Context context) {
        super(context, XLERValueHelper.getStyleRValue("cancellable_dialog_style"));
        this.cancelButton = (XLEButton) findViewById(XLERValueHelper.getIdRValue("blocking_dialog_cancel"));
        this.container = findViewById(XLERValueHelper.getIdRValue("blocking_dialog_container"));
        this.statusText = (TextView) findViewById(XLERValueHelper.getIdRValue("blocking_dialog_status_text"));
        setCancelable(false);
        setOnCancelListener(null);
        requestWindowFeature(1);
        setContentView(XLERValueHelper.getLayoutRValue("cancellable_blocking_dialog"));
    }

    public void setCancelButtonAction(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            this.cancelButton.setOnClickListener(null);
        }
        this.cancelButton.setOnClickListener(onClickListener);
    }

    public void setMessage(CharSequence charSequence) {
        this.statusText.setText(charSequence);
    }

    public void show(Context context, CharSequence charSequence) {
        boolean isShowing = isShowing();
        setMessage(charSequence);
        show();
        if (isShowing) {
            return;
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setStartOffset(1000L);
        alphaAnimation.setDuration(1000L);
        this.container.startAnimation(alphaAnimation);
    }
}

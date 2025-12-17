package com.microsoft.xboxtcui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
//import com.crafting.minecrafting.lokicraft.R;
import com.craftsman.go.R;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xboxtcui.C5528R;

/* loaded from: classes3.dex */
public class XboxTcuiWindowDialog extends Dialog {
    private DetachedCallback detachedCallback;
    private final XboxTcuiWindow xboxTcuiWindow;

    /* loaded from: classes3.dex */
    public interface DetachedCallback {
        void onDetachedFromWindow();
    }

    public XboxTcuiWindowDialog(Activity activity, Class<? extends ScreenLayout> cls, ActivityParameters activityParameters) {
        super(activity, C5528R.style.TcuiDialog);
        this.xboxTcuiWindow = new XboxTcuiWindow(activity, cls, activityParameters);
    }

    private void addCloseButton() {
        FrameLayout frameLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 8388661;
        XLEButton xLEButton = new XLEButton(getContext());
        xLEButton.setPadding(60, 0, 0, 0);
        xLEButton.setBackgroundResource(R.drawable.common_button_background);
        xLEButton.setText(C5528R.string.ic_Close);
        xLEButton.setTextColor(-1);
        xLEButton.setTextSize(2, 14.0f);
        xLEButton.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/SegXboxSymbol.ttf"));
        xLEButton.setContentDescription(getContext().getString(C5528R.string.TextInput_Confirm));
        xLEButton.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xboxtcui.XboxTcuiWindowDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    NavigationManager.getInstance().PopAllScreens();
                } catch (XLEException e) {
                    e.printStackTrace();
                }
            }
        });
        xLEButton.setOnKeyListener(NavigationManager.getInstance());
        frameLayout.addView(xLEButton);
        addContentView(frameLayout, layoutParams);
    }

    @Override // android.app.Dialog
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setLayout(-1, -1);
        getWindow().setFlags(1024, 1024);
        this.xboxTcuiWindow.onCreate(bundle);
        setContentView(this.xboxTcuiWindow);
        addCloseButton();
        NavigationManager.getInstance().setOnNavigatedListener(new NavigationManager.OnNavigatedListener() { // from class: com.microsoft.xboxtcui.XboxTcuiWindowDialog.2
            @Override // com.microsoft.xbox.toolkit.ui.NavigationManager.OnNavigatedListener
            public void onPageRestarted(ScreenLayout screenLayout) {
            }

            @Override // com.microsoft.xbox.toolkit.ui.NavigationManager.OnNavigatedListener
            public void onPageNavigated(ScreenLayout screenLayout, ScreenLayout screenLayout2) {
                if (screenLayout2 == null) {
                    XboxTcuiWindowDialog.this.dismiss();
                }
            }
        });
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        DetachedCallback detachedCallback = this.detachedCallback;
        if (detachedCallback != null) {
            detachedCallback.onDetachedFromWindow();
        }
        super.onDetachedFromWindow();
    }

    @Override // android.app.Dialog
    public void onStart() {
        this.xboxTcuiWindow.onStart();
    }

    @Override // android.app.Dialog
    public void onStop() {
        this.xboxTcuiWindow.onStop();
    }

    public void setDetachedCallback(DetachedCallback detachedCallback) {
        this.detachedCallback = detachedCallback;
    }
}

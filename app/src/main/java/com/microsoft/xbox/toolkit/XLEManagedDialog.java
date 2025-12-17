package com.microsoft.xbox.toolkit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import com.microsoft.xbox.toolkit.IXLEManagedDialog;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;

/* loaded from: classes3.dex */
public class XLEManagedDialog extends Dialog implements IXLEManagedDialog {
    protected static final String BODY_ANIMATION_NAME = "Dialog";
    protected String bodyAnimationName;
    final Runnable callAfterAnimationIn;
    final Runnable callAfterAnimationOut;
    protected View dialogBody;
    private IXLEManagedDialog.DialogType dialogType;
    protected Runnable onAnimateOutCompletedRunable;

    @Override // com.microsoft.xbox.toolkit.IXLEManagedDialog
    public Dialog getDialog() {
        return this;
    }

    public XLEManagedDialog(Context context) {
        super(context);
        this.bodyAnimationName = BODY_ANIMATION_NAME;
        this.callAfterAnimationIn = new Runnable() { // from class: com.microsoft.xbox.toolkit.XLEManagedDialog.1
            @Override // java.lang.Runnable
            public void run() {
                XLEManagedDialog.this.OnAnimationInEnd();
            }
        };
        this.callAfterAnimationOut = new Runnable() { // from class: com.microsoft.xbox.toolkit.XLEManagedDialog.2
            @Override // java.lang.Runnable
            public void run() {
                XLEManagedDialog.this.OnAnimationOutEnd();
            }
        };
        this.dialogBody = null;
        this.dialogType = IXLEManagedDialog.DialogType.NORMAL;
        this.onAnimateOutCompletedRunable = null;
    }

    public XLEManagedDialog(Context context, int i) {
        super(context, i);
        this.bodyAnimationName = BODY_ANIMATION_NAME;
        this.callAfterAnimationIn = new Runnable() { // from class: com.microsoft.xbox.toolkit.XLEManagedDialog.1
            @Override // java.lang.Runnable
            public void run() {
                XLEManagedDialog.this.OnAnimationInEnd();
            }
        };
        this.callAfterAnimationOut = new Runnable() { // from class: com.microsoft.xbox.toolkit.XLEManagedDialog.2
            @Override // java.lang.Runnable
            public void run() {
                XLEManagedDialog.this.OnAnimationOutEnd();
            }
        };
        this.dialogBody = null;
        this.dialogType = IXLEManagedDialog.DialogType.NORMAL;
        this.onAnimateOutCompletedRunable = null;
    }

    protected XLEManagedDialog(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.bodyAnimationName = BODY_ANIMATION_NAME;
        this.callAfterAnimationIn = new Runnable() { // from class: com.microsoft.xbox.toolkit.XLEManagedDialog.1
            @Override // java.lang.Runnable
            public void run() {
                XLEManagedDialog.this.OnAnimationInEnd();
            }
        };
        this.callAfterAnimationOut = new Runnable() { // from class: com.microsoft.xbox.toolkit.XLEManagedDialog.2
            @Override // java.lang.Runnable
            public void run() {
                XLEManagedDialog.this.OnAnimationOutEnd();
            }
        };
        this.dialogBody = null;
        this.dialogType = IXLEManagedDialog.DialogType.NORMAL;
        this.onAnimateOutCompletedRunable = null;
    }

    protected static boolean isKindle() {
        return SystemUtil.isKindle();
    }

    public void OnAnimationInEnd() {
        NavigationManager.getInstance().setAnimationBlocking(false);
    }

    public void OnAnimationOutEnd() {
        NavigationManager.getInstance().setAnimationBlocking(false);
        super.dismiss();
        Runnable runnable = this.onAnimateOutCompletedRunable;
        if (runnable != null) {
            try {
                runnable.run();
            } catch (Exception unused) {
            }
        }
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        if (!isShowing()) {
            super.dismiss();
            return;
        }
        XLEAnimationPackage animateOut = getAnimateOut();
        if (getDialogBody() == null || animateOut == null) {
            Runnable runnable = this.onAnimateOutCompletedRunable;
            if (runnable != null) {
                runnable.run();
            }
            super.dismiss();
            return;
        }
        NavigationManager.getInstance().setAnimationBlocking(true);
        animateOut.setOnAnimationEndRunnable(this.callAfterAnimationOut);
        animateOut.startAnimation();
    }

    public void forceKindleRespectDimOptions() {
        new Handler().postDelayed(new Runnable() { // from class: com.microsoft.xbox.toolkit.XLEManagedDialog.3
            @Override // java.lang.Runnable
            public void run() {
                XLEManagedDialog.this.getWindow().addFlags(2);
            }
        }, 100L);
    }

    public XLEAnimationPackage getAnimateIn() {
        XLEAnimation bodyAnimation = getBodyAnimation(MAAS.MAASAnimationType.ANIMATE_IN, false);
        if (bodyAnimation == null) {
            return null;
        }
        XLEAnimationPackage xLEAnimationPackage = new XLEAnimationPackage();
        xLEAnimationPackage.add(bodyAnimation);
        return xLEAnimationPackage;
    }

    public XLEAnimationPackage getAnimateOut() {
        XLEAnimation bodyAnimation = getBodyAnimation(MAAS.MAASAnimationType.ANIMATE_OUT, true);
        if (bodyAnimation == null) {
            return null;
        }
        XLEAnimationPackage xLEAnimationPackage = new XLEAnimationPackage();
        xLEAnimationPackage.add(bodyAnimation);
        return xLEAnimationPackage;
    }

    public XLEAnimation getBodyAnimation(MAAS.MAASAnimationType mAASAnimationType, boolean z) {
        if (getDialogBody() != null) {
            return ((XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(this.bodyAnimationName)).compile(mAASAnimationType, z, getDialogBody());
        }
        return null;
    }

    public String getBodyAnimationName() {
        return this.bodyAnimationName;
    }

    public View getDialogBody() {
        return this.dialogBody;
    }

    @Override // com.microsoft.xbox.toolkit.IXLEManagedDialog
    public IXLEManagedDialog.DialogType getDialogType() {
        return this.dialogType;
    }

    public void makeFullScreen() {
        getWindow().setLayout(-1, -2);
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        XLEAnimationPackage animateIn = getAnimateIn();
        if (getDialogBody() == null || animateIn == null) {
            return;
        }
        NavigationManager.getInstance().setAnimationBlocking(true);
        animateIn.setOnAnimationEndRunnable(this.callAfterAnimationIn);
        animateIn.startAnimation();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        if (z) {
            return;
        }
        safeDismiss();
    }

    @Override // com.microsoft.xbox.toolkit.IXLEManagedDialog
    public void quickDismiss() {
        super.dismiss();
    }

    @Override // com.microsoft.xbox.toolkit.IXLEManagedDialog
    public void safeDismiss() {
        DialogManager.getInstance().dismissManagedDialog(this);
    }

    public void setAnimateOutRunnable(Runnable runnable) {
        this.onAnimateOutCompletedRunable = runnable;
    }

    public void setBodyAnimationName(String str) {
        this.bodyAnimationName = str;
    }

    @Override // com.microsoft.xbox.toolkit.IXLEManagedDialog
    public void setDialogType(IXLEManagedDialog.DialogType dialogType) {
        this.dialogType = dialogType;
    }
}

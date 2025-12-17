package com.microsoft.xbox.toolkit;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;
import com.microsoft.xbox.toolkit.IXLEManagedDialog;
import com.microsoft.xbox.toolkit.ui.BlockingScreen;
import com.microsoft.xbox.toolkit.ui.CancellableBlockingScreen;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.Stack;

/* loaded from: classes3.dex */
public abstract class DialogManagerBase implements IProjectSpecificDialogManager {
    private BlockingScreen blockingSpinner;
    public CancellableBlockingScreen cancelableBlockingDialog;
    private Stack<IXLEManagedDialog> dialogStack = new Stack<>();
    private boolean isEnabled;
    private Toast visibleToast;

    public boolean shouldDismissAllBeforeOpeningADialog() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DialogManagerBase() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
    }

    private XLEManagedAlertDialog buildDialog(String str, String str2, String str3, final Runnable runnable, String str4, final Runnable runnable2) {
        final XLEManagedAlertDialog xLEManagedAlertDialog = new XLEManagedAlertDialog(XboxTcuiSdk.getActivity());
        xLEManagedAlertDialog.setTitle(str);
        xLEManagedAlertDialog.setMessage(str2);
        xLEManagedAlertDialog.setButton(-1, str3, new DialogInterface.OnClickListener() { // from class: com.microsoft.xbox.toolkit.DialogManagerBase.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ThreadManager.UIThreadPost(runnable);
            }
        });
        final Runnable runnable3 = new Runnable() { // from class: com.microsoft.xbox.toolkit.DialogManagerBase.2
            @Override // java.lang.Runnable
            public void run() {
                DialogManagerBase.this.dismissManagedDialog(xLEManagedAlertDialog);
                Runnable runnable4 = runnable2;
                if (runnable4 != null) {
                    runnable4.run();
                }
            }
        };
        xLEManagedAlertDialog.setButton(-2, str4, new DialogInterface.OnClickListener() { // from class: com.microsoft.xbox.toolkit.DialogManagerBase.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ThreadManager.UIThreadPost(runnable3);
            }
        });
        if (str4 == null || str4.length() == 0) {
            xLEManagedAlertDialog.setCancelable(false);
        } else {
            xLEManagedAlertDialog.setCancelable(true);
            xLEManagedAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.microsoft.xbox.toolkit.DialogManagerBase.4
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface dialogInterface) {
                    ThreadManager.UIThreadPost(runnable3);
                }
            });
        }
        return xLEManagedAlertDialog;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void addManagedDialog(IXLEManagedDialog iXLEManagedDialog) {
        if (this.isEnabled) {
            this.dialogStack.push(iXLEManagedDialog);
            iXLEManagedDialog.getDialog().show();
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void dismissBlocking() {
        BlockingScreen blockingScreen = this.blockingSpinner;
        if (blockingScreen != null) {
            blockingScreen.dismiss();
            this.blockingSpinner = null;
        }
        CancellableBlockingScreen cancellableBlockingScreen = this.cancelableBlockingDialog;
        if (cancellableBlockingScreen != null) {
            cancellableBlockingScreen.dismiss();
            this.cancelableBlockingDialog = null;
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void dismissManagedDialog(IXLEManagedDialog iXLEManagedDialog) {
        if (this.isEnabled) {
            this.dialogStack.remove(iXLEManagedDialog);
            iXLEManagedDialog.getDialog().dismiss();
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void dismissToast() {
        Toast toast = this.visibleToast;
        if (toast != null) {
            toast.cancel();
            this.visibleToast = null;
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void dismissTopNonFatalAlert() {
        if (this.dialogStack.size() <= 0 || this.dialogStack.peek().getDialogType() == IXLEManagedDialog.DialogType.FATAL) {
            return;
        }
        this.dialogStack.pop().getDialog().dismiss();
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void forceDismissAlerts() {
        while (this.dialogStack.size() > 0) {
            this.dialogStack.pop().quickDismiss();
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void forceDismissAll() {
        dismissToast();
        forceDismissAlerts();
        dismissBlocking();
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public boolean getIsBlocking() {
        BlockingScreen blockingScreen = this.blockingSpinner;
        if (blockingScreen == null || !blockingScreen.isShowing()) {
            CancellableBlockingScreen cancellableBlockingScreen = this.cancelableBlockingDialog;
            return cancellableBlockingScreen != null && cancellableBlockingScreen.isShowing();
        }
        return true;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public Dialog getVisibleDialog() {
        if (this.dialogStack.isEmpty()) {
            return null;
        }
        return this.dialogStack.peek().getDialog();
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void onDialogStopped(IXLEManagedDialog iXLEManagedDialog) {
        this.dialogStack.remove(iXLEManagedDialog);
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void setBlocking(boolean z, String str) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.isEnabled) {
            if (z) {
                if (this.blockingSpinner == null) {
                    this.blockingSpinner = new BlockingScreen(XboxTcuiSdk.getActivity());
                }
                this.blockingSpinner.show(XboxTcuiSdk.getActivity(), str);
                return;
            }
            BlockingScreen blockingScreen = this.blockingSpinner;
            if (blockingScreen != null) {
                blockingScreen.dismiss();
                this.blockingSpinner = null;
            }
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void setCancelableBlocking(boolean z, String str, final Runnable runnable) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.isEnabled) {
            if (z) {
                if (this.cancelableBlockingDialog == null) {
                    CancellableBlockingScreen cancellableBlockingScreen = new CancellableBlockingScreen(XboxTcuiSdk.getActivity());
                    this.cancelableBlockingDialog = cancellableBlockingScreen;
                    cancellableBlockingScreen.setCancelButtonAction(new View.OnClickListener() { // from class: com.microsoft.xbox.toolkit.DialogManagerBase.5
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            DialogManagerBase.this.cancelableBlockingDialog.dismiss();
                            DialogManagerBase.this.cancelableBlockingDialog = null;
                            runnable.run();
                        }
                    });
                }
                this.cancelableBlockingDialog.show(XboxTcuiSdk.getActivity(), str);
                return;
            }
            CancellableBlockingScreen cancellableBlockingScreen2 = this.cancelableBlockingDialog;
            if (cancellableBlockingScreen2 != null) {
                cancellableBlockingScreen2.dismiss();
                this.cancelableBlockingDialog = null;
            }
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void setEnabled(boolean z) {
        if (this.isEnabled != z) {
            this.isEnabled = z;
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void showFatalAlertDialog(String str, String str2, String str3, Runnable runnable) {
        forceDismissAll();
        if (this.isEnabled) {
            XLEManagedAlertDialog buildDialog = buildDialog(str, str2, str3, runnable, null, null);
            buildDialog.setDialogType(IXLEManagedDialog.DialogType.FATAL);
            this.dialogStack.push(buildDialog);
            buildDialog.show();
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void showManagedDialog(IXLEManagedDialog iXLEManagedDialog) {
        if (shouldDismissAllBeforeOpeningADialog()) {
            forceDismissAll();
        }
        if (!this.isEnabled || XboxTcuiSdk.getActivity() == null || XboxTcuiSdk.getActivity().isFinishing()) {
            return;
        }
        this.dialogStack.push(iXLEManagedDialog);
        try {
            iXLEManagedDialog.getDialog().show();
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message == null || !message.contains("Adding window failed")) {
                throw e;
            }
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void showNonFatalAlertDialog(String str, String str2, String str3, Runnable runnable) {
        if (this.isEnabled) {
            XLEManagedAlertDialog buildDialog = buildDialog(str, str2, str3, runnable, null, null);
            buildDialog.setDialogType(IXLEManagedDialog.DialogType.NON_FATAL);
            this.dialogStack.push(buildDialog);
            buildDialog.show();
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void showOkCancelDialog(String str, String str2, String str3, Runnable runnable, String str4, Runnable runnable2) {
        XLEAssert.assertNotNull("You must supply cancel text if this is not a must-act dialog.", str4);
        if (this.dialogStack.size() > 0 || !this.isEnabled || XboxTcuiSdk.getActivity() == null || XboxTcuiSdk.getActivity().isFinishing()) {
            return;
        }
        XLEManagedAlertDialog buildDialog = buildDialog(str, str2, str3, runnable, str4, runnable2);
        buildDialog.setDialogType(IXLEManagedDialog.DialogType.NORMAL);
        this.dialogStack.push(buildDialog);
        buildDialog.show();
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void showToast(int i) {
        dismissToast();
        if (this.isEnabled) {
            Toast makeText = Toast.makeText(XboxTcuiSdk.getActivity(), i, 1);
            this.visibleToast = makeText;
            makeText.show();
        }
    }
}

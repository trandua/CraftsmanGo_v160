package com.microsoft.xbox.toolkit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import com.microsoft.xbox.toolkit.IXLEManagedDialog;

/* loaded from: classes3.dex */
public class XLEManagedAlertDialog extends AlertDialog implements IXLEManagedDialog {
    private IXLEManagedDialog.DialogType dialogType;

    @Override // com.microsoft.xbox.toolkit.IXLEManagedDialog
    public Dialog getDialog() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public XLEManagedAlertDialog(Context context) {
        super(context);
        this.dialogType = IXLEManagedDialog.DialogType.NORMAL;
    }

    @Override // com.microsoft.xbox.toolkit.IXLEManagedDialog
    public IXLEManagedDialog.DialogType getDialogType() {
        return this.dialogType;
    }

    @Override // android.app.Dialog
    public void onStop() {
        super.onStop();
        DialogManager.getInstance().onDialogStopped(this);
    }

    @Override // com.microsoft.xbox.toolkit.IXLEManagedDialog
    public void quickDismiss() {
        super.dismiss();
    }

    @Override // com.microsoft.xbox.toolkit.IXLEManagedDialog
    public void safeDismiss() {
        DialogManager.getInstance().dismissManagedDialog(this);
    }

    @Override // com.microsoft.xbox.toolkit.IXLEManagedDialog
    public void setDialogType(IXLEManagedDialog.DialogType dialogType) {
        this.dialogType = dialogType;
    }
}

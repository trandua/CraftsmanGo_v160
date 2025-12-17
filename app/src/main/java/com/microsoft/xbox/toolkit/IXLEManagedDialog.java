package com.microsoft.xbox.toolkit;

import android.app.Dialog;

/* loaded from: classes3.dex */
public interface IXLEManagedDialog {

    /* loaded from: classes3.dex */
    public enum DialogType {
        FATAL,
        NON_FATAL,
        NORMAL
    }

    Dialog getDialog();

    DialogType getDialogType();

    void quickDismiss();

    void safeDismiss();

    void setDialogType(DialogType dialogType);
}

package com.microsoft.xbox.xle.app;

import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.DialogManagerBase;
import com.microsoft.xbox.toolkit.IProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog;
import com.microsoft.xbox.xle.viewmodel.ChangeFriendshipDialogViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.XboxTcuiSdk;

/* loaded from: classes3.dex */
public class SGProjectSpecificDialogManager extends DialogManagerBase {
    private static IProjectSpecificDialogManager instance = new SGProjectSpecificDialogManager();
    private ChangeFriendshipDialog changeFriendshipDialog;

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void onApplicationResume() {
    }

    @Override // com.microsoft.xbox.toolkit.DialogManagerBase
    public boolean shouldDismissAllBeforeOpeningADialog() {
        return false;
    }

    private SGProjectSpecificDialogManager() {
    }

    public static IProjectSpecificDialogManager getInstance() {
        return instance;
    }

    public static SGProjectSpecificDialogManager getProjectSpecificInstance() {
        return (SGProjectSpecificDialogManager) DialogManager.getInstance().getManager();
    }

    public void dismissChangeFriendshipDialog() {
        ChangeFriendshipDialog changeFriendshipDialog = this.changeFriendshipDialog;
        if (changeFriendshipDialog != null) {
            dismissManagedDialog(changeFriendshipDialog);
            this.changeFriendshipDialog = null;
        }
    }

    @Override // com.microsoft.xbox.toolkit.DialogManagerBase, com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void forceDismissAll() {
        super.forceDismissAll();
        dismissChangeFriendshipDialog();
    }

    public void notifyChangeFriendshipDialogAsyncTaskCompleted() {
        ChangeFriendshipDialog changeFriendshipDialog = this.changeFriendshipDialog;
        if (changeFriendshipDialog != null) {
            changeFriendshipDialog.reportAsyncTaskCompleted();
        }
    }

    public void notifyChangeFriendshipDialogAsyncTaskFailed(String str) {
        ChangeFriendshipDialog changeFriendshipDialog = this.changeFriendshipDialog;
        if (changeFriendshipDialog != null) {
            changeFriendshipDialog.reportAsyncTaskFailed(str);
        }
    }

    public void notifyChangeFriendshipDialogUpdateView() {
        ChangeFriendshipDialog changeFriendshipDialog = this.changeFriendshipDialog;
        if (changeFriendshipDialog != null) {
            changeFriendshipDialog.updateView();
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDialogManager
    public void onApplicationPause() {
        forceDismissAll();
    }

    public void showChangeFriendshipDialog(ChangeFriendshipDialogViewModel changeFriendshipDialogViewModel, ViewModelBase viewModelBase) {
        ChangeFriendshipDialog changeFriendshipDialog = this.changeFriendshipDialog;
        if (changeFriendshipDialog != null) {
            changeFriendshipDialog.setVm(changeFriendshipDialogViewModel);
            this.changeFriendshipDialog.getDialog().show();
            return;
        }
        ChangeFriendshipDialog changeFriendshipDialog2 = new ChangeFriendshipDialog(XboxTcuiSdk.getActivity(), changeFriendshipDialogViewModel, viewModelBase);
        this.changeFriendshipDialog = changeFriendshipDialog2;
        addManagedDialog(changeFriendshipDialog2);
    }
}

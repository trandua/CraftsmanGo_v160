package com.microsoft.xbox.xle.app.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
//import com.crafting.minecrafting.lokicraft.R;
import com.craftsman.go.R;
import com.microsoft.xbox.telemetry.helpers.UTCChangeRelationship;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLEManagedDialog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.FastProgressBar;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLECheckBox;
import com.microsoft.xbox.toolkit.ui.XLEClickableLayout;
import com.microsoft.xbox.toolkit.ui.XLEUniversalImageView;
import com.microsoft.xbox.xle.app.ImageUtil;
import com.microsoft.xbox.xle.app.SGProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.ChangeFriendshipDialogViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.XboxTcuiSdk;

/* loaded from: classes3.dex */
public class ChangeFriendshipDialog extends XLEManagedDialog {
    private RadioButton addFavorite;
    private RadioButton addFriend;
    private XLEButton cancelButton;
    public SwitchPanel changeFriendshipSwitchPanel;
    private XLEButton confirmButton;
    public ChangeFriendshipDialogViewModel f12626vm;
    private CustomTypefaceTextView favoriteIconView;
    private CustomTypefaceTextView gamertag;
    private FastProgressBar overlayLoadingIndicator;
    private ViewModelBase previousVM;
    private TextView profileAccountTier;
    private CustomTypefaceTextView profileGamerScore;
    private XLEUniversalImageView profilePic;
    private CustomTypefaceTextView realName;
    private XLEClickableLayout removeFriendLayout;
    private XLECheckBox shareRealNameCheckbox;

    public String getActivityName() {
        return "ChangeRelationship Info";
    }

    @Override // android.app.Dialog
    public void onStop() {
    }

    public ChangeFriendshipDialog(Context context, ChangeFriendshipDialogViewModel changeFriendshipDialogViewModel, ViewModelBase viewModelBase) {
        super(context, R.style.TcuiDialog);
        this.previousVM = viewModelBase;
        this.f12626vm = changeFriendshipDialogViewModel;
    }

    public void dismissSelf() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).dismissChangeFriendshipDialog();
    }

    private void setDialogLoadingView() {
        XLEUtil.updateVisibilityIfNotNull(this.overlayLoadingIndicator, 0);
        XLEButton xLEButton = this.confirmButton;
        if (xLEButton != null) {
            xLEButton.setEnabled(false);
        }
        XLEButton xLEButton2 = this.cancelButton;
        if (xLEButton2 != null) {
            xLEButton2.setEnabled(false);
        }
    }

    private void setDialogValidContentView() {
        XLEUtil.updateVisibilityIfNotNull(this.overlayLoadingIndicator, 8);
        XLEButton xLEButton = this.confirmButton;
        if (xLEButton != null) {
            xLEButton.setEnabled(true);
        }
        XLEButton xLEButton2 = this.cancelButton;
        if (xLEButton2 != null) {
            xLEButton2.setEnabled(true);
        }
    }

    public void closeDialog() {
        dismissSelf();
        this.previousVM.load(true);
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        dismissSelf();
    }

    @Override // android.app.Dialog
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setLayout(-1, -1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.change_friendship_dialog);
        this.profilePic = (XLEUniversalImageView) findViewById(R.id.change_friendship_profile_pic);
        this.gamertag = (CustomTypefaceTextView) findViewById(R.id.gamertag_text);
        this.realName = (CustomTypefaceTextView) findViewById(R.id.realname_text);
        this.profileAccountTier = (TextView) findViewById(R.id.peoplehub_info_gamerscore_icon);
        this.profileGamerScore = (CustomTypefaceTextView) findViewById(R.id.peoplehub_info_gamerscore);
        this.addFriend = (RadioButton) findViewById(R.id.add_as_friend);
        this.addFavorite = (RadioButton) findViewById(R.id.add_as_favorite);
        this.shareRealNameCheckbox = (XLECheckBox) findViewById(R.id.share_real_name_checkbox);
        this.confirmButton = (XLEButton) findViewById(R.id.submit_button);
        this.cancelButton = (XLEButton) findViewById(R.id.cancel_button);
        this.changeFriendshipSwitchPanel = (SwitchPanel) findViewById(R.id.change_friendship_switch_panel);
        this.removeFriendLayout = (XLEClickableLayout) findViewById(R.id.remove_friend_btn_layout);
        this.favoriteIconView = (CustomTypefaceTextView) findViewById(R.id.people_favorites_icon);
        this.overlayLoadingIndicator = (FastProgressBar) findViewById(R.id.overlay_loading_indicator);
        FrameLayout frameLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 5;
        XLEButton xLEButton = new XLEButton(getContext());
        xLEButton.setPadding(60, 0, 0, 0);
        xLEButton.setBackgroundResource(R.drawable.common_button_background);
        xLEButton.setText(R.string.ic_Close);
        xLEButton.setTextColor(-1);
        xLEButton.setTextSize(2, 14.0f);
        xLEButton.setTypeFace("fonts/SegXboxSymbol.ttf");
        xLEButton.setContentDescription(getContext().getResources().getString(R.string.TextInput_Confirm));
        xLEButton.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    ChangeFriendshipDialog.this.dismiss();
                    NavigationManager.getInstance().PopAllScreens();
                } catch (XLEException unused) {
                }
            }
        });
        xLEButton.setOnKeyListener(new View.OnKeyListener() { // from class: com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog.2
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == 4 && keyEvent.getAction() == 1) {
                    ChangeFriendshipDialog.this.dismiss();
                    return true;
                }
                return false;
            }
        });
        frameLayout.addView(xLEButton);
        addContentView(frameLayout, layoutParams);
    }

    @Override // android.app.Dialog
    public void onStart() {
        this.f12626vm.load();
        updateView();
        this.changeFriendshipSwitchPanel.setBackgroundColor(this.f12626vm.getPreferredColor());
        UTCChangeRelationship.trackChangeRelationshipView(getActivityName(), this.f12626vm.getXuid());
    }

    public void reportAsyncTaskCompleted() {
        if (this.f12626vm.isBusy() || this.changeFriendshipSwitchPanel.getState() != 1) {
            return;
        }
        closeDialog();
    }

    public void reportAsyncTaskFailed(String str) {
        if (this.changeFriendshipSwitchPanel.getState() == 1) {
            this.changeFriendshipSwitchPanel.setState(0);
            Toast.makeText(XboxTcuiSdk.getActivity(), str, Toast.LENGTH_SHORT).show();
        }
        updateView();
    }

    public void setVm(ChangeFriendshipDialogViewModel changeFriendshipDialogViewModel) {
        this.f12626vm = changeFriendshipDialogViewModel;
    }

    public void updateShareIdentityCheckboxStatus() {
        String callerShareRealNameStatus = this.f12626vm.getCallerShareRealNameStatus();
        if (callerShareRealNameStatus != null) {
            boolean equalsIgnoreCase = callerShareRealNameStatus.equalsIgnoreCase("Blocked");
            this.shareRealNameCheckbox.setVisibility(equalsIgnoreCase ? View.GONE : View.VISIBLE);
            if (equalsIgnoreCase) {
                return;
            }
            boolean isNullOrEmpty = JavaUtil.isNullOrEmpty(this.f12626vm.getRealName());
            if (callerShareRealNameStatus.compareToIgnoreCase("Everyone") == 0) {
                this.shareRealNameCheckbox.setChecked(true);
                this.f12626vm.setInitialRealNameSharingState(true);
                this.shareRealNameCheckbox.setEnabled(false);
                this.shareRealNameCheckbox.setSubText(XboxTcuiSdk.getResources().getString(R.string.ChangeRelationship_Checkbox_Subtext_ShareRealName_Everyone));
            }
            if (callerShareRealNameStatus.compareToIgnoreCase("PeopleOnMyList") == 0) {
                this.shareRealNameCheckbox.setChecked(true);
                this.f12626vm.setInitialRealNameSharingState(true);
                this.shareRealNameCheckbox.setEnabled(false);
                this.shareRealNameCheckbox.setSubText(XboxTcuiSdk.getResources().getString(R.string.ChangeRelationship_Checkbox_Subtext_ShareRealName_Friends));
            }
            if (callerShareRealNameStatus.compareToIgnoreCase("FriendCategoryShareIdentity") == 0) {
                if (this.f12626vm.getIsFollowing()) {
                    if (this.f12626vm.getCallerMarkedTargetAsIdentityShared()) {
                        this.shareRealNameCheckbox.setChecked(true);
                        this.f12626vm.setInitialRealNameSharingState(true);
                        this.shareRealNameCheckbox.setSubText(String.format(XboxTcuiSdk.getResources().getString(R.string.ChangeRelationship_Checkbox_Subtext_ShareRealName), this.f12626vm.getGamerTag()));
                        this.shareRealNameCheckbox.setEnabled(true);
                    }
                } else if (!isNullOrEmpty) {
                    this.shareRealNameCheckbox.setChecked(true);
                    this.f12626vm.setInitialRealNameSharingState(true);
                    this.f12626vm.setShouldAddUserToShareIdentityList(true);
                    this.shareRealNameCheckbox.setSubText(String.format(XboxTcuiSdk.getResources().getString(R.string.ChangeRelationship_Checkbox_Subtext_ShareRealName), this.f12626vm.getGamerTag()));
                    this.shareRealNameCheckbox.setEnabled(true);
                }
                this.shareRealNameCheckbox.setChecked(false);
                this.f12626vm.setInitialRealNameSharingState(false);
                this.shareRealNameCheckbox.setSubText(String.format(XboxTcuiSdk.getResources().getString(R.string.ChangeRelationship_Checkbox_Subtext_ShareRealName), this.f12626vm.getGamerTag()));
                this.shareRealNameCheckbox.setEnabled(true);
            }
        }
    }

    public void updateView() {
        if (this.f12626vm.getViewModelState() == ListState.ValidContentState) {
            setDialogValidContentView();
            XLEUtil.updateAndShowTextViewUnlessEmpty(this.gamertag, this.f12626vm.getGamerTag());
            XLEUniversalImageView xLEUniversalImageView = this.profilePic;
            if (xLEUniversalImageView != null) {
                xLEUniversalImageView.setImageURI2(ImageUtil.getMedium(this.f12626vm.getGamerPicUrl()), R.drawable.gamerpic_missing, R.drawable.gamerpic_missing);
            }
            XLEUtil.updateAndShowTextViewUnlessEmpty(this.realName, this.f12626vm.getRealName());
            XLEUtil.updateVisibilityIfNotNull(this.favoriteIconView, this.f12626vm.getIsFavorite() ? 0 : 4);
            if (this.f12626vm.getIsFavorite()) {
                this.favoriteIconView.setTextColor(getContext().getResources().getColor(R.color.XboxGreen));
            }
            String gamerScore = this.f12626vm.getGamerScore();
            if (gamerScore != null && !gamerScore.equalsIgnoreCase(String.valueOf(0))) {
                XLEUtil.updateAndShowTextViewUnlessEmpty(this.profileGamerScore, this.f12626vm.getGamerScore());
                XLEUtil.updateVisibilityIfNotNull(this.profileAccountTier, 0);
            }
            if (this.addFriend != null) {
                if (!this.f12626vm.getIsFollowing()) {
                    this.f12626vm.setShouldAddUserToFriendList(true);
                }
                this.addFriend.setChecked(true);
                this.addFriend.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (!ChangeFriendshipDialog.this.f12626vm.getIsFollowing()) {
                            ChangeFriendshipDialog.this.f12626vm.setShouldAddUserToFriendList(true);
                        }
                        if (ChangeFriendshipDialog.this.f12626vm.getIsFavorite()) {
                            ChangeFriendshipDialog.this.f12626vm.setShouldRemoveUserFromFavoriteList(true);
                        }
                        ChangeFriendshipDialog.this.f12626vm.setShouldAddUserToFavoriteList(false);
                    }
                });
            }
            if (this.addFavorite != null) {
                if (this.f12626vm.getIsFavorite()) {
                    this.addFavorite.setChecked(true);
                }
                this.addFavorite.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog.4
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (!ChangeFriendshipDialog.this.f12626vm.getIsFavorite()) {
                            ChangeFriendshipDialog.this.f12626vm.setShouldAddUserToFavoriteList(true);
                        }
                        ChangeFriendshipDialog.this.f12626vm.setShouldRemoveUserFromFavoriteList(false);
                    }
                });
            }
            XLEButton xLEButton = this.confirmButton;
            if (xLEButton != null) {
                xLEButton.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        ChangeFriendshipDialog.this.changeFriendshipSwitchPanel.setState(1);
                        ChangeFriendshipDialog.this.f12626vm.onChangeRelationshipCompleted();
                    }
                });
            }
            XLEButton xLEButton2 = this.cancelButton;
            if (xLEButton2 != null) {
                xLEButton2.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog.6
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        ChangeFriendshipDialog.this.dismissSelf();
                        ChangeFriendshipDialog.this.f12626vm.clearChangeFriendshipForm();
                    }
                });
            }
            XLECheckBox xLECheckBox = this.shareRealNameCheckbox;
            if (xLECheckBox != null) {
                xLECheckBox.setChecked(this.f12626vm.getCallerMarkedTargetAsIdentityShared());
                this.shareRealNameCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog.7
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        ChangeFriendshipDialog.this.f12626vm.setIsSharingRealNameEnd(z);
                        if (z) {
                            if (!ChangeFriendshipDialog.this.f12626vm.getCallerMarkedTargetAsIdentityShared()) {
                                ChangeFriendshipDialog.this.f12626vm.setShouldAddUserToShareIdentityList(true);
                            }
                            ChangeFriendshipDialog.this.f12626vm.setShouldRemoveUserFroShareIdentityList(false);
                            return;
                        }
                        if (ChangeFriendshipDialog.this.f12626vm.getCallerMarkedTargetAsIdentityShared()) {
                            ChangeFriendshipDialog.this.f12626vm.setShouldRemoveUserFroShareIdentityList(true);
                        }
                        ChangeFriendshipDialog.this.f12626vm.setShouldAddUserToShareIdentityList(false);
                    }
                });
                updateShareIdentityCheckboxStatus();
            }
            if (this.removeFriendLayout != null) {
                if (this.f12626vm.getIsFollowing()) {
                    this.removeFriendLayout.setVisibility(View.VISIBLE);
                    this.removeFriendLayout.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog.8
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            UTCChangeRelationship.trackChangeRelationshipRemoveFriend();
                            ChangeFriendshipDialog.this.changeFriendshipSwitchPanel.setState(1);
                            ChangeFriendshipDialog.this.f12626vm.removeFollowingUser();
                        }
                    });
                } else {
                    this.removeFriendLayout.setEnabled(false);
                    this.removeFriendLayout.setVisibility(View.GONE);
                }
                this.confirmButton.setText(this.f12626vm.getDialogButtonText());
            }
            updateShareIdentityCheckboxStatus();
        } else if (this.f12626vm.getViewModelState() == ListState.LoadingState) {
            setDialogLoadingView();
        }
    }
}

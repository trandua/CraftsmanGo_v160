package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.ScrollView;
//import com.crafting.minecrafting.lokicraft.R;
import com.craftsman.go.R;
import com.microsoft.xbox.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.FastProgressBar;
import com.microsoft.xbox.toolkit.ui.XLERoundedUniversalImageView;
import com.microsoft.xbox.xle.app.ImageUtil;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreenViewModel;
import com.microsoft.xbox.xle.ui.IconFontToggleButton;
import com.microsoft.xbox.xle.ui.XLERootView;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.XboxAppDeepLinker;

/* loaded from: classes3.dex */
public class ProfileScreenAdapter extends AdapterBase {
    public IconFontToggleButton blockButton;
    private ScrollView contentScrollView;
    private IconFontToggleButton followButton;
    private XLERoundedUniversalImageView gamerPicImageView;
    private CustomTypefaceTextView gamerscoreIconTextView;
    private CustomTypefaceTextView gamerscoreTextView;
    private CustomTypefaceTextView gamertagTextView;
    private FastProgressBar loadingProgressBar;
    public IconFontToggleButton muteButton;
    private CustomTypefaceTextView realNameTextView;
    private IconFontToggleButton reportButton;
    private XLERootView rootView;
    private IconFontToggleButton viewInXboxAppButton;
    private CustomTypefaceTextView viewInXboxAppSubTextView;
    public ProfileScreenViewModel viewModel;

    public ProfileScreenAdapter(ProfileScreenViewModel profileScreenViewModel) {
        super(profileScreenViewModel);
        CustomTypefaceTextView customTypefaceTextView;
        int i;
        this.blockButton = (IconFontToggleButton) findViewById(R.id.profile_block);
        this.contentScrollView = (ScrollView) findViewById(R.id.profile_screen_content_list);
        this.followButton = (IconFontToggleButton) findViewById(R.id.profile_follow);
        this.gamerPicImageView = (XLERoundedUniversalImageView) findViewById(R.id.profile_gamerpic);
        this.gamerscoreIconTextView = (CustomTypefaceTextView) findViewById(R.id.profile_gamerscore_icon);
        this.gamerscoreTextView = (CustomTypefaceTextView) findViewById(R.id.profile_gamerscore);
        this.gamertagTextView = (CustomTypefaceTextView) findViewById(R.id.profile_gamertag);
        this.loadingProgressBar = (FastProgressBar) findViewById(R.id.profile_screen_loading);
        this.muteButton = (IconFontToggleButton) findViewById(R.id.profile_mute);
        this.realNameTextView = (CustomTypefaceTextView) findViewById(R.id.profile_realname);
        this.reportButton = (IconFontToggleButton) findViewById(R.id.profile_report);
        this.rootView = (XLERootView) findViewById(R.id.profile_root);
        this.viewInXboxAppButton = (IconFontToggleButton) findViewById(R.id.profile_view_in_xbox_app);
        this.viewInXboxAppSubTextView = (CustomTypefaceTextView) findViewById(R.id.profile_view_in_xbox_app_subtext);
        this.viewModel = profileScreenViewModel;
        this.viewInXboxAppButton.setVisibility(View.VISIBLE);
        this.viewInXboxAppButton.setEnabled(true);
        this.viewInXboxAppButton.setChecked(true);
        if (this.viewModel.isMeProfile()) {
            this.followButton.setVisibility(View.GONE);
            this.muteButton.setVisibility(View.GONE);
            this.blockButton.setVisibility(View.GONE);
            this.reportButton.setVisibility(View.GONE);
            customTypefaceTextView = this.viewInXboxAppSubTextView;
            i = R.string.Profile_ViewInXboxApp_Details_MeProfile;
        } else {
            this.followButton.setVisibility(View.VISIBLE);
            this.followButton.setEnabled(true);
            this.muteButton.setVisibility(View.VISIBLE);
            this.muteButton.setEnabled(true);
            this.muteButton.setChecked(false);
            this.blockButton.setVisibility(View.VISIBLE);
            this.blockButton.setEnabled(false);
            this.reportButton.setVisibility(View.VISIBLE);
            this.reportButton.setEnabled(true);
            this.reportButton.setChecked(false);
            customTypefaceTextView = this.viewInXboxAppSubTextView;
            i = R.string.Profile_ViewInXboxApp_Details_YouProfile;
        }
        customTypefaceTextView.setText(i);
    }

    @Override // com.microsoft.xbox.xle.viewmodel.AdapterBase
    public void onStart() {
        super.onStart();
        IconFontToggleButton iconFontToggleButton = this.followButton;
        if (iconFontToggleButton != null) {
            iconFontToggleButton.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.adapter.ProfileScreenAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ProfileScreenAdapter.this.viewModel.navigateToChangeRelationship();
                }
            });
        }
        IconFontToggleButton iconFontToggleButton2 = this.muteButton;
        if (iconFontToggleButton2 != null) {
            iconFontToggleButton2.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.adapter.ProfileScreenAdapter.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ProfileScreenAdapter.this.muteButton.toggle();
                    ProfileScreenAdapter.this.muteButton.setEnabled(false);
                    if (ProfileScreenAdapter.this.muteButton.isChecked()) {
                        UTCPeopleHub.trackMute(true);
                        ProfileScreenAdapter.this.viewModel.muteUser();
                        return;
                    }
                    UTCPeopleHub.trackMute(false);
                    ProfileScreenAdapter.this.viewModel.unmuteUser();
                }
            });
        }
        IconFontToggleButton iconFontToggleButton3 = this.blockButton;
        if (iconFontToggleButton3 != null) {
            iconFontToggleButton3.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.adapter.ProfileScreenAdapter.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ProfileScreenAdapter.this.blockButton.toggle();
                    ProfileScreenAdapter.this.blockButton.setEnabled(false);
                    if (ProfileScreenAdapter.this.blockButton.isChecked()) {
                        UTCPeopleHub.trackBlock();
                        ProfileScreenAdapter.this.viewModel.blockUser();
                        return;
                    }
                    UTCPeopleHub.trackUnblock();
                    ProfileScreenAdapter.this.viewModel.unblockUser();
                }
            });
        }
        IconFontToggleButton iconFontToggleButton4 = this.reportButton;
        if (iconFontToggleButton4 != null) {
            iconFontToggleButton4.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.adapter.ProfileScreenAdapter.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    UTCPeopleHub.trackReport();
                    ProfileScreenAdapter.this.viewModel.showReportDialog();
                }
            });
        }
        if (this.viewInXboxAppButton == null) {
            return;
        }
        if (XboxAppDeepLinker.appDeeplinkingSupported()) {
            this.viewInXboxAppButton.setOnClickListener(new View.OnClickListener() { // from class: com.microsoft.xbox.xle.app.adapter.ProfileScreenAdapter.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    UTCPeopleHub.trackViewInXboxApp();
                    ProfileScreenAdapter.this.viewModel.launchXboxApp();
                }
            });
            return;
        }
        this.viewInXboxAppButton.setVisibility(View.GONE);
        this.viewInXboxAppSubTextView.setVisibility(View.GONE);
    }

    @Override // com.microsoft.xbox.xle.viewmodel.AdapterBase
    public void updateViewOverride() {
        XLERootView xLERootView = this.rootView;
        if (xLERootView != null) {
            xLERootView.setBackgroundColor(this.viewModel.getPreferredColor());
        }
        boolean z = false;
        this.loadingProgressBar.setVisibility(this.viewModel.isBusy() ? View.VISIBLE : View.GONE);
        this.contentScrollView.setVisibility(this.viewModel.isBusy() ? View.GONE : View.VISIBLE);
        XLERoundedUniversalImageView xLERoundedUniversalImageView = this.gamerPicImageView;
        if (xLERoundedUniversalImageView != null) {
            xLERoundedUniversalImageView.setImageURI2(ImageUtil.getMedium(this.viewModel.getGamerPicUrl()), R.drawable.gamerpic_missing, R.drawable.gamerpic_missing);
        }
        if (this.realNameTextView != null) {
            String realName = this.viewModel.getRealName();
            if (!JavaUtil.isNullOrEmpty(realName)) {
                this.realNameTextView.setText(realName);
                this.realNameTextView.setVisibility(View.VISIBLE);
            } else {
                this.realNameTextView.setVisibility(View.GONE);
            }
        }
        if (this.gamerscoreTextView != null && this.gamerscoreIconTextView != null) {
            String gamerScore = this.viewModel.getGamerScore();
            if (!JavaUtil.isNullOrEmpty(gamerScore)) {
                XLEUtil.updateTextAndVisibilityIfNotNull(this.gamerscoreTextView, gamerScore, 0);
                XLEUtil.updateVisibilityIfNotNull(this.gamerscoreIconTextView, 0);
            }
        }
        if (this.gamertagTextView != null) {
            String gamerTag = this.viewModel.getGamerTag();
            if (!JavaUtil.isNullOrEmpty(gamerTag)) {
                XLEUtil.updateTextAndVisibilityIfNotNull(this.gamertagTextView, gamerTag, 0);
            }
        }
        if (this.viewModel.isMeProfile()) {
            return;
        }
        boolean z2 = this.viewModel.getIsAddingUserToBlockList() || this.viewModel.getIsRemovingUserFromBlockList();
        this.followButton.setChecked(this.viewModel.isCallerFollowingTarget());
        this.followButton.setEnabled((z2 || this.viewModel.getIsBlocked()) ? false : true);
        this.muteButton.setChecked(this.viewModel.getIsMuted());
        IconFontToggleButton iconFontToggleButton = this.muteButton;
        if (!this.viewModel.getIsAddingUserToMutedList() && !this.viewModel.getIsRemovingUserFromMutedList()) {
            z = true;
        }
        iconFontToggleButton.setEnabled(z);
        this.blockButton.setChecked(this.viewModel.getIsBlocked());
        this.blockButton.setEnabled(!z2);
    }
}

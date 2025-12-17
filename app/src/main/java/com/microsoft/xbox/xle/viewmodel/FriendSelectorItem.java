package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.UserProfileData;

/* loaded from: classes3.dex */
public final class FriendSelectorItem extends FollowersData {
    private static final long serialVersionUID = 5799344980951867134L;
    private boolean selected;

    public FriendSelectorItem(FollowersData followersData) {
        super(followersData);
        this.selected = false;
    }

    public FriendSelectorItem(ProfileModel profileModel) {
        this.xuid = profileModel.getXuid();
        this.userProfileData = new UserProfileData();
        this.userProfileData.gamerTag = profileModel.getGamerTag();
        this.userProfileData.xuid = profileModel.getXuid();
        this.userProfileData.profileImageUrl = profileModel.getGamerPicImageUrl();
        this.userProfileData.gamerScore = profileModel.getGamerScore();
        this.userProfileData.appDisplayName = profileModel.getAppDisplayName();
        this.userProfileData.accountTier = profileModel.getAccountTier();
        this.userProfileData.gamerRealName = profileModel.getRealName();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            FriendSelectorItem friendSelectorItem = (FriendSelectorItem) obj;
            if (this.userProfileData == null || this.userProfileData.gamerTag == null) {
                if (friendSelectorItem.userProfileData != null || friendSelectorItem.userProfileData.gamerTag != null) {
                }
            } else if (!this.userProfileData.gamerTag.equals(friendSelectorItem.userProfileData.gamerTag)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean getIsSelected() {
        return this.selected;
    }

    public int hashCode() {
        return ((this.userProfileData == null || this.userProfileData.gamerTag == null) ? 0 : this.userProfileData.gamerTag.hashCode()) + 31;
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public void toggleSelection() {
        this.selected = !this.selected;
    }
}

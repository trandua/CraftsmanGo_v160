package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.io.Serializable;
import java.util.Date;

/* loaded from: classes3.dex */
public class FollowersData implements Serializable {
    private static final long serialVersionUID = 6714889261254600161L;
    private String followerText;
    public boolean isCurrentlyPlaying;
    protected boolean isDummy;
    public boolean isFavorite;
    public transient boolean isNew;
    protected DummyType itemDummyType;
    private Date lastPlayedWithDateTime;
    private IPeopleHubResult.PeopleHubPersonSummary personSummary;
    public String presenceString;
    private String recentPlayerText;
    private SearchResultPerson searchResultPerson;
    public UserStatus status;
    private Date timeStamp;
    public long titleId;
    public UserProfileData userProfileData;
    public String xuid;

    /* loaded from: classes3.dex */
    public enum DummyType {
        NOT_SET,
        DUMMY_HEADER,
        DUMMY_FRIENDS_HEADER,
        DUMMY_LINK_TO_FACEBOOK,
        DUMMY_FRIENDS_WHO_PLAY,
        DUMMY_VIPS,
        DUMMY_ERROR,
        DUMMY_NO_DATA,
        DUMMY_LOADING
    }

    public FollowersData() {
        this.personSummary = null;
        this.isCurrentlyPlaying = false;
        this.isDummy = false;
        this.isNew = false;
    }

    public FollowersData(FollowersData followersData) {
        this.personSummary = null;
        this.isCurrentlyPlaying = false;
        this.isDummy = false;
        this.isNew = false;
        this.xuid = followersData.xuid;
        this.isFavorite = followersData.isFavorite;
        this.status = followersData.status;
        this.presenceString = followersData.presenceString;
        this.titleId = followersData.titleId;
        this.userProfileData = followersData.userProfileData;
        this.isCurrentlyPlaying = followersData.isCurrentlyPlaying;
        this.timeStamp = followersData.timeStamp;
        this.isDummy = followersData.isDummy;
    }

    public FollowersData(IPeopleHubResult.PeopleHubPersonSummary peopleHubPersonSummary) {
        this.personSummary = null;
        this.isCurrentlyPlaying = false;
        this.isDummy = false;
        this.isNew = false;
        XLEAssert.assertNotNull(peopleHubPersonSummary);
        this.personSummary = peopleHubPersonSummary;
        this.xuid = peopleHubPersonSummary.xuid;
        this.userProfileData = new UserProfileData(peopleHubPersonSummary);
        this.isFavorite = peopleHubPersonSummary.isFavorite;
        this.status = UserStatus.getStatusFromString(peopleHubPersonSummary.presenceState);
        this.presenceString = peopleHubPersonSummary.presenceText;
        if (peopleHubPersonSummary.titleHistory != null) {
            this.titleId = peopleHubPersonSummary.titleHistory.TitleId;
            this.timeStamp = peopleHubPersonSummary.titleHistory.LastTimePlayed;
        }
        if (peopleHubPersonSummary.recentPlayer != null) {
            this.recentPlayerText = peopleHubPersonSummary.recentPlayer.text;
            if (!XLEUtil.isNullOrEmpty(peopleHubPersonSummary.recentPlayer.titles)) {
                this.lastPlayedWithDateTime = peopleHubPersonSummary.recentPlayer.titles.get(0).lastPlayedWithDateTime;
            }
        }
        if (peopleHubPersonSummary.follower != null) {
            this.followerText = peopleHubPersonSummary.follower.text;
        }
        if (peopleHubPersonSummary.titlePresence != null) {
            this.isCurrentlyPlaying = peopleHubPersonSummary.titlePresence.IsCurrentlyPlaying;
            this.presenceString = peopleHubPersonSummary.titlePresence.PresenceText;
        }
    }

    public FollowersData(boolean z) {
        this(z, DummyType.NOT_SET);
    }

    public FollowersData(boolean z, DummyType dummyType) {
        this.personSummary = null;
        this.isCurrentlyPlaying = false;
        this.isNew = false;
        this.isDummy = z;
        this.itemDummyType = dummyType;
    }

    public String getFollowersTitleText() {
        return this.followerText;
    }

    public int getGameScore() {
        UserProfileData userProfileData = this.userProfileData;
        if (userProfileData != null) {
            return Integer.parseInt(userProfileData.gamerScore);
        }
        return 0;
    }

    public String getGamerName() {
        UserProfileData userProfileData = this.userProfileData;
        return userProfileData != null ? userProfileData.appDisplayName : "";
    }

    public String getGamerPicUrl() {
        UserProfileData userProfileData = this.userProfileData;
        if (userProfileData != null) {
            return userProfileData.profileImageUrl;
        }
        return null;
    }

    public String getGamerRealName() {
        UserProfileData userProfileData = this.userProfileData;
        if (userProfileData == null) {
            return null;
        }
        return userProfileData.gamerRealName;
    }

    public String getGamertag() {
        UserProfileData userProfileData = this.userProfileData;
        return userProfileData != null ? userProfileData.gamerTag : "";
    }

    public boolean getIsDummy() {
        return this.isDummy;
    }

    public boolean getIsOnline() {
        return this.status == UserStatus.Online;
    }

    public DummyType getItemDummyType() {
        return this.itemDummyType;
    }

    public Date getLastPlayedWithDateTime() {
        return this.lastPlayedWithDateTime;
    }

    public IPeopleHubResult.PeopleHubPersonSummary getPersonSummary() {
        return this.personSummary;
    }

    public String getRecentPlayerTitleText() {
        return this.recentPlayerText;
    }

    public SearchResultPerson getSearchResultPerson() {
        return this.searchResultPerson;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public void setItemDummyType(DummyType dummyType) {
        this.isDummy = true;
        this.itemDummyType = dummyType;
    }

    public void setSearchResultPerson(SearchResultPerson searchResultPerson) {
        this.searchResultPerson = searchResultPerson;
    }

    public void setTimeStamp(Date date) {
        this.timeStamp = date;
    }
}

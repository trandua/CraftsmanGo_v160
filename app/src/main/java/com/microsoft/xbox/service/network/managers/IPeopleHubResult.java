package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.toolkit.JavaUtil;
import java.util.ArrayList;
import java.util.Date;

/* loaded from: classes3.dex */
public interface IPeopleHubResult {

    /* loaded from: classes3.dex */
    public static class Follower {
        public Date followedDateTime;
        public String text;
    }

    /* loaded from: classes3.dex */
    public static class MultiplayerSummary {
        public int InMultiplayerSession;
        public int InParty;
    }

    /* loaded from: classes3.dex */
    public static class PeopleHubPeopleSummary {
        public ArrayList<PeopleHubPersonSummary> people;
        public RecommendationSummary recommendationSummary;
    }

    /* loaded from: classes3.dex */
    public static class PeopleHubPreferredColor {
        public String primaryColor;
        public String secondaryColor;
        public String tertiaryColor;
    }

    /* loaded from: classes3.dex */
    public static class PeopleHubTitleHistory {
        public Date LastTimePlayed;
        public long TitleId;
        public String TitleName;
    }

    /* loaded from: classes3.dex */
    public static class PeopleHubTitlePresence {
        public boolean IsCurrentlyPlaying;
        public String PresenceText;
        public String TitleId;
        public String TitleName;
    }

    /* loaded from: classes3.dex */
    public static class PeopleHubTitleSummary {
    }

    /* loaded from: classes3.dex */
    public static class RecentPlayer {
        public String text;
        public ArrayList<Title> titles;
    }

    /* loaded from: classes3.dex */
    public static class RecommendationSummary {
        public int VIP;
        public int facebookFriend;
        public int follower;
        public int friendOfFriend;
        public int phoneContact;
        public boolean promoteSuggestions;
    }

    /* loaded from: classes3.dex */
    public static class Title {
        public Date lastPlayedWithDateTime;
        public String titleName;
    }

    /* loaded from: classes3.dex */
    public static class PeopleHubPersonSummary {
        public String displayName;
        public String displayPicRaw;
        public Follower follower;
        public String gamerScore;
        public String gamertag;
        public boolean isFavorite;
        public boolean isFollowedByCaller;
        public boolean isFollowingCaller;
        public boolean isIdentityShared;
        public MultiplayerSummary multiplayerSummary;
        public PeopleHubPreferredColor preferredColor;
        public String presenceState;
        public String presenceText;
        public String realName;
        public RecentPlayer recentPlayer;
        public PeopleHubRecommendation recommendation;
        public PeopleHubTitleHistory titleHistory;
        public PeopleHubTitlePresence titlePresence;
        public ArrayList<PeopleHubTitleSummary> titleSummaries;
        public boolean useAvatar;
        public String xboxOneRep;
        public String xuid;

        public String getRealNameFromRecommendationOrDefault() {
            PeopleHubRecommendation peopleHubRecommendation;
            String str = this.realName;
            return (!JavaUtil.isNullOrEmpty(str) || (peopleHubRecommendation = this.recommendation) == null || peopleHubRecommendation.Reasons == null || peopleHubRecommendation.Reasons.size() <= 0) ? str : peopleHubRecommendation.Reasons.get(0);
        }
    }

    /* loaded from: classes3.dex */
    public static class PeopleHubRecommendation {
        public ArrayList<String> Reasons;
        public String Type;

        public RecommendationType getRecommendationType() {
            return RecommendationType.getRecommendationType(this.Type);
        }
    }

    /* loaded from: classes3.dex */
    public enum RecommendationType {
        Unknown,
        Dummy,
        Follower,
        FacebookFriend,
        PhoneContact,
        FriendOfFriend,
        VIP;

        public static RecommendationType getRecommendationType(String str) {
            RecommendationType[] values;
            for (RecommendationType recommendationType : values()) {
                if (recommendationType.name().equalsIgnoreCase(str)) {
                    return recommendationType;
                }
            }
            return Unknown;
        }
    }
}

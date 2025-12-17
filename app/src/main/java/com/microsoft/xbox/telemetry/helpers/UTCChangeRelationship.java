package com.microsoft.xbox.telemetry.helpers;

import com.microsoft.xbox.telemetry.helpers.UTCEventTracker;
import com.microsoft.xbox.telemetry.utc.model.UTCNames;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.HashMap;

/* loaded from: classes3.dex */
public class UTCChangeRelationship {
    public static CharSequence currentActivityTitle = "";
    public static String currentXUID = "";

    /* loaded from: classes3.dex */
    public enum FavoriteStatus {
        UNKNOWN(0),
        FAVORITED(1),
        UNFAVORITED(2),
        NOTFAVORITED(3),
        EXISTINGFAVORITE(4),
        EXISTINGNOTFAVORITED(5);
        
        private int value;

        FavoriteStatus(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    /* loaded from: classes3.dex */
    public enum GamerType {
        UNKNOWN(0),
        NORMAL(1),
        FACEBOOK(2),
        SUGGESTED(3);
        
        private int value;

        GamerType(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    /* loaded from: classes3.dex */
    public enum RealNameStatus {
        UNKNOWN(0),
        SHARINGON(1),
        SHARINGOFF(2),
        EXISTINGSHARED(3),
        EXISTINGNOTSHARED(4);
        
        private static final RealNameStatus[] $VALUES = null;
        private int value;

        RealNameStatus(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    /* loaded from: classes3.dex */
    public enum Relationship {
        UNKNOWN(0),
        ADDFRIEND(1),
        REMOVEFRIEND(2),
        EXISTINGFRIEND(3),
        NOTCHANGED(4);
        
        private int value;

        Relationship(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static HashMap<String, Object> getAdditionalInfo(String str) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(UTCDeepLink.TARGET_XUID_KEY, "x:" + str);
        return hashMap;
    }

    public static void trackChangeRelationshipAction(final CharSequence charSequence, final String str, final boolean z, final boolean z2) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCChangeRelationship.1
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                HashMap<String, Object> additionalInfo = UTCChangeRelationship.getAdditionalInfo(str);
                additionalInfo.put("relationship", Integer.valueOf((z ? Relationship.EXISTINGFRIEND : Relationship.ADDFRIEND).getValue()));
                UTCPageAction.track(UTCNames.PageAction.ChangeRelationship.Action, charSequence, additionalInfo);
                if (z2) {
                    UTCChangeRelationship.trackChangeRelationshipDone(charSequence, str, Relationship.ADDFRIEND, RealNameStatus.SHARINGON, FavoriteStatus.NOTFAVORITED, GamerType.FACEBOOK);
                }
            }
        });
    }

    public static void trackChangeRelationshipAction(boolean z, boolean z2) {
        verifyTrackedDefaults();
        trackChangeRelationshipAction(currentActivityTitle, currentXUID, z, z2);
    }

    public static void trackChangeRelationshipDone(Relationship relationship, RealNameStatus realNameStatus, FavoriteStatus favoriteStatus, GamerType gamerType) {
        verifyTrackedDefaults();
        trackChangeRelationshipDone(currentActivityTitle, currentXUID, relationship, realNameStatus, favoriteStatus, gamerType);
    }

    public static void trackChangeRelationshipDone(final CharSequence charSequence, final String str, final Relationship relationship, final RealNameStatus realNameStatus, final FavoriteStatus favoriteStatus, final GamerType gamerType) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCChangeRelationship.2
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                HashMap<String, Object> additionalInfo = UTCChangeRelationship.getAdditionalInfo(str);
                additionalInfo.put("relationship", Integer.valueOf(relationship.getValue()));
                additionalInfo.put("favorite", Integer.valueOf(favoriteStatus.getValue()));
                additionalInfo.put("realname", Integer.valueOf(realNameStatus.getValue()));
                additionalInfo.put("gamertype", Integer.valueOf(gamerType.getValue()));
                UTCPageAction.track(UTCNames.PageAction.ChangeRelationship.Done, charSequence, additionalInfo);
            }
        });
    }

    public static void trackChangeRelationshipRemoveFriend() {
        verifyTrackedDefaults();
        trackChangeRelationshipRemoveFriend(currentActivityTitle, currentXUID);
    }

    public static void trackChangeRelationshipRemoveFriend(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCChangeRelationship.3
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                HashMap<String, Object> additionalInfo = UTCChangeRelationship.getAdditionalInfo(str);
                additionalInfo.put("relationship", Relationship.REMOVEFRIEND);
                UTCPageAction.track(UTCNames.PageAction.ChangeRelationship.Action, charSequence, additionalInfo);
            }
        });
    }

    public static void trackChangeRelationshipView(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCChangeRelationship.4
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                UTCChangeRelationship.currentActivityTitle = charSequence;
                UTCChangeRelationship.currentXUID = str;
                UTCPageView.track(UTCNames.PageView.ChangeRelationship.ChangeRelationshipView, UTCChangeRelationship.currentActivityTitle, UTCChangeRelationship.getAdditionalInfo(UTCChangeRelationship.currentXUID));
            }
        });
    }

    private static void verifyTrackedDefaults() {
        XLEAssert.assertFalse("Called trackPeopleHubView without set currentXUID", currentXUID.equals(""));
        XLEAssert.assertFalse("Called trackPeopleHubView without set activityTitle", currentActivityTitle.toString().equals(""));
    }
}

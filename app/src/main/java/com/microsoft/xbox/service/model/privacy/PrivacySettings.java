package com.microsoft.xbox.service.model.privacy;

/* loaded from: classes3.dex */
public class PrivacySettings {

    /* loaded from: classes3.dex */
    public static class PrivacySetting {
        public String setting;
        private PrivacySettingId settingId;
        private PrivacySettingValue settingValue;
        public String value;

        public PrivacySetting() {
        }

        public PrivacySetting(PrivacySettingId privacySettingId, PrivacySettingValue privacySettingValue) {
            this.setting = privacySettingId.name();
            this.value = privacySettingValue.name();
        }

        public PrivacySettingId getPrivacySettingId() {
            PrivacySettingId privacySettingId = PrivacySettingId.getPrivacySettingId(this.setting);
            this.settingId = privacySettingId;
            return privacySettingId;
        }

        public PrivacySettingValue getPrivacySettingValue() {
            PrivacySettingValue privacySettingValue = PrivacySettingValue.getPrivacySettingValue(this.value);
            this.settingValue = privacySettingValue;
            return privacySettingValue;
        }

        public void setPrivacySettingId(PrivacySettingId privacySettingId) {
            this.setting = privacySettingId.name();
            this.settingId = privacySettingId;
        }
    }

    /* loaded from: classes3.dex */
    public enum PrivacySettingId {
        None,
        ShareFriendList,
        ShareGameHistory,
        CommunicateUsingTextAndVoice,
        SharePresence,
        ShareProfile,
        ShareVideoAndMusicStatus,
        CommunicateUsingVideo,
        CollectVoiceData,
        ShareXboxMusicActivity,
        ShareExerciseInfo,
        ShareIdentity,
        ShareRecordedGameSessions,
        ShareIdentityTransitively,
        CanShareIdentity;

        public static PrivacySettingId getPrivacySettingId(String str) {
            PrivacySettingId[] values;
            for (PrivacySettingId privacySettingId : values()) {
                if (privacySettingId.name().equalsIgnoreCase(str)) {
                    return privacySettingId;
                }
            }
            return None;
        }
    }

    /* loaded from: classes3.dex */
    public enum PrivacySettingValue {
        NotSet,
        Everyone,
        PeopleOnMyList,
        FriendCategoryShareIdentity,
        Blocked;

        public static PrivacySettingValue getPrivacySettingValue(String str) {
            PrivacySettingValue[] values;
            for (PrivacySettingValue privacySettingValue : values()) {
                if (privacySettingValue.name().equalsIgnoreCase(str)) {
                    return privacySettingValue;
                }
            }
            return NotSet;
        }
    }
}

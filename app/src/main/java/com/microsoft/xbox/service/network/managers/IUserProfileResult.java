package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.sls.UserProfileSetting;
import com.microsoft.xbox.toolkit.GsonUtil;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes3.dex */
public interface IUserProfileResult {

    /* loaded from: classes3.dex */
    public static class Settings {
        public String f12612id;
        public String value;
    }

    /* loaded from: classes3.dex */
    public static class ProfileUser {
        private static final long FORCE_MATURITY_LEVEL_UPDATE_TIME = 10800000;
        public boolean canViewTVAdultContent;
        public ProfilePreferredColor colors;
        public String f12611id;
        private int maturityLevel;
        private int[] privileges;
        public ArrayList<Settings> settings;
        private long updateMaturityLevelTimer = -1;

        /* JADX WARN: Code restructure failed: missing block: B:12:0x0030, code lost:
            r4.canViewTVAdultContent = r0.familyUsers.get(r1).canViewTVAdultContent;
            r4.maturityLevel = r0.familyUsers.get(r1).maturityLevel;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        private void fetchMaturityLevel() {
            try {
                FamilySettings familySettings = ServiceManagerFactory.getInstance().getSLSServiceManager().getFamilySettings(this.f12611id);
                if (familySettings != null && familySettings.familyUsers != null) {
                    int i = 0;
                    while (true) {
                        if (i >= familySettings.familyUsers.size()) {
                            break;
                        } else if (familySettings.familyUsers.get(i).xuid.equalsIgnoreCase(this.f12611id)) {
                            break;
                        } else {
                            i++;
                        }
                    }
                }
            } catch (Throwable unused) {
            }
            this.updateMaturityLevelTimer = System.currentTimeMillis();
        }

        public int getMaturityLevel() {
            if (this.updateMaturityLevelTimer < 0 || System.currentTimeMillis() - this.updateMaturityLevelTimer > FORCE_MATURITY_LEVEL_UPDATE_TIME) {
                fetchMaturityLevel();
            }
            return this.maturityLevel;
        }

        public int[] getPrivileges() {
            return this.privileges;
        }

        public String getSettingValue(UserProfileSetting userProfileSetting) {
            ArrayList<Settings> arrayList = this.settings;
            if (arrayList == null) {
                return null;
            }
            Iterator<Settings> it = arrayList.iterator();
            while (it.hasNext()) {
                Settings next = it.next();
                if (next.f12612id != null && next.f12612id.equals(userProfileSetting.toString())) {
                    return next.value;
                }
            }
            return null;
        }

        public void setPrivilieges(int[] iArr) {
            this.privileges = iArr;
        }

        public void setmaturityLevel(int i) {
            this.maturityLevel = i;
            this.updateMaturityLevelTimer = System.currentTimeMillis();
        }
    }

    /* loaded from: classes3.dex */
    public static class UserProfileResult {
        public ArrayList<ProfileUser> profileUsers;

        public static UserProfileResult deserialize(String str) {
            return (UserProfileResult) GsonUtil.deserializeJson(str, UserProfileResult.class);
        }
    }
}

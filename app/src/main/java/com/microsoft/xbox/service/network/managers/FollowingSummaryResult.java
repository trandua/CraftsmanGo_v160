package com.microsoft.xbox.service.network.managers;

import java.util.ArrayList;

/* loaded from: classes3.dex */
public class FollowingSummaryResult {
    public ArrayList<People> people;
    public int totalCount;

    /* loaded from: classes3.dex */
    public static class People {
        public String displayName;
        public String displayPicRaw;
        public String gamertag;
        public boolean isFavorite;
        public boolean isIdentityShared;
        public String realName;
        public String xuid;
    }
}

package com.microsoft.xbox.service.network.managers;

import java.util.ArrayList;

/* loaded from: classes3.dex */
public interface IFollowingResult {

    /* loaded from: classes3.dex */
    public static class FollowingResult {
        public ArrayList<People> people;
        public int totalCount;
    }

    /* loaded from: classes3.dex */
    public static class People {
        public boolean isFavorite;
        public String xuid;
    }
}

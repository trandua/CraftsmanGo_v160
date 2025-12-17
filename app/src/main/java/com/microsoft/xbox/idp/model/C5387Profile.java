package com.microsoft.xbox.idp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes3.dex */
public final class C5387Profile {

    /* loaded from: classes3.dex */
    public static final class GamerpicChoiceList {
        public List<GamerpicListEntry> gamerpics;
    }

    /* loaded from: classes3.dex */
    public static final class GamerpicListEntry {
        public String f12602id;
    }

    /* loaded from: classes3.dex */
    public static final class GamerpicUpdateResponse {
    }

    /* loaded from: classes3.dex */
    public static final class Response {
        public User[] profileUsers;
    }

    /* loaded from: classes3.dex */
    public static final class Setting {
        public SettingId f12603id;
        public String value;
    }

    /* loaded from: classes3.dex */
    public enum SettingId {
        AppDisplayName,
        GameDisplayName,
        Gamertag,
        RealName,
        FirstName,
        LastName,
        AppDisplayPicRaw,
        GameDisplayPicRaw,
        AccountTier,
        TenureLevel,
        Gamerscore,
        PreferredColor,
        Watermarks,
        XboxOneRep,
        Background,
        PublicGamerpicType,
        ShowUserAsAvatar,
        TileTransparency
    }

    /* loaded from: classes3.dex */
    public static final class User {
        public String f12604id;
        public boolean isSponsoredUser;
        public Map<SettingId, String> settings;
    }

    /* loaded from: classes3.dex */
    public static final class GamerpicChangeRequest {
        public UserSetting userSetting;

        public GamerpicChangeRequest(String str) {
            this.userSetting = new UserSetting("PublicGamerpic", str);
        }
    }

    /* loaded from: classes3.dex */
    private static class SettingsAdapter extends TypeAdapter<Map<SettingId, String>> {
        private SettingsAdapter() {
        }

        @Override // com.google.gson.TypeAdapter
        public Map<SettingId, String> read(JsonReader jsonReader) throws IOException {
            Setting[] settingArr = (Setting[]) new Gson().fromJson(jsonReader, Setting[].class);
            HashMap hashMap = new HashMap();
            for (Setting setting : settingArr) {
                hashMap.put(setting.f12603id, setting.value);
            }
            return hashMap;
        }

        @Override // com.google.gson.TypeAdapter
        public void write(JsonWriter jsonWriter, Map<SettingId, String> map) throws IOException {
            Setting[] settingArr = new Setting[map.size()];
            int i = -1;
            for (Map.Entry<SettingId, String> entry : map.entrySet()) {
                Setting setting = new Setting();
                setting.f12603id = entry.getKey();
                setting.value = entry.getValue();
                i++;
                settingArr[i] = setting;
            }
            new Gson().toJson(settingArr, Setting[].class, jsonWriter);
        }
    }

    /* loaded from: classes3.dex */
    public static final class UserSetting {
        public String f12605id;
        public String value;

        public UserSetting(String str, String str2) {
            this.f12605id = str;
            this.value = str2;
        }
    }

    public static GsonBuilder registerAdapters(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(new TypeToken<Map<SettingId, String>>() { // from class: com.microsoft.xbox.idp.model.C5387Profile.1
        }.getType(), new SettingsAdapter());
    }
}

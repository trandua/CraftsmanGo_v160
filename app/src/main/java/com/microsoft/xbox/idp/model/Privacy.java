package com.microsoft.xbox.idp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes3.dex */
public final class Privacy {

    /* loaded from: classes3.dex */
    public enum Key {
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
        CanShareIdentity
    }

    /* loaded from: classes3.dex */
    public static class Setting {
        public Key setting;
        public Value value;
    }

    /* loaded from: classes3.dex */
    public enum Value {
        NotSet,
        Everyone,
        PeopleOnMyList,
        FriendCategoryShareIdentity,
        Blocked
    }

    /* loaded from: classes3.dex */
    public static class Settings {
        public Map<Key, Value> settings;

        public static Settings newWithMap() {
            Settings settings = new Settings();
            settings.settings = new HashMap();
            return settings;
        }

        public boolean isSettingSet(Key key) {
            Value value;
            Map<Key, Value> map = this.settings;
            return (map == null || (value = map.get(key)) == null || value == Value.NotSet) ? false : true;
        }
    }

    /* loaded from: classes3.dex */
    private static class SettingsAdapter extends TypeAdapter<Map<Key, Value>> {
        private SettingsAdapter() {
        }

        @Override // com.google.gson.TypeAdapter
        public Map<Key, Value> read(JsonReader jsonReader) throws IOException {
            Setting[] settingArr = (Setting[]) new Gson().fromJson(jsonReader, Setting[].class);
            HashMap hashMap = new HashMap();
            for (Setting setting : settingArr) {
                if (setting.setting != null && setting.value != null) {
                    hashMap.put(setting.setting, setting.value);
                }
            }
            return hashMap;
        }

        @Override // com.google.gson.TypeAdapter
        public void write(JsonWriter jsonWriter, Map<Key, Value> map) throws IOException {
            Setting[] settingArr = new Setting[map.size()];
            int i = -1;
            for (Map.Entry<Key, Value> entry : map.entrySet()) {
                Setting setting = new Setting();
                setting.setting = entry.getKey();
                setting.value = entry.getValue();
                i++;
                settingArr[i] = setting;
            }
            new Gson().toJson(settingArr, Setting[].class, jsonWriter);
        }
    }

    public static GsonBuilder registerAdapters(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(new TypeToken<Map<Key, Value>>() { // from class: com.microsoft.xbox.idp.model.Privacy.1
        }.getType(), new SettingsAdapter());
    }
}

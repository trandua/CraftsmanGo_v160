package com.microsoft.aad.adal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
final class SSOStateSerializer {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(TokenCacheItem.class, new TokenCacheItemSerializationAdapater()).create();
    @SerializedName("tokenCacheItems")
    private final List<TokenCacheItem> mTokenCacheItems;
    @SerializedName("version")
    private final int version;

    private int getVersion() {
        return 1;
    }

    private SSOStateSerializer() {
        this.version = 1;
        this.mTokenCacheItems = new ArrayList();
    }

    private SSOStateSerializer(TokenCacheItem tokenCacheItem) {
        this.version = 1;
        ArrayList arrayList = new ArrayList();
        this.mTokenCacheItems = arrayList;
        if (tokenCacheItem != null) {
            arrayList.add(tokenCacheItem);
            return;
        }
        throw new IllegalArgumentException("tokenItem is null");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TokenCacheItem deserialize(String str) throws AuthenticationException {
        return new SSOStateSerializer().internalDeserialize(str);
    }

    private TokenCacheItem getTokenItem() throws AuthenticationException {
        List<TokenCacheItem> list = this.mTokenCacheItems;
        if (list != null && !list.isEmpty()) {
            return this.mTokenCacheItems.get(0);
        }
        throw new AuthenticationException(ADALError.TOKEN_CACHE_ITEM_NOT_FOUND, "There is no token cache item in the SSOStateContainer.");
    }

    private TokenCacheItem internalDeserialize(String str) throws AuthenticationException {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.getInt("version") == getVersion()) {
                return ((SSOStateSerializer) GSON.fromJson(str, SSOStateSerializer.class)).getTokenItem();
            }
            throw new DeserializationAuthenticationException("Fail to deserialize because the blob version is incompatible. The version of the serializedBlob is " + jSONObject.getInt("version") + ". And the target class version is " + getVersion());
        } catch (JsonParseException | JSONException e) {
            throw new DeserializationAuthenticationException(e.getMessage());
        }
    }

    private String internalSerialize() {
        return GSON.toJson(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String serialize(TokenCacheItem tokenCacheItem) {
        return new SSOStateSerializer(tokenCacheItem).internalSerialize();
    }
}

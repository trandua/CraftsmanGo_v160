package com.microsoft.aad.adal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.microsoft.aad.adal.AuthenticationConstants;
import java.lang.reflect.Type;

/* loaded from: classes3.dex */
public final class TokenCacheItemSerializationAdapater implements JsonDeserializer<TokenCacheItem>, JsonSerializer<TokenCacheItem> {
    private static final String TAG = "TokenCacheItemSerializationAdapater";

    private void throwIfParameterMissing(JsonObject jsonObject, String str) {
        if (jsonObject.has(str)) {
            return;
        }
        throw new JsonParseException("TokenCacheItemSerializationAdapaterAttribute " + str + " is missing for deserialization.");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.gson.JsonDeserializer
    public TokenCacheItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        throwIfParameterMissing(asJsonObject, AuthenticationConstants.OAuth2.AUTHORITY);
        throwIfParameterMissing(asJsonObject, "id_token");
        throwIfParameterMissing(asJsonObject, "foci");
        throwIfParameterMissing(asJsonObject, AuthenticationConstants.OAuth2.REFRESH_TOKEN);
        String asString = asJsonObject.get("id_token").getAsString();
        TokenCacheItem tokenCacheItem = new TokenCacheItem();
        try {
            IdToken idToken = new IdToken(asString);
            tokenCacheItem.setUserInfo(new UserInfo(idToken));
            tokenCacheItem.setTenantId(idToken.getTenantId());
            tokenCacheItem.setAuthority(asJsonObject.get(AuthenticationConstants.OAuth2.AUTHORITY).getAsString());
            tokenCacheItem.setIsMultiResourceRefreshToken(true);
            tokenCacheItem.setRawIdToken(asString);
            tokenCacheItem.setFamilyClientId(asJsonObject.get("foci").getAsString());
            tokenCacheItem.setRefreshToken(asJsonObject.get(AuthenticationConstants.OAuth2.REFRESH_TOKEN).getAsString());
            return tokenCacheItem;
        } catch (AuthenticationException e) {
            throw new JsonParseException("TokenCacheItemSerializationAdapater: Could not deserialize into a tokenCacheItem object", e);
        }
    }

    @Override // com.google.gson.JsonSerializer
    public JsonElement serialize(TokenCacheItem tokenCacheItem, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(AuthenticationConstants.OAuth2.AUTHORITY, new JsonPrimitive(tokenCacheItem.getAuthority()));
        jsonObject.add(AuthenticationConstants.OAuth2.REFRESH_TOKEN, new JsonPrimitive(tokenCacheItem.getRefreshToken()));
        jsonObject.add("id_token", new JsonPrimitive(tokenCacheItem.getRawIdToken()));
        jsonObject.add("foci", new JsonPrimitive(tokenCacheItem.getFamilyClientId()));
        return jsonObject;
    }
}

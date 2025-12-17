package com.microsoft.xbox.toolkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Map;

/* loaded from: classes3.dex */
public class GsonUtil {

    /* loaded from: classes3.dex */
    public interface JsonBodyBuilder {
        void buildBody(JsonWriter jsonWriter) throws IOException;
    }

    public static String buildJsonBody(JsonBodyBuilder jsonBodyBuilder) throws IOException {
        StringWriter stringWriter = new StringWriter();
        try {
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonBodyBuilder.buildBody(jsonWriter);
            String stringWriter2 = stringWriter.toString();
            jsonWriter.close();
            return stringWriter2;
        } finally {
            stringWriter.close();
        }
    }

    public static GsonBuilder createMinimumGsonBuilder() {
        return new GsonBuilder().excludeFieldsWithModifiers(128);
    }

    public static <T> T deserializeJson(Gson gson, InputStream inputStream, Class<T> cls) throws IOException {
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader = null;
        BufferedReader bufferedReader2 = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
            try {
                bufferedReader = new BufferedReader(inputStreamReader);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Throwable th2) {
//            th = th2;
            inputStreamReader = null;
        }
        try {
            T t = (T) gson.fromJson((Reader) bufferedReader, (Class<Object>) cls);
            try {
                bufferedReader.close();
            } catch (IOException unused) {
            }
            try {
                inputStreamReader.close();
            } catch (IOException unused2) {
            }
            return t;
        } catch (Throwable th3) {
//            th = th3;
            bufferedReader2 = bufferedReader;
            if (bufferedReader2 != null) {
                try {
                    bufferedReader2.close();
                } catch (IOException unused3) {
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException unused4) {
                }
            }
//            throw th;
        }
        return null;
    }

    public static <T> T deserializeJson(Gson gson, String str, Class<T> cls) {
        try {
            return (T) gson.fromJson(str, (Class<Object>) cls);
        } catch (Exception unused) {
            return null;
        }
    }

    public static <T> T deserializeJson(InputStream inputStream, Class<T> cls) {
        try {
            return (T) deserializeJson(createMinimumGsonBuilder().create(), inputStream, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserializeJson(InputStream inputStream, Class<T> cls, Type type, Object obj) {
        try {
            return (T) deserializeJson(createMinimumGsonBuilder().registerTypeAdapter(type, obj).create(), inputStream, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserializeJson(InputStream inputStream, Class<T> cls, Map<Type, Object> map) {
        GsonBuilder createMinimumGsonBuilder = createMinimumGsonBuilder();
        for (Map.Entry<Type, Object> entry : map.entrySet()) {
            createMinimumGsonBuilder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }
        try {
            return (T) deserializeJson(createMinimumGsonBuilder.create(), inputStream, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserializeJson(String str, Class<T> cls) {
        return (T) deserializeJson(createMinimumGsonBuilder().create(), str, cls);
    }

    public static <T> T deserializeJson(String str, Class<T> cls, Type type, Object obj) {
        return (T) deserializeJson(createMinimumGsonBuilder().registerTypeAdapter(type, obj).create(), str, cls);
    }

    public static String toJsonString(Object obj) {
        return new Gson().toJson(obj);
    }
}

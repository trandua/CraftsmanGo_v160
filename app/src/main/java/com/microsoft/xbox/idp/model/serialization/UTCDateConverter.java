package com.microsoft.xbox.idp.model.serialization;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes3.dex */
public class UTCDateConverter {
    private static final int NO_MS_STRING_LENGTH = 19;
    public static final String TAG = "UTCDateConverter";
    private static SimpleDateFormat defaultFormatMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
    public static SimpleDateFormat defaultFormatNoMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
    public static SimpleDateFormat shortDateAlternateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
    public static SimpleDateFormat shortDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);

    /* loaded from: classes3.dex */
    public static class UTCDateConverterJSONDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {
        @Override // com.google.gson.JsonDeserializer
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            return UTCDateConverter.convert(jsonElement.getAsJsonPrimitive().getAsString());
        }

        @Override // com.google.gson.JsonSerializer
        public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(UTCDateConverter.defaultFormatNoMs.format(date));
        }
    }

    /* loaded from: classes3.dex */
    public static class UTCDateConverterShortDateAlternateFormatJSONDeserializer implements JsonDeserializer<Date> {
        @Override // com.google.gson.JsonDeserializer
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            Date date;
            String asString = jsonElement.getAsJsonPrimitive().getAsString();
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            UTCDateConverter.shortDateFormat.setTimeZone(timeZone);
            try {
                date = UTCDateConverter.shortDateFormat.parse(asString);
            } catch (ParseException unused) {
                Log.d(UTCDateConverter.TAG, "failed to parse short date " + asString);
                date = null;
            }
            if (date != null && date.getYear() + 1900 < 2000) {
                UTCDateConverter.shortDateAlternateFormat.setTimeZone(timeZone);
                try {
                    return UTCDateConverter.shortDateAlternateFormat.parse(asString);
                } catch (ParseException unused2) {
                    Log.d(UTCDateConverter.TAG, "failed to parse alternate short date " + asString);
                }
            }
            return date;
        }
    }

    /* loaded from: classes3.dex */
    public static class UTCDateConverterShortDateFormatJSONDeserializer implements JsonDeserializer<Date> {
        @Override // com.google.gson.JsonDeserializer
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            String asString = jsonElement.getAsJsonPrimitive().getAsString();
            UTCDateConverter.shortDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                return UTCDateConverter.shortDateFormat.parse(asString);
            } catch (ParseException unused) {
                Log.d(UTCDateConverter.TAG, "failed to parse date " + asString);
                return null;
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class UTCRoundtripDateConverterJSONDeserializer implements JsonDeserializer<Date> {
        @Override // com.google.gson.JsonDeserializer
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            String asString = jsonElement.getAsJsonPrimitive().getAsString();
            if (asString.endsWith("Z")) {
                asString = asString.replace("Z", "");
            }
            UTCDateConverter.defaultFormatNoMs.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                return UTCDateConverter.defaultFormatNoMs.parse(asString);
            } catch (ParseException unused) {
                Log.d(UTCDateConverter.TAG, "failed to parse date " + asString);
                return null;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0059 A[Catch: all -> 0x0095, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x000a, B:8:0x000c, B:10:0x0014, B:22:0x0059, B:23:0x005f, B:25:0x0067, B:27:0x0080, B:28:0x0083, B:29:0x0087, B:32:0x008a, B:33:0x0093, B:26:0x0074, B:12:0x001e, B:14:0x0026, B:15:0x002f, B:17:0x0037, B:18:0x0046, B:20:0x004e), top: B:40:0x0003, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0067 A[Catch: all -> 0x0095, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x000a, B:8:0x000c, B:10:0x0014, B:22:0x0059, B:23:0x005f, B:25:0x0067, B:27:0x0080, B:28:0x0083, B:29:0x0087, B:32:0x008a, B:33:0x0093, B:26:0x0074, B:12:0x001e, B:14:0x0026, B:15:0x002f, B:17:0x0037, B:18:0x0046, B:20:0x004e), top: B:40:0x0003, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0074 A[Catch: all -> 0x0095, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x000a, B:8:0x000c, B:10:0x0014, B:22:0x0059, B:23:0x005f, B:25:0x0067, B:27:0x0080, B:28:0x0083, B:29:0x0087, B:32:0x008a, B:33:0x0093, B:26:0x0074, B:12:0x001e, B:14:0x0026, B:15:0x002f, B:17:0x0037, B:18:0x0046, B:20:0x004e), top: B:40:0x0003, inners: #1 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static Date convert(String str) {
        TimeZone timeZone;
        SimpleDateFormat simpleDateFormat = null;
        synchronized (UTCDateConverter.class) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            try {
                if (str.endsWith("Z")) {
                    str = str.replace("Z", "");
                } else if (str.endsWith("+00:00")) {
                    str = str.replace("+00:00", "");
                } else if (str.endsWith("+01:00")) {
                    str = str.replace("+01:00", "");
                    timeZone = TimeZone.getTimeZone("GMT+01:00");
                    if (timeZone == null) {
                        timeZone = TimeZone.getTimeZone("GMT");
                    }
                    if (str.length() != 19) {
                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    } else {
                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                    }
                    simpleDateFormat.setTimeZone(timeZone);
                    return simpleDateFormat.parse(str);
                } else if (str.contains(".")) {
                    str = str.replaceAll("([.][0-9]{3})[0-9]*$", "$1");
                }
                return simpleDateFormat.parse(str);
            } catch (ParseException e) {
                Log.e(TAG, e.toString());
                return null;
            }
//            timeZone = null;
//            if (timeZone == null) {
//            }
//            if (str.length() != 19) {
//            }
//            simpleDateFormat.setTimeZone(timeZone);
        }
    }
}

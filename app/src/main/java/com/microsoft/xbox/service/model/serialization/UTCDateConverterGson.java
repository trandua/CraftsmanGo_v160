package com.microsoft.xbox.service.model.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.microsoft.xbox.toolkit.JavaUtil;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes3.dex */
public class UTCDateConverterGson {
    private static final int NO_MS_STRING_LENGTH = 19;
    private static SimpleDateFormat defaultFormatMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
    public static SimpleDateFormat defaultFormatNoMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
    public static SimpleDateFormat shortDateAlternateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
    public static SimpleDateFormat shortDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);

    /* loaded from: classes3.dex */
    public static class UTCDateConverterJSONDeserializer implements JsonDeserializer<Date> {
        @Override // com.google.gson.JsonDeserializer
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            return UTCDateConverterGson.convert(jsonElement.getAsJsonPrimitive().getAsString());
        }
    }

    /* loaded from: classes3.dex */
    public static class UTCDateConverterShortDateAlternateFormatJSONDeserializer implements JsonDeserializer<Date> {
        @Override // com.google.gson.JsonDeserializer
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            Date date;
            String asString = jsonElement.getAsJsonPrimitive().getAsString();
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            UTCDateConverterGson.shortDateFormat.setTimeZone(timeZone);
            try {
                date = UTCDateConverterGson.shortDateFormat.parse(asString);
            } catch (ParseException unused) {
                date = null;
            }
            if (date != null && date.getYear() + 1900 < 2000) {
                UTCDateConverterGson.shortDateAlternateFormat.setTimeZone(timeZone);
                try {
                    return UTCDateConverterGson.shortDateAlternateFormat.parse(asString);
                } catch (ParseException unused2) {
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
            UTCDateConverterGson.shortDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                return UTCDateConverterGson.shortDateFormat.parse(asString);
            } catch (ParseException unused) {
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
            UTCDateConverterGson.defaultFormatNoMs.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                return UTCDateConverterGson.defaultFormatNoMs.parse(asString);
            } catch (ParseException unused) {
                return null;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x006c A[Catch: all -> 0x0096, TRY_LEAVE, TryCatch #2 {, blocks: (B:4:0x0003, B:6:0x000a, B:8:0x000c, B:10:0x0014, B:20:0x0054, B:28:0x006c, B:29:0x0079, B:30:0x007d, B:32:0x007f, B:34:0x0081, B:35:0x008e, B:36:0x0092, B:38:0x0094, B:11:0x001d, B:13:0x0025, B:14:0x002e, B:16:0x0036, B:17:0x0044, B:19:0x004c), top: B:47:0x0003, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0081 A[Catch: all -> 0x0096, TRY_LEAVE, TryCatch #2 {, blocks: (B:4:0x0003, B:6:0x000a, B:8:0x000c, B:10:0x0014, B:20:0x0054, B:28:0x006c, B:29:0x0079, B:30:0x007d, B:32:0x007f, B:34:0x0081, B:35:0x008e, B:36:0x0092, B:38:0x0094, B:11:0x001d, B:13:0x0025, B:14:0x002e, B:16:0x0036, B:17:0x0044, B:19:0x004c), top: B:47:0x0003, inners: #0, #1 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static Date convert(String str) {
        boolean z;
        synchronized (UTCDateConverterGson.class) {
            if (JavaUtil.isNullOrEmpty(str)) {
                return null;
            }
            if (str.endsWith("Z")) {
                str = str.replace("Z", "");
            } else if (str.endsWith("+00:00")) {
                str = str.replace("+00:00", "");
            } else if (str.endsWith("+01:00")) {
                str = str.replace("+01:00", "");
                TimeZone.getTimeZone("GMT+01:00");
            } else if (str.contains(".")) {
                str = str.replaceAll("([.][0-9]{3})[0-9]*$", "$1");
            }
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            int length = str.length();
            if (length != 23 && length != 24) {
                z = false;
                if (!z) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
                    simpleDateFormat.setTimeZone(timeZone);
                    try {
                        return simpleDateFormat.parse(str);
                    } catch (ParseException unused) {
                        return null;
                    }
                }
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                simpleDateFormat2.setTimeZone(timeZone);
                try {
                    return simpleDateFormat2.parse(str);
                } catch (ParseException unused2) {
                    return null;
                }
            }
            z = true;
//            if (!z) {
//            }
        }
        return null;
    }
}

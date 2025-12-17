package com.microsoft.aad.adal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes3.dex */
public final class DateTimeAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {
    private static final String TAG = "DateTimeAdapter";
    private final DateFormat mEnUs24HourFormat = buildEnUs24HourDateFormat();
    private final DateFormat mEnUsFormat = DateFormat.getDateTimeInstance(2, 2, Locale.US);
    private final DateFormat mISO8601Format = buildIso8601Format();
    private final DateFormat mLocal24HourFormat = buildLocal24HourDateFormat();
    private final DateFormat mLocalFormat = DateFormat.getDateTimeInstance(2, 2);

    private static DateFormat buildEnUs24HourDateFormat() {
        return new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.US);
    }

    private static DateFormat buildIso8601Format() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    private static DateFormat buildLocal24HourDateFormat() {
        return new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
    }

    @Override // com.google.gson.JsonDeserializer
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String str = "";
        Date parse;
        synchronized (this) {
            try {
                parse = this.mISO8601Format.parse(jsonElement.getAsString());
            } catch (ParseException e) {
                Logger.m14610e("DateTimeAdapter:deserialize", "Could not parse date. ", e.getMessage(), ADALError.DATE_PARSING_FAILURE, e);
                try {
                    parse = this.mLocalFormat.parse(str);
                } catch (ParseException unused) {
                    Logger.m14614v("DateTimeAdapter:deserialize", "Cannot parse with local format, try again with local 24 hour format.");
                    try {
                        parse = this.mLocal24HourFormat.parse(str);
                    } catch (ParseException unused2) {
                        Logger.m14614v("DateTimeAdapter:deserialize", "Cannot parse with local 24 hour format, try again with en us format.");
                        try {
                            parse = this.mEnUsFormat.parse(str);
                        } catch (ParseException unused3) {
                            Logger.m14614v("DateTimeAdapter:deserialize", "Cannot parse with en us format, try again with en us 24 hour format.");
                            try {
                                parse = this.mEnUs24HourFormat.parse(str);
                            } catch (ParseException e2) {
                                Logger.m14610e("DateTimeAdapter:deserialize", "Could not parse date. ", e2.getMessage(), ADALError.DATE_PARSING_FAILURE, e2);
                                throw new JsonParseException("Could not parse date: " + str);
                            }
                        }
                    }
                }
            }
        }
        return parse;
    }

    @Override // com.google.gson.JsonSerializer
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonPrimitive jsonPrimitive;
        synchronized (this) {
            jsonPrimitive = new JsonPrimitive(this.mISO8601Format.format(date));
        }
        return jsonPrimitive;
    }
}

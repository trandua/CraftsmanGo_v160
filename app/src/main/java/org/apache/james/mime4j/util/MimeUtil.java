package org.apache.james.mime4j.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import kotlin.text.Typography;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.field.ContentTypeField;

/* loaded from: classes.dex */
public final class MimeUtil {
    public static final String ENC_7BIT = "7bit";
    public static final String ENC_8BIT = "8bit";
    public static final String ENC_BASE64 = "base64";
    public static final String ENC_BINARY = "binary";
    public static final String ENC_QUOTED_PRINTABLE = "quoted-printable";
    public static final String MIME_HEADER_CONTENT_DESCRIPTION = "content-description";
    public static final String MIME_HEADER_CONTENT_DISPOSITION = "content-disposition";
    public static final String MIME_HEADER_CONTENT_ID = "content-id";
    public static final String MIME_HEADER_LANGAUGE = "content-language";
    public static final String MIME_HEADER_LOCATION = "content-location";
    public static final String MIME_HEADER_MD5 = "content-md5";
    public static final String MIME_HEADER_MIME_VERSION = "mime-version";
    public static final String PARAM_CREATION_DATE = "creation-date";
    public static final String PARAM_FILENAME = "filename";
    public static final String PARAM_MODIFICATION_DATE = "modification-date";
    public static final String PARAM_READ_DATE = "read-date";
    public static final String PARAM_SIZE = "size";
    private static final Log log = LogFactory.getLog(MimeUtil.class);
    private static final Random random = new Random();
    private static int counter = 0;
    private static final ThreadLocal<DateFormat> RFC822_DATE_FORMAT = new ThreadLocal<DateFormat>() { // from class: org.apache.james.mime4j.util.MimeUtil.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public DateFormat initialValue() {
            return new Rfc822DateFormat();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Rfc822DateFormat extends SimpleDateFormat {
        private static final long serialVersionUID = 1;

        public Rfc822DateFormat() {
            super("EEE, d MMM yyyy HH:mm:ss ", Locale.US);
        }

        @Override // java.text.SimpleDateFormat, java.text.DateFormat
        public StringBuffer format(Date date, StringBuffer stringBuffer, FieldPosition fieldPosition) {
            StringBuffer format = super.format(date, stringBuffer, fieldPosition);
            int i = ((this.calendar.get(15) + this.calendar.get(16)) / 1000) / 60;
            if (i < 0) {
                format.append('-');
                i = -i;
            } else {
                format.append('+');
            }
            format.append(String.format("%02d%02d", Integer.valueOf(i / 60), Integer.valueOf(i % 60)));
            return format;
        }
    }

    private MimeUtil() {
    }

    public static String createUniqueBoundary() {
        StringBuilder sb = new StringBuilder();
        sb.append("-=Part.");
        sb.append(Integer.toHexString(nextCounterValue()));
        sb.append('.');
        Random random2 = random;
        sb.append(Long.toHexString(random2.nextLong()));
        sb.append('.');
        sb.append(Long.toHexString(System.currentTimeMillis()));
        sb.append('.');
        sb.append(Long.toHexString(random2.nextLong()));
        sb.append("=-");
        return sb.toString();
    }

    public static String createUniqueMessageId(String str) {
        StringBuilder sb = new StringBuilder("<Mime4j.");
        sb.append(Integer.toHexString(nextCounterValue()));
        sb.append('.');
        sb.append(Long.toHexString(random.nextLong()));
        sb.append('.');
        sb.append(Long.toHexString(System.currentTimeMillis()));
        if (str != null) {
            sb.append('@');
            sb.append(str);
        }
        sb.append(Typography.greater);
        return sb.toString();
    }

    public static String fold(String str, int i) {
        int length = str.length();
        if (i + length <= 76) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        int i2 = -i;
        int indexOfWsp = indexOfWsp(str, 0);
        while (indexOfWsp != length) {
            int indexOfWsp2 = indexOfWsp(str, indexOfWsp + 1);
            if (indexOfWsp2 - i2 > 76) {
                sb.append(str.substring(Math.max(0, i2), indexOfWsp));
                sb.append(CharsetUtil.CRLF);
                i2 = indexOfWsp;
            }
            indexOfWsp = indexOfWsp2;
        }
        sb.append(str.substring(Math.max(0, i2)));
        return sb.toString();
    }

    public static String formatDate(Date date, TimeZone timeZone) {
        DateFormat dateFormat = RFC822_DATE_FORMAT.get();
        if (timeZone == null) {
            dateFormat.setTimeZone(TimeZone.getDefault());
        } else {
            dateFormat.setTimeZone(timeZone);
        }
        return dateFormat.format(date);
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x006a, code lost:
        if (r11 != ';') goto L_0x006e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00bb, code lost:
        if (r4 == false) goto L_0x006e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00e4, code lost:
        if (r4 == false) goto L_0x006e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00ed, code lost:
        if (r11 != ';') goto L_0x00ef;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00f3, code lost:
        r8 = 0;
     */
    /* JADX WARN: Removed duplicated region for block: B:64:0x010c  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0115  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.Map<java.lang.String, java.lang.String> getHeaderParams(java.lang.String r17) {
        /*
            Method dump skipped, instructions count: 312
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.james.mime4j.util.MimeUtil.getHeaderParams(java.lang.String):java.util.Map");
    }

    private static int indexOfWsp(String str, int i) {
        int length = str.length();
        while (i < length) {
            char charAt = str.charAt(i);
            if (charAt == ' ' || charAt == '\t') {
                return i;
            }
            i++;
        }
        return length;
    }

    public static boolean isBase64Encoding(String str) {
        return ENC_BASE64.equalsIgnoreCase(str);
    }

    public static boolean isMessage(String str) {
        return str != null && str.equalsIgnoreCase(ContentTypeField.TYPE_MESSAGE_RFC822);
    }

    public static boolean isMultipart(String str) {
        return str != null && str.toLowerCase().startsWith(ContentTypeField.TYPE_MULTIPART_PREFIX);
    }

    public static boolean isQuotedPrintableEncoded(String str) {
        return ENC_QUOTED_PRINTABLE.equalsIgnoreCase(str);
    }

    public static boolean isSameMimeType(String str, String str2) {
        return (str == null || str2 == null || !str.equalsIgnoreCase(str2)) ? false : true;
    }

    private static synchronized int nextCounterValue() {
        int i;
        synchronized (MimeUtil.class) {
            i = counter;
            counter = i + 1;
        }
        return i;
    }

    public static String unfold(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt == '\r' || charAt == '\n') {
                return unfold0(str, i);
            }
        }
        return str;
    }

    private static String unfold0(String str, int i) {
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        if (i > 0) {
            sb.append(str.substring(0, i));
        }
        while (true) {
            i++;
            if (i >= length) {
                return sb.toString();
            }
            char charAt = str.charAt(i);
            if (!(charAt == '\r' || charAt == '\n')) {
                sb.append(charAt);
            }
        }
    }
}

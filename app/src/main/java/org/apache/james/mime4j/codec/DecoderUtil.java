package org.apache.james.mime4j.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.util.CharsetUtil;

/* loaded from: classes.dex */
public class DecoderUtil {
    private static Log log = LogFactory.getLog(DecoderUtil.class);

    public static String decodeB(String str, String str2) throws UnsupportedEncodingException {
        return new String(decodeBase64(str), str2);
    }

    public static byte[] decodeBase64(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            Base64InputStream base64InputStream = new Base64InputStream(new ByteArrayInputStream(str.getBytes("US-ASCII")));
            while (true) {
                int read = base64InputStream.read();
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(read);
            }
        } catch (IOException e) {
            log.error(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] decodeBaseQuotedPrintable(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            QuotedPrintableInputStream quotedPrintableInputStream = new QuotedPrintableInputStream(new ByteArrayInputStream(str.getBytes("US-ASCII")));
            while (true) {
                int read = quotedPrintableInputStream.read();
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(read);
            }
        } catch (IOException e) {
            log.error(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static String decodeEncodedWord(String str, int i, int i2) {
        int i3;
        int indexOf;
        int i4 = i + 2;
        int indexOf2 = str.indexOf(63, i4);
        int i5 = i2 - 2;
        if (indexOf2 == i5 || (indexOf = str.indexOf(63, (i3 = indexOf2 + 1))) == i5) {
            return null;
        }
        String substring = str.substring(i4, indexOf2);
        String substring2 = str.substring(i3, indexOf);
        String substring3 = str.substring(indexOf + 1, i5);
        String javaCharset = CharsetUtil.toJavaCharset(substring);
        if (javaCharset == null) {
            if (log.isWarnEnabled()) {
                log.warn("MIME charset '" + substring + "' in encoded word '" + str.substring(i, i2) + "' doesn't have a corresponding Java charset");
            }
            return null;
        } else if (!CharsetUtil.isDecodingSupported(javaCharset)) {
            if (log.isWarnEnabled()) {
                log.warn("Current JDK doesn't support decoding of charset '" + javaCharset + "' (MIME charset '" + substring + "' in encoded word '" + str.substring(i, i2) + "')");
            }
            return null;
        } else if (substring3.length() == 0) {
            if (log.isWarnEnabled()) {
                log.warn("Missing encoded text in encoded word: '" + str.substring(i, i2) + "'");
            }
            return null;
        } else {
            try {
                if (substring2.equalsIgnoreCase("Q")) {
                    return decodeQ(substring3, javaCharset);
                }
                if (substring2.equalsIgnoreCase("B")) {
                    return decodeB(substring3, javaCharset);
                }
                if (log.isWarnEnabled()) {
                    log.warn("Warning: Unknown encoding in encoded word '" + str.substring(i, i2) + "'");
                }
                return null;
            } catch (UnsupportedEncodingException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Unsupported encoding in encoded word '" + str.substring(i, i2) + "'", e);
                }
                return null;
            } catch (RuntimeException e2) {
                if (log.isWarnEnabled()) {
                    log.warn("Could not decode encoded word '" + str.substring(i, i2) + "'", e2);
                }
                return null;
            }
        }
    }

    public static String decodeEncodedWords(String str) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        boolean z = false;
        while (true) {
            int indexOf = str.indexOf("=?", i);
            int indexOf2 = indexOf == -1 ? -1 : str.indexOf("?=", indexOf + 2);
            if (indexOf2 == -1) {
                break;
            }
            int i2 = indexOf2 + 2;
            String substring = str.substring(i, indexOf);
            String decodeEncodedWord = decodeEncodedWord(str, indexOf, i2);
            if (decodeEncodedWord == null) {
                sb.append(substring);
                sb.append(str.substring(indexOf, i2));
            } else {
                if (!z || !CharsetUtil.isWhitespace(substring)) {
                    sb.append(substring);
                }
                sb.append(decodeEncodedWord);
            }
            z = decodeEncodedWord != null;
            i = i2;
        }
        if (i == 0) {
            return str;
        }
        sb.append(str.substring(i));
        return sb.toString();
    }

    public static String decodeQ(String str, String str2) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '_') {
                sb.append("=20");
            } else {
                sb.append(charAt);
            }
        }
        return new String(decodeBaseQuotedPrintable(sb.toString()), str2);
    }
}

package org.apache.james.mime4j.codec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Locale;
import kotlin.UByte;
import org.apache.james.mime4j.util.CharsetUtil;

/* loaded from: classes.dex */
public class EncoderUtil {
    private static final char BASE64_PAD = '=';
    private static final int ENCODED_WORD_MAX_LENGTH = 75;
    private static final String ENC_WORD_PREFIX = "=?";
    private static final String ENC_WORD_SUFFIX = "?=";
    private static final int MAX_USED_CHARACTERS = 50;
    private static final byte[] BASE64_TABLE = Base64OutputStream.BASE64_TABLE;
    private static final BitSet Q_REGULAR_CHARS = initChars("=_?");
    private static final BitSet Q_RESTRICTED_CHARS = initChars("=_?\"#$%&'(),.:;<>@[\\]^`{|}~");
    private static final BitSet TOKEN_CHARS = initChars("()<>@,;:\\\"/[]?=");
    private static final BitSet ATEXT_CHARS = initChars("()<>@.,;:\\\"[]");

    /* loaded from: classes.dex */
    public enum Encoding {
        B,
        Q
    }

    /* loaded from: classes.dex */
    public enum Usage {
        TEXT_TOKEN,
        WORD_ENTITY
    }

    private EncoderUtil() {
    }

    private static int bEncodedLength(byte[] bArr) {
        return ((bArr.length + 2) / 3) * 4;
    }

    private static Charset determineCharset(String str) {
        int length = str.length();
        boolean z = true;
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt > 255) {
                return CharsetUtil.UTF_8;
            }
            if (charAt > 127) {
                z = false;
            }
        }
        return z ? CharsetUtil.US_ASCII : CharsetUtil.ISO_8859_1;
    }

    private static Encoding determineEncoding(byte[] bArr, Usage usage) {
        if (bArr.length == 0) {
            return Encoding.Q;
        }
        BitSet bitSet = usage == Usage.TEXT_TOKEN ? Q_REGULAR_CHARS : Q_RESTRICTED_CHARS;
        int i = 0;
        for (byte b : bArr) {
            int i2 = b & UByte.MAX_VALUE;
            if (i2 != 32 && !bitSet.get(i2)) {
                i++;
            }
        }
        return (i * 100) / bArr.length > 30 ? Encoding.B : Encoding.Q;
    }

    private static byte[] encode(String str, Charset charset) {
        ByteBuffer encode = charset.encode(str);
        byte[] bArr = new byte[encode.limit()];
        encode.get(bArr);
        return bArr;
    }

    public static String encodeAddressDisplayName(String str) {
        return isAtomPhrase(str) ? str : hasToBeEncoded(str, 0) ? encodeEncodedWord(str, Usage.WORD_ENTITY) : quote(str);
    }

    public static String encodeAddressLocalPart(String str) {
        return isDotAtomText(str) ? str : quote(str);
    }

    private static String encodeB(String str, String str2, int i, Charset charset, byte[] bArr) {
        if (str.length() + bEncodedLength(bArr) + 2 <= 75 - i) {
            return str + encodeB(bArr) + ENC_WORD_SUFFIX;
        }
        String substring = str2.substring(0, str2.length() / 2);
        String encodeB = encodeB(str, substring, i, charset, encode(substring, charset));
        String substring2 = str2.substring(str2.length() / 2);
        String encodeB2 = encodeB(str, substring2, 0, charset, encode(substring2, charset));
        return encodeB + " " + encodeB2;
    }

    public static String encodeB(byte[] bArr) {
        int i;
        StringBuilder sb = new StringBuilder();
        int length = bArr.length;
        int i2 = 0;
        while (true) {
            i = length - 2;
            if (i2 >= i) {
                break;
            }
            int i3 = ((bArr[i2] & UByte.MAX_VALUE) << 16) | ((bArr[i2 + 1] & UByte.MAX_VALUE) << 8) | (bArr[i2 + 2] & UByte.MAX_VALUE);
            byte[] bArr2 = BASE64_TABLE;
            sb.append((char) bArr2[(i3 >> 18) & 63]);
            sb.append((char) bArr2[(i3 >> 12) & 63]);
            sb.append((char) bArr2[(i3 >> 6) & 63]);
            sb.append((char) bArr2[i3 & 63]);
            i2 += 3;
        }
        if (i2 == i) {
            int i4 = ((bArr[i2 + 1] & UByte.MAX_VALUE) << 8) | ((bArr[i2] & UByte.MAX_VALUE) << 16);
            byte[] bArr3 = BASE64_TABLE;
            sb.append((char) bArr3[(i4 >> 18) & 63]);
            sb.append((char) bArr3[(i4 >> 12) & 63]);
            sb.append((char) bArr3[(i4 >> 6) & 63]);
            sb.append(BASE64_PAD);
        } else if (i2 == length - 1) {
            int i5 = (bArr[i2] & UByte.MAX_VALUE) << 16;
            byte[] bArr4 = BASE64_TABLE;
            sb.append((char) bArr4[(i5 >> 18) & 63]);
            sb.append((char) bArr4[(i5 >> 12) & 63]);
            sb.append(BASE64_PAD);
            sb.append(BASE64_PAD);
        }
        return sb.toString();
    }

    public static String encodeEncodedWord(String str, Usage usage) {
        return encodeEncodedWord(str, usage, 0, null, null);
    }

    public static String encodeEncodedWord(String str, Usage usage, int i) {
        return encodeEncodedWord(str, usage, i, null, null);
    }

    public static String encodeEncodedWord(String str, Usage usage, int i, Charset charset, Encoding encoding) {
        if (str == null) {
            throw new IllegalArgumentException();
        } else if (i < 0 || i > 50) {
            throw new IllegalArgumentException();
        } else {
            if (charset == null) {
                charset = determineCharset(str);
            }
            String mimeCharset = CharsetUtil.toMimeCharset(charset.name());
            if (mimeCharset != null) {
                byte[] encode = encode(str, charset);
                if (encoding == null) {
                    encoding = determineEncoding(encode, usage);
                }
                if (encoding == Encoding.B) {
                    return encodeB(ENC_WORD_PREFIX + mimeCharset + "?B?", str, i, charset, encode);
                }
                return encodeQ(ENC_WORD_PREFIX + mimeCharset + "?Q?", str, usage, i, charset, encode);
            }
            throw new IllegalArgumentException("Unsupported charset");
        }
    }

    public static String encodeHeaderParameter(String str, String str2) {
        String lowerCase = str.toLowerCase(Locale.US);
        if (isToken(str2)) {
            return lowerCase + "=" + str2;
        }
        return lowerCase + "=" + quote(str2);
    }

    public static String encodeIfNecessary(String str, Usage usage, int i) {
        return hasToBeEncoded(str, i) ? encodeEncodedWord(str, usage, i) : str;
    }

    private static String encodeQ(String str, String str2, Usage usage, int i, Charset charset, byte[] bArr) {
        if (str.length() + qEncodedLength(bArr, usage) + 2 <= 75 - i) {
            return str + encodeQ(bArr, usage) + ENC_WORD_SUFFIX;
        }
        String substring = str2.substring(0, str2.length() / 2);
        String encodeQ = encodeQ(str, substring, usage, i, charset, encode(substring, charset));
        String substring2 = str2.substring(str2.length() / 2);
        String encodeQ2 = encodeQ(str, substring2, usage, 0, charset, encode(substring2, charset));
        return encodeQ + " " + encodeQ2;
    }

    public static String encodeQ(byte[] bArr, Usage usage) {
        BitSet bitSet = usage == Usage.TEXT_TOKEN ? Q_REGULAR_CHARS : Q_RESTRICTED_CHARS;
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            int i = b & UByte.MAX_VALUE;
            if (i == 32) {
                sb.append('_');
            } else if (!bitSet.get(i)) {
                sb.append(BASE64_PAD);
                sb.append(hexDigit(i >>> 4));
                sb.append(hexDigit(i & 15));
            } else {
                sb.append((char) i);
            }
        }
        return sb.toString();
    }

    public static boolean hasToBeEncoded(String str, int i) {
        if (str == null) {
            throw new IllegalArgumentException();
        } else if (i < 0 || i > 50) {
            throw new IllegalArgumentException();
        } else {
            for (int i2 = 0; i2 < str.length(); i2++) {
                char charAt = str.charAt(i2);
                if (charAt == '\t' || charAt == ' ') {
                    i = 0;
                } else {
                    i++;
                    if (i > 77 || charAt < ' ' || charAt >= 127) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static char hexDigit(int i) {
        return (char) (i < 10 ? i + 48 : (i - 10) + 65);
    }

    private static BitSet initChars(String str) {
        BitSet bitSet = new BitSet(128);
        for (char c = '!'; c < 127; c = (char) (c + 1)) {
            if (str.indexOf(c) == -1) {
                bitSet.set(c);
            }
        }
        return bitSet;
    }

    private static boolean isAtomPhrase(String str) {
        int length = str.length();
        boolean z = false;
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (ATEXT_CHARS.get(charAt)) {
                z = true;
            } else if (!CharsetUtil.isWhitespace(charAt)) {
                return false;
            }
        }
        return z;
    }

    private static boolean isDotAtomText(String str) {
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        char c = '.';
        while (i < length) {
            char charAt = str.charAt(i);
            if (charAt == '.') {
                if (c == '.' || i == length - 1) {
                    return false;
                }
            } else if (!ATEXT_CHARS.get(charAt)) {
                return false;
            }
            i++;
            c = charAt;
        }
        return true;
    }

    public static boolean isToken(String str) {
        int length = str.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!TOKEN_CHARS.get(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static int qEncodedLength(byte[] bArr, Usage usage) {
        BitSet bitSet = usage == Usage.TEXT_TOKEN ? Q_REGULAR_CHARS : Q_RESTRICTED_CHARS;
        int i = 0;
        for (byte b : bArr) {
            int i2 = b & UByte.MAX_VALUE;
            i = (i2 != 32 && !bitSet.get(i2)) ? i + 3 : i + 1;
        }
        return i;
    }

    private static String quote(String str) {
        String replaceAll = str.replaceAll("[\\\\\"]", "\\\\$0");
        return "\"" + replaceAll + "\"";
    }
}

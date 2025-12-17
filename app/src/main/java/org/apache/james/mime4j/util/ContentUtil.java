package org.apache.james.mime4j.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public class ContentUtil {
    private ContentUtil() {
    }

    public static String decode(Charset charset, ByteSequence byteSequence) {
        return decode(charset, byteSequence, 0, byteSequence.length());
    }

    public static String decode(Charset charset, ByteSequence byteSequence, int i, int i2) {
        return byteSequence instanceof ByteArrayBuffer ? decode(charset, ((ByteArrayBuffer) byteSequence).buffer(), i, i2) : decode(charset, byteSequence.toByteArray(), i, i2);
    }

    private static String decode(Charset charset, byte[] bArr, int i, int i2) {
        return charset.decode(ByteBuffer.wrap(bArr, i, i2)).toString();
    }

    public static String decode(ByteSequence byteSequence) {
        return decode(CharsetUtil.US_ASCII, byteSequence, 0, byteSequence.length());
    }

    public static String decode(ByteSequence byteSequence, int i, int i2) {
        return decode(CharsetUtil.US_ASCII, byteSequence, i, i2);
    }

    public static ByteSequence encode(String str) {
        return encode(CharsetUtil.US_ASCII, str);
    }

    public static ByteSequence encode(Charset charset, String str) {
        ByteBuffer encode = charset.encode(CharBuffer.wrap(str));
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(encode.remaining());
        byteArrayBuffer.append(encode.array(), encode.position(), encode.remaining());
        return byteArrayBuffer;
    }
}

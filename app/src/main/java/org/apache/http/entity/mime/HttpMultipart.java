package org.apache.http.entity.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.james.mime4j.field.ContentTypeField;
import org.apache.james.mime4j.message.Body;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.MessageWriter;
import org.apache.james.mime4j.message.Multipart;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.util.ByteArrayBuffer;
import org.apache.james.mime4j.util.ByteSequence;
import org.apache.james.mime4j.util.CharsetUtil;

/* loaded from: classes.dex */
public class HttpMultipart extends Multipart {
    private static final ByteArrayBuffer CR_LF = encode(MIME.DEFAULT_CHARSET, CharsetUtil.CRLF);
    private static final ByteArrayBuffer TWO_DASHES = encode(MIME.DEFAULT_CHARSET, "--");
    private HttpMultipartMode mode = HttpMultipartMode.STRICT;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.apache.http.entity.mime.HttpMultipart$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$http$entity$mime$HttpMultipartMode;

        static {
            int[] iArr = new int[HttpMultipartMode.values().length];
            $SwitchMap$org$apache$http$entity$mime$HttpMultipartMode = iArr;
            try {
                iArr[HttpMultipartMode.STRICT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$apache$http$entity$mime$HttpMultipartMode[HttpMultipartMode.BROWSER_COMPATIBLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public HttpMultipart(String str) {
        super(str);
    }

    private void doWriteTo(HttpMultipartMode httpMultipartMode, OutputStream outputStream, boolean z) throws IOException {
        List<BodyPart> bodyParts = getBodyParts();
        Charset charset = getCharset();
        ByteArrayBuffer encode = encode(charset, getBoundary());
        int i = AnonymousClass1.$SwitchMap$org$apache$http$entity$mime$HttpMultipartMode[httpMultipartMode.ordinal()];
        int i2 = 0;
        if (i == 1) {
            String preamble = getPreamble();
            if (!(preamble == null || preamble.length() == 0)) {
                writeBytes(encode(charset, preamble), outputStream);
                writeBytes(CR_LF, outputStream);
            }
            while (i2 < bodyParts.size()) {
                writeBytes(TWO_DASHES, outputStream);
                writeBytes(encode, outputStream);
                writeBytes(CR_LF, outputStream);
                BodyPart bodyPart = bodyParts.get(i2);
                for (Field field : bodyPart.getHeader().getFields()) {
                    writeBytes(field.getRaw(), outputStream);
                    writeBytes(CR_LF, outputStream);
                }
                ByteArrayBuffer byteArrayBuffer = CR_LF;
                writeBytes(byteArrayBuffer, outputStream);
                if (z) {
                    MessageWriter.DEFAULT.writeBody(bodyPart.getBody(), outputStream);
                }
                writeBytes(byteArrayBuffer, outputStream);
                i2++;
            }
            ByteArrayBuffer byteArrayBuffer2 = TWO_DASHES;
            writeBytes(byteArrayBuffer2, outputStream);
            writeBytes(encode, outputStream);
            writeBytes(byteArrayBuffer2, outputStream);
            ByteArrayBuffer byteArrayBuffer3 = CR_LF;
            writeBytes(byteArrayBuffer3, outputStream);
            String epilogue = getEpilogue();
            if (!(epilogue == null || epilogue.length() == 0)) {
                writeBytes(encode(charset, epilogue), outputStream);
                writeBytes(byteArrayBuffer3, outputStream);
            }
        } else if (i == 2) {
            while (i2 < bodyParts.size()) {
                writeBytes(TWO_DASHES, outputStream);
                writeBytes(encode, outputStream);
                ByteArrayBuffer byteArrayBuffer4 = CR_LF;
                writeBytes(byteArrayBuffer4, outputStream);
                BodyPart bodyPart2 = bodyParts.get(i2);
                Field field2 = bodyPart2.getHeader().getField("Content-Disposition");
                writeBytes(encode(charset, field2.getName() + ": " + field2.getBody()), outputStream);
                writeBytes(byteArrayBuffer4, outputStream);
                writeBytes(byteArrayBuffer4, outputStream);
                if (z) {
                    MessageWriter.DEFAULT.writeBody(bodyPart2.getBody(), outputStream);
                }
                writeBytes(byteArrayBuffer4, outputStream);
                i2++;
            }
            ByteArrayBuffer byteArrayBuffer5 = TWO_DASHES;
            writeBytes(byteArrayBuffer5, outputStream);
            writeBytes(encode, outputStream);
            writeBytes(byteArrayBuffer5, outputStream);
            writeBytes(CR_LF, outputStream);
        }
    }

    private static ByteArrayBuffer encode(Charset charset, String str) {
        ByteBuffer encode = charset.encode(CharBuffer.wrap(str));
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(encode.remaining());
        byteArrayBuffer.append(encode.array(), encode.position(), encode.remaining());
        return byteArrayBuffer;
    }

    private static void writeBytes(ByteArrayBuffer byteArrayBuffer, OutputStream outputStream) throws IOException {
        outputStream.write(byteArrayBuffer.buffer(), 0, byteArrayBuffer.length());
    }

    private static void writeBytes(ByteSequence byteSequence, OutputStream outputStream) throws IOException {
        if (byteSequence instanceof ByteArrayBuffer) {
            writeBytes((ByteArrayBuffer) byteSequence, outputStream);
        } else {
            outputStream.write(byteSequence.toByteArray());
        }
    }

    protected String getBoundary() {
        return ((ContentTypeField) getParent().getHeader().getField("Content-Type")).getBoundary();
    }

    protected Charset getCharset() {
        ContentTypeField contentTypeField = (ContentTypeField) getParent().getHeader().getField("Content-Type");
        int i = AnonymousClass1.$SwitchMap$org$apache$http$entity$mime$HttpMultipartMode[this.mode.ordinal()];
        if (i == 1) {
            return MIME.DEFAULT_CHARSET;
        }
        if (i != 2) {
            return null;
        }
        return contentTypeField.getCharset() != null ? CharsetUtil.getCharset(contentTypeField.getCharset()) : CharsetUtil.getCharset("ISO-8859-1");
    }

    public HttpMultipartMode getMode() {
        return this.mode;
    }

    public long getTotalLength() {
        ByteArrayOutputStream byteArrayOutputStream = null;
        List<BodyPart> bodyParts = getBodyParts();
        long j = 0;
        for (int i = 0; i < bodyParts.size(); i++) {
            Body body = bodyParts.get(i).getBody();
            if (body instanceof ContentBody) {
                long contentLength = ((ContentBody) body).getContentLength();
                if (contentLength >= 0) {
                    j += contentLength;
                }
            }
            return -1L;
        }
        try {
            doWriteTo(this.mode, new ByteArrayOutputStream(), false);
            return j + byteArrayOutputStream.toByteArray().length;
        } catch (IOException unused) {
            return -1L;
        }
    }

    public void setMode(HttpMultipartMode httpMultipartMode) {
        this.mode = httpMultipartMode;
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        doWriteTo(this.mode, outputStream, true);
    }
}

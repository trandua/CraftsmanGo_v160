package org.apache.james.mime4j.message;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import org.apache.james.mime4j.util.CharsetUtil;

/* loaded from: classes.dex */
class StringTextBody extends TextBody {
    private final Charset charset;
    private final String text;

    public StringTextBody(String str, Charset charset) {
        this.text = str;
        this.charset = charset;
    }

    @Override // org.apache.james.mime4j.message.SingleBody
    public StringTextBody copy() {
        return new StringTextBody(this.text, this.charset);
    }

    @Override // org.apache.james.mime4j.message.TextBody
    public String getMimeCharset() {
        return CharsetUtil.toMimeCharset(this.charset.name());
    }

    @Override // org.apache.james.mime4j.message.TextBody
    public Reader getReader() throws IOException {
        return new StringReader(this.text);
    }

    @Override // org.apache.james.mime4j.message.SingleBody
    public void writeTo(OutputStream outputStream) throws IOException {
        if (outputStream != null) {
            StringReader stringReader = new StringReader(this.text);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, this.charset);
            char[] cArr = new char[1024];
            while (true) {
                int read = stringReader.read(cArr);
                if (read == -1) {
                    stringReader.close();
                    outputStreamWriter.flush();
                    return;
                }
                outputStreamWriter.write(cArr, 0, read);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}

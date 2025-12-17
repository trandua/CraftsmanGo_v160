package org.apache.james.mime4j.parser;

import java.io.IOException;
import java.io.InputStream;
import org.apache.james.mime4j.MimeException;

/* loaded from: classes.dex */
public class MimeStreamParser {
    private boolean contentDecoding;
    private ContentHandler handler;
    private final MimeTokenStream mimeTokenStream;

    public MimeStreamParser() {
        this(null);
    }

    public MimeStreamParser(MimeEntityConfig mimeEntityConfig) {
        this.handler = null;
        this.mimeTokenStream = new MimeTokenStream(mimeEntityConfig != null ? mimeEntityConfig.clone() : new MimeEntityConfig());
        this.contentDecoding = false;
    }

    public boolean isContentDecoding() {
        return this.contentDecoding;
    }

    public boolean isRaw() {
        return this.mimeTokenStream.isRaw();
    }

    public void parse(InputStream inputStream) throws MimeException, IOException {
        this.mimeTokenStream.parse(inputStream);
        while (true) {
            int state = this.mimeTokenStream.getState();
            switch (state) {
                case -1:
                    return;
                case 0:
                    this.handler.startMessage();
                    break;
                case 1:
                    this.handler.endMessage();
                    break;
                case 2:
                    this.handler.raw(this.mimeTokenStream.getInputStream());
                    break;
                case 3:
                    this.handler.startHeader();
                    break;
                case 4:
                    this.handler.field(this.mimeTokenStream.getField());
                    break;
                case 5:
                    this.handler.endHeader();
                    break;
                case 6:
                    this.handler.startMultipart(this.mimeTokenStream.getBodyDescriptor());
                    break;
                case 7:
                    this.handler.endMultipart();
                    break;
                case 8:
                    this.handler.preamble(this.mimeTokenStream.getInputStream());
                    break;
                case 9:
                    this.handler.epilogue(this.mimeTokenStream.getInputStream());
                    break;
                case 10:
                    this.handler.startBodyPart();
                    break;
                case 11:
                    this.handler.endBodyPart();
                    break;
                case 12:
                    this.handler.body(this.mimeTokenStream.getBodyDescriptor(), this.contentDecoding ? this.mimeTokenStream.getDecodedInputStream() : this.mimeTokenStream.getInputStream());
                    break;
                default:
                    throw new IllegalStateException("Invalid state: " + state);
            }
            this.mimeTokenStream.next();
        }
    }

    public void setContentDecoding(boolean z) {
        this.contentDecoding = z;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.handler = contentHandler;
    }

    public void setRaw(boolean z) {
        this.mimeTokenStream.setRecursionMode(2);
    }

    public void stop() {
        this.mimeTokenStream.stop();
    }
}

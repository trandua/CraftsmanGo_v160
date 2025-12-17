package org.apache.james.mime4j.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.codec.Base64InputStream;
import org.apache.james.mime4j.codec.QuotedPrintableInputStream;
import org.apache.james.mime4j.descriptor.BodyDescriptor;
import org.apache.james.mime4j.io.BufferedLineReaderInputStream;
import org.apache.james.mime4j.util.CharsetUtil;
import org.apache.james.mime4j.util.MimeUtil;

/* loaded from: classes.dex */
public class MimeTokenStream implements EntityStates, RecursionMode {
    private final MimeEntityConfig config;
    private EntityStateMachine currentStateMachine;
    private final LinkedList<EntityStateMachine> entities;
    private BufferedLineReaderInputStream inbuffer;
    private int recursionMode;
    private int state;

    public MimeTokenStream() {
        this(new MimeEntityConfig());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MimeTokenStream(MimeEntityConfig mimeEntityConfig) {
        this.entities = new LinkedList<>();
        this.state = -1;
        this.recursionMode = 0;
        this.config = mimeEntityConfig;
    }

    public static final MimeTokenStream createMaximalDescriptorStream() {
        MimeEntityConfig mimeEntityConfig = new MimeEntityConfig();
        mimeEntityConfig.setMaximalBodyDescriptor(true);
        return new MimeTokenStream(mimeEntityConfig);
    }

    public static final MimeTokenStream createStrictValidationStream() {
        MimeEntityConfig mimeEntityConfig = new MimeEntityConfig();
        mimeEntityConfig.setStrictParsing(true);
        return new MimeTokenStream(mimeEntityConfig);
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0031, code lost:
        if (r8 != 3) goto L_0x0055;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0, types: [org.apache.james.mime4j.io.LineNumberSource] */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Unknown variable types count: 1 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void doParse(java.io.InputStream r8, java.lang.String r9) {
        /*
            r7 = this;
            java.util.LinkedList<org.apache.james.mime4j.parser.EntityStateMachine> r0 = r7.entities
            r0.clear()
            org.apache.james.mime4j.parser.MimeEntityConfig r0 = r7.config
            boolean r0 = r0.isCountLineNumbers()
            if (r0 == 0) goto L_0x0015
            org.apache.james.mime4j.io.LineNumberInputStream r0 = new org.apache.james.mime4j.io.LineNumberInputStream
            r0.<init>(r8)
            r8 = r0
            r1 = r8
            goto L_0x0017
        L_0x0015:
            r0 = 0
            r1 = r0
        L_0x0017:
            org.apache.james.mime4j.io.BufferedLineReaderInputStream r0 = new org.apache.james.mime4j.io.BufferedLineReaderInputStream
            r2 = 4096(0x1000, float:5.74E-42)
            org.apache.james.mime4j.parser.MimeEntityConfig r3 = r7.config
            int r3 = r3.getMaxLineLen()
            r0.<init>(r8, r2, r3)
            r7.inbuffer = r0
            int r8 = r7.recursionMode
            if (r8 == 0) goto L_0x003c
            r2 = 1
            if (r8 == r2) goto L_0x003c
            r2 = 2
            if (r8 == r2) goto L_0x0034
            r0 = 3
            if (r8 == r0) goto L_0x003c
            goto L_0x0055
        L_0x0034:
            org.apache.james.mime4j.parser.RawEntity r8 = new org.apache.james.mime4j.parser.RawEntity
            r8.<init>(r0)
            r7.currentStateMachine = r8
            goto L_0x0055
        L_0x003c:
            org.apache.james.mime4j.parser.MimeEntity r8 = new org.apache.james.mime4j.parser.MimeEntity
            org.apache.james.mime4j.io.BufferedLineReaderInputStream r2 = r7.inbuffer
            r3 = 0
            r4 = 0
            r5 = 1
            org.apache.james.mime4j.parser.MimeEntityConfig r6 = r7.config
            r0 = r8
            r0.<init>(r1, r2, r3, r4, r5, r6)
            int r0 = r7.recursionMode
            r8.setRecursionMode(r0)
            if (r9 == 0) goto L_0x0053
            r8.skipHeader(r9)
        L_0x0053:
            r7.currentStateMachine = r8
        L_0x0055:
            java.util.LinkedList<org.apache.james.mime4j.parser.EntityStateMachine> r8 = r7.entities
            org.apache.james.mime4j.parser.EntityStateMachine r9 = r7.currentStateMachine
            r8.add(r9)
            org.apache.james.mime4j.parser.EntityStateMachine r8 = r7.currentStateMachine
            int r8 = r8.getState()
            r7.state = r8
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.james.mime4j.parser.MimeTokenStream.doParse(java.io.InputStream, java.lang.String):void");
    }

    public static final String stateToString(int i) {
        return AbstractEntity.stateToString(i);
    }

    public BodyDescriptor getBodyDescriptor() {
        return this.currentStateMachine.getBodyDescriptor();
    }

    public InputStream getDecodedInputStream() {
        InputStream quotedPrintableInputStream;
        String transferEncoding = getBodyDescriptor().getTransferEncoding();
        InputStream contentStream = this.currentStateMachine.getContentStream();
        if (MimeUtil.isBase64Encoding(transferEncoding)) {
            quotedPrintableInputStream = new Base64InputStream(contentStream);
        } else if (!MimeUtil.isQuotedPrintableEncoded(transferEncoding)) {
            return contentStream;
        } else {
            quotedPrintableInputStream = new QuotedPrintableInputStream(contentStream);
        }
        return quotedPrintableInputStream;
    }

    public Field getField() {
        return this.currentStateMachine.getField();
    }

    public InputStream getInputStream() {
        return this.currentStateMachine.getContentStream();
    }

    public Reader getReader() {
        String charset = getBodyDescriptor().getCharset();
        return new InputStreamReader(getDecodedInputStream(), (charset == null || "".equals(charset)) ? CharsetUtil.US_ASCII : Charset.forName(charset));
    }

    public int getRecursionMode() {
        return this.recursionMode;
    }

    public int getState() {
        return this.state;
    }

    public boolean isRaw() {
        return this.recursionMode == 2;
    }

    public int next() throws IOException, MimeException {
        if (this.state == -1 || this.currentStateMachine == null) {
            throw new IllegalStateException("No more tokens are available.");
        }
        while (true) {
            EntityStateMachine entityStateMachine = this.currentStateMachine;
            if (entityStateMachine != null) {
                EntityStateMachine advance = entityStateMachine.advance();
                if (advance != null) {
                    this.entities.add(advance);
                    this.currentStateMachine = advance;
                }
                int state = this.currentStateMachine.getState();
                this.state = state;
                if (state != -1) {
                    return state;
                }
                this.entities.removeLast();
                if (this.entities.isEmpty()) {
                    this.currentStateMachine = null;
                } else {
                    EntityStateMachine last = this.entities.getLast();
                    this.currentStateMachine = last;
                    last.setRecursionMode(this.recursionMode);
                }
            } else {
                this.state = -1;
                return -1;
            }
        }
    }

    public void parse(InputStream inputStream) {
        doParse(inputStream, null);
    }

    public void parseHeadless(InputStream inputStream, String str) {
        if (str != null) {
            doParse(inputStream, str);
            return;
        }
        throw new IllegalArgumentException("Content type may not be null");
    }

    public void setRecursionMode(int i) {
        this.recursionMode = i;
        EntityStateMachine entityStateMachine = this.currentStateMachine;
        if (entityStateMachine != null) {
            entityStateMachine.setRecursionMode(i);
        }
    }

    public void stop() {
        this.inbuffer.truncate();
    }
}

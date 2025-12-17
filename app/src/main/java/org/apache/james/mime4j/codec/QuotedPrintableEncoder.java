package org.apache.james.mime4j.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import kotlin.UByte;

/* loaded from: classes.dex */
final class QuotedPrintableEncoder {
    private static final byte CR = 13;
    private static final byte EQUALS = 61;
    private static final byte[] HEX_DIGITS = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
    private static final byte LF = 10;
    private static final byte QUOTED_PRINTABLE_LAST_PLAIN = 126;
    private static final int QUOTED_PRINTABLE_MAX_LINE_LENGTH = 76;
    private static final int QUOTED_PRINTABLE_OCTETS_PER_ESCAPE = 3;
    private static final byte SPACE = 32;
    private static final byte TAB = 9;
    private final boolean binary;
    private final byte[] inBuffer;
    private final byte[] outBuffer;
    private int outputIndex = 0;
    private int nextSoftBreak = 77;
    private OutputStream out = null;
    private boolean pendingSpace = false;
    private boolean pendingTab = false;
    private boolean pendingCR = false;

    public QuotedPrintableEncoder(int i, boolean z) {
        this.inBuffer = new byte[i];
        this.outBuffer = new byte[i * 3];
        this.binary = z;
    }

    private void clearPending() throws IOException {
        this.pendingSpace = false;
        this.pendingTab = false;
        this.pendingCR = false;
    }

    private void encode(byte b) throws IOException {
        if (b == 10) {
            if (this.binary) {
                writePending();
                escape(b);
            } else if (this.pendingCR) {
                if (this.pendingSpace) {
                    escape((byte) 32);
                } else if (this.pendingTab) {
                    escape((byte) 9);
                }
                lineBreak();
                clearPending();
            } else {
                writePending();
                plain(b);
            }
        } else if (b != 13) {
            writePending();
            if (b == 32) {
                if (this.binary) {
                    escape(b);
                } else {
                    this.pendingSpace = true;
                }
            } else if (b == 9) {
                if (this.binary) {
                    escape(b);
                } else {
                    this.pendingTab = true;
                }
            } else if (b < 32) {
                escape(b);
            } else if (b > 126) {
                escape(b);
            } else if (b == 61) {
                escape(b);
            } else {
                plain(b);
            }
        } else if (this.binary) {
            escape(b);
        } else {
            this.pendingCR = true;
        }
    }

    private void escape(byte b) throws IOException {
        int i = this.nextSoftBreak - 1;
        this.nextSoftBreak = i;
        if (i <= 3) {
            softBreak();
        }
        int i2 = b & UByte.MAX_VALUE;
        write(EQUALS);
        this.nextSoftBreak--;
        byte[] bArr = HEX_DIGITS;
        write(bArr[i2 >> 4]);
        this.nextSoftBreak--;
        write(bArr[i2 % 16]);
    }

    private void lineBreak() throws IOException {
        write((byte) 13);
        write((byte) 10);
        this.nextSoftBreak = 76;
    }

    private void plain(byte b) throws IOException {
        int i = this.nextSoftBreak - 1;
        this.nextSoftBreak = i;
        if (i <= 1) {
            softBreak();
        }
        write(b);
    }

    private void softBreak() throws IOException {
        write(EQUALS);
        lineBreak();
    }

    private void write(byte b) throws IOException {
        byte[] bArr = this.outBuffer;
        int i = this.outputIndex;
        int i2 = i + 1;
        this.outputIndex = i2;
        bArr[i] = b;
        if (i2 >= bArr.length) {
            flushOutput();
        }
    }

    private void writePending() throws IOException {
        if (this.pendingSpace) {
            plain((byte) 32);
        } else if (this.pendingTab) {
            plain((byte) 9);
        } else if (this.pendingCR) {
            plain((byte) 13);
        }
        clearPending();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void completeEncoding() throws IOException {
        writePending();
        flushOutput();
    }

    public void encode(InputStream inputStream, OutputStream outputStream) throws IOException {
        initEncoding(outputStream);
        while (true) {
            int read = inputStream.read(this.inBuffer);
            if (read > -1) {
                encodeChunk(this.inBuffer, 0, read);
            } else {
                completeEncoding();
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void encodeChunk(byte[] bArr, int i, int i2) throws IOException {
        for (int i3 = i; i3 < i2 + i; i3++) {
            encode(bArr[i3]);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void flushOutput() throws IOException {
        int i = this.outputIndex;
        byte[] bArr = this.outBuffer;
        if (i < bArr.length) {
            this.out.write(bArr, 0, i);
        } else {
            this.out.write(bArr);
        }
        this.outputIndex = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initEncoding(OutputStream outputStream) {
        this.out = outputStream;
        this.pendingSpace = false;
        this.pendingTab = false;
        this.pendingCR = false;
        this.nextSoftBreak = 77;
    }
}

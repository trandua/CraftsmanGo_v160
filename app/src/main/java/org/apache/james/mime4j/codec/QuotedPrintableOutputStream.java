package org.apache.james.mime4j.codec;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class QuotedPrintableOutputStream extends FilterOutputStream {
    private boolean closed = false;
    private QuotedPrintableEncoder encoder;

    public QuotedPrintableOutputStream(OutputStream outputStream, boolean z) {
        super(outputStream);
        QuotedPrintableEncoder quotedPrintableEncoder = new QuotedPrintableEncoder(1024, z);
        this.encoder = quotedPrintableEncoder;
        quotedPrintableEncoder.initEncoding(outputStream);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this.closed) {
            try {
                this.encoder.completeEncoding();
            } finally {
                this.closed = true;
            }
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.encoder.flushOutput();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int i) throws IOException {
        write(new byte[]{(byte) i}, 0, 1);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        if (!this.closed) {
            this.encoder.encodeChunk(bArr, i, i2);
            return;
        }
        throw new IOException("QuotedPrintableOutputStream has been closed");
    }
}

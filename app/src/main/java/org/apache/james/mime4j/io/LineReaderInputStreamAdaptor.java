package org.apache.james.mime4j.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.james.mime4j.util.ByteArrayBuffer;

/* loaded from: classes.dex */
public class LineReaderInputStreamAdaptor extends LineReaderInputStream {
    private final LineReaderInputStream bis;
    private boolean eof;
    private final int maxLineLen;
    private boolean used;

    public LineReaderInputStreamAdaptor(InputStream inputStream) {
        this(inputStream, -1);
    }

    public LineReaderInputStreamAdaptor(InputStream inputStream, int i) {
        super(inputStream);
        this.used = false;
        this.eof = false;
        if (inputStream instanceof LineReaderInputStream) {
            this.bis = (LineReaderInputStream) inputStream;
        } else {
            this.bis = null;
        }
        this.maxLineLen = i;
    }

    private int doReadLine(ByteArrayBuffer byteArrayBuffer) throws IOException {
        int read;
        int i = 0;
        do {
            read = this.in.read();
            if (read == -1) {
                break;
            }
            byteArrayBuffer.append(read);
            i++;
            if (this.maxLineLen > 0 && byteArrayBuffer.length() >= this.maxLineLen) {
                throw new MaxLineLimitException("Maximum line length limit exceeded");
            }
        } while (read != 10);
        if (i == 0 && read == -1) {
            return -1;
        }
        return i;
    }

    public boolean eof() {
        return this.eof;
    }

    public boolean isUsed() {
        return this.used;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int read = this.in.read();
        this.eof = read == -1;
        this.used = true;
        return read;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int read = this.in.read(bArr, i, i2);
        this.eof = read == -1;
        this.used = true;
        return read;
    }

    @Override // org.apache.james.mime4j.io.LineReaderInputStream
    public int readLine(ByteArrayBuffer byteArrayBuffer) throws IOException {
        LineReaderInputStream lineReaderInputStream = this.bis;
        int readLine = lineReaderInputStream != null ? lineReaderInputStream.readLine(byteArrayBuffer) : doReadLine(byteArrayBuffer);
        this.eof = readLine == -1;
        this.used = true;
        return readLine;
    }

    public String toString() {
        return "[LineReaderInputStreamAdaptor: " + this.bis + "]";
    }
}

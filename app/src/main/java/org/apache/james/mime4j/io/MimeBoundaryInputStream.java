package org.apache.james.mime4j.io;

import java.io.IOException;
import org.apache.james.mime4j.util.ByteArrayBuffer;

/* loaded from: classes.dex */
public class MimeBoundaryInputStream extends LineReaderInputStream {
    private boolean atBoundary;
    private final byte[] boundary;
    private int boundaryLen;
    private BufferedLineReaderInputStream buffer;
    private boolean completed;
    private boolean eof;
    private boolean lastPart;
    private int limit;

    public MimeBoundaryInputStream(BufferedLineReaderInputStream bufferedLineReaderInputStream, String str) throws IOException {
        super(bufferedLineReaderInputStream);
        if (bufferedLineReaderInputStream.capacity() > str.length()) {
            this.buffer = bufferedLineReaderInputStream;
            this.eof = false;
            this.limit = -1;
            this.atBoundary = false;
            this.boundaryLen = 0;
            this.lastPart = false;
            this.completed = false;
            byte[] bArr = new byte[str.length() + 2];
            this.boundary = bArr;
            bArr[0] = 45;
            bArr[1] = 45;
            for (int i = 0; i < str.length(); i++) {
                byte charAt = (byte) str.charAt(i);
                if (charAt == 13 || charAt == 10) {
                    throw new IllegalArgumentException("Boundary may not contain CR or LF");
                }
                this.boundary[i + 2] = charAt;
            }
            fillBuffer();
            return;
        }
        throw new IllegalArgumentException("Boundary is too long");
    }

    private void calculateBoundaryLen() throws IOException {
        this.boundaryLen = this.boundary.length;
        int pos = this.limit - this.buffer.pos();
        if (pos > 0 && this.buffer.charAt(this.limit - 1) == 10) {
            this.boundaryLen++;
            this.limit--;
        }
        if (pos > 1 && this.buffer.charAt(this.limit - 1) == 13) {
            this.boundaryLen++;
            this.limit--;
        }
    }

    private boolean endOfStream() {
        return this.eof || this.atBoundary;
    }

    private int fillBuffer() throws IOException {
        if (this.eof) {
            return -1;
        }
        boolean z = false;
        int fillBuffer = !hasData() ? this.buffer.fillBuffer() : 0;
        if (fillBuffer == -1) {
            z = true;
        }
        this.eof = z;
        int indexOf = this.buffer.indexOf(this.boundary);
        while (indexOf > 0 && this.buffer.charAt(indexOf - 1) != 10) {
            byte[] bArr = this.boundary;
            int length = indexOf + bArr.length;
            BufferedLineReaderInputStream bufferedLineReaderInputStream = this.buffer;
            indexOf = bufferedLineReaderInputStream.indexOf(bArr, length, bufferedLineReaderInputStream.limit() - length);
        }
        if (indexOf != -1) {
            this.limit = indexOf;
            this.atBoundary = true;
            calculateBoundaryLen();
        } else if (this.eof) {
            this.limit = this.buffer.limit();
        } else {
            this.limit = this.buffer.limit() - (this.boundary.length + 1);
        }
        return fillBuffer;
    }

    private boolean hasData() {
        return this.limit > this.buffer.pos() && this.limit <= this.buffer.limit();
    }

    private void skipBoundary() throws IOException {
        if (!this.completed) {
            this.completed = true;
            this.buffer.skip(this.boundaryLen);
            boolean z = true;
            while (true) {
                if (this.buffer.length() > 1) {
                    BufferedLineReaderInputStream bufferedLineReaderInputStream = this.buffer;
                    byte charAt = bufferedLineReaderInputStream.charAt(bufferedLineReaderInputStream.pos());
                    BufferedLineReaderInputStream bufferedLineReaderInputStream2 = this.buffer;
                    byte charAt2 = bufferedLineReaderInputStream2.charAt(bufferedLineReaderInputStream2.pos() + 1);
                    if (z && charAt == 45 && charAt2 == 45) {
                        this.lastPart = true;
                        this.buffer.skip(2);
                        z = false;
                    } else if (charAt == 13 && charAt2 == 10) {
                        this.buffer.skip(2);
                        return;
                    } else if (charAt == 10) {
                        this.buffer.skip(1);
                        return;
                    } else {
                        this.buffer.skip(1);
                    }
                } else if (!this.eof) {
                    fillBuffer();
                } else {
                    return;
                }
            }
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    public boolean eof() {
        return this.eof && !this.buffer.hasBufferedData();
    }

    public boolean isLastPart() {
        return this.lastPart;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (this.completed) {
            return -1;
        }
        if (!endOfStream() || hasData()) {
            while (!hasData()) {
                if (endOfStream()) {
                    skipBoundary();
                    return -1;
                }
                fillBuffer();
            }
            return this.buffer.read();
        }
        skipBoundary();
        return -1;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.completed) {
            return -1;
        }
        if (!endOfStream() || hasData()) {
            fillBuffer();
            if (!hasData()) {
                return read(bArr, i, i2);
            }
            return this.buffer.read(bArr, i, Math.min(i2, this.limit - this.buffer.pos()));
        }
        skipBoundary();
        return -1;
    }

    @Override // org.apache.james.mime4j.io.LineReaderInputStream
    public int readLine(ByteArrayBuffer byteArrayBuffer) throws IOException {
        if (byteArrayBuffer == null) {
            throw new IllegalArgumentException("Destination buffer may not be null");
        } else if (this.completed) {
            return -1;
        } else {
            if (!endOfStream() || hasData()) {
                boolean z = false;
                int i = 0;
                int i2 = 0;
                while (true) {
                    if (z) {
                        break;
                    }
                    if (!hasData()) {
                        i2 = fillBuffer();
                        if (!hasData() && endOfStream()) {
                            skipBoundary();
                            i2 = -1;
                            break;
                        }
                    }
                    int pos = this.limit - this.buffer.pos();
                    BufferedLineReaderInputStream bufferedLineReaderInputStream = this.buffer;
                    int indexOf = bufferedLineReaderInputStream.indexOf((byte) 10, bufferedLineReaderInputStream.pos(), pos);
                    if (indexOf != -1) {
                        pos = (indexOf + 1) - this.buffer.pos();
                        z = true;
                    }
                    if (pos > 0) {
                        byteArrayBuffer.append(this.buffer.buf(), this.buffer.pos(), pos);
                        this.buffer.skip(pos);
                        i += pos;
                    }
                }
                if (i == 0 && i2 == -1) {
                    return -1;
                }
                return i;
            }
            skipBoundary();
            return -1;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("MimeBoundaryInputStream, boundary ");
        for (byte b : this.boundary) {
            sb.append((char) b);
        }
        return sb.toString();
    }
}

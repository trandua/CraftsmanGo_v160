package org.apache.james.mime4j.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/* loaded from: classes.dex */
public abstract class StorageOutputStream extends OutputStream {
    private boolean closed;
    private byte[] singleByte;
    private boolean usedUp;

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.closed = true;
    }

    public final Storage toStorage() throws IOException {
        if (!this.usedUp) {
            if (!this.closed) {
                close();
            }
            this.usedUp = true;
            return toStorage0();
        }
        throw new IllegalStateException("toStorage may be invoked only once");
    }

    protected abstract Storage toStorage0() throws IOException;

    @Override // java.io.OutputStream
    public final void write(int i) throws IOException {
        if (!this.closed) {
            if (this.singleByte == null) {
                this.singleByte = new byte[1];
            }
            byte[] bArr = this.singleByte;
            bArr[0] = (byte) i;
            write0(bArr, 0, 1);
            return;
        }
        throw new IOException("StorageOutputStream has been closed");
    }

    @Override // java.io.OutputStream
    public final void write(byte[] bArr) throws IOException {
        if (!this.closed) {
            Objects.requireNonNull(bArr);
            if (bArr.length != 0) {
                write0(bArr, 0, bArr.length);
                return;
            }
            return;
        }
        throw new IOException("StorageOutputStream has been closed");
    }

    @Override // java.io.OutputStream
    public final void write(byte[] bArr, int i, int i2) throws IOException {
        if (!this.closed) {
            Objects.requireNonNull(bArr);
            if (i < 0 || i2 < 0 || i + i2 > bArr.length) {
                throw new IndexOutOfBoundsException();
            } else if (i2 != 0) {
                write0(bArr, i, i2);
            }
        } else {
            throw new IOException("StorageOutputStream has been closed");
        }
    }

    protected abstract void write0(byte[] bArr, int i, int i2) throws IOException;
}

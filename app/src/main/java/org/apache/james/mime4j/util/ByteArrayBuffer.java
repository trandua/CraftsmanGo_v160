package org.apache.james.mime4j.util;

/* loaded from: classes.dex */
public final class ByteArrayBuffer implements ByteSequence {
    private byte[] buffer;
    private int len;

    public ByteArrayBuffer(int i) {
        if (i >= 0) {
            this.buffer = new byte[i];
            return;
        }
        throw new IllegalArgumentException("Buffer capacity may not be negative");
    }

    public ByteArrayBuffer(byte[] bArr, int i, boolean z) {
        if (bArr == null) {
            throw new IllegalArgumentException();
        } else if (i < 0 || i > bArr.length) {
            throw new IllegalArgumentException();
        } else {
            if (z) {
                this.buffer = bArr;
            } else {
                byte[] bArr2 = new byte[i];
                this.buffer = bArr2;
                System.arraycopy(bArr, 0, bArr2, 0, i);
            }
            this.len = i;
        }
    }

    public ByteArrayBuffer(byte[] bArr, boolean z) {
        this(bArr, bArr.length, z);
    }

    private void expand(int i) {
        byte[] bArr = new byte[Math.max(this.buffer.length << 1, i)];
        System.arraycopy(this.buffer, 0, bArr, 0, this.len);
        this.buffer = bArr;
    }

    public void append(int i) {
        int i2 = this.len + 1;
        if (i2 > this.buffer.length) {
            expand(i2);
        }
        this.buffer[this.len] = (byte) i;
        this.len = i2;
    }

    public void append(byte[] bArr, int i, int i2) {
        int i3;
        if (bArr != null) {
            if (i < 0 || i > bArr.length || i2 < 0 || (i3 = i + i2) < 0 || i3 > bArr.length) {
                throw new IndexOutOfBoundsException();
            } else if (i2 != 0) {
                int i4 = this.len + i2;
                if (i4 > this.buffer.length) {
                    expand(i4);
                }
                System.arraycopy(bArr, i, this.buffer, this.len, i2);
                this.len = i4;
            }
        }
    }

    public byte[] buffer() {
        return this.buffer;
    }

    @Override // org.apache.james.mime4j.util.ByteSequence
    public byte byteAt(int i) {
        if (i >= 0 && i < this.len) {
            return this.buffer[i];
        }
        throw new IndexOutOfBoundsException();
    }

    public int capacity() {
        return this.buffer.length;
    }

    public void clear() {
        this.len = 0;
    }

    public int indexOf(byte b) {
        return indexOf(b, 0, this.len);
    }

    public int indexOf(byte b, int i, int i2) {
        if (i < 0) {
            i = 0;
        }
        int i3 = this.len;
        if (i2 > i3) {
            i2 = i3;
        }
        if (i > i2) {
            return -1;
        }
        while (i < i2) {
            if (this.buffer[i] == b) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public boolean isEmpty() {
        return this.len == 0;
    }

    public boolean isFull() {
        return this.len == this.buffer.length;
    }

    @Override // org.apache.james.mime4j.util.ByteSequence
    public int length() {
        return this.len;
    }

    public void setLength(int i) {
        if (i < 0 || i > this.buffer.length) {
            throw new IndexOutOfBoundsException();
        }
        this.len = i;
    }

    @Override // org.apache.james.mime4j.util.ByteSequence
    public byte[] toByteArray() {
        int i = this.len;
        byte[] bArr = new byte[i];
        if (i > 0) {
            System.arraycopy(this.buffer, 0, bArr, 0, i);
        }
        return bArr;
    }

    public String toString() {
        return new String(toByteArray());
    }
}

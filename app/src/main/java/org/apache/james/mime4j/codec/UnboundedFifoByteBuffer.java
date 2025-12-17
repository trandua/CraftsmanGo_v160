package org.apache.james.mime4j.codec;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class UnboundedFifoByteBuffer {
    protected byte[] buffer;
    protected int head;
    protected int tail;

    public UnboundedFifoByteBuffer() {
        this(32);
    }

    public UnboundedFifoByteBuffer(int i) {
        if (i > 0) {
            this.buffer = new byte[i + 1];
            this.head = 0;
            this.tail = 0;
            return;
        }
        throw new IllegalArgumentException("The size must be greater than 0");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int decrement(int i) {
        int i2 = i - 1;
        return i2 < 0 ? this.buffer.length - 1 : i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int increment(int i) {
        int i2 = i + 1;
        if (i2 >= this.buffer.length) {
            return 0;
        }
        return i2;
    }

    public boolean add(byte b) {
        int size = size() + 1;
        byte[] bArr = this.buffer;
        if (size >= bArr.length) {
            byte[] bArr2 = new byte[((bArr.length - 1) * 2) + 1];
            int i = this.head;
            int i2 = 0;
            while (i != this.tail) {
                byte[] bArr3 = this.buffer;
                bArr2[i2] = bArr3[i];
                bArr3[i] = 0;
                i2++;
                i++;
                if (i == bArr3.length) {
                    i = 0;
                }
            }
            this.buffer = bArr2;
            this.head = 0;
            this.tail = i2;
        }
        byte[] bArr4 = this.buffer;
        int i3 = this.tail;
        bArr4[i3] = b;
        int i4 = i3 + 1;
        this.tail = i4;
        if (i4 >= bArr4.length) {
            this.tail = 0;
        }
        return true;
    }

    public byte get() {
        if (!isEmpty()) {
            return this.buffer[this.head];
        }
        throw new IllegalStateException("The buffer is already empty");
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() { // from class: org.apache.james.mime4j.codec.UnboundedFifoByteBuffer.1
            private int index;
            private int lastReturnedIndex = -1;

            {
                this.index = UnboundedFifoByteBuffer.this.head;
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.index != UnboundedFifoByteBuffer.this.tail;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public Byte next() {
                if (hasNext()) {
                    int i = this.index;
                    this.lastReturnedIndex = i;
                    this.index = UnboundedFifoByteBuffer.this.increment(i);
                    return new Byte(UnboundedFifoByteBuffer.this.buffer[this.lastReturnedIndex]);
                }
                throw new NoSuchElementException();
            }

            @Override // java.util.Iterator
            public void remove() {
                int i = this.lastReturnedIndex;
                if (i == -1) {
                    throw new IllegalStateException();
                } else if (i == UnboundedFifoByteBuffer.this.head) {
                    UnboundedFifoByteBuffer.this.remove();
                    this.lastReturnedIndex = -1;
                } else {
                    int i2 = this.lastReturnedIndex + 1;
                    while (i2 != UnboundedFifoByteBuffer.this.tail) {
                        if (i2 >= UnboundedFifoByteBuffer.this.buffer.length) {
                            UnboundedFifoByteBuffer.this.buffer[i2 - 1] = UnboundedFifoByteBuffer.this.buffer[0];
                            i2 = 0;
                        } else {
                            UnboundedFifoByteBuffer.this.buffer[i2 - 1] = UnboundedFifoByteBuffer.this.buffer[i2];
                            i2++;
                        }
                    }
                    this.lastReturnedIndex = -1;
                    UnboundedFifoByteBuffer unboundedFifoByteBuffer = UnboundedFifoByteBuffer.this;
                    unboundedFifoByteBuffer.tail = unboundedFifoByteBuffer.decrement(unboundedFifoByteBuffer.tail);
                    UnboundedFifoByteBuffer.this.buffer[UnboundedFifoByteBuffer.this.tail] = 0;
                    this.index = UnboundedFifoByteBuffer.this.decrement(this.index);
                }
            }
        };
    }

    public byte remove() {
        if (!isEmpty()) {
            byte[] bArr = this.buffer;
            int i = this.head;
            byte b = bArr[i];
            int i2 = i + 1;
            this.head = i2;
            if (i2 >= bArr.length) {
                this.head = 0;
            }
            return b;
        }
        throw new IllegalStateException("The buffer is already empty");
    }

    public int size() {
        int i = this.tail;
        int i2 = this.head;
        return i < i2 ? (this.buffer.length - i2) + i : i - i2;
    }
}

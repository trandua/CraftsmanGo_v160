package org.apache.james.mime4j.codec;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import kotlin.UByte;

/* loaded from: classes.dex */
public class Base64OutputStream extends FilterOutputStream {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final byte BASE64_PAD = 61;
    static final byte[] BASE64_TABLE;
    private static final int DEFAULT_LINE_LENGTH = 76;
    private static final int ENCODED_BUFFER_SIZE = 2048;
    private static final int MASK_6BITS = 63;
    private boolean closed;
    private int data;
    private final byte[] encoded;
    private final int lineLength;
    private int linePosition;
    private final byte[] lineSeparator;
    private int modulus;
    private int position;
    private final byte[] singleByte;
    private static final byte[] CRLF_SEPARATOR = {13, 10};
    private static final Set<Byte> BASE64_CHARS = new HashSet();

    static {
        byte[] bArr = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        BASE64_TABLE = bArr;
        for (byte b : bArr) {
            BASE64_CHARS.add(Byte.valueOf(b));
        }
        BASE64_CHARS.add(Byte.valueOf((byte) BASE64_PAD));
    }

    public Base64OutputStream(OutputStream outputStream) {
        this(outputStream, 76, CRLF_SEPARATOR);
    }

    public Base64OutputStream(OutputStream outputStream, int i) {
        this(outputStream, i, CRLF_SEPARATOR);
    }

    public Base64OutputStream(OutputStream outputStream, int i, byte[] bArr) {
        super(outputStream);
        this.singleByte = new byte[1];
        this.closed = false;
        this.position = 0;
        this.data = 0;
        this.modulus = 0;
        this.linePosition = 0;
        if (outputStream == null) {
            throw new IllegalArgumentException();
        } else if (i >= 0) {
            checkLineSeparator(bArr);
            this.lineLength = i;
            byte[] bArr2 = new byte[bArr.length];
            this.lineSeparator = bArr2;
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            this.encoded = new byte[2048];
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void checkLineSeparator(byte[] bArr) {
        if (bArr.length <= 2048) {
            for (byte b : bArr) {
                if (BASE64_CHARS.contains(Byte.valueOf(b))) {
                    throw new IllegalArgumentException("line separator must not contain base64 character '" + ((char) (b & UByte.MAX_VALUE)) + "'");
                }
            }
            return;
        }
        throw new IllegalArgumentException("line separator length exceeds 2048");
    }

    private void close0() throws IOException {
        if (this.modulus != 0) {
            writePad();
        }
        if (this.lineLength > 0 && this.linePosition > 0) {
            writeLineSeparator();
        }
        flush0();
    }

    private void flush0() throws IOException {
        if (this.position > 0) {
            this.out.write(this.encoded, 0, this.position);
            this.position = 0;
        }
    }

    private void write0(byte[] bArr, int i, int i2) throws IOException {
        byte[] bArr2;
        while (i < i2) {
            this.data = (this.data << 8) | (bArr[i] & UByte.MAX_VALUE);
            int i3 = this.modulus + 1;
            this.modulus = i3;
            if (i3 == 3) {
                this.modulus = 0;
                int i4 = this.lineLength;
                if (i4 > 0 && this.linePosition >= i4) {
                    this.linePosition = 0;
                    if (this.encoded.length - this.position < this.lineSeparator.length) {
                        flush0();
                    }
                    for (byte b : this.lineSeparator) {
                        byte[] bArr3 = this.encoded;
                        int i5 = this.position;
                        this.position = i5 + 1;
                        bArr3[i5] = b;
                    }
                }
                if (this.encoded.length - this.position < 4) {
                    flush0();
                }
                byte[] bArr4 = this.encoded;
                int i6 = this.position;
                int i7 = i6 + 1;
                this.position = i7;
                byte[] bArr5 = BASE64_TABLE;
                int i8 = this.data;
                bArr4[i6] = bArr5[(i8 >> 18) & 63];
                int i9 = i7 + 1;
                this.position = i9;
                bArr4[i7] = bArr5[(i8 >> 12) & 63];
                int i10 = i9 + 1;
                this.position = i10;
                bArr4[i9] = bArr5[(i8 >> 6) & 63];
                this.position = i10 + 1;
                bArr4[i10] = bArr5[i8 & 63];
                this.linePosition += 4;
            }
            i++;
        }
    }

    private void writeLineSeparator() throws IOException {
        byte[] bArr;
        this.linePosition = 0;
        if (this.encoded.length - this.position < this.lineSeparator.length) {
            flush0();
        }
        for (byte b : this.lineSeparator) {
            byte[] bArr2 = this.encoded;
            int i = this.position;
            this.position = i + 1;
            bArr2[i] = b;
        }
    }

    private void writePad() throws IOException {
        int i = this.lineLength;
        if (i > 0 && this.linePosition >= i) {
            writeLineSeparator();
        }
        if (this.encoded.length - this.position < 4) {
            flush0();
        }
        if (this.modulus == 1) {
            byte[] bArr = this.encoded;
            int i2 = this.position;
            int i3 = i2 + 1;
            this.position = i3;
            byte[] bArr2 = BASE64_TABLE;
            int i4 = this.data;
            bArr[i2] = bArr2[(i4 >> 2) & 63];
            int i5 = i3 + 1;
            this.position = i5;
            bArr[i3] = bArr2[(i4 << 4) & 63];
            int i6 = i5 + 1;
            this.position = i6;
            bArr[i5] = BASE64_PAD;
            this.position = i6 + 1;
            bArr[i6] = BASE64_PAD;
        } else {
            byte[] bArr3 = this.encoded;
            int i7 = this.position;
            int i8 = i7 + 1;
            this.position = i8;
            byte[] bArr4 = BASE64_TABLE;
            int i9 = this.data;
            bArr3[i7] = bArr4[(i9 >> 10) & 63];
            int i10 = i8 + 1;
            this.position = i10;
            bArr3[i8] = bArr4[(i9 >> 4) & 63];
            int i11 = i10 + 1;
            this.position = i11;
            bArr3[i10] = bArr4[(i9 << 2) & 63];
            this.position = i11 + 1;
            bArr3[i11] = BASE64_PAD;
        }
        this.linePosition += 4;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            close0();
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        if (!this.closed) {
            flush0();
            return;
        }
        throw new IOException("Base64OutputStream has been closed");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public final void write(int i) throws IOException {
        if (!this.closed) {
            byte[] bArr = this.singleByte;
            bArr[0] = (byte) i;
            write0(bArr, 0, 1);
            return;
        }
        throw new IOException("Base64OutputStream has been closed");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public final void write(byte[] bArr) throws IOException {
        if (!this.closed) {
            Objects.requireNonNull(bArr);
            if (bArr.length != 0) {
                write0(bArr, 0, bArr.length);
                return;
            }
            return;
        }
        throw new IOException("Base64OutputStream has been closed");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public final void write(byte[] bArr, int i, int i2) throws IOException {
        int i3;
        if (!this.closed) {
            Objects.requireNonNull(bArr);
            if (i < 0 || i2 < 0 || (i3 = i + i2) > bArr.length) {
                throw new IndexOutOfBoundsException();
            } else if (i2 != 0) {
                write0(bArr, i, i3);
            }
        } else {
            throw new IOException("Base64OutputStream has been closed");
        }
    }
}

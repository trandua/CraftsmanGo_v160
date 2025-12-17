package org.apache.james.mime4j.codec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import kotlin.UByte;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* loaded from: classes.dex */
public class Base64InputStream extends InputStream {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final byte BASE64_PAD = 61;
    private static final int ENCODED_BUFFER_SIZE = 1536;
    private static final int EOF = -1;
    private boolean closed;
    private final byte[] encoded;
    private boolean eof;
    private final InputStream in;
    private int position;
    private final ByteQueue q;
    private final byte[] singleByte;
    private int size;
    private boolean strict;
    private static Log log = LogFactory.getLog(Base64InputStream.class);
    private static final int[] BASE64_DECODE = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            BASE64_DECODE[i] = -1;
        }
        for (int i2 = 0; i2 < Base64OutputStream.BASE64_TABLE.length; i2++) {
            BASE64_DECODE[Base64OutputStream.BASE64_TABLE[i2] & UByte.MAX_VALUE] = i2;
        }
    }

    public Base64InputStream(InputStream inputStream) {
        this(inputStream, false);
    }

    public Base64InputStream(InputStream inputStream, boolean z) {
        this.singleByte = new byte[1];
        this.closed = false;
        this.encoded = new byte[ENCODED_BUFFER_SIZE];
        this.position = 0;
        this.size = 0;
        this.q = new ByteQueue();
        if (inputStream != null) {
            this.in = inputStream;
            this.strict = z;
            return;
        }
        throw new IllegalArgumentException();
    }

    private int decodePad(int i, int i2, byte[] bArr, int i3, int i4) throws IOException {
        this.eof = true;
        if (i2 == 2) {
            byte b = (byte) (i >>> 4);
            if (i3 < i4) {
                int i5 = i3 + 1;
                bArr[i3] = b;
                return i5;
            }
            this.q.enqueue(b);
            return i3;
        } else if (i2 == 3) {
            byte b2 = (byte) (i >>> 10);
            byte b3 = (byte) ((i >>> 2) & 255);
            if (i3 < i4 - 1) {
                int i6 = i3 + 1;
                bArr[i3] = b2;
                int i7 = i6 + 1;
                bArr[i6] = b3;
                return i7;
            } else if (i3 < i4) {
                int i8 = i3 + 1;
                bArr[i3] = b2;
                this.q.enqueue(b3);
                return i8;
            } else {
                this.q.enqueue(b2);
                this.q.enqueue(b3);
                return i3;
            }
        } else {
            handleUnexpecedPad(i2);
            return i3;
        }
    }

    private void handleUnexpecedPad(int i) throws IOException {
        if (!this.strict) {
            Log log2 = log;
            log2.warn("unexpected padding character; dropping " + i + " sextet(s)");
            return;
        }
        throw new IOException("unexpected padding character");
    }

    private void handleUnexpectedEof(int i) throws IOException {
        if (!this.strict) {
            Log log2 = log;
            log2.warn("unexpected end of file; dropping " + i + " sextet(s)");
            return;
        }
        throw new IOException("unexpected end of file");
    }

    private int read0(byte[] bArr, int i, int i2) throws IOException {
        int count = this.q.count();
        int i3 = i;
        while (true) {
            count--;
            if (count <= 0 || i3 >= i2) {
                break;
            }
            i3++;
            bArr[i3] = this.q.dequeue();
        }
        if (!this.eof) {
            int i4 = 0;
            int i5 = 0;
            while (i3 < i2) {
                while (this.position == this.size) {
                    InputStream inputStream = this.in;
                    byte[] bArr2 = this.encoded;
                    int read = inputStream.read(bArr2, 0, bArr2.length);
                    if (read == -1) {
                        this.eof = true;
                        if (i4 != 0) {
                            handleUnexpectedEof(i4);
                        }
                        if (i3 == i) {
                            return -1;
                        }
                        return i3 - i;
                    } else if (read > 0) {
                        this.position = 0;
                        this.size = read;
                    }
                }
                int i6 = i3;
                int i7 = i4;
                while (true) {
                    int i8 = this.position;
                    if (i8 < this.size && i6 < i2) {
                        byte[] bArr3 = this.encoded;
                        this.position = i8 + 1;
                        int i9 = bArr3[i8] & UByte.MAX_VALUE;
                        if (i9 == 61) {
                            return decodePad(i5, i7, bArr, i6, i2) - i;
                        }
                        int i10 = BASE64_DECODE[i9];
                        if (i10 >= 0) {
                            i5 = (i5 << 6) | i10;
                            i7++;
                            if (i7 == 4) {
                                byte b = (byte) (i5 >>> 16);
                                byte b2 = (byte) (i5 >>> 8);
                                byte b3 = (byte) i5;
                                if (i6 < i2 - 2) {
                                    int i11 = i6 + 1;
                                    bArr[i6] = b;
                                    int i12 = i11 + 1;
                                    bArr[i11] = b2;
                                    i6 = i12 + 1;
                                    bArr[i12] = b3;
                                    i7 = 0;
                                } else {
                                    if (i6 < i2 - 1) {
                                        bArr[i6] = b;
                                        bArr[i6 + 1] = b2;
                                        this.q.enqueue(b3);
                                    } else if (i6 < i2) {
                                        bArr[i6] = b;
                                        this.q.enqueue(b2);
                                        this.q.enqueue(b3);
                                    } else {
                                        this.q.enqueue(b);
                                        this.q.enqueue(b2);
                                        this.q.enqueue(b3);
                                    }
                                    return i2 - i;
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                }
//                i4 = i7;
//                i3 = i6;
            }
            return i2 - i;
        } else if (i3 == i) {
            return -1;
        } else {
            return i3 - i;
        }
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
        }
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int read0;
        if (!this.closed) {
            do {
                read0 = read0(this.singleByte, 0, 1);
                if (read0 == -1) {
                    return -1;
                }
            } while (read0 != 1);
            return this.singleByte[0] & UByte.MAX_VALUE;
        }
        throw new IOException("Base64InputStream has been closed");
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        if (!this.closed) {
            Objects.requireNonNull(bArr);
            if (bArr.length == 0) {
                return 0;
            }
            return read0(bArr, 0, bArr.length);
        }
        throw new IOException("Base64InputStream has been closed");
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3;
        if (!this.closed) {
            Objects.requireNonNull(bArr);
            if (i < 0 || i2 < 0 || (i3 = i + i2) > bArr.length) {
                throw new IndexOutOfBoundsException();
            } else if (i2 == 0) {
                return 0;
            } else {
                return read0(bArr, i, i3);
            }
        } else {
            throw new IOException("Base64InputStream has been closed");
        }
    }
}

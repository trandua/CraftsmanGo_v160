package org.apache.james.mime4j.codec;

import java.io.IOException;
import java.io.InputStream;
import kotlin.UByte;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* loaded from: classes.dex */
public class QuotedPrintableInputStream extends InputStream {
    private static Log log = LogFactory.getLog(QuotedPrintableInputStream.class);
    private InputStream stream;
    ByteQueue byteq = new ByteQueue();
    ByteQueue pushbackq = new ByteQueue();
    private byte state = 0;
    private boolean closed = false;

    public QuotedPrintableInputStream(InputStream inputStream) {
        this.stream = inputStream;
    }

    private byte asciiCharToNumericValue(byte b) {
        int i;
        if (b < 48 || b > 57) {
            byte b2 = 65;
            if (b < 65 || b > 90) {
                b2 = 97;
                if (b < 97 || b > 122) {
                    throw new IllegalArgumentException(((char) b) + " is not a hexadecimal digit");
                }
            }
            i = (b - b2) + 10;
        } else {
            i = b - 48;
        }
        return (byte) i;
    }

    private void fillBuffer() throws IOException {
        byte b = 0;
        while (this.byteq.count() == 0) {
            if (this.pushbackq.count() == 0) {
                populatePushbackQueue();
                if (this.pushbackq.count() == 0) {
                    return;
                }
            }
            byte dequeue = this.pushbackq.dequeue();
            byte b2 = this.state;
            if (b2 != 0) {
                if (b2 != 1) {
                    if (b2 != 2) {
                        if (b2 != 3) {
                            Log log2 = log;
                            log2.error("Illegal state: " + ((int) this.state));
                            this.state = (byte) 0;
                            this.byteq.enqueue(dequeue);
                        } else if ((dequeue < 48 || dequeue > 57) && ((dequeue < 65 || dequeue > 70) && (dequeue < 97 || dequeue > 102))) {
                            if (log.isWarnEnabled()) {
                                Log log3 = log;
                                log3.warn("Malformed MIME; expected [0-9A-Z], got " + ((int) dequeue));
                            }
                            this.state = (byte) 0;
                            this.byteq.enqueue((byte) 61);
                            this.byteq.enqueue(b);
                            this.byteq.enqueue(dequeue);
                        } else {
                            byte asciiCharToNumericValue = asciiCharToNumericValue(b);
                            byte asciiCharToNumericValue2 = asciiCharToNumericValue(dequeue);
                            this.state = (byte) 0;
                            this.byteq.enqueue((byte) (asciiCharToNumericValue2 | (asciiCharToNumericValue << 4)));
                        }
                    } else if (dequeue == 10) {
                        this.state = (byte) 0;
                    } else {
                        if (log.isWarnEnabled()) {
                            Log log4 = log;
                            log4.warn("Malformed MIME; expected 10, got " + ((int) dequeue));
                        }
                        this.state = (byte) 0;
                        this.byteq.enqueue((byte) 61);
                        this.byteq.enqueue((byte) 13);
                        this.byteq.enqueue(dequeue);
                    }
                } else if (dequeue == 13) {
                    this.state = (byte) 2;
                } else if ((dequeue >= 48 && dequeue <= 57) || ((dequeue >= 65 && dequeue <= 70) || (dequeue >= 97 && dequeue <= 102))) {
                    this.state = (byte) 3;
                    b = dequeue;
                } else if (dequeue == 61) {
                    if (log.isWarnEnabled()) {
                        log.warn("Malformed MIME; got ==");
                    }
                    this.byteq.enqueue((byte) 61);
                } else {
                    if (log.isWarnEnabled()) {
                        Log log5 = log;
                        log5.warn("Malformed MIME; expected \\r or [0-9A-Z], got " + ((int) dequeue));
                    }
                    this.state = (byte) 0;
                    this.byteq.enqueue((byte) 61);
                    this.byteq.enqueue(dequeue);
                }
            } else if (dequeue != 61) {
                this.byteq.enqueue(dequeue);
            } else {
                this.state = (byte) 1;
            }
        }
    }

    private void populatePushbackQueue() throws IOException {
        int read;
        if (this.pushbackq.count() == 0) {
            while (true) {
                read = this.stream.read();
                if (read == -1) {
                    this.pushbackq.clear();
                    return;
                } else if (read == 13) {
                    break;
                } else if (read == 32 || read == 9) {
                    this.pushbackq.enqueue((byte) read);
                } else if (read != 10) {
                    this.pushbackq.enqueue((byte) read);
                    return;
                }
            }
            this.pushbackq.clear();
            this.pushbackq.enqueue((byte) read);
        }
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.closed = true;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (!this.closed) {
            fillBuffer();
            if (this.byteq.count() == 0) {
                return -1;
            }
            byte dequeue = this.byteq.dequeue();
            return dequeue >= 0 ? dequeue : dequeue & UByte.MAX_VALUE;
        }
        throw new IOException("QuotedPrintableInputStream has been closed");
    }
}

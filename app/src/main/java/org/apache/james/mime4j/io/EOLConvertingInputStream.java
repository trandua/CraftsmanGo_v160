package org.apache.james.mime4j.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/* loaded from: classes.dex */
public class EOLConvertingInputStream extends InputStream {
    public static final int CONVERT_BOTH = 3;
    public static final int CONVERT_CR = 1;
    public static final int CONVERT_LF = 2;
    private int flags;
    private PushbackInputStream in;
    private int previous;

    public EOLConvertingInputStream(InputStream inputStream) {
        this(inputStream, 3);
    }

    public EOLConvertingInputStream(InputStream inputStream, int i) {
        this.in = null;
        this.previous = 0;
        this.flags = 3;
        this.in = new PushbackInputStream(inputStream, 2);
        this.flags = i;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.in.close();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int read = this.in.read();
        if (read == -1) {
            return -1;
        }
        int i = this.flags;
        if ((i & 1) != 0 && read == 13) {
            int read2 = this.in.read();
            if (read2 != -1) {
                this.in.unread(read2);
            }
            if (read2 != 10) {
                this.in.unread(10);
            }
        } else if (!((i & 2) == 0 || read != 10 || this.previous == 13)) {
            this.in.unread(10);
            read = 13;
        }
        this.previous = read;
        return read;
    }
}

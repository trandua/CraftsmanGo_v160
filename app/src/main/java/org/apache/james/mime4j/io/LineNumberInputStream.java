package org.apache.james.mime4j.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class LineNumberInputStream extends FilterInputStream implements LineNumberSource {
    private int lineNumber = 1;

    public LineNumberInputStream(InputStream inputStream) {
        super(inputStream);
    }

    @Override // org.apache.james.mime4j.io.LineNumberSource
    public int getLineNumber() {
        return this.lineNumber;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int read = this.in.read();
        if (read == 10) {
            this.lineNumber++;
        }
        return read;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int read = this.in.read(bArr, i, i2);
        for (int i3 = i; i3 < i + read; i3++) {
            if (bArr[i3] == 10) {
                this.lineNumber++;
            }
        }
        return read;
    }
}

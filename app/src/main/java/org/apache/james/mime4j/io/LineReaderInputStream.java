package org.apache.james.mime4j.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.james.mime4j.util.ByteArrayBuffer;

/* loaded from: classes.dex */
public abstract class LineReaderInputStream extends FilterInputStream {
    /* JADX INFO: Access modifiers changed from: protected */
    public LineReaderInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public abstract int readLine(ByteArrayBuffer byteArrayBuffer) throws IOException;
}

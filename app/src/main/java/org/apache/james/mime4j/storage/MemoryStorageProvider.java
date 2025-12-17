package org.apache.james.mime4j.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.james.mime4j.util.ByteArrayBuffer;

/* loaded from: classes.dex */
public class MemoryStorageProvider extends AbstractStorageProvider {

    /* loaded from: classes.dex */
    static final class MemoryStorage implements Storage {
        private final int count;
        private byte[] data;

        public MemoryStorage(byte[] bArr, int i) {
            this.data = bArr;
            this.count = i;
        }

        @Override // org.apache.james.mime4j.storage.Storage
        public void delete() {
            this.data = null;
        }

        @Override // org.apache.james.mime4j.storage.Storage
        public InputStream getInputStream() throws IOException {
            if (this.data != null) {
                return new ByteArrayInputStream(this.data, 0, this.count);
            }
            throw new IllegalStateException("storage has been deleted");
        }
    }

    /* loaded from: classes.dex */
    private static final class MemoryStorageOutputStream extends StorageOutputStream {
        ByteArrayBuffer bab;

        private MemoryStorageOutputStream() {
            this.bab = new ByteArrayBuffer(1024);
        }

        @Override // org.apache.james.mime4j.storage.StorageOutputStream
        protected Storage toStorage0() throws IOException {
            return new MemoryStorage(this.bab.buffer(), this.bab.length());
        }

        @Override // org.apache.james.mime4j.storage.StorageOutputStream
        protected void write0(byte[] bArr, int i, int i2) throws IOException {
            this.bab.append(bArr, i, i2);
        }
    }

    @Override // org.apache.james.mime4j.storage.StorageProvider
    public StorageOutputStream createStorageOutputStream() {
        return new MemoryStorageOutputStream();
    }
}

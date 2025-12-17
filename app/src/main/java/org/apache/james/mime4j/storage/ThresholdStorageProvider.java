package org.apache.james.mime4j.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import org.apache.james.mime4j.storage.MemoryStorageProvider;
import org.apache.james.mime4j.util.ByteArrayBuffer;

/* loaded from: classes.dex */
public class ThresholdStorageProvider extends AbstractStorageProvider {
    private final StorageProvider backend;
    private final int thresholdSize;

    /* loaded from: classes.dex */
    private static final class ThresholdStorage implements Storage {
        private byte[] head;
        private final int headLen;
        private Storage tail;

        public ThresholdStorage(byte[] bArr, int i, Storage storage) {
            this.head = bArr;
            this.headLen = i;
            this.tail = storage;
        }

        @Override // org.apache.james.mime4j.storage.Storage
        public void delete() {
            if (this.head != null) {
                this.head = null;
                this.tail.delete();
                this.tail = null;
            }
        }

        @Override // org.apache.james.mime4j.storage.Storage
        public InputStream getInputStream() throws IOException {
            if (this.head != null) {
                return new SequenceInputStream(new ByteArrayInputStream(this.head, 0, this.headLen), this.tail.getInputStream());
            }
            throw new IllegalStateException("storage has been deleted");
        }
    }

    /* loaded from: classes.dex */
    private final class ThresholdStorageOutputStream extends StorageOutputStream {
        private final ByteArrayBuffer head;
        private StorageOutputStream tail;

        public ThresholdStorageOutputStream() {
            this.head = new ByteArrayBuffer(Math.min(ThresholdStorageProvider.this.thresholdSize, 1024));
        }

        @Override // org.apache.james.mime4j.storage.StorageOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            super.close();
            StorageOutputStream storageOutputStream = this.tail;
            if (storageOutputStream != null) {
                storageOutputStream.close();
            }
        }

        @Override // org.apache.james.mime4j.storage.StorageOutputStream
        protected Storage toStorage0() throws IOException {
            return this.tail == null ? new MemoryStorageProvider.MemoryStorage(this.head.buffer(), this.head.length()) : new ThresholdStorage(this.head.buffer(), this.head.length(), this.tail.toStorage());
        }

        @Override // org.apache.james.mime4j.storage.StorageOutputStream
        protected void write0(byte[] bArr, int i, int i2) throws IOException {
            int length = ThresholdStorageProvider.this.thresholdSize - this.head.length();
            if (length > 0) {
                int min = Math.min(length, i2);
                this.head.append(bArr, i, min);
                i += min;
                i2 -= min;
            }
            if (i2 > 0) {
                if (this.tail == null) {
                    this.tail = ThresholdStorageProvider.this.backend.createStorageOutputStream();
                }
                this.tail.write(bArr, i, i2);
            }
        }
    }

    public ThresholdStorageProvider(StorageProvider storageProvider) {
        this(storageProvider, 2048);
    }

    public ThresholdStorageProvider(StorageProvider storageProvider, int i) {
        if (storageProvider == null) {
            throw new IllegalArgumentException();
        } else if (i >= 1) {
            this.backend = storageProvider;
            this.thresholdSize = i;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override // org.apache.james.mime4j.storage.StorageProvider
    public StorageOutputStream createStorageOutputStream() {
        return new ThresholdStorageOutputStream();
    }
}

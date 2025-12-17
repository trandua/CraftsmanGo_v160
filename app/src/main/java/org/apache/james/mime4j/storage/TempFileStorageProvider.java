package org.apache.james.mime4j.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
public class TempFileStorageProvider extends AbstractStorageProvider {
    private static final String DEFAULT_PREFIX = "m4j";
    private final File directory;
    private final String prefix;
    private final String suffix;

    /* loaded from: classes.dex */
    private static final class TempFileStorage implements Storage {
        private static final Set<File> filesToDelete = new HashSet();
        private File file;

        public TempFileStorage(File file) {
            this.file = file;
        }

        @Override // org.apache.james.mime4j.storage.Storage
        public void delete() {
            Set<File> set = filesToDelete;
            synchronized (set) {
                File file = this.file;
                if (file != null) {
                    set.add(file);
                    this.file = null;
                }
                Iterator<File> it = set.iterator();
                while (it.hasNext()) {
                    if (it.next().delete()) {
                        it.remove();
                    }
                }
            }
        }

        @Override // org.apache.james.mime4j.storage.Storage
        public InputStream getInputStream() throws IOException {
            if (this.file != null) {
                return new BufferedInputStream(new FileInputStream(this.file));
            }
            throw new IllegalStateException("storage has been deleted");
        }
    }

    /* loaded from: classes.dex */
    private static final class TempFileStorageOutputStream extends StorageOutputStream {
        private File file;
        private OutputStream out;

        public TempFileStorageOutputStream(File file) throws IOException {
            this.file = file;
            this.out = new FileOutputStream(file);
        }

        @Override // org.apache.james.mime4j.storage.StorageOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            super.close();
            this.out.close();
        }

        @Override // org.apache.james.mime4j.storage.StorageOutputStream
        protected Storage toStorage0() throws IOException {
            return new TempFileStorage(this.file);
        }

        @Override // org.apache.james.mime4j.storage.StorageOutputStream
        protected void write0(byte[] bArr, int i, int i2) throws IOException {
            this.out.write(bArr, i, i2);
        }
    }

    public TempFileStorageProvider() {
        this(DEFAULT_PREFIX, null, null);
    }

    public TempFileStorageProvider(File file) {
        this(DEFAULT_PREFIX, null, file);
    }

    public TempFileStorageProvider(String str, String str2, File file) {
        if (str == null || str.length() < 3) {
            throw new IllegalArgumentException("invalid prefix");
        } else if (file == null || file.isDirectory() || file.mkdirs()) {
            this.prefix = str;
            this.suffix = str2;
            this.directory = file;
        } else {
            throw new IllegalArgumentException("invalid directory");
        }
    }

    @Override // org.apache.james.mime4j.storage.StorageProvider
    public StorageOutputStream createStorageOutputStream() throws IOException {
        File createTempFile = File.createTempFile(this.prefix, this.suffix, this.directory);
        createTempFile.deleteOnExit();
        return new TempFileStorageOutputStream(createTempFile);
    }
}

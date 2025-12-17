package com.microsoft.xbox.toolkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/* loaded from: classes3.dex */
public class XLEFileCache {
    private static final String TAG = "XLEFileCache";
    private boolean enabled;
    private final long expiredTimer;
    final int maxFileNumber;
    private int readAccessCnt;
    private int readSuccessfulCnt;
    int size;
    private int writeAccessCnt;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class CachedFileInputStreamItem {
        private byte[] computedMd5;
        private InputStream contentInputStream;
        private MessageDigest mDigest;
        private byte[] savedMd5;
        final XLEFileCache this$0;

        public CachedFileInputStreamItem(XLEFileCache xLEFileCache, XLEFileCacheItemKey xLEFileCacheItemKey, File file) throws IOException {
            this.mDigest = null;
            this.this$0 = xLEFileCache;
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                this.mDigest = messageDigest;
                byte[] bArr = new byte[messageDigest.getDigestLength()];
                this.savedMd5 = bArr;
                if (fileInputStream.read(bArr) == this.mDigest.getDigestLength()) {
                    int readInt = XLEFileCache.readInt(fileInputStream);
                    byte[] bArr2 = new byte[readInt];
                    if (readInt != fileInputStream.read(bArr2) || !xLEFileCacheItemKey.getKeyString().equals(new String(bArr2))) {
                        file.delete();
                        throw new IOException("File key check failed because keyLength != readKeyLength or !key.getKeyString().equals(new String(urlOrSomething))");
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    StreamUtil.CopyStream(byteArrayOutputStream, fileInputStream);
                    fileInputStream.close();
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    this.mDigest.update(byteArray);
                    this.computedMd5 = this.mDigest.digest();
                    if (!isMd5Error()) {
                        this.contentInputStream = new ByteArrayInputStream(byteArray);
                        return;
                    }
                    file.delete();
                    throw new IOException(fileInputStream.getFD() + "the saved md5 is not equal computed md5.ComputedMd5:" + this.computedMd5 + "     SavedMd5:" + this.savedMd5);
                }
                fileInputStream.close();
                throw new IOException("Ddigest lengh check failed!");
            } catch (OutOfMemoryError e) {
                fileInputStream.close();
                throw new IOException("File digest failed! Out of memory: " + e.getMessage());
            } catch (NoSuchAlgorithmException e2) {
                fileInputStream.close();
                throw new IOException("File digest failed! " + e2.getMessage());
            }
        }

        private boolean isMd5Error() {
            for (int i = 0; i < this.mDigest.getDigestLength(); i++) {
                if (this.savedMd5[i] != this.computedMd5[i]) {
                    return true;
                }
            }
            return false;
        }

        public InputStream getContentInputStream() {
            return this.contentInputStream;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class CachedFileOutputStreamItem extends FileOutputStream {
        private File destFile;
        private MessageDigest mDigest;
        private boolean startDigest;
        final XLEFileCache this$0;
        private boolean writeMd5Finished;

        public CachedFileOutputStreamItem(XLEFileCache xLEFileCache, XLEFileCacheItemKey xLEFileCacheItemKey, File file) throws IOException {
            super(file);
            this.mDigest = null;
            this.startDigest = false;
            this.writeMd5Finished = false;
            this.this$0 = xLEFileCache;
            this.destFile = file;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                this.mDigest = messageDigest;
                write(new byte[messageDigest.getDigestLength()]);
                byte[] bytes = xLEFileCacheItemKey.getKeyString().getBytes();
                writeInt(bytes.length);
                write(bytes);
                this.startDigest = true;
            } catch (NoSuchAlgorithmException e) {
                throw new IOException("File digest failed!" + e.getMessage());
            }
        }

        private final void writeInt(int i) throws IOException {
            write((i >>> 24) & 255);
            write((i >>> 16) & 255);
            write((i >>> 8) & 255);
            write((i >>> 0) & 255);
        }

        @Override // java.io.FileOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            super.close();
            if (this.writeMd5Finished) {
                return;
            }
            this.writeMd5Finished = true;
            RandomAccessFile randomAccessFile = new RandomAccessFile(this.destFile, "rw");
            byte[] digest = this.mDigest.digest();
            randomAccessFile.seek(0L);
            randomAccessFile.write(digest);
            randomAccessFile.close();
        }

        @Override // java.io.FileOutputStream, java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            super.write(bArr, i, i2);
            if (this.startDigest) {
                this.mDigest.update(bArr, i, i2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XLEFileCache() {
        this.size = 0;
        this.readAccessCnt = 0;
        this.writeAccessCnt = 0;
        this.readSuccessfulCnt = 0;
        this.expiredTimer = Long.MAX_VALUE;
        this.maxFileNumber = 0;
        this.enabled = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XLEFileCache(String str, int i) {
        this(str, i, Long.MAX_VALUE);
    }

    XLEFileCache(String str, int i, long j) {
        this.size = 0;
        this.enabled = true;
        this.readAccessCnt = 0;
        this.writeAccessCnt = 0;
        this.readSuccessfulCnt = 0;
        this.maxFileNumber = i;
        this.expiredTimer = j;
    }

    private void checkAndEnsureCapacity() {
        if (this.size < this.maxFileNumber || !this.enabled) {
            return;
        }
        File[] listFiles = XLEFileCacheManager.getCacheRootDir(this).listFiles();
        listFiles[new Random().nextInt(listFiles.length)].delete();
        this.size = listFiles.length - 1;
    }

    private String getCachedItemFileName(XLEFileCacheItemKey xLEFileCacheItemKey) {
        return String.valueOf(xLEFileCacheItemKey.getKeyString().hashCode());
    }

    public static int readInt(InputStream inputStream) throws IOException {
        int read = inputStream.read();
        int read2 = inputStream.read();
        int read3 = inputStream.read();
        int read4 = inputStream.read();
        if ((read | read2 | read3 | read4) >= 0) {
            return (read << 24) + (read2 << 16) + (read3 << 8) + (read4 << 0);
        }
        throw new EOFException();
    }

    public boolean contains(XLEFileCacheItemKey xLEFileCacheItemKey) {
        synchronized (this) {
            if (this.enabled) {
                return new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(xLEFileCacheItemKey)).exists();
            }
            return false;
        }
    }

    public InputStream getInputStreamForRead(XLEFileCacheItemKey xLEFileCacheItemKey) {
        synchronized (this) {
            if (this.enabled) {
                XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
                this.readAccessCnt++;
                File file = new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(xLEFileCacheItemKey));
                if (file.exists()) {
                    if (file.lastModified() < System.currentTimeMillis() - this.expiredTimer) {
                        file.delete();
                        this.size--;
                        return null;
                    }
                    try {
                        InputStream contentInputStream = new CachedFileInputStreamItem(this, xLEFileCacheItemKey, file).getContentInputStream();
                        this.readSuccessfulCnt++;
                        return contentInputStream;
                    } catch (IOException unused) {
                        return null;
                    }
                }
                return null;
            }
            return null;
        }
    }

    public int getItemsInCache() {
        return this.size;
    }

    public OutputStream getOuputStreamForSave(XLEFileCacheItemKey xLEFileCacheItemKey) throws IOException {
        synchronized (this) {
            if (!this.enabled) {
                return new OutputStream() { // from class: com.microsoft.xbox.toolkit.XLEFileCache.1
                    @Override // java.io.OutputStream
                    public void write(int i) throws IOException {
                    }
                };
            }
            XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
            this.writeAccessCnt++;
            checkAndEnsureCapacity();
            File file = new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(xLEFileCacheItemKey));
            if (file.exists()) {
                file.delete();
                this.size--;
            }
            if (file.createNewFile()) {
                this.size++;
            }
            return new CachedFileOutputStreamItem(this, xLEFileCacheItemKey, file);
        }
    }

    public void save(XLEFileCacheItemKey xLEFileCacheItemKey, InputStream inputStream) {
//        OutputStream ouputStreamForSave;
//        synchronized (this) {
//            try {
//                ouputStreamForSave = getOuputStreamForSave(xLEFileCacheItemKey);
//            } catch (IOException unused) {
//            }
//            try {
//                StreamUtil.CopyStream(ouputStreamForSave, inputStream);
//                if (ouputStreamForSave != null) {
//                    ouputStreamForSave.close();
//                }
//            } catch (Throwable th) {
//                if (ouputStreamForSave != null) {
//                    try {
//                        ouputStreamForSave.close();
//                    } catch (Throwable th2) {
//                        th.addSuppressed(th2);
//                    }
//                }
//                throw th;
//            }
//        }
        try {
            OutputStream ouputStreamForSave = getOuputStreamForSave(xLEFileCacheItemKey);
            StreamUtil.CopyStream(ouputStreamForSave, inputStream);
            ouputStreamForSave.close();
        } catch (IOException unused) {
        }
    }

    public String toString() {
        return "Size=" + this.size + "\tRootDir=" + XLEFileCacheManager.getCacheRootDir(this) + "\tMaxFileNumber=" + this.maxFileNumber + "\tExpiredTimerInSeconds=" + this.expiredTimer + "\tWriteAccessCnt=" + this.writeAccessCnt + "\tReadAccessCnt=" + this.readAccessCnt + "\tReadSuccessfulCnt=" + this.readSuccessfulCnt;
    }
}

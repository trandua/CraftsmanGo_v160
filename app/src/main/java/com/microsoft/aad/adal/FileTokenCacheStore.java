package com.microsoft.aad.adal;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

/* loaded from: classes3.dex */
public class FileTokenCacheStore implements ITokenCacheStore {
    private static final String TAG = null;
    private static final long serialVersionUID = -8252291336171327870L;
    private final Object mCacheLock = new Object();
    private final File mFile;
    private final MemoryTokenCacheStore mInMemoryCache;

    public FileTokenCacheStore(Context context, String str) {
        MemoryTokenCacheStore memoryTokenCacheStore;
        if (context == null) {
            throw new IllegalArgumentException("context");
        }
        if (!StringExtensions.isNullOrBlank(str)) {
            File dir = context.getDir(context.getPackageName(), 0);
            if (dir != null) {
                try {
                    File file = new File(dir, str);
                    this.mFile = file;
                    if (file.exists()) {
                        StringBuilder sb = new StringBuilder();
                        String str2 = TAG;
                        sb.append(str2);
                        sb.append(":FileTokenCacheStore");
                        Logger.m14614v(sb.toString(), "There is previous cache file to load cache. ");
                        FileInputStream fileInputStream = new FileInputStream(file);
                        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                        Object readObject = objectInputStream.readObject();
                        fileInputStream.close();
                        objectInputStream.close();
                        if (readObject instanceof MemoryTokenCacheStore) {
                            this.mInMemoryCache = (MemoryTokenCacheStore) readObject;
                            return;
                        }
                        Logger.m14617w(str2 + ":FileTokenCacheStore", "Existing cache format is wrong. ", "", ADALError.DEVICE_FILE_CACHE_FORMAT_IS_WRONG);
                        memoryTokenCacheStore = new MemoryTokenCacheStore();
                    } else {
                        Logger.m14614v(TAG + ":FileTokenCacheStore", "There is not any previous cache file to load cache. ");
                        memoryTokenCacheStore = new MemoryTokenCacheStore();
                    }
                    this.mInMemoryCache = memoryTokenCacheStore;
                    return;
                } catch (IOException | ClassNotFoundException e) {
                    Logger.m14609e(TAG + ":FileTokenCacheStore", "Exception during cache load. ", ExceptionExtensions.getExceptionMessage(e), ADALError.DEVICE_FILE_CACHE_IS_NOT_LOADED_FROM_FILE);
                    throw new IllegalStateException(e);
                }
            }
            throw new IllegalStateException("It could not access the Authorization cache directory");
        }
        throw new IllegalArgumentException("fileName");
    }

    private void writeToFile() {
        synchronized (this.mCacheLock) {
            if (this.mFile != null && this.mInMemoryCache != null) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(this.mFile);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(this.mInMemoryCache);
                    objectOutputStream.flush();
                    objectOutputStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    Logger.m14609e(TAG, "Exception during cache flush", ExceptionExtensions.getExceptionMessage(e), ADALError.DEVICE_FILE_CACHE_IS_NOT_WRITING_TO_FILE);
                }
            }
        }
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public boolean contains(String str) {
        return this.mInMemoryCache.contains(str);
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore, com.microsoft.aad.adal.ITokenStoreQuery
    public Iterator<TokenCacheItem> getAll() {
        return this.mInMemoryCache.getAll();
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public TokenCacheItem getItem(String str) {
        return this.mInMemoryCache.getItem(str);
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public void removeAll() {
        this.mInMemoryCache.removeAll();
        writeToFile();
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public void removeItem(String str) {
        this.mInMemoryCache.removeItem(str);
        writeToFile();
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public void setItem(String str, TokenCacheItem tokenCacheItem) {
        this.mInMemoryCache.setItem(str, tokenCacheItem);
        writeToFile();
    }
}

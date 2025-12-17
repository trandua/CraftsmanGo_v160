package org.apache.james.mime4j.message;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.storage.DefaultStorageProvider;
import org.apache.james.mime4j.storage.MultiReferenceStorage;
import org.apache.james.mime4j.storage.Storage;
import org.apache.james.mime4j.storage.StorageProvider;
import org.apache.james.mime4j.util.CharsetUtil;

/* loaded from: classes.dex */
public class BodyFactory {
    private StorageProvider storageProvider;
    private static Log log = LogFactory.getLog(BodyFactory.class);
    private static final Charset FALLBACK_CHARSET = CharsetUtil.DEFAULT_CHARSET;

    public BodyFactory() {
        this.storageProvider = DefaultStorageProvider.getInstance();
    }

    public BodyFactory(StorageProvider storageProvider) {
        this.storageProvider = storageProvider == null ? DefaultStorageProvider.getInstance() : storageProvider;
    }

    private static Charset toJavaCharset(String str, boolean z) {
        String javaCharset = CharsetUtil.toJavaCharset(str);
        if (javaCharset == null) {
            if (log.isWarnEnabled()) {
                Log log2 = log;
                log2.warn("MIME charset '" + str + "' has no corresponding Java charset. Using " + FALLBACK_CHARSET + " instead.");
            }
            return FALLBACK_CHARSET;
        } else if (z && !CharsetUtil.isEncodingSupported(javaCharset)) {
            if (log.isWarnEnabled()) {
                Log log3 = log;
                log3.warn("MIME charset '" + str + "' does not support encoding. Using " + FALLBACK_CHARSET + " instead.");
            }
            return FALLBACK_CHARSET;
        } else if (z || CharsetUtil.isDecodingSupported(javaCharset)) {
            return Charset.forName(javaCharset);
        } else {
            if (log.isWarnEnabled()) {
                Log log4 = log;
                log4.warn("MIME charset '" + str + "' does not support decoding. Using " + FALLBACK_CHARSET + " instead.");
            }
            return FALLBACK_CHARSET;
        }
    }

    public BinaryBody binaryBody(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            return new StorageBinaryBody(new MultiReferenceStorage(this.storageProvider.store(inputStream)));
        }
        throw new IllegalArgumentException();
    }

    public BinaryBody binaryBody(Storage storage) throws IOException {
        if (storage != null) {
            return new StorageBinaryBody(new MultiReferenceStorage(storage));
        }
        throw new IllegalArgumentException();
    }

    public StorageProvider getStorageProvider() {
        return this.storageProvider;
    }

    public TextBody textBody(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            return new StorageTextBody(new MultiReferenceStorage(this.storageProvider.store(inputStream)), CharsetUtil.DEFAULT_CHARSET);
        }
        throw new IllegalArgumentException();
    }

    public TextBody textBody(InputStream inputStream, String str) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException();
        } else if (str != null) {
            Storage store = this.storageProvider.store(inputStream);
            return new StorageTextBody(new MultiReferenceStorage(store), toJavaCharset(str, false));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public TextBody textBody(String str) {
        if (str != null) {
            return new StringTextBody(str, CharsetUtil.DEFAULT_CHARSET);
        }
        throw new IllegalArgumentException();
    }

    public TextBody textBody(String str, String str2) {
        if (str == null) {
            throw new IllegalArgumentException();
        } else if (str2 != null) {
            return new StringTextBody(str, toJavaCharset(str2, true));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public TextBody textBody(Storage storage) throws IOException {
        if (storage != null) {
            return new StorageTextBody(new MultiReferenceStorage(storage), CharsetUtil.DEFAULT_CHARSET);
        }
        throw new IllegalArgumentException();
    }

    public TextBody textBody(Storage storage, String str) throws IOException {
        if (storage == null) {
            throw new IllegalArgumentException();
        } else if (str != null) {
            return new StorageTextBody(new MultiReferenceStorage(storage), toJavaCharset(str, false));
        } else {
            throw new IllegalArgumentException();
        }
    }
}

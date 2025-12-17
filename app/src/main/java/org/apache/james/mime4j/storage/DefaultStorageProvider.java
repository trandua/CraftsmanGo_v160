package org.apache.james.mime4j.storage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* loaded from: classes.dex */
public class DefaultStorageProvider {
    public static final String DEFAULT_STORAGE_PROVIDER_PROPERTY = "org.apache.james.mime4j.defaultStorageProvider";
    private static Log log = LogFactory.getLog(DefaultStorageProvider.class);
    private static volatile StorageProvider instance = null;

    static {
        initialize();
    }

    private DefaultStorageProvider() {
    }

    public static StorageProvider getInstance() {
        return instance;
    }

    private static void initialize() {
        String property = System.getProperty(DEFAULT_STORAGE_PROVIDER_PROPERTY);
        if (property != null) {
            try {
                instance = (StorageProvider) Class.forName(property).newInstance();
            } catch (Exception e) {
                Log log2 = log;
                log2.warn("Unable to create or instantiate StorageProvider class '" + property + "'. Using default instead.", e);
            }
        }
        if (instance == null) {
            instance = new ThresholdStorageProvider(new TempFileStorageProvider(), 1024);
        }
    }

    static void reset() {
        instance = null;
        initialize();
    }

    public static void setInstance(StorageProvider storageProvider) {
        if (storageProvider != null) {
            instance = storageProvider;
            return;
        }
        throw new IllegalArgumentException();
    }
}

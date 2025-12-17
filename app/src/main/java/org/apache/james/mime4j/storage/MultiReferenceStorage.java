package org.apache.james.mime4j.storage;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class MultiReferenceStorage implements Storage {
    private int referenceCounter;
    private final Storage storage;

    public MultiReferenceStorage(Storage storage) {
        if (storage != null) {
            this.storage = storage;
            this.referenceCounter = 1;
            return;
        }
        throw new IllegalArgumentException();
    }

    private synchronized boolean decrementCounter() {
        boolean z;
        int i = this.referenceCounter;
        if (i != 0) {
            z = true;
            int i2 = i - 1;
            this.referenceCounter = i2;
            if (i2 != 0) {
                z = false;
            }
        } else {
            throw new IllegalStateException("storage has been deleted");
        }
        return z;
    }

    private synchronized void incrementCounter() {
        int i = this.referenceCounter;
        if (i != 0) {
            this.referenceCounter = i + 1;
        } else {
            throw new IllegalStateException("storage has been deleted");
        }
    }

    public void addReference() {
        incrementCounter();
    }

    @Override // org.apache.james.mime4j.storage.Storage
    public void delete() {
        if (decrementCounter()) {
            this.storage.delete();
        }
    }

    @Override // org.apache.james.mime4j.storage.Storage
    public InputStream getInputStream() throws IOException {
        return this.storage.getInputStream();
    }
}

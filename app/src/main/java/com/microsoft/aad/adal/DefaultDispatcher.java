package com.microsoft.aad.adal;

import android.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes3.dex */
class DefaultDispatcher {
    private final IDispatcher mDispatcher;
    private final Map<String, List<IEvents>> mObjectsToBeDispatched;

    private DefaultDispatcher() {
        this.mObjectsToBeDispatched = new HashMap();
        this.mDispatcher = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultDispatcher(IDispatcher iDispatcher) {
        this.mObjectsToBeDispatched = new HashMap();
        this.mDispatcher = iDispatcher;
    }

    public void flush(String str) {
        synchronized (this) {
        }
    }

    public IDispatcher getDispatcher() {
        return this.mDispatcher;
    }

    public Map<String, List<IEvents>> getObjectsToBeDispatched() {
        return this.mObjectsToBeDispatched;
    }

    public void receive(String str, IEvents iEvents) {
        if (this.mDispatcher != null) {
            HashMap hashMap = new HashMap();
            for (Pair<String, String> pair : iEvents.getEvents()) {
                hashMap.put(pair.first, pair.second);
            }
            this.mDispatcher.dispatchEvent(hashMap);
        }
    }
}

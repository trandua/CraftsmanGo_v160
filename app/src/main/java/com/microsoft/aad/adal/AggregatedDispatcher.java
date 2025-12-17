package com.microsoft.aad.adal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes3.dex */
final class AggregatedDispatcher extends DefaultDispatcher {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AggregatedDispatcher(IDispatcher iDispatcher) {
        super(iDispatcher);
    }

    @Override // com.microsoft.aad.adal.DefaultDispatcher
    public synchronized void flush(String str) {
        List<IEvents> remove;
        HashMap hashMap = new HashMap();
        if (getDispatcher() != null && (remove = getObjectsToBeDispatched().remove(str)) != null && !remove.isEmpty()) {
            for (int i = 0; i < remove.size(); i++) {
                remove.get(i).processEvent(hashMap);
            }
            getDispatcher().dispatchEvent(hashMap);
        }
    }

    @Override // com.microsoft.aad.adal.DefaultDispatcher
    public void receive(String str, IEvents iEvents) {
        List<IEvents> list = getObjectsToBeDispatched().get(str);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(iEvents);
        getObjectsToBeDispatched().put(str, list);
    }
}

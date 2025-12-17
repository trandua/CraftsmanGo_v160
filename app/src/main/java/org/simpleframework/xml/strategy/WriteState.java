package org.simpleframework.xml.strategy;

import org.simpleframework.xml.util.WeakCache;

/* loaded from: classes.dex */
class WriteState extends WeakCache<WriteGraph> {
    private Contract contract;

    public WriteState(Contract contract) {
        this.contract = contract;
    }

    public WriteGraph find(Object map) {
        WriteGraph write = fetch(map);
        if (write != null) {
            return write;
        }
        WriteGraph write2 = new WriteGraph(this.contract);
        cache(map, write2);
        return write2;
    }
}

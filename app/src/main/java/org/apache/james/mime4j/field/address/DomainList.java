package org.apache.james.mime4j.field.address;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class DomainList extends AbstractList<String> implements Serializable {
    private static final long serialVersionUID = 1;
    private final List<String> domains;

    public DomainList(List<String> list, boolean z) {
        if (list != null) {
            this.domains = !z ? new ArrayList<>(list) : list;
        } else {
            this.domains = Collections.emptyList();
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public String get(int i) {
        return this.domains.get(i);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.domains.size();
    }

    public String toRouteString() {
        StringBuilder sb = new StringBuilder();
        for (String str : this.domains) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append("@");
            sb.append(str);
        }
        return sb.toString();
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        return toRouteString();
    }
}

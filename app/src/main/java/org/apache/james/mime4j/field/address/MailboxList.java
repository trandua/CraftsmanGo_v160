package org.apache.james.mime4j.field.address;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class MailboxList extends AbstractList<Mailbox> implements Serializable {
    private static final long serialVersionUID = 1;
    private final List<Mailbox> mailboxes;

    public MailboxList(List<Mailbox> list, boolean z) {
        if (list != null) {
            this.mailboxes = !z ? new ArrayList<>(list) : list;
        } else {
            this.mailboxes = Collections.emptyList();
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public Mailbox get(int i) {
        return this.mailboxes.get(i);
    }

    public void print() {
        for (int i = 0; i < size(); i++) {
            System.out.println(get(i).toString());
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.mailboxes.size();
    }
}

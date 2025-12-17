package org.apache.james.mime4j.field.address;

import java.io.Serializable;
import java.io.StringReader;
import java.util.List;
import org.apache.james.mime4j.field.address.parser.AddressListParser;
import org.apache.james.mime4j.field.address.parser.ParseException;

/* loaded from: classes.dex */
public abstract class Address implements Serializable {
    private static final long serialVersionUID = 634090661990433426L;

    public static Address parse(String str) {
        try {
            return Builder.getInstance().buildAddress(new AddressListParser(new StringReader(str)).parseAddress());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addMailboxesTo(List<Mailbox> list) {
        doAddMailboxesTo(list);
    }

    protected abstract void doAddMailboxesTo(List<Mailbox> list);

    public final String getDisplayString() {
        return getDisplayString(false);
    }

    public abstract String getDisplayString(boolean z);

    public abstract String getEncodedString();

    public String toString() {
        return getDisplayString(false);
    }
}

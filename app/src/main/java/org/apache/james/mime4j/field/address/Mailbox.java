package org.apache.james.mime4j.field.address;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import kotlin.text.Typography;
import org.apache.james.mime4j.codec.EncoderUtil;
import org.apache.james.mime4j.field.address.parser.AddressListParser;
import org.apache.james.mime4j.field.address.parser.ParseException;

/* loaded from: classes.dex */
public class Mailbox extends Address {
    private static final DomainList EMPTY_ROUTE_LIST = new DomainList(Collections.emptyList(), true);
    private static final long serialVersionUID = 1;
    private final String domain;
    private final String localPart;
    private final String name;
    private final DomainList route;

    public Mailbox(String str, String str2) {
        this(null, null, str, str2);
    }

    public Mailbox(String str, String str2, String str3) {
        this(str, null, str2, str3);
    }

    public Mailbox(String str, DomainList domainList, String str2, String str3) {
        if (str2 == null || str2.length() == 0) {
            throw new IllegalArgumentException();
        }
        this.name = (str == null || str.length() == 0) ? null : str;
        this.route = domainList == null ? EMPTY_ROUTE_LIST : domainList;
        this.localPart = str2;
        this.domain = (str3 == null || str3.length() == 0) ? null : str3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Mailbox(String str, Mailbox mailbox) {
        this(str, mailbox.getRoute(), mailbox.getLocalPart(), mailbox.getDomain());
    }

    public Mailbox(DomainList domainList, String str, String str2) {
        this(null, domainList, str, str2);
    }

    private Object getCanonicalizedAddress() {
        if (this.domain == null) {
            return this.localPart;
        }
        return this.localPart + '@' + this.domain.toLowerCase(Locale.US);
    }

    public static Mailbox parse(String str) {
        try {
            return Builder.getInstance().buildMailbox(new AddressListParser(new StringReader(str)).parseMailbox());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override // org.apache.james.mime4j.field.address.Address
    protected final void doAddMailboxesTo(List<Mailbox> list) {
        list.add(this);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Mailbox)) {
            return false;
        }
        return getCanonicalizedAddress().equals(((Mailbox) obj).getCanonicalizedAddress());
    }

    public String getAddress() {
        if (this.domain == null) {
            return this.localPart;
        }
        return this.localPart + '@' + this.domain;
    }

    @Override // org.apache.james.mime4j.field.address.Address
    public String getDisplayString(boolean z) {
        boolean z2 = true;
        boolean z3 = z & (this.route != null);
        if (this.name == null && !z3) {
            z2 = false;
        }
        StringBuilder sb = new StringBuilder();
        String str = this.name;
        if (str != null) {
            sb.append(str);
            sb.append(' ');
        }
        if (z2) {
            sb.append(Typography.less);
        }
        if (z3) {
            sb.append(this.route.toRouteString());
            sb.append(':');
        }
        sb.append(this.localPart);
        if (this.domain != null) {
            sb.append('@');
            sb.append(this.domain);
        }
        if (z2) {
            sb.append(Typography.greater);
        }
        return sb.toString();
    }

    public String getDomain() {
        return this.domain;
    }

    @Override // org.apache.james.mime4j.field.address.Address
    public String getEncodedString() {
        StringBuilder sb = new StringBuilder();
        String str = this.name;
        if (str != null) {
            sb.append(EncoderUtil.encodeAddressDisplayName(str));
            sb.append(" <");
        }
        sb.append(EncoderUtil.encodeAddressLocalPart(this.localPart));
        if (this.domain != null) {
            sb.append('@');
            sb.append(this.domain);
        }
        if (this.name != null) {
            sb.append(Typography.greater);
        }
        return sb.toString();
    }

    public String getLocalPart() {
        return this.localPart;
    }

    public String getName() {
        return this.name;
    }

    public DomainList getRoute() {
        return this.route;
    }

    public int hashCode() {
        return getCanonicalizedAddress().hashCode();
    }
}

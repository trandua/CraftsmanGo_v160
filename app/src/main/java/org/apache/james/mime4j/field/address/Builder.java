package org.apache.james.mime4j.field.address;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.field.address.parser.ASTaddr_spec;
import org.apache.james.mime4j.field.address.parser.ASTaddress;
import org.apache.james.mime4j.field.address.parser.ASTaddress_list;
import org.apache.james.mime4j.field.address.parser.ASTangle_addr;
import org.apache.james.mime4j.field.address.parser.ASTdomain;
import org.apache.james.mime4j.field.address.parser.ASTgroup_body;
import org.apache.james.mime4j.field.address.parser.ASTlocal_part;
import org.apache.james.mime4j.field.address.parser.ASTmailbox;
import org.apache.james.mime4j.field.address.parser.ASTname_addr;
import org.apache.james.mime4j.field.address.parser.ASTphrase;
import org.apache.james.mime4j.field.address.parser.ASTroute;
import org.apache.james.mime4j.field.address.parser.Node;
import org.apache.james.mime4j.field.address.parser.SimpleNode;
import org.apache.james.mime4j.field.address.parser.Token;

/* loaded from: classes.dex */
class Builder {
    private static Builder singleton = new Builder();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ChildNodeIterator implements Iterator<Node> {
        private int index = 0;
        private int len;
        private SimpleNode simpleNode;

        public ChildNodeIterator(SimpleNode simpleNode) {
            this.simpleNode = simpleNode;
            this.len = simpleNode.jjtGetNumChildren();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.index < this.len;
        }

        @Override // java.util.Iterator
        public Node next() {
            SimpleNode simpleNode = this.simpleNode;
            int i = this.index;
            this.index = i + 1;
            return simpleNode.jjtGetChild(i);
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    Builder() {
    }

    private void addSpecials(StringBuilder sb, Token token) {
        if (token != null) {
            addSpecials(sb, token.specialToken);
            sb.append(token.image);
        }
    }

    private Mailbox buildAddrSpec(DomainList domainList, ASTaddr_spec aSTaddr_spec) {
        ChildNodeIterator childNodeIterator = new ChildNodeIterator(aSTaddr_spec);
        return new Mailbox(domainList, buildString((ASTlocal_part) childNodeIterator.next(), true), buildString((ASTdomain) childNodeIterator.next(), true));
    }

    private Mailbox buildAddrSpec(ASTaddr_spec aSTaddr_spec) {
        return buildAddrSpec(null, aSTaddr_spec);
    }

    private Mailbox buildAngleAddr(ASTangle_addr aSTangle_addr) {
        DomainList domainList;
        ChildNodeIterator childNodeIterator = new ChildNodeIterator(aSTangle_addr);
        Node next = childNodeIterator.next();
        if (next instanceof ASTroute) {
            domainList = buildRoute((ASTroute) next);
            next = childNodeIterator.next();
        } else if (next instanceof ASTaddr_spec) {
            domainList = null;
        } else {
            throw new IllegalStateException();
        }
        if (next instanceof ASTaddr_spec) {
            return buildAddrSpec(domainList, (ASTaddr_spec) next);
        }
        throw new IllegalStateException();
    }

    private MailboxList buildGroupBody(ASTgroup_body aSTgroup_body) {
        ArrayList arrayList = new ArrayList();
        ChildNodeIterator childNodeIterator = new ChildNodeIterator(aSTgroup_body);
        while (childNodeIterator.hasNext()) {
            Node next = childNodeIterator.next();
            if (next instanceof ASTmailbox) {
                arrayList.add(buildMailbox((ASTmailbox) next));
            } else {
                throw new IllegalStateException();
            }
        }
        return new MailboxList(arrayList, true);
    }

    private Mailbox buildNameAddr(ASTname_addr aSTname_addr) {
        ChildNodeIterator childNodeIterator = new ChildNodeIterator(aSTname_addr);
        Node next = childNodeIterator.next();
        if (next instanceof ASTphrase) {
            String buildString = buildString((ASTphrase) next, false);
            Node next2 = childNodeIterator.next();
            if (next2 instanceof ASTangle_addr) {
                return new Mailbox(DecoderUtil.decodeEncodedWords(buildString), buildAngleAddr((ASTangle_addr) next2));
            }
            throw new IllegalStateException();
        }
        throw new IllegalStateException();
    }

    private DomainList buildRoute(ASTroute aSTroute) {
        ArrayList arrayList = new ArrayList(aSTroute.jjtGetNumChildren());
        ChildNodeIterator childNodeIterator = new ChildNodeIterator(aSTroute);
        while (childNodeIterator.hasNext()) {
            Node next = childNodeIterator.next();
            if (next instanceof ASTdomain) {
                arrayList.add(buildString((ASTdomain) next, true));
            } else {
                throw new IllegalStateException();
            }
        }
        return new DomainList(arrayList, true);
    }

    private String buildString(SimpleNode simpleNode, boolean z) {
        Token token = simpleNode.firstToken;
        Token token2 = simpleNode.lastToken;
        StringBuilder sb = new StringBuilder();
        while (token != token2) {
            sb.append(token.image);
            token = token.next;
            if (!z) {
                addSpecials(sb, token.specialToken);
            }
        }
        sb.append(token2.image);
        return sb.toString();
    }

    public static Builder getInstance() {
        return singleton;
    }

    public Address buildAddress(ASTaddress aSTaddress) {
        ChildNodeIterator childNodeIterator = new ChildNodeIterator(aSTaddress);
        Node next = childNodeIterator.next();
        if (next instanceof ASTaddr_spec) {
            return buildAddrSpec((ASTaddr_spec) next);
        }
        if (next instanceof ASTangle_addr) {
            return buildAngleAddr((ASTangle_addr) next);
        }
        if (next instanceof ASTphrase) {
            String buildString = buildString((ASTphrase) next, false);
            Node next2 = childNodeIterator.next();
            if (next2 instanceof ASTgroup_body) {
                return new Group(buildString, buildGroupBody((ASTgroup_body) next2));
            }
            if (next2 instanceof ASTangle_addr) {
                return new Mailbox(DecoderUtil.decodeEncodedWords(buildString), buildAngleAddr((ASTangle_addr) next2));
            }
            throw new IllegalStateException();
        }
        throw new IllegalStateException();
    }

    public AddressList buildAddressList(ASTaddress_list aSTaddress_list) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < aSTaddress_list.jjtGetNumChildren(); i++) {
            arrayList.add(buildAddress((ASTaddress) aSTaddress_list.jjtGetChild(i)));
        }
        return new AddressList(arrayList, true);
    }

    public Mailbox buildMailbox(ASTmailbox aSTmailbox) {
        Node next = new ChildNodeIterator(aSTmailbox).next();
        if (next instanceof ASTaddr_spec) {
            return buildAddrSpec((ASTaddr_spec) next);
        }
        if (next instanceof ASTangle_addr) {
            return buildAngleAddr((ASTangle_addr) next);
        }
        if (next instanceof ASTname_addr) {
            return buildNameAddr((ASTname_addr) next);
        }
        throw new IllegalStateException();
    }
}

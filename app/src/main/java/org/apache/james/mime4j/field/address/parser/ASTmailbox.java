package org.apache.james.mime4j.field.address.parser;

/* loaded from: classes.dex */
public class ASTmailbox extends SimpleNode {
    public ASTmailbox(int i) {
        super(i);
    }

    public ASTmailbox(AddressListParser addressListParser, int i) {
        super(addressListParser, i);
    }

    @Override // org.apache.james.mime4j.field.address.parser.SimpleNode, org.apache.james.mime4j.field.address.parser.Node
    public Object jjtAccept(AddressListParserVisitor addressListParserVisitor, Object obj) {
        return addressListParserVisitor.visit(this, obj);
    }
}

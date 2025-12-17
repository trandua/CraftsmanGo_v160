package org.apache.james.mime4j.field.address.parser;

/* loaded from: classes.dex */
public class ASTgroup_body extends SimpleNode {
    public ASTgroup_body(int i) {
        super(i);
    }

    public ASTgroup_body(AddressListParser addressListParser, int i) {
        super(addressListParser, i);
    }

    @Override // org.apache.james.mime4j.field.address.parser.SimpleNode, org.apache.james.mime4j.field.address.parser.Node
    public Object jjtAccept(AddressListParserVisitor addressListParserVisitor, Object obj) {
        return addressListParserVisitor.visit(this, obj);
    }
}

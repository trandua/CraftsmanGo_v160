package org.apache.james.mime4j.field.address.parser;

/* loaded from: classes.dex */
public class ASTaddress_list extends SimpleNode {
    public ASTaddress_list(int i) {
        super(i);
    }

    public ASTaddress_list(AddressListParser addressListParser, int i) {
        super(addressListParser, i);
    }

    @Override // org.apache.james.mime4j.field.address.parser.SimpleNode, org.apache.james.mime4j.field.address.parser.Node
    public Object jjtAccept(AddressListParserVisitor addressListParserVisitor, Object obj) {
        return addressListParserVisitor.visit(this, obj);
    }
}

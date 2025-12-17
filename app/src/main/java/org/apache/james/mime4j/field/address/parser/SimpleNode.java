package org.apache.james.mime4j.field.address.parser;

/* loaded from: classes.dex */
public class SimpleNode extends BaseNode implements Node {
    protected Node[] children;
    protected int id;
    protected Node parent;
    protected AddressListParser parser;

    public SimpleNode(int i) {
        this.id = i;
    }

    public SimpleNode(AddressListParser addressListParser, int i) {
        this(i);
        this.parser = addressListParser;
    }

    public Object childrenAccept(AddressListParserVisitor addressListParserVisitor, Object obj) {
        if (this.children != null) {
            int i = 0;
            while (true) {
                Node[] nodeArr = this.children;
                if (i >= nodeArr.length) {
                    break;
                }
                nodeArr[i].jjtAccept(addressListParserVisitor, obj);
                i++;
            }
        }
        return obj;
    }

    public void dump(String str) {
        System.out.println(toString(str));
        if (this.children != null) {
            int i = 0;
            while (true) {
                Node[] nodeArr = this.children;
                if (i < nodeArr.length) {
                    SimpleNode simpleNode = (SimpleNode) nodeArr[i];
                    if (simpleNode != null) {
                        simpleNode.dump(str + " ");
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    @Override // org.apache.james.mime4j.field.address.parser.Node
    public Object jjtAccept(AddressListParserVisitor addressListParserVisitor, Object obj) {
        return addressListParserVisitor.visit(this, obj);
    }

    @Override // org.apache.james.mime4j.field.address.parser.Node
    public void jjtAddChild(Node node, int i) {
        Node[] nodeArr = this.children;
        if (nodeArr == null) {
            this.children = new Node[i + 1];
        } else if (i >= nodeArr.length) {
            Node[] nodeArr2 = new Node[i + 1];
            System.arraycopy(nodeArr, 0, nodeArr2, 0, nodeArr.length);
            this.children = nodeArr2;
        }
        this.children[i] = node;
    }

    @Override // org.apache.james.mime4j.field.address.parser.Node
    public void jjtClose() {
    }

    @Override // org.apache.james.mime4j.field.address.parser.Node
    public Node jjtGetChild(int i) {
        return this.children[i];
    }

    @Override // org.apache.james.mime4j.field.address.parser.Node
    public int jjtGetNumChildren() {
        Node[] nodeArr = this.children;
        if (nodeArr == null) {
            return 0;
        }
        return nodeArr.length;
    }

    @Override // org.apache.james.mime4j.field.address.parser.Node
    public Node jjtGetParent() {
        return this.parent;
    }

    @Override // org.apache.james.mime4j.field.address.parser.Node
    public void jjtOpen() {
    }

    @Override // org.apache.james.mime4j.field.address.parser.Node
    public void jjtSetParent(Node node) {
        this.parent = node;
    }

    public String toString() {
        return AddressListParserTreeConstants.jjtNodeName[this.id];
    }

    public String toString(String str) {
        return str + toString();
    }
}

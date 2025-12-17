package org.apache.james.mime4j.field.address.parser;

import java.util.Stack;

/* loaded from: classes.dex */
class JJTAddressListParserState {
    private boolean node_created;
    private Stack<Node> nodes = new Stack<>();
    private Stack<Integer> marks = new Stack<>();
    private int sp = 0;
    private int mk = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearNodeScope(Node node) {
        while (this.sp > this.mk) {
            popNode();
        }
        this.mk = this.marks.pop().intValue();
    }

    void closeNodeScope(Node node, int i) {
        this.mk = this.marks.pop().intValue();
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                Node popNode = popNode();
                popNode.jjtSetParent(node);
                node.jjtAddChild(popNode, i2);
                i = i2;
            } else {
                node.jjtClose();
                pushNode(node);
                this.node_created = true;
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeNodeScope(Node node, boolean z) {
        if (z) {
            int nodeArity = nodeArity();
            this.mk = this.marks.pop().intValue();
            while (true) {
                int i = nodeArity - 1;
                if (nodeArity > 0) {
                    Node popNode = popNode();
                    popNode.jjtSetParent(node);
                    node.jjtAddChild(popNode, i);
                    nodeArity = i;
                } else {
                    node.jjtClose();
                    pushNode(node);
                    this.node_created = true;
                    return;
                }
            }
        } else {
            this.mk = this.marks.pop().intValue();
            this.node_created = false;
        }
    }

    int nodeArity() {
        return this.sp - this.mk;
    }

    boolean nodeCreated() {
        return this.node_created;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void openNodeScope(Node node) {
        this.marks.push(new Integer(this.mk));
        this.mk = this.sp;
        node.jjtOpen();
    }

    Node peekNode() {
        return this.nodes.peek();
    }

    Node popNode() {
        int i = this.sp - 1;
        this.sp = i;
        if (i < this.mk) {
            this.mk = this.marks.pop().intValue();
        }
        return this.nodes.pop();
    }

    void pushNode(Node node) {
        this.nodes.push(node);
        this.sp++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reset() {
        this.nodes.removeAllElements();
        this.marks.removeAllElements();
        this.sp = 0;
        this.mk = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Node rootNode() {
        return this.nodes.elementAt(0);
    }
}

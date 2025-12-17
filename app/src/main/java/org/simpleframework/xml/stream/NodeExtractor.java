package org.simpleframework.xml.stream;

import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: classes.dex */
class NodeExtractor extends LinkedList<Node> {
    public NodeExtractor(Document source) {
        extract(source);
    }

    private void extract(Document source) {
        Element documentElement = source.getDocumentElement();
        if (documentElement != null) {
            offer(documentElement);
            extract(documentElement);
        }
    }

    private void extract(Node source) {
        NodeList list = source.getChildNodes();
        int length = list.getLength();
        for (int i = 0; i < length; i++) {
            Node node = list.item(i);
            short type = node.getNodeType();
            if (type != 8) {
                offer(node);
                extract(node);
            }
        }
    }
}

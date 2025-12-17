package org.simpleframework.xml.stream;

import java.util.Iterator;
import java.util.LinkedHashMap;

/* loaded from: classes.dex */
class InputNodeMap extends LinkedHashMap<String, InputNode> implements NodeMap<InputNode> {
    private final InputNode source;

    /* JADX INFO: Access modifiers changed from: protected */
    public InputNodeMap(InputNode source) {
        this.source = source;
    }

    public InputNodeMap(InputNode source, EventNode element) {
        this.source = source;
        build(element);
    }

    private void build(EventNode element) {
        for (Attribute entry : element) {
            InputAttribute value = new InputAttribute(this.source, entry);
            if (!entry.isReserved()) {
                put(value.getName(), value);
            }
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.stream.NodeMap
    public InputNode getNode() {
        return this.source;
    }

    @Override // org.simpleframework.xml.stream.NodeMap
    public String getName() {
        return this.source.getName();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.stream.NodeMap
    public InputNode put(String name, String value) {
        InputNode node = new InputAttribute(this.source, name, value);
        if (name != null) {
            put(name, node);
        }
        return node;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.stream.NodeMap
    public InputNode remove(String name) {
        return (InputNode) super.remove((Object) name);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.stream.NodeMap
    public InputNode get(String name) {
        return (InputNode) super.get((Object) name);
    }

    @Override // org.simpleframework.xml.stream.NodeMap, java.lang.Iterable
    public Iterator<String> iterator() {
        return keySet().iterator();
    }
}

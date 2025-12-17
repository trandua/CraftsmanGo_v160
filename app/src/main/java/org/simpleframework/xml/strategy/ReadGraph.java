package org.simpleframework.xml.strategy;

import java.util.HashMap;
import org.simpleframework.xml.stream.Node;
import org.simpleframework.xml.stream.NodeMap;

/* loaded from: classes.dex */
class ReadGraph extends HashMap {
    private final String label;
    private final String length;
    private final Loader loader;
    private final String mark;
    private final String refer;

    public ReadGraph(Contract contract, Loader loader) {
        this.refer = contract.getReference();
        this.mark = contract.getIdentity();
        this.length = contract.getLength();
        this.label = contract.getLabel();
        this.loader = loader;
    }

    public Value read(Type type, NodeMap node) throws Exception {
        Node entry = node.remove(this.label);
        Class expect = type.getType();
        if (expect.isArray()) {
            expect = expect.getComponentType();
        }
        if (entry != null) {
            String name = entry.getValue();
            expect = this.loader.load(name);
        }
        return readInstance(type, expect, node);
    }

    private Value readInstance(Type type, Class real, NodeMap node) throws Exception {
        Node entry = node.remove(this.mark);
        if (entry == null) {
            return readReference(type, real, node);
        }
        String key = entry.getValue();
        if (!containsKey(key)) {
            return readValue(type, real, node, key);
        }
        throw new CycleException("Element '%s' already exists", key);
    }

    private Value readReference(Type type, Class real, NodeMap node) throws Exception {
        Node entry = node.remove(this.refer);
        if (entry == null) {
            return readValue(type, real, node);
        }
        String key = entry.getValue();
        Object value = get(key);
        if (containsKey(key)) {
            return new Reference(value, real);
        }
        throw new CycleException("Invalid reference '%s' found", key);
    }

    private Value readValue(Type type, Class real, NodeMap node) throws Exception {
        Class expect = type.getType();
        return expect.isArray() ? readArray(type, real, node) : new ObjectValue(real);
    }

    private Value readValue(Type type, Class real, NodeMap node, String key) throws Exception {
        Value value = readValue(type, real, node);
        if (key != null) {
            return new Allocate(value, this, key);
        }
        return value;
    }

    private Value readArray(Type type, Class real, NodeMap node) throws Exception {
        Node entry = node.remove(this.length);
        int size = 0;
        if (entry != null) {
            String value = entry.getValue();
            size = Integer.parseInt(value);
        }
        return new ArrayValue(real, size);
    }
}

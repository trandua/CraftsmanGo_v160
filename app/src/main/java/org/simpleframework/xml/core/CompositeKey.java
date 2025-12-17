package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Position;
import org.simpleframework.xml.stream.Style;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class CompositeKey implements Converter {
    private final Context context;
    private final Entry entry;
    private final Traverser root;
    private final Style style;
    private final Type type;

    public CompositeKey(Context context, Entry entry, Type type) throws Exception {
        this.root = new Traverser(context);
        this.style = context.getStyle();
        this.context = context;
        this.entry = entry;
        this.type = type;
    }

    @Override // org.simpleframework.xml.core.Converter
    public Object read(InputNode node) throws Exception {
        Position line = node.getPosition();
        Class expect = this.type.getType();
        String name = this.entry.getKey();
        if (name == null) {
            name = this.context.getName(expect);
        }
        if (!this.entry.isAttribute()) {
            return read(node, name);
        }
        throw new AttributeException("Can not have %s as an attribute for %s at %s", expect, this.entry, line);
    }

    @Override // org.simpleframework.xml.core.Converter
    public Object read(InputNode node, Object value) throws Exception {
        Position line = node.getPosition();
        Class expect = this.type.getType();
        if (value == null) {
            return read(node);
        }
        throw new PersistenceException("Can not read key of %s for %s at %s", expect, this.entry, line);
    }

    private Object read(InputNode node, String key) throws Exception {
        String name = this.style.getElement(key);
        Class expect = this.type.getType();
        if (name != null) {
            node = node.getNext(name);
        }
        if (node != null && !node.isEmpty()) {
            return this.root.read(node, expect);
        }
        return null;
    }

    @Override // org.simpleframework.xml.core.Converter
    public boolean validate(InputNode node) throws Exception {
        Position line = node.getPosition();
        Class expect = this.type.getType();
        String name = this.entry.getKey();
        if (name == null) {
            name = this.context.getName(expect);
        }
        if (!this.entry.isAttribute()) {
            return validate(node, name);
        }
        throw new ElementException("Can not have %s as an attribute for %s at %s", expect, this.entry, line);
    }

    private boolean validate(InputNode node, String key) throws Exception {
        String name = this.style.getElement(key);
        InputNode next = node.getNext(name);
        Class expect = this.type.getType();
        if (next != null && !next.isEmpty()) {
            return this.root.validate(next, expect);
        }
        return true;
    }

    @Override // org.simpleframework.xml.core.Converter
    public void write(OutputNode node, Object item) throws Exception {
        Class expect = this.type.getType();
        String key = this.entry.getKey();
        if (this.entry.isAttribute()) {
            throw new ElementException("Can not have %s as an attribute for %s", expect, this.entry);
        }
        if (key == null) {
            key = this.context.getName(expect);
        }
        String name = this.style.getElement(key);
        this.root.write(node, item, expect, name);
    }
}

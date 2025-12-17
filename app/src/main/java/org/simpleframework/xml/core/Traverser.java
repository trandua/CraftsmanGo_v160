package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class Traverser {
    private final Context context;
    private final Style style;

    public Traverser(Context context) {
        this.style = context.getStyle();
        this.context = context;
    }

    private Decorator getDecorator(Class type) throws Exception {
        return this.context.getDecorator(type);
    }

    public Object read(InputNode node, Class type) throws Exception {
        Composite factory = getComposite(type);
        Object value = factory.read(node);
        if (value == null) {
            return null;
        }
        Class real = value.getClass();
        return read(node, real, value);
    }

    public Object read(InputNode node, Object value) throws Exception {
        Class type = value.getClass();
        Composite factory = getComposite(type);
        Object real = factory.read(node, value);
        return read(node, type, real);
    }

    private Object read(InputNode node, Class type, Object value) throws Exception {
        String root = getName(type);
        if (root != null) {
            return value;
        }
        throw new RootException("Root annotation required for %s", type);
    }

    public boolean validate(InputNode node, Class type) throws Exception {
        Composite factory = getComposite(type);
        String root = getName(type);
        if (root != null) {
            return factory.validate(node);
        }
        throw new RootException("Root annotation required for %s", type);
    }

    public void write(OutputNode node, Object source) throws Exception {
        write(node, source, source.getClass());
    }

    public void write(OutputNode node, Object source, Class expect) throws Exception {
        Class type = source.getClass();
        String root = getName(type);
        if (root == null) {
            throw new RootException("Root annotation required for %s", type);
        }
        write(node, source, expect, root);
    }

    public void write(OutputNode node, Object source, Class expect, String name) throws Exception {
        OutputNode child = node.getChild(name);
        Type type = getType(expect);
        if (source != null) {
            Class actual = source.getClass();
            Decorator decorator = getDecorator(actual);
            if (decorator != null) {
                decorator.decorate(child);
            }
            if (!this.context.setOverride(type, source, child)) {
                getComposite(actual).write(child, source);
            }
        }
        child.commit();
    }

    private Composite getComposite(Class expect) throws Exception {
        Type type = getType(expect);
        if (expect != null) {
            return new Composite(this.context, type);
        }
        throw new RootException("Can not instantiate null class", new Object[0]);
    }

    private Type getType(Class type) {
        return new ClassType(type);
    }

    protected String getName(Class type) throws Exception {
        String root = this.context.getName(type);
        String name = this.style.getElement(root);
        return name;
    }
}

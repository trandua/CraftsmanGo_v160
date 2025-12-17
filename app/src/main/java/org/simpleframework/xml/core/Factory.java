package org.simpleframework.xml.core;

import java.lang.reflect.Modifier;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Position;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class Factory {
    protected Context context;
    protected Class override;
    protected Support support;
    protected Type type;

    /* JADX INFO: Access modifiers changed from: protected */
    public Factory(Context context, Type type) {
        this(context, type, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Factory(Context context, Type type, Class override) {
        this.support = context.getSupport();
        this.override = override;
        this.context = context;
        this.type = type;
    }

    public Class getType() {
        return this.override != null ? this.override : this.type.getType();
    }

    public Object getInstance() throws Exception {
        Class type = getType();
        if (isInstantiable(type)) {
            return type.newInstance();
        }
        throw new InstantiationException("Type %s can not be instantiated", type);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Value getOverride(InputNode node) throws Exception {
        Value value = getConversion(node);
        if (value != null) {
            Position line = node.getPosition();
            Class proposed = value.getType();
            Class expect = getType();
            if (!isCompatible(expect, proposed)) {
                throw new InstantiationException("Incompatible %s for %s at %s", proposed, this.type, line);
            }
        }
        return value;
    }

    public boolean setOverride(Type type, Object value, OutputNode node) throws Exception {
        Class expect = type.getType();
        if (expect.isPrimitive()) {
            type = getPrimitive(type, expect);
        }
        return this.context.setOverride(type, value, node);
    }

    private Type getPrimitive(Type type, Class expect) throws Exception {
        Support support = this.support;
        Class convert = Support.getPrimitive(expect);
        if (convert != expect) {
            return new OverrideType(type, convert);
        }
        return type;
    }

    public Value getConversion(InputNode node) throws Exception {
        Value value = this.context.getOverride(this.type, node);
        if (value == null || this.override == null) {
            return value;
        }
        Class proposed = value.getType();
        if (!isCompatible(this.override, proposed)) {
            return new OverrideValue(value, this.override);
        }
        return value;
    }

    public static boolean isCompatible(Class expect, Class type) {
        if (expect.isArray()) {
            expect = expect.getComponentType();
        }
        return expect.isAssignableFrom(type);
    }

    public static boolean isInstantiable(Class type) {
        int modifiers = type.getModifiers();
        return !Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers);
    }
}

package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class EnumTransform implements Transform<Enum> {
    private final Class type;

    public EnumTransform(Class type) {
        this.type = type;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.transform.Transform
    public Enum read(String value) throws Exception {
        return Enum.valueOf(this.type, value);
    }

    public String write(Enum value) throws Exception {
        return value.name();
    }
}

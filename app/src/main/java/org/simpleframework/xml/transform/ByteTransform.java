package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class ByteTransform implements Transform<Byte> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.transform.Transform
    public Byte read(String value) {
        return Byte.valueOf(value);
    }

    public String write(Byte value) {
        return value.toString();
    }
}

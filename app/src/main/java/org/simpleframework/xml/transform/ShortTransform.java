package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class ShortTransform implements Transform<Short> {
    @Override // org.simpleframework.xml.transform.Transform
    public Short read(String value) {
        return Short.valueOf(value);
    }

    public String write(Short value) {
        return value.toString();
    }
}

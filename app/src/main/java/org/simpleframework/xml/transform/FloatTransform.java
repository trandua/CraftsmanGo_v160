package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class FloatTransform implements Transform<Float> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.transform.Transform
    public Float read(String value) {
        return Float.valueOf(value);
    }

    public String write(Float value) {
        return value.toString();
    }
}

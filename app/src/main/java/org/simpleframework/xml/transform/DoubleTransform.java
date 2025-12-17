package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class DoubleTransform implements Transform<Double> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.transform.Transform
    public Double read(String value) {
        return Double.valueOf(value);
    }

    public String write(Double value) {
        return value.toString();
    }
}

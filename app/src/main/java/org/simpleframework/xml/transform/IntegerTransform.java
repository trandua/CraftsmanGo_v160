package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class IntegerTransform implements Transform<Integer> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.transform.Transform
    public Integer read(String value) {
        return Integer.valueOf(value);
    }

    public String write(Integer value) {
        return value.toString();
    }
}

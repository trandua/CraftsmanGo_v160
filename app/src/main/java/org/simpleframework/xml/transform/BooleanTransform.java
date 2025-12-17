package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class BooleanTransform implements Transform<Boolean> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.transform.Transform
    public Boolean read(String value) {
        return Boolean.valueOf(value);
    }

    public String write(Boolean value) {
        return value.toString();
    }
}

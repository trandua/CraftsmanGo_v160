package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class LongTransform implements Transform<Long> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.transform.Transform
    public Long read(String value) {
        return Long.valueOf(value);
    }

    public String write(Long value) {
        return value.toString();
    }
}

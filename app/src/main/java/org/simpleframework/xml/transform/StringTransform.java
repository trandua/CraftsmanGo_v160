package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class StringTransform implements Transform<String> {
    @Override // org.simpleframework.xml.transform.Transform
    public String read(String value) {
        return value;
    }

    public String write(String value) {
        return value;
    }
}

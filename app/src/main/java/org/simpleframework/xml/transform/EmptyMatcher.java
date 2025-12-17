package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class EmptyMatcher implements Matcher {
    EmptyMatcher() {
    }

    @Override // org.simpleframework.xml.transform.Matcher
    public Transform match(Class type) throws Exception {
        return null;
    }
}

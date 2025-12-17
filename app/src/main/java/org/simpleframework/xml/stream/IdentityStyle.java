package org.simpleframework.xml.stream;

/* loaded from: classes.dex */
class IdentityStyle implements Style {
    @Override // org.simpleframework.xml.stream.Style
    public String getAttribute(String name) {
        return name;
    }

    @Override // org.simpleframework.xml.stream.Style
    public String getElement(String name) {
        return name;
    }
}

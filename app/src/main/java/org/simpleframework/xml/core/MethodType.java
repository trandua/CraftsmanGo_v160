package org.simpleframework.xml.core;

/* loaded from: classes.dex */
enum MethodType {
    GET(3),
    IS(2),
    SET(3),
    NONE(0);
    
    private int prefix;

    MethodType(int prefix) {
        this.prefix = prefix;
    }

    public int getPrefix() {
        return this.prefix;
    }
}

package org.simpleframework.xml.core;

/* loaded from: classes.dex */
interface Instance {
    Object getInstance() throws Exception;

    Class getType();

    boolean isReference();

    Object setInstance(Object obj) throws Exception;
}

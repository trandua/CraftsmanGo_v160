package org.simpleframework.xml.stream;

/* loaded from: classes.dex */
public interface Node {
    String getName();

    Node getParent();

    String getValue() throws Exception;
}

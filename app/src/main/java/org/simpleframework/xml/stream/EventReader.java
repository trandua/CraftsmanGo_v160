package org.simpleframework.xml.stream;

/* loaded from: classes.dex */
interface EventReader {
    EventNode next() throws Exception;

    EventNode peek() throws Exception;
}

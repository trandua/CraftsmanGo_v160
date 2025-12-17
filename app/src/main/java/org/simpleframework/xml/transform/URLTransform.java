package org.simpleframework.xml.transform;

import java.net.URL;

/* loaded from: classes.dex */
class URLTransform implements Transform<URL> {
    @Override // org.simpleframework.xml.transform.Transform
    public URL read(String target) throws Exception {
        return new URL(target);
    }

    public String write(URL target) throws Exception {
        return target.toString();
    }
}

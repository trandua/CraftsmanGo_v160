package org.simpleframework.xml.transform;

import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
class AtomicLongTransform implements Transform<AtomicLong> {
    @Override // org.simpleframework.xml.transform.Transform
    public AtomicLong read(String value) {
        Long number = Long.valueOf(value);
        return new AtomicLong(number.longValue());
    }

    public String write(AtomicLong value) {
        return value.toString();
    }
}

package org.simpleframework.xml.transform;

import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
class AtomicIntegerTransform implements Transform<AtomicInteger> {
    @Override // org.simpleframework.xml.transform.Transform
    public AtomicInteger read(String value) {
        Integer number = Integer.valueOf(value);
        return new AtomicInteger(number.intValue());
    }

    public String write(AtomicInteger value) {
        return value.toString();
    }
}

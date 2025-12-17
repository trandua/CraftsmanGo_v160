package org.simpleframework.xml.transform;

import java.math.BigInteger;

/* loaded from: classes.dex */
class BigIntegerTransform implements Transform<BigInteger> {
    @Override // org.simpleframework.xml.transform.Transform
    public BigInteger read(String value) {
        return new BigInteger(value);
    }

    public String write(BigInteger value) {
        return value.toString();
    }
}

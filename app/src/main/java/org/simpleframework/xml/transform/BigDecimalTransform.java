package org.simpleframework.xml.transform;

import java.math.BigDecimal;

/* loaded from: classes.dex */
class BigDecimalTransform implements Transform<BigDecimal> {
    @Override // org.simpleframework.xml.transform.Transform
    public BigDecimal read(String value) {
        return new BigDecimal(value);
    }

    public String write(BigDecimal value) {
        return value.toString();
    }
}

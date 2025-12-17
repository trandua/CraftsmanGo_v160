package org.simpleframework.xml.transform;

import java.util.Currency;

/* loaded from: classes.dex */
class CurrencyTransform implements Transform<Currency> {
    @Override // org.simpleframework.xml.transform.Transform
    public Currency read(String symbol) {
        return Currency.getInstance(symbol);
    }

    public String write(Currency currency) {
        return currency.toString();
    }
}

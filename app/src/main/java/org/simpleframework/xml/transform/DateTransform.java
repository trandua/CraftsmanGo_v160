package org.simpleframework.xml.transform;

import java.util.Date;

/* loaded from: classes.dex */
class DateTransform<T extends Date> implements Transform<T> {
    private final DateFactory<T> factory;

    /* JADX WARN: Multi-variable type inference failed */
//    @Override // org.simpleframework.xml.transform.Transform
//    public /* bridge */ /* synthetic */ String write(Object x0) throws Exception {
//        return write((DateTransform<T>) ((Date) x0));
//    }

    public DateTransform(Class<T> type) throws Exception {
        this.factory = new DateFactory<>(type);
    }

    @Override // org.simpleframework.xml.transform.Transform
    public synchronized T read(String text) throws Exception {
        Long time;
        Date date = DateType.getDate(text);
        time = Long.valueOf(date.getTime());
        return this.factory.getInstance(time);
    }

    public synchronized String write(T date) throws Exception {
        return DateType.getText(date);
    }
}

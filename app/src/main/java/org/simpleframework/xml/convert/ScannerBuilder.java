package org.simpleframework.xml.convert;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.util.ConcurrentCache;

/* loaded from: classes.dex */
class ScannerBuilder extends ConcurrentCache<Scanner> {
    public Scanner build(Class<?> type) {
        Scanner scanner = (Scanner) get(type);
        if (scanner != null) {
            return scanner;
        }
        Scanner scanner2 = new Entry(type);
        put(type, scanner2);
        return scanner2;
    }

    /* loaded from: classes.dex */
    private static class Entry extends ConcurrentCache<Annotation> implements Scanner {
        private final Class root;

        public Entry(Class root) {
            this.root = root;
        }

        @Override // org.simpleframework.xml.convert.Scanner
        public <T extends Annotation> T scan(Class<T> type) {
            if (!contains(type)) {
                Annotation find = find(type);
                if (!(type == null || find == null)) {
                    put(type, find);
                }
            }
            return (T) ((Annotation) get(type));
        }

        private <T extends Annotation> T find(Class<T> label) {
            for (Class<?> type = this.root; type != null; type = type.getSuperclass()) {
                T value = (T) type.getAnnotation(label);
                if (value != null) {
                    return value;
                }
            }
            return null;
        }
    }
}

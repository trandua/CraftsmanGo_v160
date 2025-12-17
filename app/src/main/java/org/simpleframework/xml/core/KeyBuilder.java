package org.simpleframework.xml.core;

import java.util.Arrays;

/* loaded from: classes.dex */
class KeyBuilder {
    private final Label label;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum KeyType {
        TEXT,
        ATTRIBUTE,
        ELEMENT
    }

    public KeyBuilder(Label label) {
        this.label = label;
    }

    public Object getKey() throws Exception {
        return this.label.isAttribute() ? getKey(KeyType.ATTRIBUTE) : getKey(KeyType.ELEMENT);
    }

    private Object getKey(KeyType type) throws Exception {
        String[] list = this.label.getPaths();
        String text = getKey(list);
        return type == null ? text : new Key(type, text);
    }

    private String getKey(String[] list) throws Exception {
        StringBuilder builder = new StringBuilder();
        if (list.length > 0) {
            Arrays.sort(list);
            for (String path : list) {
                builder.append(path);
                builder.append('>');
            }
        }
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Key {
        private final KeyType type;
        private final String value;

        public Key(KeyType type, String value) throws Exception {
            this.value = value;
            this.type = type;
        }

        public boolean equals(Object value) {
            if (!(value instanceof Key)) {
                return false;
            }
            Key key = (Key) value;
            return equals(key);
        }

        public boolean equals(Key key) {
            if (this.type == key.type) {
                return key.value.equals(this.value);
            }
            return false;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return this.value;
        }
    }
}

package org.apache.james.mime4j.parser;

/* loaded from: classes.dex */
public final class Event {
    private final String code;
    public static final Event MIME_BODY_PREMATURE_END = new Event("Body part ended prematurely. Boundary detected in header or EOF reached.");
    public static final Event HEADERS_PREMATURE_END = new Event("Unexpected end of headers detected. Higher level boundary detected or EOF reached.");
    public static final Event INALID_HEADER = new Event("Invalid header encountered");

    public Event(String str) {
        if (str != null) {
            this.code = str;
            return;
        }
        throw new IllegalArgumentException("Code may not be null");
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Event) {
            return this.code.equals(((Event) obj).code);
        }
        return false;
    }

    public int hashCode() {
        return this.code.hashCode();
    }

    public String toString() {
        return this.code;
    }
}

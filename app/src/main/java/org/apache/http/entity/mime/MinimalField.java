package org.apache.http.entity.mime;

import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.util.ByteSequence;
import org.apache.james.mime4j.util.ContentUtil;

/* loaded from: classes.dex */
public class MinimalField implements Field {
    private final String name;
    private ByteSequence raw = null;
    private final String value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MinimalField(String str, String str2) {
        this.name = str;
        this.value = str2;
    }

    @Override // org.apache.james.mime4j.parser.Field
    public String getBody() {
        return this.value;
    }

    @Override // org.apache.james.mime4j.parser.Field
    public String getName() {
        return this.name;
    }

    @Override // org.apache.james.mime4j.parser.Field
    public ByteSequence getRaw() {
        if (this.raw == null) {
            this.raw = ContentUtil.encode(toString());
        }
        return this.raw;
    }

    public String toString() {
        return this.name + ": " + this.value;
    }
}

package org.apache.james.mime4j.parser;

import org.apache.james.mime4j.util.ByteSequence;
import org.apache.james.mime4j.util.ContentUtil;

/* loaded from: classes.dex */
class RawField implements Field {
    private String body;
    private int colonIdx;
    private String name;
    private final ByteSequence raw;

    public RawField(ByteSequence byteSequence, int i) {
        this.raw = byteSequence;
        this.colonIdx = i;
    }

    private String parseBody() {
        int i = this.colonIdx + 1;
        return ContentUtil.decode(this.raw, i, this.raw.length() - i);
    }

    private String parseName() {
        return ContentUtil.decode(this.raw, 0, this.colonIdx);
    }

    @Override // org.apache.james.mime4j.parser.Field
    public String getBody() {
        if (this.body == null) {
            this.body = parseBody();
        }
        return this.body;
    }

    @Override // org.apache.james.mime4j.parser.Field
    public String getName() {
        if (this.name == null) {
            this.name = parseName();
        }
        return this.name;
    }

    @Override // org.apache.james.mime4j.parser.Field
    public ByteSequence getRaw() {
        return this.raw;
    }

    public String toString() {
        return getName() + ':' + getBody();
    }
}

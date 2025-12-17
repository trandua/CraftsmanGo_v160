package org.apache.james.mime4j.field;

import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.util.ByteSequence;

/* loaded from: classes.dex */
public class UnstructuredField extends AbstractField {
    static final FieldParser PARSER = new FieldParser() { // from class: org.apache.james.mime4j.field.UnstructuredField.1
        @Override // org.apache.james.mime4j.field.FieldParser
        public ParsedField parse(String str, String str2, ByteSequence byteSequence) {
            return new UnstructuredField(str, str2, byteSequence);
        }
    };
    private boolean parsed = false;
    private String value;

    UnstructuredField(String str, String str2, ByteSequence byteSequence) {
        super(str, str2, byteSequence);
    }

    private void parse() {
        this.value = DecoderUtil.decodeEncodedWords(getBody());
        this.parsed = true;
    }

    public String getValue() {
        if (!this.parsed) {
            parse();
        }
        return this.value;
    }
}

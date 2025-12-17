package org.apache.james.mime4j.field;

import org.apache.james.mime4j.util.ByteSequence;
import org.apache.james.mime4j.util.MimeUtil;

/* loaded from: classes.dex */
public class ContentTransferEncodingField extends AbstractField {
    static final FieldParser PARSER = new FieldParser() { // from class: org.apache.james.mime4j.field.ContentTransferEncodingField.1
        @Override // org.apache.james.mime4j.field.FieldParser
        public ParsedField parse(String str, String str2, ByteSequence byteSequence) {
            return new ContentTransferEncodingField(str, str2, byteSequence);
        }
    };
    private String encoding;

    ContentTransferEncodingField(String str, String str2, ByteSequence byteSequence) {
        super(str, str2, byteSequence);
        this.encoding = str2.trim().toLowerCase();
    }

    public static String getEncoding(ContentTransferEncodingField contentTransferEncodingField) {
        return (contentTransferEncodingField == null || contentTransferEncodingField.getEncoding().length() == 0) ? MimeUtil.ENC_7BIT : contentTransferEncodingField.getEncoding();
    }

    public String getEncoding() {
        return this.encoding;
    }
}

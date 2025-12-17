package org.apache.james.mime4j.field;

import java.util.HashMap;
import java.util.Map;
import org.apache.james.mime4j.util.ByteSequence;

/* loaded from: classes.dex */
public class DelegatingFieldParser implements FieldParser {
    private Map<String, FieldParser> parsers = new HashMap();
    private FieldParser defaultParser = UnstructuredField.PARSER;

    public FieldParser getParser(String str) {
        FieldParser fieldParser = this.parsers.get(str.toLowerCase());
        return fieldParser == null ? this.defaultParser : fieldParser;
    }

    @Override // org.apache.james.mime4j.field.FieldParser
    public ParsedField parse(String str, String str2, ByteSequence byteSequence) {
        return getParser(str).parse(str, str2, byteSequence);
    }

    public void setFieldParser(String str, FieldParser fieldParser) {
        this.parsers.put(str.toLowerCase(), fieldParser);
    }
}

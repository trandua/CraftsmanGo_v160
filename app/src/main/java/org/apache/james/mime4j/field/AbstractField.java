package org.apache.james.mime4j.field;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.util.ByteSequence;
import org.apache.james.mime4j.util.ContentUtil;
import org.apache.james.mime4j.util.MimeUtil;

/* loaded from: classes.dex */
public abstract class AbstractField implements ParsedField {
    private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("^([\\x21-\\x39\\x3b-\\x7e]+):");
    private static final DefaultFieldParser parser = new DefaultFieldParser();
    private final String body;
    private final String name;
    private final ByteSequence raw;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractField(String str, String str2, ByteSequence byteSequence) {
        this.name = str;
        this.body = str2;
        this.raw = byteSequence;
    }

    public static DefaultFieldParser getParser() {
        return parser;
    }

    public static ParsedField parse(String str) throws MimeException {
        return parse(ContentUtil.encode(str), str);
    }

    public static ParsedField parse(ByteSequence byteSequence) throws MimeException {
        return parse(byteSequence, ContentUtil.decode(byteSequence));
    }

    private static ParsedField parse(ByteSequence byteSequence, String str) throws MimeException {
        String unfold = MimeUtil.unfold(str);
        Matcher matcher = FIELD_NAME_PATTERN.matcher(unfold);
        if (matcher.find()) {
            String group = matcher.group(1);
            String substring = unfold.substring(matcher.end());
            if (substring.length() > 0 && substring.charAt(0) == ' ') {
                substring = substring.substring(1);
            }
            return parser.parse(group, substring, byteSequence);
        }
        throw new MimeException("Invalid field in string");
    }

    @Override // org.apache.james.mime4j.parser.Field
    public String getBody() {
        return this.body;
    }

    @Override // org.apache.james.mime4j.parser.Field
    public String getName() {
        return this.name;
    }

    @Override // org.apache.james.mime4j.field.ParsedField
    public ParseException getParseException() {
        return null;
    }

    @Override // org.apache.james.mime4j.parser.Field
    public ByteSequence getRaw() {
        return this.raw;
    }

    @Override // org.apache.james.mime4j.field.ParsedField
    public boolean isValidField() {
        return getParseException() == null;
    }

    public String toString() {
        return this.name + ": " + this.body;
    }
}

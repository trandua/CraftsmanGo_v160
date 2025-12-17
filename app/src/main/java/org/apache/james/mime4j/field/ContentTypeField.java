package org.apache.james.mime4j.field;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.field.contenttype.parser.ContentTypeParser;
import org.apache.james.mime4j.field.contenttype.parser.ParseException;
import org.apache.james.mime4j.field.contenttype.parser.TokenMgrError;
import org.apache.james.mime4j.util.ByteSequence;

/* loaded from: classes.dex */
public class ContentTypeField extends AbstractField {
    public static final String PARAM_BOUNDARY = "boundary";
    public static final String PARAM_CHARSET = "charset";
    public static final String TYPE_MESSAGE_RFC822 = "message/rfc822";
    public static final String TYPE_MULTIPART_DIGEST = "multipart/digest";
    public static final String TYPE_MULTIPART_PREFIX = "multipart/";
    public static final String TYPE_TEXT_PLAIN = "text/plain";
    private ParseException parseException;
    private static Log log = LogFactory.getLog(ContentTypeField.class);
    static final FieldParser PARSER = new FieldParser() { // from class: org.apache.james.mime4j.field.ContentTypeField.1
        @Override // org.apache.james.mime4j.field.FieldParser
        public ParsedField parse(String str, String str2, ByteSequence byteSequence) {
            return new ContentTypeField(str, str2, byteSequence);
        }
    };
    private boolean parsed = false;
    private String mimeType = "";
    private Map<String, String> parameters = new HashMap();

    ContentTypeField(String str, String str2, ByteSequence byteSequence) {
        super(str, str2, byteSequence);
    }

    public static String getCharset(ContentTypeField contentTypeField) {
        String charset;
        return (contentTypeField == null || (charset = contentTypeField.getCharset()) == null || charset.length() <= 0) ? "us-ascii" : charset;
    }

    public static String getMimeType(ContentTypeField contentTypeField, ContentTypeField contentTypeField2) {
        return (contentTypeField == null || contentTypeField.getMimeType().length() == 0 || (contentTypeField.isMultipart() && contentTypeField.getBoundary() == null)) ? (contentTypeField2 == null || !contentTypeField2.isMimeType(TYPE_MULTIPART_DIGEST)) ? "text/plain" : TYPE_MESSAGE_RFC822 : contentTypeField.getMimeType();
    }

    private void parse() {
        String body = getBody();
        ContentTypeParser contentTypeParser = new ContentTypeParser(new StringReader(body));
        try {
            contentTypeParser.parseAll();
        } catch (ParseException e) {
            if (log.isDebugEnabled()) {
                Log log2 = log;
                log2.debug("Parsing value '" + body + "': " + e.getMessage());
            }
            this.parseException = e;
        } catch (TokenMgrError e2) {
            if (log.isDebugEnabled()) {
                Log log3 = log;
                log3.debug("Parsing value '" + body + "': " + e2.getMessage());
            }
            this.parseException = new ParseException(e2.getMessage());
        }
        String type = contentTypeParser.getType();
        String subType = contentTypeParser.getSubType();
        if (!(type == null || subType == null)) {
            this.mimeType = (type + "/" + subType).toLowerCase();
            List<String> paramNames = contentTypeParser.getParamNames();
            List<String> paramValues = contentTypeParser.getParamValues();
            if (!(paramNames == null || paramValues == null)) {
                int min = Math.min(paramNames.size(), paramValues.size());
                for (int i = 0; i < min; i++) {
                    this.parameters.put(paramNames.get(i).toLowerCase(), paramValues.get(i));
                }
            }
        }
        this.parsed = true;
    }

    public String getBoundary() {
        return getParameter(PARAM_BOUNDARY);
    }

    public String getCharset() {
        return getParameter(PARAM_CHARSET);
    }

    public String getMimeType() {
        if (!this.parsed) {
            parse();
        }
        return this.mimeType;
    }

    public String getParameter(String str) {
        if (!this.parsed) {
            parse();
        }
        return this.parameters.get(str.toLowerCase());
    }

    public Map<String, String> getParameters() {
        if (!this.parsed) {
            parse();
        }
        return Collections.unmodifiableMap(this.parameters);
    }

    @Override // org.apache.james.mime4j.field.AbstractField, org.apache.james.mime4j.field.ParsedField
    public ParseException getParseException() {
        if (!this.parsed) {
            parse();
        }
        return this.parseException;
    }

    public boolean isMimeType(String str) {
        if (!this.parsed) {
            parse();
        }
        return this.mimeType.equalsIgnoreCase(str);
    }

    public boolean isMultipart() {
        if (!this.parsed) {
            parse();
        }
        return this.mimeType.startsWith(TYPE_MULTIPART_PREFIX);
    }
}

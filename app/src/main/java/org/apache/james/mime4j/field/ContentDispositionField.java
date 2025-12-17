package org.apache.james.mime4j.field;

import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.field.contentdisposition.parser.ContentDispositionParser;
import org.apache.james.mime4j.field.contentdisposition.parser.TokenMgrError;
import org.apache.james.mime4j.field.datetime.parser.DateTimeParser;
import org.apache.james.mime4j.util.ByteSequence;

/* loaded from: classes.dex */
public class ContentDispositionField extends AbstractField {
    public static final String DISPOSITION_TYPE_ATTACHMENT = "attachment";
    public static final String DISPOSITION_TYPE_INLINE = "inline";
    public static final String PARAM_CREATION_DATE = "creation-date";
    public static final String PARAM_FILENAME = "filename";
    public static final String PARAM_MODIFICATION_DATE = "modification-date";
    public static final String PARAM_READ_DATE = "read-date";
    public static final String PARAM_SIZE = "size";
    private Date creationDate;
    private boolean creationDateParsed;
    private Date modificationDate;
    private boolean modificationDateParsed;
    private ParseException parseException;
    private Date readDate;
    private boolean readDateParsed;
    private static Log log = LogFactory.getLog(ContentDispositionField.class);
    static final FieldParser PARSER = new FieldParser() { // from class: org.apache.james.mime4j.field.ContentDispositionField.1
        @Override // org.apache.james.mime4j.field.FieldParser
        public ParsedField parse(String str, String str2, ByteSequence byteSequence) {
            return new ContentDispositionField(str, str2, byteSequence);
        }
    };
    private boolean parsed = false;
    private String dispositionType = "";
    private Map<String, String> parameters = new HashMap();

    ContentDispositionField(String str, String str2, ByteSequence byteSequence) {
        super(str, str2, byteSequence);
    }

    private void parse() {
        String body = getBody();
        ContentDispositionParser contentDispositionParser = new ContentDispositionParser(new StringReader(body));
        try {
            contentDispositionParser.parseAll();
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
        String dispositionType = contentDispositionParser.getDispositionType();
        if (dispositionType != null) {
            this.dispositionType = dispositionType.toLowerCase(Locale.US);
            List<String> paramNames = contentDispositionParser.getParamNames();
            List<String> paramValues = contentDispositionParser.getParamValues();
            if (!(paramNames == null || paramValues == null)) {
                int min = Math.min(paramNames.size(), paramValues.size());
                for (int i = 0; i < min; i++) {
                    this.parameters.put(paramNames.get(i).toLowerCase(Locale.US), paramValues.get(i));
                }
            }
        }
        this.parsed = true;
    }

    private Date parseDate(String str) {
        String parameter = getParameter(str);
        if (parameter == null) {
            if (log.isDebugEnabled()) {
                Log log2 = log;
                log2.debug("Parsing " + str + " null");
            }
            return null;
        }
        try {
            return new DateTimeParser(new StringReader(parameter)).parseAll().getDate();
        } catch (ParseException e) {
            if (log.isDebugEnabled()) {
                Log log3 = log;
                log3.debug("Parsing " + str + " '" + parameter + "': " + e.getMessage());
            }
            return null;
        } catch (org.apache.james.mime4j.field.datetime.parser.TokenMgrError e2) {
            if (log.isDebugEnabled()) {
                Log log4 = log;
                log4.debug("Parsing " + str + " '" + parameter + "': " + e2.getMessage());
            }
            return null;
        }
    }

    public Date getCreationDate() {
        if (!this.creationDateParsed) {
            this.creationDate = parseDate("creation-date");
            this.creationDateParsed = true;
        }
        return this.creationDate;
    }

    public String getDispositionType() {
        if (!this.parsed) {
            parse();
        }
        return this.dispositionType;
    }

    public String getFilename() {
        return getParameter("filename");
    }

    public Date getModificationDate() {
        if (!this.modificationDateParsed) {
            this.modificationDate = parseDate("modification-date");
            this.modificationDateParsed = true;
        }
        return this.modificationDate;
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

    public Date getReadDate() {
        if (!this.readDateParsed) {
            this.readDate = parseDate("read-date");
            this.readDateParsed = true;
        }
        return this.readDate;
    }

    public long getSize() {
        String parameter = getParameter("size");
        if (parameter == null) {
            return -1L;
        }
        try {
            long parseLong = Long.parseLong(parameter);
            if (parseLong < 0) {
                return -1L;
            }
            return parseLong;
        } catch (NumberFormatException unused) {
            return -1L;
        }
    }

    public boolean isAttachment() {
        if (!this.parsed) {
            parse();
        }
        return this.dispositionType.equals(DISPOSITION_TYPE_ATTACHMENT);
    }

    public boolean isDispositionType(String str) {
        if (!this.parsed) {
            parse();
        }
        return this.dispositionType.equalsIgnoreCase(str);
    }

    public boolean isInline() {
        if (!this.parsed) {
            parse();
        }
        return this.dispositionType.equals(DISPOSITION_TYPE_INLINE);
    }
}

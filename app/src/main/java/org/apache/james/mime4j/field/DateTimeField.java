package org.apache.james.mime4j.field;

import java.io.StringReader;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.field.datetime.parser.DateTimeParser;
import org.apache.james.mime4j.field.datetime.parser.ParseException;
import org.apache.james.mime4j.field.datetime.parser.TokenMgrError;
import org.apache.james.mime4j.util.ByteSequence;

/* loaded from: classes.dex */
public class DateTimeField extends AbstractField {
    private Date date;
    private ParseException parseException;
    private boolean parsed = false;
    private static Log log = LogFactory.getLog(DateTimeField.class);
    static final FieldParser PARSER = new FieldParser() { // from class: org.apache.james.mime4j.field.DateTimeField.1
        @Override // org.apache.james.mime4j.field.FieldParser
        public ParsedField parse(String str, String str2, ByteSequence byteSequence) {
            return new DateTimeField(str, str2, byteSequence);
        }
    };

    DateTimeField(String str, String str2, ByteSequence byteSequence) {
        super(str, str2, byteSequence);
    }

    private void parse() {
        String body = getBody();
        try {
            this.date = new DateTimeParser(new StringReader(body)).parseAll().getDate();
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
        this.parsed = true;
    }

    public Date getDate() {
        if (!this.parsed) {
            parse();
        }
        return this.date;
    }

    @Override // org.apache.james.mime4j.field.AbstractField, org.apache.james.mime4j.field.ParsedField
    public ParseException getParseException() {
        if (!this.parsed) {
            parse();
        }
        return this.parseException;
    }
}

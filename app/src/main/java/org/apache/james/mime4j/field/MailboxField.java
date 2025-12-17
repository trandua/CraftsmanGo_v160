package org.apache.james.mime4j.field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.field.address.AddressList;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.field.address.MailboxList;
import org.apache.james.mime4j.field.address.parser.ParseException;
import org.apache.james.mime4j.util.ByteSequence;

/* loaded from: classes.dex */
public class MailboxField extends AbstractField {
    private Mailbox mailbox;
    private ParseException parseException;
    private boolean parsed = false;
    private static Log log = LogFactory.getLog(MailboxField.class);
    static final FieldParser PARSER = new FieldParser() { // from class: org.apache.james.mime4j.field.MailboxField.1
        @Override // org.apache.james.mime4j.field.FieldParser
        public ParsedField parse(String str, String str2, ByteSequence byteSequence) {
            return new MailboxField(str, str2, byteSequence);
        }
    };

    MailboxField(String str, String str2, ByteSequence byteSequence) {
        super(str, str2, byteSequence);
    }

    private void parse() {
        String body = getBody();
        try {
            MailboxList flatten = AddressList.parse(body).flatten();
            if (flatten.size() > 0) {
                this.mailbox = flatten.get(0);
            }
        } catch (ParseException e) {
            if (log.isDebugEnabled()) {
                Log log2 = log;
                log2.debug("Parsing value '" + body + "': " + e.getMessage());
            }
            this.parseException = e;
        }
        this.parsed = true;
    }

    public Mailbox getMailbox() {
        if (!this.parsed) {
            parse();
        }
        return this.mailbox;
    }

    @Override // org.apache.james.mime4j.field.AbstractField, org.apache.james.mime4j.field.ParsedField
    public ParseException getParseException() {
        if (!this.parsed) {
            parse();
        }
        return this.parseException;
    }
}

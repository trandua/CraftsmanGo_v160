package org.apache.james.mime4j.field;

/* loaded from: classes.dex */
public class DefaultFieldParser extends DelegatingFieldParser {
    public DefaultFieldParser() {
        setFieldParser("Content-Transfer-Encoding", ContentTransferEncodingField.PARSER);
        setFieldParser("Content-Type", ContentTypeField.PARSER);
        setFieldParser("Content-Disposition", ContentDispositionField.PARSER);
        FieldParser fieldParser = DateTimeField.PARSER;
        setFieldParser("Date", fieldParser);
        setFieldParser(FieldName.RESENT_DATE, fieldParser);
        FieldParser fieldParser2 = MailboxListField.PARSER;
        setFieldParser(FieldName.FROM, fieldParser2);
        setFieldParser(FieldName.RESENT_FROM, fieldParser2);
        FieldParser fieldParser3 = MailboxField.PARSER;
        setFieldParser(FieldName.SENDER, fieldParser3);
        setFieldParser(FieldName.RESENT_SENDER, fieldParser3);
        FieldParser fieldParser4 = AddressListField.PARSER;
        setFieldParser(FieldName.TO, fieldParser4);
        setFieldParser(FieldName.RESENT_TO, fieldParser4);
        setFieldParser(FieldName.CC, fieldParser4);
        setFieldParser(FieldName.RESENT_CC, fieldParser4);
        setFieldParser(FieldName.BCC, fieldParser4);
        setFieldParser(FieldName.RESENT_BCC, fieldParser4);
        setFieldParser(FieldName.REPLY_TO, fieldParser4);
    }
}

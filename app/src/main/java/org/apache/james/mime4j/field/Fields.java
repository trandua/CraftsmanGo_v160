package org.apache.james.mime4j.field;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.apache.james.mime4j.codec.EncoderUtil;
import org.apache.james.mime4j.field.address.Address;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.util.ContentUtil;
import org.apache.james.mime4j.util.MimeUtil;

/* loaded from: classes.dex */
public class Fields {
    private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("[\\x21-\\x39\\x3b-\\x7e]+");

    private Fields() {
    }

    public static AddressListField addressList(String str, Iterable<Address> iterable) {
        checkValidFieldName(str);
        return addressList0(str, iterable);
    }

    private static AddressListField addressList0(String str, Iterable<Address> iterable) {
        return (AddressListField) parse(AddressListField.PARSER, str, encodeAddresses(iterable));
    }

    public static AddressListField bcc(Iterable<Address> iterable) {
        return addressList0(FieldName.BCC, iterable);
    }

    public static AddressListField bcc(Address address) {
        return addressList0(FieldName.BCC, Collections.singleton(address));
    }

    public static AddressListField bcc(Address... addressArr) {
        return addressList0(FieldName.BCC, Arrays.asList(addressArr));
    }

    public static AddressListField cc(Iterable<Address> iterable) {
        return addressList0(FieldName.CC, iterable);
    }

    public static AddressListField cc(Address address) {
        return addressList0(FieldName.CC, Collections.singleton(address));
    }

    public static AddressListField cc(Address... addressArr) {
        return addressList0(FieldName.CC, Arrays.asList(addressArr));
    }

    private static void checkValidFieldName(String str) {
        if (!FIELD_NAME_PATTERN.matcher(str).matches()) {
            throw new IllegalArgumentException("Invalid field name");
        }
    }

    public static ContentDispositionField contentDisposition(String str) {
        return (ContentDispositionField) parse(ContentDispositionField.PARSER, "Content-Disposition", str);
    }

    public static ContentDispositionField contentDisposition(String str, String str2) {
        return contentDisposition(str, str2, -1L, null, null, null);
    }

    public static ContentDispositionField contentDisposition(String str, String str2, long j) {
        return contentDisposition(str, str2, j, null, null, null);
    }

    public static ContentDispositionField contentDisposition(String str, String str2, long j, Date date, Date date2, Date date3) {
        HashMap hashMap = new HashMap();
        if (str2 != null) {
            hashMap.put("filename", str2);
        }
        if (j >= 0) {
            hashMap.put("size", Long.toString(j));
        }
        if (date != null) {
            hashMap.put("creation-date", MimeUtil.formatDate(date, null));
        }
        if (date2 != null) {
            hashMap.put("modification-date", MimeUtil.formatDate(date2, null));
        }
        if (date3 != null) {
            hashMap.put("read-date", MimeUtil.formatDate(date3, null));
        }
        return contentDisposition(str, hashMap);
    }

    public static ContentDispositionField contentDisposition(String str, Map<String, String> map) {
        if (!isValidDispositionType(str)) {
            throw new IllegalArgumentException();
        } else if (map == null || map.isEmpty()) {
            return (ContentDispositionField) parse(ContentDispositionField.PARSER, "Content-Disposition", str);
        } else {
            StringBuilder sb = new StringBuilder(str);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append("; ");
                sb.append(EncoderUtil.encodeHeaderParameter(entry.getKey(), entry.getValue()));
            }
            return contentDisposition(sb.toString());
        }
    }

    public static ContentTransferEncodingField contentTransferEncoding(String str) {
        return (ContentTransferEncodingField) parse(ContentTransferEncodingField.PARSER, "Content-Transfer-Encoding", str);
    }

    public static ContentTypeField contentType(String str) {
        return (ContentTypeField) parse(ContentTypeField.PARSER, "Content-Type", str);
    }

    public static ContentTypeField contentType(String str, Map<String, String> map) {
        if (!isValidMimeType(str)) {
            throw new IllegalArgumentException();
        } else if (map == null || map.isEmpty()) {
            return (ContentTypeField) parse(ContentTypeField.PARSER, "Content-Type", str);
        } else {
            StringBuilder sb = new StringBuilder(str);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append("; ");
                sb.append(EncoderUtil.encodeHeaderParameter(entry.getKey(), entry.getValue()));
            }
            return contentType(sb.toString());
        }
    }

    public static DateTimeField date(String str, Date date) {
        checkValidFieldName(str);
        return date0(str, date, null);
    }

    public static DateTimeField date(String str, Date date, TimeZone timeZone) {
        checkValidFieldName(str);
        return date0(str, date, timeZone);
    }

    public static DateTimeField date(Date date) {
        return date0("Date", date, null);
    }

    private static DateTimeField date0(String str, Date date, TimeZone timeZone) {
        return (DateTimeField) parse(DateTimeField.PARSER, str, MimeUtil.formatDate(date, timeZone));
    }

    private static String encodeAddresses(Iterable<? extends Address> iterable) {
        StringBuilder sb = new StringBuilder();
        for (Address address : iterable) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(address.getEncodedString());
        }
        return sb.toString();
    }

    public static MailboxListField from(Iterable<Mailbox> iterable) {
        return mailboxList0(FieldName.FROM, iterable);
    }

    public static MailboxListField from(Mailbox mailbox) {
        return mailboxList0(FieldName.FROM, Collections.singleton(mailbox));
    }

    public static MailboxListField from(Mailbox... mailboxArr) {
        return mailboxList0(FieldName.FROM, Arrays.asList(mailboxArr));
    }

    private static boolean isValidDispositionType(String str) {
        if (str == null) {
            return false;
        }
        return EncoderUtil.isToken(str);
    }

    private static boolean isValidMimeType(String str) {
        int indexOf;
        if (str == null || (indexOf = str.indexOf(47)) == -1) {
            return false;
        }
        return EncoderUtil.isToken(str.substring(0, indexOf)) && EncoderUtil.isToken(str.substring(indexOf + 1));
    }

    public static MailboxField mailbox(String str, Mailbox mailbox) {
        checkValidFieldName(str);
        return mailbox0(str, mailbox);
    }

    private static MailboxField mailbox0(String str, Mailbox mailbox) {
        return (MailboxField) parse(MailboxField.PARSER, str, encodeAddresses(Collections.singleton(mailbox)));
    }

    public static MailboxListField mailboxList(String str, Iterable<Mailbox> iterable) {
        checkValidFieldName(str);
        return mailboxList0(str, iterable);
    }

    private static MailboxListField mailboxList0(String str, Iterable<Mailbox> iterable) {
        return (MailboxListField) parse(MailboxListField.PARSER, str, encodeAddresses(iterable));
    }

    public static Field messageId(String str) {
        return parse(UnstructuredField.PARSER, FieldName.MESSAGE_ID, MimeUtil.createUniqueMessageId(str));
    }

    private static <F extends Field> F parse(FieldParser fieldParser, String str, String str2) {
        return (F) fieldParser.parse(str, str2, ContentUtil.encode(MimeUtil.fold(str + ": " + str2, 0)));
    }

    public static AddressListField replyTo(Iterable<Address> iterable) {
        return addressList0(FieldName.REPLY_TO, iterable);
    }

    public static AddressListField replyTo(Address address) {
        return addressList0(FieldName.REPLY_TO, Collections.singleton(address));
    }

    public static AddressListField replyTo(Address... addressArr) {
        return addressList0(FieldName.REPLY_TO, Arrays.asList(addressArr));
    }

    public static MailboxField sender(Mailbox mailbox) {
        return mailbox0(FieldName.SENDER, mailbox);
    }

    public static UnstructuredField subject(String str) {
        return (UnstructuredField) parse(UnstructuredField.PARSER, FieldName.SUBJECT, EncoderUtil.encodeIfNecessary(str, EncoderUtil.Usage.TEXT_TOKEN, 9));
    }

    public static AddressListField to(Iterable<Address> iterable) {
        return addressList0(FieldName.TO, iterable);
    }

    public static AddressListField to(Address address) {
        return addressList0(FieldName.TO, Collections.singleton(address));
    }

    public static AddressListField to(Address... addressArr) {
        return addressList0(FieldName.TO, Arrays.asList(addressArr));
    }
}

package org.apache.james.mime4j.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.field.AddressListField;
import org.apache.james.mime4j.field.DateTimeField;
import org.apache.james.mime4j.field.FieldName;
import org.apache.james.mime4j.field.Fields;
import org.apache.james.mime4j.field.MailboxField;
import org.apache.james.mime4j.field.MailboxListField;
import org.apache.james.mime4j.field.UnstructuredField;
import org.apache.james.mime4j.field.address.Address;
import org.apache.james.mime4j.field.address.AddressList;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.field.address.MailboxList;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.parser.MimeEntityConfig;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.storage.DefaultStorageProvider;
import org.apache.james.mime4j.storage.StorageProvider;

/* loaded from: classes.dex */
public class Message extends Entity implements Body {
    public Message() {
    }

    public Message(InputStream inputStream) throws IOException, MimeIOException {
        this(inputStream, null, DefaultStorageProvider.getInstance());
    }

    public Message(InputStream inputStream, MimeEntityConfig mimeEntityConfig) throws IOException, MimeIOException {
        this(inputStream, mimeEntityConfig, DefaultStorageProvider.getInstance());
    }

    public Message(InputStream inputStream, MimeEntityConfig mimeEntityConfig, StorageProvider storageProvider) throws IOException, MimeIOException {
        try {
            MimeStreamParser mimeStreamParser = new MimeStreamParser(mimeEntityConfig);
            mimeStreamParser.setContentHandler(new MessageBuilder(this, storageProvider));
            mimeStreamParser.parse(inputStream);
        } catch (MimeException e) {
            throw new MimeIOException(e);
        }
    }

    public Message(Message message) {
        super(message);
    }

    private AddressList getAddressList(String str) {
        AddressListField addressListField = (AddressListField) obtainField(str);
        if (addressListField == null) {
            return null;
        }
        return addressListField.getAddressList();
    }

    private Mailbox getMailbox(String str) {
        MailboxField mailboxField = (MailboxField) obtainField(str);
        if (mailboxField == null) {
            return null;
        }
        return mailboxField.getMailbox();
    }

    private MailboxList getMailboxList(String str) {
        MailboxListField mailboxListField = (MailboxListField) obtainField(str);
        if (mailboxListField == null) {
            return null;
        }
        return mailboxListField.getMailboxList();
    }

    private void setAddressList(String str, Collection<Address> collection) {
        Header obtainHeader = obtainHeader();
        if (collection == null || collection.isEmpty()) {
            obtainHeader.removeFields(str);
        } else {
            obtainHeader.setField(Fields.addressList(str, collection));
        }
    }

    private void setAddressList(String str, Address address) {
        setAddressList(str, address == null ? null : Collections.singleton(address));
    }

    private void setAddressList(String str, Address... addressArr) {
        setAddressList(str, addressArr == null ? null : Arrays.asList(addressArr));
    }

    private void setMailbox(String str, Mailbox mailbox) {
        Header obtainHeader = obtainHeader();
        if (mailbox == null) {
            obtainHeader.removeFields(str);
        } else {
            obtainHeader.setField(Fields.mailbox(str, mailbox));
        }
    }

    private void setMailboxList(String str, Collection<Mailbox> collection) {
        Header obtainHeader = obtainHeader();
        if (collection == null || collection.isEmpty()) {
            obtainHeader.removeFields(str);
        } else {
            obtainHeader.setField(Fields.mailboxList(str, collection));
        }
    }

    private void setMailboxList(String str, Mailbox mailbox) {
        setMailboxList(str, mailbox == null ? null : Collections.singleton(mailbox));
    }

    private void setMailboxList(String str, Mailbox... mailboxArr) {
        setMailboxList(str, mailboxArr == null ? null : Arrays.asList(mailboxArr));
    }

    public void createMessageId(String str) {
        obtainHeader().setField(Fields.messageId(str));
    }

    public AddressList getBcc() {
        return getAddressList(FieldName.BCC);
    }

    public AddressList getCc() {
        return getAddressList(FieldName.CC);
    }

    public Date getDate() {
        DateTimeField dateTimeField = (DateTimeField) obtainField("Date");
        if (dateTimeField == null) {
            return null;
        }
        return dateTimeField.getDate();
    }

    public MailboxList getFrom() {
        return getMailboxList(FieldName.FROM);
    }

    public String getMessageId() {
        Field obtainField = obtainField(FieldName.MESSAGE_ID);
        if (obtainField == null) {
            return null;
        }
        return obtainField.getBody();
    }

    public AddressList getReplyTo() {
        return getAddressList(FieldName.REPLY_TO);
    }

    public Mailbox getSender() {
        return getMailbox(FieldName.SENDER);
    }

    public String getSubject() {
        UnstructuredField unstructuredField = (UnstructuredField) obtainField(FieldName.SUBJECT);
        if (unstructuredField == null) {
            return null;
        }
        return unstructuredField.getValue();
    }

    public AddressList getTo() {
        return getAddressList(FieldName.TO);
    }

    public void setBcc(Collection<Address> collection) {
        setAddressList(FieldName.BCC, collection);
    }

    public void setBcc(Address address) {
        setAddressList(FieldName.BCC, address);
    }

    public void setBcc(Address... addressArr) {
        setAddressList(FieldName.BCC, addressArr);
    }

    public void setCc(Collection<Address> collection) {
        setAddressList(FieldName.CC, collection);
    }

    public void setCc(Address address) {
        setAddressList(FieldName.CC, address);
    }

    public void setCc(Address... addressArr) {
        setAddressList(FieldName.CC, addressArr);
    }

    public void setDate(Date date) {
        setDate(date, null);
    }

    public void setDate(Date date, TimeZone timeZone) {
        Header obtainHeader = obtainHeader();
        if (date == null) {
            obtainHeader.removeFields("Date");
        } else {
            obtainHeader.setField(Fields.date("Date", date, timeZone));
        }
    }

    public void setFrom(Collection<Mailbox> collection) {
        setMailboxList(FieldName.FROM, collection);
    }

    public void setFrom(Mailbox mailbox) {
        setMailboxList(FieldName.FROM, mailbox);
    }

    public void setFrom(Mailbox... mailboxArr) {
        setMailboxList(FieldName.FROM, mailboxArr);
    }

    public void setReplyTo(Collection<Address> collection) {
        setAddressList(FieldName.REPLY_TO, collection);
    }

    public void setReplyTo(Address address) {
        setAddressList(FieldName.REPLY_TO, address);
    }

    public void setReplyTo(Address... addressArr) {
        setAddressList(FieldName.REPLY_TO, addressArr);
    }

    public void setSender(Mailbox mailbox) {
        setMailbox(FieldName.SENDER, mailbox);
    }

    public void setSubject(String str) {
        Header obtainHeader = obtainHeader();
        if (str == null) {
            obtainHeader.removeFields(FieldName.SUBJECT);
        } else {
            obtainHeader.setField(Fields.subject(str));
        }
    }

    public void setTo(Collection<Address> collection) {
        setAddressList(FieldName.TO, collection);
    }

    public void setTo(Address address) {
        setAddressList(FieldName.TO, address);
    }

    public void setTo(Address... addressArr) {
        setAddressList(FieldName.TO, addressArr);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        MessageWriter.DEFAULT.writeEntity(this, outputStream);
    }
}

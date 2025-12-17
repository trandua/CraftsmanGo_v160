package org.apache.james.mime4j.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.descriptor.BodyDescriptor;
import org.apache.james.mime4j.field.AbstractField;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.storage.StorageProvider;
import org.apache.james.mime4j.util.ByteArrayBuffer;
import org.apache.james.mime4j.util.ByteSequence;

/* loaded from: classes.dex */
public class MessageBuilder implements ContentHandler {
    private final BodyFactory bodyFactory;
    private final Entity entity;
    private Stack<Object> stack;

    public MessageBuilder(Entity entity) {
        this.stack = new Stack<>();
        this.entity = entity;
        this.bodyFactory = new BodyFactory();
    }

    public MessageBuilder(Entity entity, StorageProvider storageProvider) {
        this.stack = new Stack<>();
        this.entity = entity;
        this.bodyFactory = new BodyFactory(storageProvider);
    }

    private void expect(Class<?> cls) {
        if (!cls.isInstance(this.stack.peek())) {
            throw new IllegalStateException("Internal stack error: Expected '" + cls.getName() + "' found '" + this.stack.peek().getClass().getName() + "'");
        }
    }

    private static ByteSequence loadStream(InputStream inputStream) throws IOException {
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(64);
        while (true) {
            int read = inputStream.read();
            if (read == -1) {
                return byteArrayBuffer;
            }
            byteArrayBuffer.append(read);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:12:0x003f  */
    @Override // org.apache.james.mime4j.parser.ContentHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void body(org.apache.james.mime4j.descriptor.BodyDescriptor r3, java.io.InputStream r4) throws org.apache.james.mime4j.MimeException, java.io.IOException {
        /*
            r2 = this;
            java.lang.Class<org.apache.james.mime4j.message.Entity> r0 = org.apache.james.mime4j.message.Entity.class
            r2.expect(r0)
            java.lang.String r0 = r3.getTransferEncoding()
            java.lang.String r1 = "base64"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0018
            org.apache.james.mime4j.codec.Base64InputStream r0 = new org.apache.james.mime4j.codec.Base64InputStream
            r0.<init>(r4)
        L_0x0016:
            r4 = r0
            goto L_0x0027
        L_0x0018:
            java.lang.String r1 = "quoted-printable"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0027
            org.apache.james.mime4j.codec.QuotedPrintableInputStream r0 = new org.apache.james.mime4j.codec.QuotedPrintableInputStream
            r0.<init>(r4)
            goto L_0x0016
        L_0x0027:
            java.lang.String r0 = r3.getMimeType()
            java.lang.String r1 = "text/"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x003f
            org.apache.james.mime4j.message.BodyFactory r0 = r2.bodyFactory
            java.lang.String r3 = r3.getCharset()
            org.apache.james.mime4j.message.TextBody r3 = r0.textBody(r4, r3)
            goto L_0x0045
        L_0x003f:
            org.apache.james.mime4j.message.BodyFactory r3 = r2.bodyFactory
            org.apache.james.mime4j.message.BinaryBody r3 = r3.binaryBody(r4)
        L_0x0045:
            java.util.Stack<java.lang.Object> r4 = r2.stack
            java.lang.Object r4 = r4.peek()
            org.apache.james.mime4j.message.Entity r4 = (org.apache.james.mime4j.message.Entity) r4
            r4.setBody(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.james.mime4j.message.MessageBuilder.body(org.apache.james.mime4j.descriptor.BodyDescriptor, java.io.InputStream):void");
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void endBodyPart() throws MimeException {
        expect(BodyPart.class);
        this.stack.pop();
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void endHeader() throws MimeException {
        expect(Header.class);
        expect(Entity.class);
        ((Entity) this.stack.peek()).setHeader((Header) this.stack.pop());
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void endMessage() throws MimeException {
        expect(Message.class);
        this.stack.pop();
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void endMultipart() throws MimeException {
        this.stack.pop();
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void epilogue(InputStream inputStream) throws MimeException, IOException {
        expect(Multipart.class);
        ((Multipart) this.stack.peek()).setEpilogueRaw(loadStream(inputStream));
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void field(Field field) throws MimeException {
        expect(Header.class);
        ((Header) this.stack.peek()).addField(AbstractField.parse(field.getRaw()));
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void preamble(InputStream inputStream) throws MimeException, IOException {
        expect(Multipart.class);
        ((Multipart) this.stack.peek()).setPreambleRaw(loadStream(inputStream));
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void raw(InputStream inputStream) throws MimeException, IOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void startBodyPart() throws MimeException {
        expect(Multipart.class);
        BodyPart bodyPart = new BodyPart();
        ((Multipart) this.stack.peek()).addBodyPart(bodyPart);
        this.stack.push(bodyPart);
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void startHeader() throws MimeException {
        this.stack.push(new Header());
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void startMessage() throws MimeException {
        if (this.stack.isEmpty()) {
            this.stack.push(this.entity);
            return;
        }
        expect(Entity.class);
        Message message = new Message();
        ((Entity) this.stack.peek()).setBody(message);
        this.stack.push(message);
    }

    @Override // org.apache.james.mime4j.parser.ContentHandler
    public void startMultipart(BodyDescriptor bodyDescriptor) throws MimeException {
        expect(Entity.class);
        Multipart multipart = new Multipart(bodyDescriptor.getSubType());
        ((Entity) this.stack.peek()).setBody(multipart);
        this.stack.push(multipart);
    }
}

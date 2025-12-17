package org.apache.james.mime4j.message;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.james.mime4j.field.ContentDispositionField;
import org.apache.james.mime4j.field.ContentTransferEncodingField;
import org.apache.james.mime4j.field.ContentTypeField;
import org.apache.james.mime4j.field.Fields;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.util.MimeUtil;

/* loaded from: classes.dex */
public abstract class Entity implements Disposable {
    private Body body;
    private Header header;
    private Entity parent;

    /* JADX INFO: Access modifiers changed from: protected */
    public Entity() {
        this.header = null;
        this.body = null;
        this.parent = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Entity(Entity entity) {
        this.header = null;
        this.body = null;
        this.parent = null;
        Header header = entity.header;
        if (header != null) {
            this.header = new Header(header);
        }
        Body body = entity.body;
        if (body != null) {
            setBody(BodyCopier.copy(body));
        }
    }

    @Override // org.apache.james.mime4j.message.Disposable
    public void dispose() {
        Body body = this.body;
        if (body != null) {
            body.dispose();
        }
    }

    public Body getBody() {
        return this.body;
    }

    public String getCharset() {
        return ContentTypeField.getCharset((ContentTypeField) getHeader().getField("Content-Type"));
    }

    public String getContentTransferEncoding() {
        return ContentTransferEncodingField.getEncoding((ContentTransferEncodingField) getHeader().getField("Content-Transfer-Encoding"));
    }

    public String getDispositionType() {
        ContentDispositionField contentDispositionField = (ContentDispositionField) obtainField("Content-Disposition");
        if (contentDispositionField == null) {
            return null;
        }
        return contentDispositionField.getDispositionType();
    }

    public String getFilename() {
        ContentDispositionField contentDispositionField = (ContentDispositionField) obtainField("Content-Disposition");
        if (contentDispositionField == null) {
            return null;
        }
        return contentDispositionField.getFilename();
    }

    public Header getHeader() {
        return this.header;
    }

    public String getMimeType() {
        return ContentTypeField.getMimeType((ContentTypeField) getHeader().getField("Content-Type"), getParent() != null ? (ContentTypeField) getParent().getHeader().getField("Content-Type") : null);
    }

    public Entity getParent() {
        return this.parent;
    }

    public boolean isMimeType(String str) {
        return getMimeType().equalsIgnoreCase(str);
    }

    public boolean isMultipart() {
        ContentTypeField contentTypeField = (ContentTypeField) getHeader().getField("Content-Type");
        return (contentTypeField == null || contentTypeField.getBoundary() == null || !getMimeType().startsWith(ContentTypeField.TYPE_MULTIPART_PREFIX)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <F extends Field> F obtainField(String str) {
        Header header = getHeader();
        if (header == null) {
            return null;
        }
        return (F) header.getField(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Header obtainHeader() {
        if (this.header == null) {
            this.header = new Header();
        }
        return this.header;
    }

    public Body removeBody() {
        Body body = this.body;
        if (body == null) {
            return null;
        }
        this.body = null;
        body.setParent(null);
        return body;
    }

    public void setBody(Body body) {
        if (this.body == null) {
            this.body = body;
            body.setParent(this);
            return;
        }
        throw new IllegalStateException("body already set");
    }

    public void setBody(Body body, String str) {
        setBody(body, str, null);
    }

    public void setBody(Body body, String str, Map<String, String> map) {
        setBody(body);
        obtainHeader().setField(Fields.contentType(str, map));
    }

    public void setContentDisposition(String str) {
        obtainHeader().setField(Fields.contentDisposition(str, null, -1L, null, null, null));
    }

    public void setContentDisposition(String str, String str2) {
        obtainHeader().setField(Fields.contentDisposition(str, str2, -1L, null, null, null));
    }

    public void setContentDisposition(String str, String str2, long j) {
        obtainHeader().setField(Fields.contentDisposition(str, str2, j, null, null, null));
    }

    public void setContentDisposition(String str, String str2, long j, Date date, Date date2, Date date3) {
        obtainHeader().setField(Fields.contentDisposition(str, str2, j, date, date2, date3));
    }

    public void setContentTransferEncoding(String str) {
        obtainHeader().setField(Fields.contentTransferEncoding(str));
    }

    public void setFilename(String str) {
        Header obtainHeader = obtainHeader();
        ContentDispositionField contentDispositionField = (ContentDispositionField) obtainHeader.getField("Content-Disposition");
        if (contentDispositionField != null) {
            String dispositionType = contentDispositionField.getDispositionType();
            HashMap hashMap = new HashMap(contentDispositionField.getParameters());
            if (str == null) {
                hashMap.remove("filename");
            } else {
                hashMap.put("filename", str);
            }
            obtainHeader.setField(Fields.contentDisposition(dispositionType, hashMap));
        } else if (str != null) {
            obtainHeader.setField(Fields.contentDisposition(ContentDispositionField.DISPOSITION_TYPE_ATTACHMENT, str, -1L, null, null, null));
        }
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public void setMessage(Message message) {
        setBody(message, ContentTypeField.TYPE_MESSAGE_RFC822, null);
    }

    public void setMultipart(Multipart multipart) {
        setBody(multipart, ContentTypeField.TYPE_MULTIPART_PREFIX + multipart.getSubType(), Collections.singletonMap(ContentTypeField.PARAM_BOUNDARY, MimeUtil.createUniqueBoundary()));
    }

    public void setMultipart(Multipart multipart, Map<String, String> map) {
        String str = ContentTypeField.TYPE_MULTIPART_PREFIX + multipart.getSubType();
        if (!map.containsKey(ContentTypeField.PARAM_BOUNDARY)) {
            HashMap hashMap = new HashMap(map);
            hashMap.put(ContentTypeField.PARAM_BOUNDARY, MimeUtil.createUniqueBoundary());
            map = hashMap;
        }
        setBody(multipart, str, map);
    }

    public void setParent(Entity entity) {
        this.parent = entity;
    }

    public void setText(TextBody textBody) {
        setText(textBody, "plain");
    }

    public void setText(TextBody textBody, String str) {
        String str2 = "text/" + str;
        String mimeCharset = textBody.getMimeCharset();
        setBody(textBody, str2, (mimeCharset == null || mimeCharset.equalsIgnoreCase("us-ascii")) ? null : Collections.singletonMap(ContentTypeField.PARAM_CHARSET, mimeCharset));
    }
}

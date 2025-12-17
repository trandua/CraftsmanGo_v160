package org.apache.http.entity.mime;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.protocol.HTTP;
import org.apache.james.mime4j.descriptor.ContentDescriptor;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.Header;

/* loaded from: classes.dex */
public class FormBodyPart extends BodyPart {
    private final String name;

    public FormBodyPart(String str, ContentBody contentBody) {
        if (str == null) {
            throw new IllegalArgumentException("Name may not be null");
        } else if (contentBody != null) {
            this.name = str;
            setHeader(new Header());
            setBody(contentBody);
            generateContentDisp(contentBody);
            generateContentType(contentBody);
            generateTransferEncoding(contentBody);
        } else {
            throw new IllegalArgumentException("Body may not be null");
        }
    }

    private void addField(String str, String str2) {
        getHeader().addField(new MinimalField(str, str2));
    }

    protected void generateContentDisp(ContentBody contentBody) {
        StringBuilder sb = new StringBuilder();
        sb.append("form-data; name=\"");
        sb.append(getName());
        sb.append("\"");
        if (contentBody.getFilename() != null) {
            sb.append("; filename=\"");
            sb.append(contentBody.getFilename());
            sb.append("\"");
        }
        addField("Content-Disposition", sb.toString());
    }

    protected void generateContentType(ContentDescriptor contentDescriptor) {
        if (contentDescriptor.getMimeType() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(contentDescriptor.getMimeType());
            if (contentDescriptor.getCharset() != null) {
                sb.append(HTTP.CHARSET_PARAM);
                sb.append(contentDescriptor.getCharset());
            }
            addField("Content-Type", sb.toString());
        }
    }

    protected void generateTransferEncoding(ContentDescriptor contentDescriptor) {
        if (contentDescriptor.getTransferEncoding() != null) {
            addField("Content-Transfer-Encoding", contentDescriptor.getTransferEncoding());
        }
    }

    public String getName() {
        return this.name;
    }
}

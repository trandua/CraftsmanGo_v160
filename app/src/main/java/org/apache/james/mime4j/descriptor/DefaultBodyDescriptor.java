package org.apache.james.mime4j.descriptor;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.field.ContentTypeField;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.util.MimeUtil;

/* loaded from: classes.dex */
public class DefaultBodyDescriptor implements MutableBodyDescriptor {
    private static final String DEFAULT_MEDIA_TYPE = "text";
    private static final String DEFAULT_MIME_TYPE = "text/plain";
    private static final String DEFAULT_SUB_TYPE = "plain";
    private static final String EMAIL_MESSAGE_MIME_TYPE = "message/rfc822";
    private static final String MEDIA_TYPE_MESSAGE = "message";
    private static final String MEDIA_TYPE_TEXT = "text";
    private static final String SUB_TYPE_EMAIL = "rfc822";
    private static final String US_ASCII = "us-ascii";
    private static Log log = LogFactory.getLog(DefaultBodyDescriptor.class);
    private String boundary;
    private String charset;
    private long contentLength;
    private boolean contentTransferEncSet;
    private boolean contentTypeSet;
    private String mediaType;
    private String mimeType;
    private Map<String, String> parameters;
    private String subType;
    private String transferEncoding;

    public DefaultBodyDescriptor() {
        this(null);
    }

    public DefaultBodyDescriptor(BodyDescriptor bodyDescriptor) {
        this.mediaType = "text";
        this.subType = DEFAULT_SUB_TYPE;
        this.mimeType = "text/plain";
        this.boundary = null;
        this.charset = US_ASCII;
        this.transferEncoding = MimeUtil.ENC_7BIT;
        this.parameters = new HashMap();
        this.contentLength = -1L;
        if (bodyDescriptor == null || !MimeUtil.isSameMimeType(ContentTypeField.TYPE_MULTIPART_DIGEST, bodyDescriptor.getMimeType())) {
            this.mimeType = "text/plain";
            this.subType = DEFAULT_SUB_TYPE;
            this.mediaType = "text";
            return;
        }
        this.mimeType = "message/rfc822";
        this.subType = SUB_TYPE_EMAIL;
        this.mediaType = "message";
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x005b  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x006a  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0088  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0096  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00aa  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseContentType(java.lang.String r10) {
        /*
            r9 = this;
            r0 = 1
            r9.contentTypeSet = r0
            java.util.Map r10 = org.apache.james.mime4j.util.MimeUtil.getHeaderParams(r10)
            java.lang.String r1 = ""
            java.lang.Object r2 = r10.get(r1)
            java.lang.String r2 = (java.lang.String) r2
            r3 = 0
            if (r2 == 0) goto L_0x005e
            java.lang.String r2 = r2.toLowerCase()
            java.lang.String r2 = r2.trim()
            r4 = 47
            int r4 = r2.indexOf(r4)
            r5 = -1
            r6 = 0
            if (r4 == r5) goto L_0x0056
            java.lang.String r5 = r2.substring(r6, r4)
            java.lang.String r5 = r5.trim()
            int r4 = r4 + r0
            java.lang.String r4 = r2.substring(r4)
            java.lang.String r4 = r4.trim()
            int r7 = r5.length()
            if (r7 <= 0) goto L_0x0058
            int r7 = r4.length()
            if (r7 <= 0) goto L_0x0058
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r5)
            java.lang.String r6 = "/"
            r2.append(r6)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            goto L_0x0059
        L_0x0056:
            r4 = r3
            r5 = r4
        L_0x0058:
            r0 = 0
        L_0x0059:
            if (r0 != 0) goto L_0x0060
            r2 = r3
            r4 = r2
            goto L_0x005f
        L_0x005e:
            r4 = r3
        L_0x005f:
            r5 = r4
        L_0x0060:
            java.lang.String r0 = "boundary"
            java.lang.Object r6 = r10.get(r0)
            java.lang.String r6 = (java.lang.String) r6
            if (r2 == 0) goto L_0x0080
            java.lang.String r7 = "multipart/"
            boolean r8 = r2.startsWith(r7)
            if (r8 == 0) goto L_0x0074
            if (r6 != 0) goto L_0x007a
        L_0x0074:
            boolean r7 = r2.startsWith(r7)
            if (r7 != 0) goto L_0x0080
        L_0x007a:
            r9.mimeType = r2
            r9.subType = r4
            r9.mediaType = r5
        L_0x0080:
            java.lang.String r2 = r9.mimeType
            boolean r2 = org.apache.james.mime4j.util.MimeUtil.isMultipart(r2)
            if (r2 == 0) goto L_0x008a
            r9.boundary = r6
        L_0x008a:
            java.lang.String r2 = "charset"
            java.lang.Object r4 = r10.get(r2)
            java.lang.String r4 = (java.lang.String) r4
            r9.charset = r3
            if (r4 == 0) goto L_0x00a6
            java.lang.String r3 = r4.trim()
            int r4 = r3.length()
            if (r4 <= 0) goto L_0x00a6
            java.lang.String r3 = r3.toLowerCase()
            r9.charset = r3
        L_0x00a6:
            java.lang.String r3 = r9.charset
            if (r3 != 0) goto L_0x00ba
            java.lang.String r3 = r9.mediaType
            java.lang.String r4 = "text"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x00ba
            java.lang.String r3 = "us-ascii"
            r9.charset = r3
        L_0x00ba:
            java.util.Map<java.lang.String, java.lang.String> r3 = r9.parameters
            r3.putAll(r10)
            java.util.Map<java.lang.String, java.lang.String> r10 = r9.parameters
            r10.remove(r1)
            java.util.Map<java.lang.String, java.lang.String> r10 = r9.parameters
            r10.remove(r0)
            java.util.Map<java.lang.String, java.lang.String> r10 = r9.parameters
            r10.remove(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.james.mime4j.descriptor.DefaultBodyDescriptor.parseContentType(java.lang.String):void");
    }

    @Override // org.apache.james.mime4j.descriptor.MutableBodyDescriptor
    public void addField(Field field) {
        String name = field.getName();
        String body = field.getBody();
        String lowerCase = name.trim().toLowerCase();
        if (lowerCase.equals("content-transfer-encoding") && !this.contentTransferEncSet) {
            this.contentTransferEncSet = true;
            String lowerCase2 = body.trim().toLowerCase();
            if (lowerCase2.length() > 0) {
                this.transferEncoding = lowerCase2;
            }
        } else if (lowerCase.equals("content-length") && this.contentLength == -1) {
            try {
                this.contentLength = Long.parseLong(body.trim());
            } catch (NumberFormatException unused) {
                Log log2 = log;
                log2.error("Invalid content-length: " + body);
            }
        } else if (lowerCase.equals("content-type") && !this.contentTypeSet) {
            parseContentType(body);
        }
    }

    @Override // org.apache.james.mime4j.descriptor.BodyDescriptor
    public String getBoundary() {
        return this.boundary;
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public String getCharset() {
        return this.charset;
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public long getContentLength() {
        return this.contentLength;
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public Map<String, String> getContentTypeParameters() {
        return this.parameters;
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public String getMediaType() {
        return this.mediaType;
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public String getMimeType() {
        return this.mimeType;
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public String getSubType() {
        return this.subType;
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public String getTransferEncoding() {
        return this.transferEncoding;
    }

    public String toString() {
        return this.mimeType;
    }
}

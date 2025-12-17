package org.apache.http.entity.mime.content;

import java.util.Collections;
import java.util.Map;
import org.apache.james.mime4j.message.Entity;
import org.apache.james.mime4j.message.SingleBody;

/* loaded from: classes.dex */
public abstract class AbstractContentBody extends SingleBody implements ContentBody {
    private final String mediaType;
    private final String mimeType;
    private Entity parent = null;
    private final String subType;

    public AbstractContentBody(String str) {
        if (str != null) {
            this.mimeType = str;
            int indexOf = str.indexOf(47);
            if (indexOf != -1) {
                this.mediaType = str.substring(0, indexOf);
                this.subType = str.substring(indexOf + 1);
                return;
            }
            this.mediaType = str;
            this.subType = null;
            return;
        }
        throw new IllegalArgumentException("MIME type may not be null");
    }

    @Override // org.apache.james.mime4j.message.SingleBody, org.apache.james.mime4j.message.Disposable
    public void dispose() {
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public Map<String, String> getContentTypeParameters() {
        return Collections.emptyMap();
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public String getMediaType() {
        return this.mediaType;
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public String getMimeType() {
        return this.mimeType;
    }

    @Override // org.apache.james.mime4j.message.SingleBody, org.apache.james.mime4j.message.Body
    public Entity getParent() {
        return this.parent;
    }

    @Override // org.apache.james.mime4j.descriptor.ContentDescriptor
    public String getSubType() {
        return this.subType;
    }

    @Override // org.apache.james.mime4j.message.SingleBody, org.apache.james.mime4j.message.Body
    public void setParent(Entity entity) {
        this.parent = entity;
    }
}

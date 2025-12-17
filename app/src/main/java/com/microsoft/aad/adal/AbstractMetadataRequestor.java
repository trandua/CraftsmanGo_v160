package com.microsoft.aad.adal;

import com.google.gson.Gson;
import java.util.UUID;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public abstract class AbstractMetadataRequestor<MetadataType, MetadataRequestOptions> {
    private UUID mCorrelationId;
    private Gson mGson;
    private final IWebRequestHandler mWebrequestHandler = new WebRequestHandler();

    public abstract MetadataType parseMetadata(HttpWebResponse httpWebResponse) throws Exception;

    public abstract MetadataType requestMetadata(MetadataRequestOptions metadatarequestoptions) throws Exception;

    public final UUID getCorrelationId() {
        return this.mCorrelationId;
    }

    public IWebRequestHandler getWebrequestHandler() {
        return this.mWebrequestHandler;
    }

    public Gson parser() {
        Gson gson;
        synchronized (this) {
            if (this.mGson == null) {
                this.mGson = new Gson();
            }
            gson = this.mGson;
        }
        return gson;
    }

    public final void setCorrelationId(UUID uuid) {
        this.mCorrelationId = uuid;
    }
}

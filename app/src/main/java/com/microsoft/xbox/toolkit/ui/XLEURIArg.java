package com.microsoft.xbox.toolkit.ui;

import java.net.URI;

/* loaded from: classes3.dex */
public class XLEURIArg {
    private final int errorResourceId;
    private final int loadingResourceId;
    private final URI uri;

    public XLEURIArg(URI uri) {
        this(uri, -1, -1);
    }

    public XLEURIArg(URI uri, int i, int i2) {
        this.uri = uri;
        this.loadingResourceId = i;
        this.errorResourceId = i2;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof XLEURIArg) {
            XLEURIArg xLEURIArg = (XLEURIArg) obj;
            if (this.loadingResourceId == xLEURIArg.loadingResourceId && this.errorResourceId == xLEURIArg.errorResourceId) {
                URI uri = this.uri;
                URI uri2 = xLEURIArg.uri;
                return uri == uri2 || (uri != null && uri.equals(uri2));
            }
            return false;
        }
        return false;
    }

    public int getErrorResourceId() {
        return this.errorResourceId;
    }

    public int getLoadingResourceId() {
        return this.loadingResourceId;
    }

    public TextureBindingOption getTextureBindingOption() {
        return new TextureBindingOption(-1, -1, this.loadingResourceId, this.errorResourceId, false);
    }

    public URI getUri() {
        return this.uri;
    }

    public int hashCode() {
        int i = ((this.loadingResourceId + 13) * 17) + this.errorResourceId;
        URI uri = this.uri;
        return uri != null ? (i * 23) + uri.hashCode() : i;
    }
}

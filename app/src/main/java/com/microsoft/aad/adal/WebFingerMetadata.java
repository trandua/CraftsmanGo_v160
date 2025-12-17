package com.microsoft.aad.adal;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/* loaded from: classes3.dex */
final class WebFingerMetadata {
    @SerializedName("links")
    private List<Link> mLinks;
    @SerializedName("subject")
    private String mSubject;

    WebFingerMetadata() {
    }

    public List<Link> getLinks() {
        return this.mLinks;
    }

    public String getSubject() {
        return this.mSubject;
    }

    public void setLinks(List<Link> list) {
        this.mLinks = list;
    }

    public void setSubject(String str) {
        this.mSubject = str;
    }
}

package com.microsoft.xbox.idp.services;

/* loaded from: classes3.dex */
public interface Endpoints {

    /* loaded from: classes3.dex */
    public enum Type {
        PROD,
        DNET
    }

    String accounts();

    String privacy();

    String profile();

    String userAccount();

    String userManagement();
}

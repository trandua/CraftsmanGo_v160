package com.microsoft.aad.adal;

/* loaded from: classes3.dex */
class DeserializationAuthenticationException extends AuthenticationException {
    static final long serialVersionUID = 1;

    public DeserializationAuthenticationException(String str) {
        super(ADALError.INCOMPATIBLE_BLOB_VERSION, str);
    }
}

package com.microsoft.aad.adal;

/* loaded from: classes3.dex */
class AuthenticationServerProtocolException extends AuthenticationException {
    static final long serialVersionUID = 1;

    public AuthenticationServerProtocolException(String str) {
        super(ADALError.DEVICE_CHALLENGE_FAILURE, str);
    }
}

package com.googleplay.licensing;

/* loaded from: classes2.dex */
public interface Obfuscator {
    String obfuscate(String str, String str2);

    String unobfuscate(String str, String str2) throws ValidationException;
}

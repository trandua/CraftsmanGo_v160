package com.microsoft.xbox.service.notification;

import com.microsoft.xbox.idp.interop.Interop;

/* loaded from: classes3.dex */
public class NotificationFirebaseMessagingService {
    public void onNewToken(String str) {
        Interop.NotificationRegisterCallback(str);
    }
}

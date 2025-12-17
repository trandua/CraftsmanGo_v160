package com.microsoft.xbox.service.notification;

import android.content.Context;
import android.os.Bundle;

/* loaded from: classes3.dex */
public class NotificationHelper {
    public static NotificationResult tryParseXboxLiveNotification(Bundle bundle, Context context) {
        return new NotificationResult(bundle, context);
    }
}

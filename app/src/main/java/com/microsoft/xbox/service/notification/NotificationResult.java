package com.microsoft.xbox.service.notification;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.microsoft.xboxtcui.C5528R;
import org.spongycastle.i18n.MessageBundle;

/* loaded from: classes3.dex */
public class NotificationResult {
    public String body;
    public String data;
    public NotificationType notificationType;
    public String title;

    /* loaded from: classes3.dex */
    public enum NotificationType {
        Achievement,
        Invite,
        Unknown
    }

    public NotificationResult(Bundle bundle, Context context) {
        String string = bundle.getString("type");
        if (string != null) {
            String str = null;
            if (string.equals("xbox_live_game_invite")) {
                this.notificationType = NotificationType.Invite;
                this.title = context.getString(C5528R.string.xbox_live_game_invite_title);
                String string2 = context.getString(C5528R.string.xbox_live_game_invite_body);
                String[] stringArray = bundle.getStringArray("bodyLocalizationArgs");
                if (stringArray != null) {
                    str = String.format(string2, stringArray[0], stringArray[1]);
                } else {
                    Log.i("XSAPI.Android", "could not parse notification");
                }
                this.data = bundle.getString("xbl");
            } else if (string.equals("xbox_live_achievement_unlock")) {
                this.notificationType = NotificationType.Achievement;
                this.title = bundle.getString(MessageBundle.TITLE_ENTRY);
                str = bundle.getString("body");
                this.data = bundle.getString("xbl");
            }
            this.body = str;
            this.data = bundle.getString("xbl");
            return;
        }
        this.notificationType = NotificationType.Unknown;
        this.data = bundle.getString("xbl");
    }
}

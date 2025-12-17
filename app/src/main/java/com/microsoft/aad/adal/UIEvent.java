package com.microsoft.aad.adal;

import android.util.Pair;
import java.util.List;
import java.util.Map;

/* loaded from: classes3.dex */
final class UIEvent extends DefaultEvent {
    /* JADX INFO: Access modifiers changed from: package-private */
    public UIEvent(String str) {
        getEventList().add(Pair.create("Microsoft.ADAL.event_name", str));
    }

    @Override // com.microsoft.aad.adal.DefaultEvent, com.microsoft.aad.adal.IEvents
    public void processEvent(Map<String, String> map) {
        List<Pair<String, String>> eventList = getEventList();
        String str = map.get("Microsoft.ADAL.ui_event_count");
        map.put("Microsoft.ADAL.ui_event_count", str == null ? "1" : Integer.toString(Integer.parseInt(str) + 1));
        if (map.containsKey("Microsoft.ADAL.user_cancel")) {
            map.put("Microsoft.ADAL.user_cancel", "");
        }
        if (map.containsKey("Microsoft.ADAL.ntlm")) {
            map.put("Microsoft.ADAL.ntlm", "");
        }
        for (Pair<String, String> pair : eventList) {
            String str2 = (String) pair.first;
            if (str2.equals("Microsoft.ADAL.user_cancel") || str2.equals("Microsoft.ADAL.ntlm")) {
                map.put(str2, (String) pair.second);
            }
        }
    }

    public void setNTLM(boolean z) {
        setProperty("Microsoft.ADAL.ntlm", String.valueOf(z));
    }

    public void setRedirectCount(Integer num) {
        setProperty("Microsoft.ADAL.redirect_count", num.toString());
    }

    public void setUserCancel() {
        setProperty("Microsoft.ADAL.user_cancel", "true");
    }
}

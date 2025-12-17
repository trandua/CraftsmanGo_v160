package com.microsoft.aad.adal;

import android.util.Pair;
import java.util.List;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class CacheEvent extends DefaultEvent {
    private final String mEventName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CacheEvent(String str) {
        this.mEventName = str;
        setProperty("Microsoft.ADAL.event_name", str);
    }

    @Override // com.microsoft.aad.adal.DefaultEvent, com.microsoft.aad.adal.IEvents
    public void processEvent(Map<String, String> map) {
        if (this.mEventName == "Microsoft.ADAL.token_cache_lookup") {
            List<Pair<String, String>> eventList = getEventList();
            String str = map.get("Microsoft.ADAL.cache_event_count");
            map.put("Microsoft.ADAL.cache_event_count", str == null ? "1" : Integer.toString(Integer.parseInt(str) + 1));
            map.put("Microsoft.ADAL.is_frt", "");
            map.put("Microsoft.ADAL.is_mrrt", "");
            map.put("Microsoft.ADAL.is_rt", "");
            if (map.containsKey("Microsoft.ADAL.spe_info")) {
                map.remove("Microsoft.ADAL.spe_info");
            }
            for (Pair<String, String> pair : eventList) {
                String str2 = (String) pair.first;
                if (str2.equals("Microsoft.ADAL.is_frt") || str2.equals("Microsoft.ADAL.is_rt") || str2.equals("Microsoft.ADAL.is_mrrt") || str2.equals("Microsoft.ADAL.spe_info")) {
                    map.put(str2, (String) pair.second);
                }
            }
        }
    }

    public void setSpeRing(String str) {
        if (StringExtensions.isNullOrBlank(str)) {
            return;
        }
        setProperty("Microsoft.ADAL.spe_info", str.trim());
    }

    public void setTokenType(String str) {
        getEventList().add(Pair.create("Microsoft.ADAL.token_type", str));
    }

    public void setTokenTypeFRT(boolean z) {
        setProperty("Microsoft.ADAL.is_frt", String.valueOf(z));
    }

    public void setTokenTypeMRRT(boolean z) {
        setProperty("Microsoft.ADAL.is_mrrt", String.valueOf(z));
    }

    public void setTokenTypeRT(boolean z) {
        setProperty("Microsoft.ADAL.is_rt", String.valueOf(z));
    }
}

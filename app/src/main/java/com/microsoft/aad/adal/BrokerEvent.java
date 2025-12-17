package com.microsoft.aad.adal;

import android.util.Pair;
import java.util.List;
import java.util.Map;

/* loaded from: classes3.dex */
final class BrokerEvent extends DefaultEvent {
    /* JADX INFO: Access modifiers changed from: package-private */
    public BrokerEvent(String str) {
        setProperty("Microsoft.ADAL.event_name", str);
    }

    @Override // com.microsoft.aad.adal.DefaultEvent, com.microsoft.aad.adal.IEvents
    public void processEvent(Map<String, String> map) {
        List<Pair<String, String>> eventList = getEventList();
        map.put("Microsoft.ADAL.broker_app_used", Boolean.toString(true));
        for (Pair<String, String> pair : eventList) {
            if (!((String) pair.first).equals("Microsoft.ADAL.event_name")) {
                map.put((String) pair.first, (String) pair.second);
            }
        }
    }

    public void setBrokerAccountServerStartsBinding() {
        setProperty("Microsoft.ADAL.broker_account_service_starts_binding", Boolean.toString(true));
    }

    public void setBrokerAccountServiceBindingSucceed(boolean z) {
        setProperty("Microsoft.ADAL.broker_account_service_binding_succeed", Boolean.toString(z));
    }

    public void setBrokerAccountServiceConnected() {
        setProperty("Microsoft.ADAL.broker_account_service_connected", Boolean.toString(true));
    }

    public void setBrokerAppName(String str) {
        setProperty("Microsoft.ADAL.broker_app", str);
    }

    public void setBrokerAppVersion(String str) {
        setProperty("Microsoft.ADAL.broker_version", str);
    }

    public void setRefreshTokenAge(String str) {
        if (StringExtensions.isNullOrBlank(str)) {
            return;
        }
        setProperty("Microsoft.ADAL.rt_age", str.trim());
    }

    public void setServerErrorCode(String str) {
        if (StringExtensions.isNullOrBlank(str) || str.equals(0)) {
            return;
        }
        setProperty("Microsoft.ADAL.server_error_code", str.trim());
    }

    public void setServerSubErrorCode(String str) {
        if (StringExtensions.isNullOrBlank(str) || str.equals(0)) {
            return;
        }
        setProperty("Microsoft.ADAL.server_sub_error_code", str.trim());
    }

    public void setSpeRing(String str) {
        if (StringExtensions.isNullOrBlank(str)) {
            return;
        }
        setProperty("Microsoft.ADAL.spe_info", str.trim());
    }
}

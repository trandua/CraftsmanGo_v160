package com.microsoft.xbox.telemetry.helpers;

//import com.ironsource.mediationsdk.utils.IronSourceConstants;
import com.microsoft.xbox.telemetry.helpers.UTCEventTracker;
import com.microsoft.xbox.telemetry.utc.model.UTCNames;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.HashMap;

/* loaded from: classes3.dex */
public class UTCReportUser {
    public static CharSequence currentActivityTitle = "";
    public static String currentXUID = "";

    public static HashMap<String, Object> getAdditionalInfo(String str) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(UTCDeepLink.TARGET_XUID_KEY, "x:" + str);
        return hashMap;
    }

    public static void trackReportDialogOK(final CharSequence charSequence, final String str, final String str2) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCReportUser.1
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                HashMap<String, Object> additionalInfo = UTCReportUser.getAdditionalInfo(str);
                additionalInfo.put("reason", str2);
                UTCPageAction.track(UTCNames.PageAction.PeopleHub.ReportOK, charSequence, additionalInfo);
            }
        });
    }

    public static void trackReportDialogOK(String str) {
        verifyTrackedDefaults();
        trackReportDialogOK(currentActivityTitle, currentXUID, str);
    }

    public static void trackReportView(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCReportUser.2
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                UTCReportUser.currentActivityTitle = charSequence;
                UTCReportUser.currentXUID = str;
                UTCPageView.track(UTCNames.PageView.PeopleHub.ReportView, UTCReportUser.currentActivityTitle, UTCReportUser.getAdditionalInfo(str));
            }
        });
    }

    private static void verifyTrackedDefaults() {
        XLEAssert.assertFalse("Called trackPeopleHubView without set currentXUID", currentXUID.equals(""));
        XLEAssert.assertFalse("Called trackPeopleHubView without set activityTitle", currentActivityTitle.toString().equals(""));
    }
}

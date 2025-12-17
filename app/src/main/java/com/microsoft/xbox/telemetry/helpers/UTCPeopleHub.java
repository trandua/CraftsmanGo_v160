package com.microsoft.xbox.telemetry.helpers;

import com.microsoft.xbox.telemetry.helpers.UTCEventTracker;
import com.microsoft.xbox.telemetry.utc.model.UTCNames;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.HashMap;

/* loaded from: classes3.dex */
public class UTCPeopleHub {
    public static CharSequence currentActivityTitle = "";
    public static String currentXUID = "";

    public static HashMap<String, Object> getAdditionalInfo(String str) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(UTCDeepLink.TARGET_XUID_KEY, "x:" + str);
        return hashMap;
    }

    public static void trackBlock() {
        verifyTrackedDefaults();
        trackBlock(currentActivityTitle, currentXUID);
    }

    public static void trackBlock(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCPeopleHub.1
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                UTCPageAction.track(UTCNames.PageAction.PeopleHub.Block, charSequence, UTCPeopleHub.getAdditionalInfo(str));
            }
        });
    }

    public static void trackBlockDialogComplete() {
        verifyTrackedDefaults();
        trackBlockDialogComplete(currentActivityTitle, currentXUID);
    }

    public static void trackBlockDialogComplete(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCPeopleHub.2
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                UTCPageAction.track(UTCNames.PageAction.PeopleHub.BlockOK, charSequence, UTCPeopleHub.getAdditionalInfo(str));
            }
        });
    }

    public static void trackMute(final CharSequence charSequence, final String str, final boolean z) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCPeopleHub.3
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                HashMap<String, Object> additionalInfo = UTCPeopleHub.getAdditionalInfo(str);
                additionalInfo.put("isMuted", Boolean.valueOf(z));
                UTCPageAction.track(UTCNames.PageAction.PeopleHub.Mute, charSequence, additionalInfo);
            }
        });
    }

    public static void trackMute(boolean z) {
        verifyTrackedDefaults();
        trackMute(currentActivityTitle, currentXUID, z);
    }

    public static void trackPeopleHubView(final CharSequence charSequence, final String str, final boolean z) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCPeopleHub.4
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                UTCPeopleHub.currentXUID = str;
                UTCPeopleHub.currentActivityTitle = charSequence;
                UTCPageView.track(z ? UTCNames.PageView.PeopleHub.PeopleHubMeView : UTCNames.PageView.PeopleHub.PeopleHubYouView, UTCPeopleHub.currentActivityTitle, UTCPeopleHub.getAdditionalInfo(str));
            }
        });
    }

    public static void trackReport() {
        verifyTrackedDefaults();
        trackReport(currentActivityTitle, currentXUID);
    }

    public static void trackReport(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCPeopleHub.5
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                UTCPageAction.track(UTCNames.PageAction.PeopleHub.Report, charSequence, UTCPeopleHub.getAdditionalInfo(str));
            }
        });
    }

    public static void trackUnblock() {
        verifyTrackedDefaults();
        trackUnblock(currentActivityTitle, currentXUID);
    }

    public static void trackUnblock(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCPeopleHub.6
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                UTCPageAction.track(UTCNames.PageAction.PeopleHub.Unblock, charSequence, UTCPeopleHub.getAdditionalInfo(str));
            }
        });
    }

    public static void trackViewInXboxApp() {
        verifyTrackedDefaults();
        trackViewInXboxApp(currentActivityTitle, currentXUID);
    }

    public static void trackViewInXboxApp(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCPeopleHub.7
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                UTCPageAction.track(UTCNames.PageAction.PeopleHub.ViewXboxApp, charSequence, UTCPeopleHub.getAdditionalInfo(str));
            }
        });
    }

    public static void trackViewInXboxAppDialogComplete() {
        verifyTrackedDefaults();
        trackViewInXboxAppDialogComplete(currentActivityTitle, currentXUID);
    }

    public static void trackViewInXboxAppDialogComplete(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() { // from class: com.microsoft.xbox.telemetry.helpers.UTCPeopleHub.8
            @Override // com.microsoft.xbox.telemetry.helpers.UTCEventTracker.UTCEventDelegate
            public void call() {
                UTCPageAction.track(UTCNames.PageAction.PeopleHub.ViewXboxAppOK, charSequence, UTCPeopleHub.getAdditionalInfo(str));
            }
        });
    }

    private static void verifyTrackedDefaults() {
        XLEAssert.assertFalse("Called trackPeopleHubView without set currentXUID", currentXUID.equals(""));
        XLEAssert.assertFalse("Called trackPeopleHubView without set activityTitle", currentActivityTitle.toString().equals(""));
    }
}

package com.microsoft.xbox.telemetry.helpers;

import com.microsoft.xbox.idp.ui.ErrorActivity;
import com.microsoft.xbox.telemetry.utc.CommonData;
import com.microsoft.xbox.telemetry.utc.model.UTCNames;

/* loaded from: classes3.dex */
public class UTCTelemetry {
    public static final String UNKNOWNPAGE = "Unknown";

    /* loaded from: classes3.dex */
    public enum CallBackSources {
        Account,
        Ticket
    }

    public static void LogEvent(CommonData commonData) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class C54531 {
        static final int[] $SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen;

        C54531() {
        }

        static {
            int[] iArr = new int[ErrorActivity.ErrorScreen.values().length];
            $SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen = iArr;
            try {
                iArr[ErrorActivity.ErrorScreen.BAN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen[ErrorActivity.ErrorScreen.CATCHALL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen[ErrorActivity.ErrorScreen.CREATION.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen[ErrorActivity.ErrorScreen.OFFLINE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public static String getErrorScreen(ErrorActivity.ErrorScreen errorScreen) {
        int i = C54531.$SwitchMap$com$microsoft$xbox$idp$ui$ErrorActivity$ErrorScreen[errorScreen.ordinal()];
        return i == 1 ? UTCNames.PageView.Errors.Banned : i == 2 ? UTCNames.PageView.Errors.Generic : i == 3 ? UTCNames.PageView.Errors.Create : i == 4 ? UTCNames.PageView.Errors.Offline : String.format("%sErrorScreen", UNKNOWNPAGE);
    }
}

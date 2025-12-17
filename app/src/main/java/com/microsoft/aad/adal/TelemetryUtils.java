package com.microsoft.aad.adal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/* loaded from: classes3.dex */
final class TelemetryUtils {
    static final Set<String> GDPR_FILTERED_FIELDS = new HashSet();
    private static final String TAG = "TelemetryUtils";

    static {
        initializeGdprFilteredFields();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class CliTelemInfo implements Serializable {
        private String mRefreshTokenAge;
        private String mServerErrorCode;
        private String mServerSubErrorCode;
        private String mSpeRing;
        private String mVersion;

        public String getRefreshTokenAge() {
            return this.mRefreshTokenAge;
        }

        public String getServerErrorCode() {
            return this.mServerErrorCode;
        }

        public String getServerSubErrorCode() {
            return this.mServerSubErrorCode;
        }

        public String getSpeRing() {
            return this.mSpeRing;
        }

        public String getVersion() {
            return this.mVersion;
        }

        public void setRefreshTokenAge(String str) {
            this.mRefreshTokenAge = str;
        }

        public void setServerErrorCode(String str) {
            this.mServerErrorCode = str;
        }

        public void setServerSubErrorCode(String str) {
            this.mServerSubErrorCode = str;
        }

        public void setSpeRing(String str) {
            this.mSpeRing = str;
        }

        public void setVersion(String str) {
            this.mVersion = str;
        }
    }

    private TelemetryUtils() {
    }

    private static void initializeGdprFilteredFields() {
        GDPR_FILTERED_FIELDS.addAll(Arrays.asList("Microsoft.ADAL.login_hint", "Microsoft.ADAL.user_id", "Microsoft.ADAL.tenant_id"));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CliTelemInfo parseXMsCliTelemHeader(String str) {
        if (StringExtensions.isNullOrBlank(str)) {
            return null;
        }
        String[] split = str.split(",");
        if (split.length == 0) {
            Logger.m14617w(TAG, "SPE Ring header missing version field.", null, ADALError.X_MS_CLITELEM_VERSION_UNRECOGNIZED);
            return null;
        }
        String str2 = split[0];
        CliTelemInfo cliTelemInfo = new CliTelemInfo();
        cliTelemInfo.setVersion(str2);
        if (!str2.equals("1")) {
            Logger.m14617w(TAG, "Unexpected header version. ", "Header version: " + str2, ADALError.X_MS_CLITELEM_VERSION_UNRECOGNIZED);
            return null;
        } else if (!Pattern.compile("^[1-9]+\\.?[0-9|\\.]*,[0-9|\\.]*,[0-9|\\.]*,[^,]*[0-9\\.]*,[^,]*$").matcher(str).matches()) {
            Logger.m14617w(TAG, "", "", ADALError.X_MS_CLITELEM_MALFORMED);
            return null;
        } else {
            String[] split2 = str.split(",", 5);
            cliTelemInfo.setServerErrorCode(split2[1]);
            cliTelemInfo.setServerSubErrorCode(split2[2]);
            cliTelemInfo.setRefreshTokenAge(split2[3]);
            cliTelemInfo.setSpeRing(split2[4]);
            return cliTelemInfo;
        }
    }
}

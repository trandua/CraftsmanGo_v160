package com.mojang.minecraftpe;

import android.content.Context;
import android.content.pm.PackageManager;

//import com.craftsman.go.StringFog;

import java.io.Serializable;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: classes3.dex */
public class SessionInfo implements Serializable {
    private static final String NOT_YET_CONFIGURED =  "Not yet configured";
    private static final String SEP = ";";
    public int appVersion;
    public String branchId;
    public String buildId;
    public String commitId;
    public transient Date crashTimestamp;
    public String flavor;
    public String gameVersionName;
    public Date recordDate;
    public String sessionId;

    public SessionInfo() {
        this.appVersion = 0;
        this.recordDate = null;
        this.crashTimestamp = null;
        this.sessionId = NOT_YET_CONFIGURED;
        this.buildId = NOT_YET_CONFIGURED;
        this.commitId = NOT_YET_CONFIGURED;
        this.branchId = NOT_YET_CONFIGURED;
        this.flavor = NOT_YET_CONFIGURED;
        this.gameVersionName = NOT_YET_CONFIGURED;
        this.recordDate = new Date();
    }

    public SessionInfo(String str, String str2, String str3, String str4, String str5, String str6, int i, Date date) {
        this.crashTimestamp = null;
        this.sessionId = str;
        this.buildId = str2;
        this.commitId = str3;
        this.branchId = str4;
        this.flavor = str5;
        this.gameVersionName = str6;
        this.appVersion = i;
        this.recordDate = date;
    }

    public void setContents(Context context, String str, String str2, String str3, String str4, String str5) {
        this.sessionId = str;
        this.buildId = str2;
        this.commitId = str3;
        this.branchId = str4;
        this.flavor = str5;
        updateJavaConstants(context);
    }

    public void updateJavaConstants(Context context) {
        this.appVersion = AppConstants.APP_VERSION;
        try {
            this.gameVersionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException unused) {
            this.gameVersionName = "Not found";
        }
    }

    public static SessionInfo fromString(String s) {
        SessionInfo sessionInfo = new SessionInfo();
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Empty SessionInfo string");
        }
        String[] split = s.split(";");
        if (split.length == 8) {
            sessionInfo.sessionId = split[0];
            sessionInfo.buildId = split[1];
            sessionInfo.commitId = split[2];
            sessionInfo.branchId = split[3];
            sessionInfo.flavor = split[4];
            sessionInfo.gameVersionName = split[5];
            try {
                sessionInfo.appVersion = Integer.parseInt(split[6]);
                Date parse = getDateFormat().parse(split[7], new ParsePosition(0));
                sessionInfo.recordDate = parse;
                if (parse != null) {
                    return sessionInfo;
                }
                throw new IllegalArgumentException("Failed to parse date/time in SessionInfo string '" + s + "'");
            } catch (NumberFormatException unused) {
                throw new IllegalArgumentException("Failed to convert app version '" + split[6] + "' into an integer");
            }
        } else {
            throw new IllegalArgumentException("Invalid SessionInfo string '" + s + "', must be 8 parts split by ';'");
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sessionId);
        String str = SEP;
        sb.append(str);
        sb.append(this.buildId);
        sb.append(str);
        sb.append(this.commitId);
        sb.append(str);
        sb.append(this.branchId);
        sb.append(str);
        sb.append(this.flavor);
        sb.append(str);
        sb.append(this.gameVersionName);
        sb.append(str);
        sb.append(this.appVersion);
        sb.append(str);
        sb.append(getDateFormat().format(this.recordDate));
        return sb.toString();
    }

    public static SimpleDateFormat getDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }
}

package com.microsoft.aad.adal;

import android.util.Log;
import com.google.android.vending.expansion.downloader.Constants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/* loaded from: classes3.dex */
public class Logger {
    private static final String CUSTOM_LOG_ERROR = "Custom log failed to log message:%s";
    static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static Logger sINSTANCE = new Logger();
    private boolean mAndroidLogEnabled = false;
    private String mCorrelationId = null;
    private boolean mEnablePII = false;
    private ILogger mExternalLogger = null;
    private LogLevel mLogLevel = LogLevel.Verbose;

    /* loaded from: classes3.dex */
    public interface ILogger {
        void Log(String str, String str2, String str3, LogLevel logLevel, ADALError aDALError);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class C53741 {
        static final int[] $SwitchMap$com$microsoft$aad$adal$Logger$LogLevel;

        C53741() {
        }

        static {
            int[] iArr = new int[LogLevel.values().length];
            $SwitchMap$com$microsoft$aad$adal$Logger$LogLevel = iArr;
            try {
                iArr[LogLevel.Error.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$microsoft$aad$adal$Logger$LogLevel[LogLevel.Warn.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$microsoft$aad$adal$Logger$LogLevel[LogLevel.Info.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$microsoft$aad$adal$Logger$LogLevel[LogLevel.Verbose.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$microsoft$aad$adal$Logger$LogLevel[LogLevel.Debug.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    /* loaded from: classes3.dex */
    public enum LogLevel {
        Error(0),
        Warn(1),
        Info(2),
        Verbose(3),
        Debug(4);
        
        private int mValue;

        LogLevel(int i) {
            this.mValue = i;
        }
    }

    private static String addMoreInfo(String str) {
        if (!StringExtensions.isNullOrBlank(str)) {
            return getUTCDateTimeAsString() + Constants.FILENAME_SEQUENCE_SEPARATOR + getInstance().mCorrelationId + Constants.FILENAME_SEQUENCE_SEPARATOR + str + " ver:" + AuthenticationContext.getVersionName();
        }
        return getUTCDateTimeAsString() + Constants.FILENAME_SEQUENCE_SEPARATOR + getInstance().mCorrelationId + "- ver:" + AuthenticationContext.getVersionName();
    }

    public static void m14608d(String str, String str2) {
        if (StringExtensions.isNullOrBlank(str2)) {
            return;
        }
        getInstance().log(str, str2, null, LogLevel.Debug, null, null);
    }

    public static void m14609e(String str, String str2, String str3, ADALError aDALError) {
        getInstance().log(str, str2, str3, LogLevel.Error, aDALError, null);
    }

    public static void m14610e(String str, String str2, String str3, ADALError aDALError, Throwable th) {
        getInstance().log(str, str2, str3, LogLevel.Error, aDALError, th);
    }

    public static void m14611e(String str, String str2, Throwable th) {
        getInstance().log(str, str2, null, LogLevel.Error, null, th);
    }

    private static String getCodeName(ADALError aDALError) {
        return aDALError != null ? aDALError.name() : "";
    }

    public static Logger getInstance() {
        return sINSTANCE;
    }

    private static String getUTCDateTimeAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date());
    }

    public static void m14612i(String str, String str2, String str3) {
        getInstance().log(str, str2, str3, LogLevel.Info, null, null);
    }

    public static void m14613i(String str, String str2, String str3, ADALError aDALError) {
        getInstance().log(str, str2, str3, LogLevel.Info, aDALError, null);
    }

    private void log(String str, String str2, String str3, LogLevel logLevel, ADALError aDALError, Throwable th) {
        if (logLevel.compareTo(this.mLogLevel) <= 0) {
            StringBuilder sb = new StringBuilder();
            if (aDALError != null) {
                sb.append(getCodeName(aDALError));
                sb.append(':');
            }
            sb.append(addMoreInfo(str2));
            if (!StringExtensions.isNullOrBlank(str3) && this.mEnablePII) {
                sb.append(' ');
                sb.append(str3);
            }
            if (th != null) {
                sb.append(10);
                sb.append(Log.getStackTraceString(th));
            }
            if (this.mAndroidLogEnabled) {
                sendLogcatLogs(str, logLevel, sb.toString());
            }
            if (this.mExternalLogger != null) {
                try {
                    if (!StringExtensions.isNullOrBlank(str3) && this.mEnablePII) {
                        ILogger iLogger = this.mExternalLogger;
                        String addMoreInfo = addMoreInfo(str2);
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(str3);
                        sb2.append(th == null ? "" : Log.getStackTraceString(th));
                        iLogger.Log(str, addMoreInfo, sb2.toString(), logLevel, aDALError);
                        return;
                    }
                    this.mExternalLogger.Log(str, addMoreInfo(str2), th == null ? null : Log.getStackTraceString(th), logLevel, aDALError);
                } catch (Exception unused) {
                    Log.w(str, String.format(CUSTOM_LOG_ERROR, str2));
                }
            }
        }
    }

    private void sendLogcatLogs(String str, LogLevel logLevel, String str2) {
        int i = C53741.$SwitchMap$com$microsoft$aad$adal$Logger$LogLevel[logLevel.ordinal()];
        if (i == 1) {
            Log.e(str, str2);
        } else if (i == 2) {
            Log.w(str, str2);
        } else if (i == 3) {
            Log.i(str, str2);
        } else if (i == 4) {
            Log.v(str, str2);
        } else if (i == 5) {
            Log.d(str, str2);
        } else {
            throw new IllegalArgumentException("Unknown loglevel");
        }
    }

    public static void setCorrelationId(UUID uuid) {
        getInstance().mCorrelationId = "";
        if (uuid != null) {
            getInstance().mCorrelationId = uuid.toString();
        }
    }

    public static void m14614v(String str, String str2) {
        getInstance().log(str, str2, null, LogLevel.Verbose, null, null);
    }

    public static void m14615v(String str, String str2, String str3, ADALError aDALError) {
        getInstance().log(str, str2, str3, LogLevel.Verbose, aDALError, null);
    }

    public static void m14616w(String str, String str2) {
        getInstance().log(str, str2, null, LogLevel.Warn, null, null);
    }

    public static void m14617w(String str, String str2, String str3, ADALError aDALError) {
        getInstance().log(str, str2, str3, LogLevel.Warn, aDALError, null);
    }

    public String getCorrelationId() {
        return this.mCorrelationId;
    }

    public void setAndroidLogEnabled(boolean z) {
        this.mAndroidLogEnabled = z;
    }

    public void setEnablePII(boolean z) {
        this.mEnablePII = z;
    }

    public void setExternalLogger(ILogger iLogger) {
        synchronized (this) {
            this.mExternalLogger = iLogger;
        }
    }

    public void setLogLevel(LogLevel logLevel) {
        this.mLogLevel = logLevel;
    }
}

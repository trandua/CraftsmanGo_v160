package com.microsoft.xboxtcui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreen;
import com.microsoft.xboxtcui.XboxTcuiWindowDialog;
import java.lang.reflect.Field;
import java.util.Map;

/* loaded from: classes3.dex */
public class Interop {
    public static final String TAG = "Interop";
    public static final XboxTcuiWindowDialog.DetachedCallback detachedCallback = new XboxTcuiWindowDialog.DetachedCallback() { // from class: com.microsoft.xboxtcui.Interop.1
        @Override // com.microsoft.xboxtcui.XboxTcuiWindowDialog.DetachedCallback
        public void onDetachedFromWindow() {
            Interop.tcui_completed_callback(0);
        }
    };

    public static native void tcui_completed_callback(int i);

    public static void ShowAddFriends(Context context) {
        Log.i("Interop", "Deeplink - ShowAddFriends");
        tcui_completed_callback(!XboxAppDeepLinker.showAddFriends(context) ? 1 : 0);
    }

    public static void ShowProfileCardUI(Activity activity, String str, String str2, String str3) {
        Log.i("ShowProfileCardUI", "TCUI- ShowProfileCardUI: meXuid:" + str);
        Log.i("ShowProfileCardUI", "TCUI- ShowProfileCardUI: targetProfileXuid:" + str2);
        Log.i("ShowProfileCardUI", "TCUI- ShowProfileCardUI: privileges:" + str3);
        Activity foregroundActivity = getForegroundActivity();
        if (foregroundActivity == null) {
            foregroundActivity = activity;
        }
        final Activity activityToUse = foregroundActivity == null ? activity : foregroundActivity;
        final ActivityParameters activityParameters = new ActivityParameters();
        activityParameters.putMeXuid(str);
        activityParameters.putSelectedProfile(str2);
        activityParameters.putPrivileges(str3);
        activity.runOnUiThread(new Runnable() { // from class: com.microsoft.xboxtcui.Interop.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    XboxTcuiWindowDialog xboxTcuiWindowDialog = new XboxTcuiWindowDialog(activityToUse, ProfileScreen.class, activityParameters);
                    xboxTcuiWindowDialog.setDetachedCallback(Interop.detachedCallback);
                    xboxTcuiWindowDialog.show();
                } catch (Exception e) {
                    Log.i("Interop", Log.getStackTraceString(e));
                    Interop.tcui_completed_callback(1);
                }
            }
        });
    }

    public static void ShowTitleAchievements(Context context, String str) {
        Log.i("Interop", "Deeplink - ShowTitleAchievements");
        tcui_completed_callback(!XboxAppDeepLinker.showTitleAchievements(context, str) ? 1 : 0);
    }

    public static void ShowTitleHub(Context context, String str) {
        Log.i("Interop", "Deeplink - ShowTitleHub");
        tcui_completed_callback(!XboxAppDeepLinker.showTitleHub(context, str) ? 1 : 0);
    }

    public static void ShowUserProfile(Context context, String str) {
        Log.i("Interop", "Deeplink - ShowUserProfile");
        tcui_completed_callback(!XboxAppDeepLinker.showUserProfile(context, str) ? 1 : 0);
    }

    public static void ShowUserSettings(Context context) {
        Log.i("Interop", "Deeplink - ShowUserSettings");
        tcui_completed_callback(!XboxAppDeepLinker.showUserSettings(context) ? 1 : 0);
    }

    private static Activity getForegroundActivity() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Object invoke = cls.getMethod("currentActivityThread", new Class[0]).invoke(null, new Object[0]);
            Field declaredField = cls.getDeclaredField("mActivities");
            declaredField.setAccessible(true);
            for (Object obj : ((Map) declaredField.get(invoke)).values()) {
                Class<?> cls2 = obj.getClass();
                Field declaredField2 = cls2.getDeclaredField("paused");
                declaredField2.setAccessible(true);
                if (!declaredField2.getBoolean(obj)) {
                    Field declaredField3 = cls2.getDeclaredField("activity");
                    declaredField3.setAccessible(true);
                    return (Activity) declaredField3.get(obj);
                }
            }
        } catch (Exception e) {
            Log.i("Interop", Log.getStackTraceString(e));
        }
        return null;
    }
}

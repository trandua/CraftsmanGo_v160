package com.microsoft.xbox.toolkit;

import android.app.ActivityManager;
import android.os.Debug;
import com.microsoft.xboxtcui.XboxTcuiSdk;

/* loaded from: classes3.dex */
public class MemoryMonitor {
    public static final int KB_TO_BYTES = 1024;
    public static final int MB_TO_BYTES = 1048576;
    public static final int MB_TO_KB = 1024;
    private static MemoryMonitor instance = new MemoryMonitor();
    private Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();

    private MemoryMonitor() {
    }

    public static int getTotalPss() {
        int totalPss;
        synchronized (MemoryMonitor.class) {
            Debug.getMemoryInfo(instance.memoryInfo);
            totalPss = instance.memoryInfo.getTotalPss();
        }
        return totalPss;
    }

    public static MemoryMonitor instance() {
        return instance;
    }

    public int getDalvikFreeKb() {
        int memoryClass;
        int dalvikUsedKb;
        synchronized (this) {
            Debug.getMemoryInfo(this.memoryInfo);
            memoryClass = ((ActivityManager) XboxTcuiSdk.getSystemService("activity")).getMemoryClass();
            dalvikUsedKb = getDalvikUsedKb();
        }
        return (memoryClass * 1024) - dalvikUsedKb;
    }

    public int getDalvikFreeMb() {
        int dalvikFreeKb;
        synchronized (this) {
            dalvikFreeKb = getDalvikFreeKb() / 1024;
        }
        return dalvikFreeKb;
    }

    public int getDalvikUsedKb() {
        int i;
        synchronized (this) {
            Debug.getMemoryInfo(this.memoryInfo);
            i = this.memoryInfo.dalvikPss;
        }
        return i;
    }

    public int getMemoryClass() {
        return ((ActivityManager) XboxTcuiSdk.getSystemService("activity")).getLargeMemoryClass();
    }

    public int getUsedKb() {
        int i;
        int i2;
        synchronized (this) {
            Debug.getMemoryInfo(this.memoryInfo);
            i = this.memoryInfo.dalvikPss;
            i2 = this.memoryInfo.nativePss;
        }
        return i + i2;
    }
}

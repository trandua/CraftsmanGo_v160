package com.mojang.minecraftpe.packagesource;

/* loaded from: classes3.dex */
public class NativePackageSourceListener implements PackageSourceListener {
    long mPackageSourceListener = 0;

    public native void nativeOnDownloadProgress(long j, long j2, long j3, float f, long j4);

    public native void nativeOnDownloadStarted(long j);

    public native void nativeOnDownloadStateChanged(long j, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, int i, int i2);

    public native void nativeOnMountStateChanged(long j, String str, int i);

    public void setListener(long j) {
        this.mPackageSourceListener = j;
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSourceListener
    public void onDownloadStarted() {
        nativeOnDownloadStarted(this.mPackageSourceListener);
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSourceListener
    public void onDownloadStateChanged(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, int i, int i2) {
        nativeOnDownloadStateChanged(this.mPackageSourceListener, z, z2, z3, z4, z5, i, i2);
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSourceListener
    public void onDownloadProgress(long j, long j2, float f, long j3) {
        nativeOnDownloadProgress(this.mPackageSourceListener, j, j2, f, j3);
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSourceListener
    public void onMountStateChanged(String str, int i) {
        nativeOnMountStateChanged(this.mPackageSourceListener, str, i);
    }
}

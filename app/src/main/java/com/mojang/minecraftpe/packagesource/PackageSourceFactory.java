package com.mojang.minecraftpe.packagesource;

import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.packagesource.googleplay.ApkXDownloaderClient;

/* loaded from: classes3.dex */
public class PackageSourceFactory {
    static PackageSource createGooglePlayPackageSource(String str, PackageSourceListener packageSourceListener) {
        return new ApkXDownloaderClient(MainActivity.mInstance, str, packageSourceListener);
    }
}

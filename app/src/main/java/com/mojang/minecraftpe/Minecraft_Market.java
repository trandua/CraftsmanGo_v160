package com.mojang.minecraftpe;

import android.content.Intent;
import android.net.Uri;
//import com.craftsman.go.StringFog;

/* loaded from: classes3.dex */
public class Minecraft_Market extends MainActivity {
    @Override // com.mojang.minecraftpe.MainActivity
    public void buyGame() {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.mojang.minecraftpe")));
    }
}

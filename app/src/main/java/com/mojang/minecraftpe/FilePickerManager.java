package com.mojang.minecraftpe;

import android.content.Intent;

//import com.craftsman.go.StringFog;

/* loaded from: classes3.dex */
public class FilePickerManager implements ActivityListener {
    static final int PICK_DIRECTORY_REQUEST_CODE = 246242755;
    FilePickerManagerHandler mHandler;

    private static native void nativeDirectoryPickResult(String str, String str2);

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onDestroy() {
    }

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onResume() {
    }

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onStop() {
    }

    public FilePickerManager(FilePickerManagerHandler filePickerManagerHandler) {
        this.mHandler = filePickerManagerHandler;
    }

    public void pickDirectory(String prompt, String startingLocationURI) {
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
        if (prompt != null && !prompt.isEmpty()) {
            intent.putExtra("android.provider.extra.PROMPT", prompt);
        }
        if (startingLocationURI != null && !startingLocationURI.isEmpty()) {
            intent.putExtra("android.provider.extra.INITIAL_URI", startingLocationURI);
        }
        this.mHandler.startPickerActivity(intent, PICK_DIRECTORY_REQUEST_CODE);
    }

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != PICK_DIRECTORY_REQUEST_CODE) {
            return;
        }
        if (i2 == -1) {
            nativeDirectoryPickResult(intent.getData().toString(), "");
        } else {
            nativeDirectoryPickResult("", "No directory selected");
        }
    }
}

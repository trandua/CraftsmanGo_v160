package com.mojang.minecraftpe.python;

import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
//import com.craftsman.go.StringFog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/* loaded from: classes3.dex */
public class PythonPackageLoader {
    private AssetManager assetManager;
    private File destination;

    /* loaded from: classes3.dex */
    public enum CreateDirectory {
        Created,
        Exists
    }

    public PythonPackageLoader(AssetManager assetManager, File file) {
        this.assetManager = assetManager;
        this.destination = new File(file + "/python");
    }

    private boolean shouldUnpack() throws Throwable {
        File file = new File(this.destination.getAbsolutePath() + "/python-tracker.txt");
        String[] list = this.assetManager.list("python-tracker.txt");
        if (!file.exists() || list == null || list.length <= 0) {
            return true;
        }
        return !new BufferedReader(new FileReader(file)).readLine().equals(new BufferedReader(new InputStreamReader(this.assetManager.open(list[0]), StandardCharsets.UTF_8)).readLine());
    }

    private final void copy(InputStream inputStream, File file) throws Throwable {
        File parentFile = file.getParentFile();
        Objects.requireNonNull(parentFile);
        createDirectory(parentFile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
//        String decrypt = StringFog.decrypt("VAqLfQ==\n", "GUnbOASBDDU=\n");
//        Log.i(decrypt, StringFog.decrypt("veJyzT7XnOI=\n", "/pAXrEqy+MI=\n") + file.getAbsolutePath());
    }

    public void unpack() {
        try {
            if (createDirectory(this.destination) == CreateDirectory.Exists && !shouldUnpack()) {
//                Log.i(StringFog.decrypt("RDptaVkUKId3KHhmUzYXh3Amaw==\n", "FEMZATZ6eOY=\n"), StringFog.decrypt("9a/ADlH0AEbPr5QCV7oPA8mlmkZ+8wsDm7bRFEvzCAjI4NcOXfkMCc60lAlN7klGyaXAE0r0Dgjc\n7g==\n", "u8C0ZjiaZ2Y=\n"));
                return;
            }
//            Log.i(StringFog.decrypt("mynP/CNZBcqoO9rzKXs6yq81yQ==\n", "y1C7lEw3Vas=\n"), StringFog.decrypt("MWasHfzZOKwedfgM/okvqgBr+BfnzD7lAGusEP7HbKMZfr0Lsd0j5RRzrBm/\n", "cBLYeJGpTMU=\n"));
            unpackAssetPrefix("python", this.destination);
        } catch (Throwable th) {
//            Log.e(StringFog.decrypt("YXDfbmHu1UJSYspha8zqQlVs2Q==\n", "MQmrBg6AhSM=\n"), th.toString());
        }
    }

    private CreateDirectory createDirectory(File file) throws Throwable {
        if (file.exists()) {
            return CreateDirectory.Exists;
        }
        if (file.mkdirs()) {
            return CreateDirectory.Created;
        }
        throw new IOException("Failed to mkdir " + file.getAbsolutePath());
    }

    private void delete(File file) {
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                delete(file2);
            }
        }
        file.delete();
    }

    private void traverse(File file) {
        File[] listFiles;
        if (file.exists()) {
            for (File file2 : file.listFiles()) {
                if (file2.isDirectory()) {
                    traverse(file2);
                } else {
//                    Log.i(StringFog.decrypt("Swpu+g==\n", "Bkk+v6h+epI=\n"), StringFog.decrypt("hemPPgcEN2Wh9Lc/Ckpxf7n1wXZP\n", "1ZD7VmhqFxY=\n") + file2.getAbsolutePath() + StringFog.decrypt("mw==\n", "vEHrVrRZu9A=\n"));
                }
            }
        }
    }

    private void unpackAssetPrefix(String str, File file) throws Throwable {
//        Log.i(StringFog.decrypt("9HZpow==\n", "uTU55gjCF4c=\n"), StringFog.decrypt("C0xbVzNcsQVoT0tCYUW+FiAA\n", "SCA+NkE132I=\n") + file.getAbsolutePath());
        delete(file);
        String[] list = this.assetManager.list(str);
        if (list == null || list.length == 0) {
            throw new IOException("No assets at prefix " + str);
        }
        for (String str2 : list) {
            unpackAssetPath(str + '/' + str2, str.length(), file);
        }
    }

    private final void unpackAssetPath(String str, int i, File file) throws Throwable {
        String[] list = this.assetManager.list(String.valueOf(str));
        if (list == null) {
            throw new IOException("Unable to list assets at path " + str + '/');
        } else if (list.length == 0) {
            copy(this.assetManager.open(str), new File(file.getAbsolutePath() + '/' + str.substring(i)));
        } else {
            for (String str2 : list) {
                unpackAssetPath(str + '/' + str2, i, file);
            }
        }
    }
}

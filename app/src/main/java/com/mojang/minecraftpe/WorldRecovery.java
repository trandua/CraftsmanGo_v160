package com.mojang.minecraftpe;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.StatFs;
import android.util.Log;
import androidx.documentfile.provider.DocumentFile;
//import com.craftsman.go.StringFog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/* loaded from: classes3.dex */
public class WorldRecovery {
    private ContentResolver mContentResolver;
    private Context mContext;
    private long mTotalBytesRequired = 0;
    private int mTotalFilesToCopy = 0;

    private static native void nativeComplete();

    private static native void nativeError(String str, long j, long j2);

    private static native void nativeUpdate(String str, int i, int i2, long j, long j2);

    public WorldRecovery(Context context, ContentResolver contentResolver) {
        this.mContext = context;
        this.mContentResolver = contentResolver;
    }

    public String migrateFolderContents(String str, String str2) {
        final DocumentFile fromTreeUri = DocumentFile.fromTreeUri(this.mContext, Uri.parse(str));
        if (fromTreeUri == null) {
            return "Could not resolve URI to a DocumentFile tree: " + str;
        } else if (!fromTreeUri.isDirectory()) {
            return "Root file of URI is not a directory: " + str;
        } else {
            final File file = new File(str2);
            if (!file.isDirectory()) {
                return "Destination folder does not exist: " + str2;
            }
            String[] list = file.list();
            Objects.requireNonNull(list);
            if (list.length != 0) {
                return "Destination folder is not empty: " + str2;
            }
            new Thread(new Runnable() { // from class: com.mojang.minecraftpe.WorldRecovery.1
                @Override // java.lang.Runnable
                public final void run() {
                    WorldRecovery.this.lambda$migrateFolderContents$0$WorldRecovery(fromTreeUri, file);
                }
            }).start();
            return "";
        }
    }

    public void lambda$migrateFolderContents$0$WorldRecovery(DocumentFile documentFile, File file) {
        ArrayList<DocumentFile> arrayList = new ArrayList<>();
        this.mTotalFilesToCopy = 0;
        long j = 0;
        this.mTotalBytesRequired = 0L;
        generateCopyFilesRecursively(arrayList, documentFile);
        long availableBytes = new StatFs(file.getAbsolutePath()).getAvailableBytes();
        long j2 = this.mTotalBytesRequired;
        if (j2 >= availableBytes) {
            nativeError("Insufficient space", j2, availableBytes);
            return;
        }
        String path = documentFile.getUri().getPath();
        String str = file + "_temp";
        File file2 = new File(str);
        byte[] bArr = new byte[8192];
        Iterator<DocumentFile> it = arrayList.iterator();
        long j3 = 0;
        int i = 0;
        while (it.hasNext()) {
            DocumentFile next = it.next();
            String str2 = str + next.getUri().getPath().substring(path.length());
            if (next.isDirectory()) {
                File file3 = new File(str2);
                if (!file3.isDirectory()) {
//                    Log.i(StringFog.decrypt("LjjJ81QrYNYX\n", "Y1GnljdZAbA=\n"), StringFog.decrypt("dQpJxAdSxggWHEXXFljcAEQBDII=\n", "NngspXM7qG8=\n") + str2 + StringFog.decrypt("EA==\n", "N7eXsTK7Yb8=\n"));
                    if (!file3.mkdirs()) {
                        nativeError("Could not create directory: " + str2, j, j);
                        return;
                    }
                } else {
//                    Log.i(StringFog.decrypt("0/JBZLwm4bPq\n", "npsvAd9UgNU=\n"), StringFog.decrypt("wwwQqwC/Ac7+RUU=\n", "h2VizmPLbrw=\n") + str2 + StringFog.decrypt("Vfph2XQX8XYL+mXNbwHkYQ==\n", "ctoAtQZykBI=\n"));
                }
            } else {
//                Log.i(StringFog.decrypt("TzBfxgO1gx12\n", "Alkxo2DH4ns=\n"), StringFog.decrypt("YXnOdR922c8F\n", "Iha+DHYYvu8=\n") + next.getUri().getPath() + StringFog.decrypt("0QEcswVn\n", "9iFo3CVAKXo=\n") + str2 + StringFog.decrypt("zQ==\n", "6sihjLrF15E=\n"));
                StringBuilder sb = new StringBuilder();
                sb.append("Copying: ");
                sb.append(str2);
                i++;
                nativeUpdate(sb.toString(), this.mTotalFilesToCopy, i, this.mTotalBytesRequired, j3);
                try {
                    InputStream openInputStream = this.mContentResolver.openInputStream(next.getUri());
                    FileOutputStream fileOutputStream = new FileOutputStream(str2);
                    int i2 = 0;
                    while (true) {
                        int read = openInputStream.read(bArr, i2, 8192);
                        if (read < 0) {
                            break;
                        }
                        i2 = 0;
                        fileOutputStream.write(bArr, 0, read);
                        j3 += read;
                    }
                    fileOutputStream.close();
                    openInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    nativeError(e.getMessage(), 0L, 0L);
                    return;
                }
            }
            j = 0;
        }
        if (!file.delete()) {
            nativeError("Could not delete empty destination directory: " + file.getAbsolutePath(), 0L, 0L);
        } else if (file2.renameTo(file)) {
            nativeComplete();
        } else if (file.mkdir()) {
            nativeError("Could not replace destination directory: " + file.getAbsolutePath(), 0L, 0L);
        } else {
            nativeError("Could not recreate destination directory after failed replace: " + file.getAbsolutePath(), 0L, 0L);
        }
    }

    private void generateCopyFilesRecursively(ArrayList<DocumentFile> arrayList, DocumentFile documentFile) {
        DocumentFile[] listFiles;
        for (DocumentFile documentFile2 : documentFile.listFiles()) {
            arrayList.add(documentFile2);
            if (documentFile2.isDirectory()) {
                generateCopyFilesRecursively(arrayList, documentFile2);
            } else {
                this.mTotalBytesRequired += documentFile2.length();
                this.mTotalFilesToCopy++;
            }
        }
    }
}

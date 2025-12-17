package org.simpleframework.xml.transform;

import java.io.File;

/* loaded from: classes.dex */
class FileTransform implements Transform<File> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.transform.Transform
    public File read(String path) {
        return new File(path);
    }

    public String write(File path) {
        return path.getPath();
    }
}

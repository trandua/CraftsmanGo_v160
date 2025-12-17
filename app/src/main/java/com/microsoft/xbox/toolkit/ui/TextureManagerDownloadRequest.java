package com.microsoft.xbox.toolkit.ui;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes3.dex */
public class TextureManagerDownloadRequest implements Comparable<TextureManagerDownloadRequest> {
    private static AtomicInteger nextIndex = new AtomicInteger(0);
    public int index = nextIndex.incrementAndGet();
    public TextureManagerScaledNetworkBitmapRequest key;
    public InputStream stream;

    public TextureManagerDownloadRequest(TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest) {
        this.key = textureManagerScaledNetworkBitmapRequest;
    }

    @Override // java.lang.Comparable
    public int compareTo(TextureManagerDownloadRequest textureManagerDownloadRequest) {
        return this.index - textureManagerDownloadRequest.index;
    }
}

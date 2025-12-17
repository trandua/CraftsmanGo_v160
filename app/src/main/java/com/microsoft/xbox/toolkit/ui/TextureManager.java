package com.microsoft.xbox.toolkit.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Looper;
import android.widget.ImageView;
//import com.crafting.minecrafting.lokicraft.R;
import com.google.android.vending.expansion.downloader.Constants;
import com.craftsman.go.R;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.MemoryMonitor;
import com.microsoft.xbox.toolkit.MultiMap;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.ThreadSafePriorityQueue;
import com.microsoft.xbox.toolkit.TimeMonitor;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEFileCache;
import com.microsoft.xbox.toolkit.XLEFileCacheManager;
import com.microsoft.xbox.toolkit.XLEMemoryCache;
import com.microsoft.xbox.toolkit.XLEThread;
import com.microsoft.xbox.toolkit.network.HttpClientFactory;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.ui.XLEBitmap;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.http.client.methods.HttpGet;

/* loaded from: classes3.dex */
public class TextureManager {
    private static final int ANIM_TIME = 100;
    private static final int BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES = 5242880;
    private static final String BMP_FILE_CACHE_DIR_NAME = "texture";
    private static final int BMP_FILE_CACHE_SIZE = 2000;
    private static final int DECODE_THREAD_WAIT_TIMEOUT_MS = 3000;
    private static final int TEXTURE_TIMEOUT_MS = 15000;
    private static final long TIME_TO_RETRY_MS = 300000;
    public static TextureManager instance = new TextureManager();
    private Thread decodeThread;
    public XLEMemoryCache<TextureManagerScaledNetworkBitmapRequest, XLEBitmap> bitmapCache = new XLEMemoryCache<>(Math.min(getNetworkBitmapCacheSizeInMB(), 50) * 1048576, BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES);
    public XLEFileCache bitmapFileCache = XLEFileCacheManager.createCache(BMP_FILE_CACHE_DIR_NAME, 2000);
    public HashSet<TextureManagerScaledNetworkBitmapRequest> inProgress = new HashSet<>();
    public Object listLock = new Object();
    private HashMap<TextureManagerScaledResourceBitmapRequest, XLEBitmap> resourceBitmapCache = new HashMap<>();
    private TimeMonitor stopwatch = new TimeMonitor();
    public HashMap<TextureManagerScaledNetworkBitmapRequest, RetryEntry> timeToRetryCache = new HashMap<>();
    public ThreadSafePriorityQueue<TextureManagerDownloadRequest> toDecode = new ThreadSafePriorityQueue<>();
    public MultiMap<TextureManagerScaledNetworkBitmapRequest, ImageView> waitingForImage = new MultiMap<>();

    public void logMemoryUsage() {
    }

    public void preload(int i) {
    }

    public void preload(URI uri) {
    }

    public void preloadFromFile(String str) {
    }

    public void unsafeClearBitmapCache() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class RetryEntry {
        private static final long SEC = 1000;
        private static final long[] TIMES_MS = {Constants.ACTIVE_THREAD_WATCHDOG, 9000, 19000, 37000, 75000, 150000, 300000};
        private int curIdx;
        private long currStart;

        private RetryEntry() {
            this.curIdx = 0;
            this.currStart = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return this.currStart + TIMES_MS[this.curIdx] < System.currentTimeMillis();
        }

        public void startNext() {
            int i = this.curIdx;
            if (i < TIMES_MS.length - 1) {
                this.curIdx = i + 1;
            }
            this.currStart = System.currentTimeMillis();
        }
    }

    /* loaded from: classes3.dex */
    private class TextureManagerDecodeThread implements Runnable {
        private TextureManagerDecodeThread() {
        }

        /* JADX WARN: Removed duplicated region for block: B:15:0x00a0  */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void run() {
            XLEBitmap xLEBitmap = null;
            while (true) {
                TextureManagerDownloadRequest pop = TextureManager.this.toDecode.pop();
                if (pop.stream != null) {
                    BackgroundThreadWaitor.getInstance().waitForReady(3000);
                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        StreamUtil.CopyStream(byteArrayOutputStream, pop.stream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        Rect rect = null;
                        BitmapFactory.decodeStream(new ByteArrayInputStream(byteArray), null, options);
                        BitmapFactory.Options computeInSampleSizeOptions = TextureManager.this.computeInSampleSizeOptions(pop.key.bindingOption.width, pop.key.bindingOption.height, options);
                        int i = options.outWidth / computeInSampleSizeOptions.inSampleSize;
                        int i2 = options.outHeight / computeInSampleSizeOptions.inSampleSize;
                        XLEBitmap decodeStream = XLEBitmap.decodeStream(new ByteArrayInputStream(byteArray), computeInSampleSizeOptions);
                        if (pop.key.bindingOption.useFileCache && !TextureManager.this.bitmapFileCache.contains(pop.key)) {
                            TextureManager.this.bitmapFileCache.save(pop.key, new ByteArrayInputStream(byteArray));
                        }
                        xLEBitmap = TextureManager.this.createScaledBitmap(decodeStream, pop.key.bindingOption.width, pop.key.bindingOption.height);
                    } catch (Exception unused) {
                    }
                    BackgroundThreadWaitor.getInstance().waitForReady(3000);
                    synchronized (TextureManager.this.listLock) {
                        if (xLEBitmap != null) {
                            TextureManager.this.bitmapCache.add(pop.key, xLEBitmap, xLEBitmap.getByteCount());
                            TextureManager.this.timeToRetryCache.remove(pop.key);
                        } else if (pop.key.bindingOption.resourceIdForError != -1) {
                            xLEBitmap = TextureManager.this.loadResource(pop.key.bindingOption.resourceIdForError);
                            RetryEntry retryEntry = TextureManager.this.timeToRetryCache.get(pop.key);
                            if (retryEntry != null) {
                                retryEntry.startNext();
                            } else {
                                TextureManager.this.timeToRetryCache.put(pop.key, new RetryEntry());
                            }
                        }
                        TextureManager.this.drainWaitingForImage(pop.key, xLEBitmap);
                        TextureManager.this.inProgress.remove(pop.key);
                    }
                }
                xLEBitmap = null;
                BackgroundThreadWaitor.getInstance().waitForReady(3000);
                synchronized (TextureManager.this.listLock) {
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    private class TextureManagerDownloadThreadWorker implements Runnable {
        private TextureManagerDownloadRequest request;
        final TextureManager this$0;

        public TextureManagerDownloadThreadWorker(TextureManager textureManager, TextureManagerDownloadRequest textureManagerDownloadRequest) {
            this.this$0 = textureManager;
            this.request = textureManagerDownloadRequest;
        }

        private InputStream downloadFromAssets(String str) {
            try {
                return XboxTcuiSdk.getAssetManager().open(str);
            } catch (IOException unused) {
                return null;
            }
        }

        private InputStream downloadFromWeb(String str) {
            try {
                XLEHttpStatusAndStream httpStatusAndStreamInternal = HttpClientFactory.textureFactory.getHttpClient(TextureManager.TEXTURE_TIMEOUT_MS).getHttpStatusAndStreamInternal(new HttpGet(URI.create(str)), false);
                if (httpStatusAndStreamInternal.statusCode == 200) {
                    return httpStatusAndStreamInternal.stream;
                }
                return null;
            } catch (Exception unused) {
                return null;
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            XLEAssert.assertTrue((this.request.key == null || this.request.key.url == null) ? false : true);
            this.request.stream = null;
            try {
                if (!this.request.key.url.startsWith("http")) {
                    TextureManagerDownloadRequest textureManagerDownloadRequest = this.request;
                    textureManagerDownloadRequest.stream = downloadFromAssets(textureManagerDownloadRequest.key.url);
                } else if (this.request.key.bindingOption.useFileCache) {
                    this.request.stream = TextureManager.this.bitmapFileCache.getInputStreamForRead(this.request.key);
                    if (this.request.stream == null) {
                        TextureManagerDownloadRequest textureManagerDownloadRequest2 = this.request;
                        textureManagerDownloadRequest2.stream = downloadFromWeb(textureManagerDownloadRequest2.key.url);
                    }
                } else {
                    TextureManagerDownloadRequest textureManagerDownloadRequest3 = this.request;
                    textureManagerDownloadRequest3.stream = downloadFromWeb(textureManagerDownloadRequest3.key.url);
                }
            } catch (Exception unused) {
            }
            synchronized (TextureManager.this.listLock) {
                TextureManager.this.toDecode.push(this.request);
            }
        }
    }

    public TextureManager() {
        this.decodeThread = null;
        this.stopwatch.start();
        XLEThread xLEThread = new XLEThread(new TextureManagerDecodeThread(), "XLETextureDecodeThread");
        this.decodeThread = xLEThread;
        xLEThread.setDaemon(true);
        this.decodeThread.setPriority(4);
        this.decodeThread.start();
    }

    public static TextureManager Instance() {
        return instance;
    }

    private void bindToViewInternal(String str, ImageView imageView, TextureBindingOption textureBindingOption) {
        XLEBitmap xLEBitmap;
        XLEBitmap xLEBitmap2;
        RetryEntry retryEntry;
        TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest = new TextureManagerScaledNetworkBitmapRequest(str, textureBindingOption);
        synchronized (this.listLock) {
            if (this.waitingForImage.containsValue(imageView)) {
                this.waitingForImage.removeValue(imageView);
            }
            if (!invalidUrl(str)) {
                xLEBitmap2 = this.bitmapCache.get(textureManagerScaledNetworkBitmapRequest);
                if (xLEBitmap2 == null && (retryEntry = this.timeToRetryCache.get(textureManagerScaledNetworkBitmapRequest)) != null && !retryEntry.isExpired() && textureBindingOption.resourceIdForError != -1) {
                    xLEBitmap2 = loadResource(textureBindingOption.resourceIdForError);
                }
            } else {
                if (textureBindingOption.resourceIdForError != -1) {
                    xLEBitmap = loadResource(textureBindingOption.resourceIdForError);
                    XLEAssert.assertNotNull(xLEBitmap);
                } else {
                    xLEBitmap = null;
                }
                xLEBitmap2 = xLEBitmap;
            }
        }
        setImage(imageView, xLEBitmap2);
        if (imageView instanceof XLEImageView) {
            ((XLEImageView) imageView).TEST_loadingOrLoadedImageUrl = str;
        }
    }

    public BitmapFactory.Options computeInSampleSizeOptions(int i, int i2, BitmapFactory.Options options) {
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        int i3 = 1;
        if (validResizeDimention(i, i2) && options.outWidth > i && options.outHeight > i2) {
            double d = options.outWidth;
            double d2 = i;
            Double.isNaN(d);
            Double.isNaN(d2);
            double d3 = d / d2;
            double d4 = options.outHeight;
            double d5 = i2;
            Double.isNaN(d4);
            Double.isNaN(d5);
            double max = Math.max(d3, d4 / d5);
            while (true) {
                double d6 = i3;
                Double.isNaN(d6);
                if (d6 * max <= 2.0d) {
                    break;
                }
                i3 *= 2;
            }
            options2.inSampleSize = i3;
        } else {
            options2.inSampleSize = 1;
        }
        return options2;
    }

    public XLEBitmap createScaledBitmap(XLEBitmap xLEBitmap, int i, int i2) {
        if (!validResizeDimention(i, i2) || xLEBitmap.getBitmap() == null) {
            return xLEBitmap;
        }
        float height = xLEBitmap.getBitmap().getHeight() / xLEBitmap.getBitmap().getWidth();
        float f = i2;
        float f2 = i;
        if (f / f2 < height) {
            i = Math.max(1, (int) (f / height));
        } else {
            i2 = Math.max(1, (int) (f2 * height));
        }
        return XLEBitmap.createScaledBitmap8888(xLEBitmap, i, i2, true);
    }

    public void drainWaitingForImage(TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest, XLEBitmap xLEBitmap) {
        if (this.waitingForImage.containsKey(textureManagerScaledNetworkBitmapRequest)) {
            Iterator<ImageView> it = this.waitingForImage.get(textureManagerScaledNetworkBitmapRequest).iterator();
            while (it.hasNext()) {
                ImageView next = it.next();
                if (next != null) {
                    if (next instanceof XLEImageView) {
                        setXLEImageView(textureManagerScaledNetworkBitmapRequest, (XLEImageView) next, xLEBitmap);
                    } else {
                        setView(textureManagerScaledNetworkBitmapRequest, next, xLEBitmap);
                    }
                }
            }
        }
    }

    private int getNetworkBitmapCacheSizeInMB() {
        return (Math.max(0, MemoryMonitor.instance().getMemoryClass() - 64) / 2) + 12;
    }

    private static boolean invalidUrl(String str) {
        return str == null || str.length() == 0;
    }

    private void load(TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest) {
        if (invalidUrl(textureManagerScaledNetworkBitmapRequest.url)) {
            return;
        }
        XLEThreadPool.textureThreadPool.run(new TextureManagerDownloadThreadWorker(this, new TextureManagerDownloadRequest(textureManagerScaledNetworkBitmapRequest)));
    }

    public void setImage(ImageView imageView, XLEBitmap xLEBitmap) {
        Bitmap bitmap = xLEBitmap == null ? null : xLEBitmap.getBitmap();
        OnBitmapSetListener onBitmapSetListener = (OnBitmapSetListener) imageView.getTag(R.id.image_callback);
        if (onBitmapSetListener != null) {
            onBitmapSetListener.onBeforeImageSet(imageView, bitmap);
        }
        imageView.setImageBitmap(bitmap);
        imageView.setTag(R.id.image_bound, true);
        if (onBitmapSetListener != null) {
            onBitmapSetListener.onAfterImageSet(imageView, bitmap);
        }
    }

    private void setView(final TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest, final ImageView imageView, final XLEBitmap xLEBitmap) {
        ThreadManager.UIThreadPost(new Runnable() { // from class: com.microsoft.xbox.toolkit.ui.TextureManager.1
            @Override // java.lang.Runnable
            public void run() {
                boolean keyValueMatches;
                if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                    throw new AssertionError("must be called from the UI thread");
                }
                synchronized (TextureManager.this.listLock) {
                    keyValueMatches = TextureManager.this.waitingForImage.keyValueMatches(textureManagerScaledNetworkBitmapRequest, imageView);
                }
                if (keyValueMatches) {
                    TextureManager.this.setImage(imageView, xLEBitmap);
                    synchronized (TextureManager.this.listLock) {
                        TextureManager.this.waitingForImage.removeValue(imageView);
                    }
                }
            }
        });
    }

    private void setXLEImageView(final TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest, final XLEImageView xLEImageView, final XLEBitmap xLEBitmap) {
        ThreadManager.UIThreadPost(new Runnable() { // from class: com.microsoft.xbox.toolkit.ui.TextureManager.2
            @Override // java.lang.Runnable
            public void run() {
                boolean keyValueMatches;
                XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
                synchronized (TextureManager.this.listLock) {
                    keyValueMatches = TextureManager.this.waitingForImage.keyValueMatches(textureManagerScaledNetworkBitmapRequest, xLEImageView);
                }
                if (keyValueMatches) {
                    final float alpha = xLEImageView.getAlpha();
                    if (xLEImageView.getShouldAnimate()) {
                        xLEImageView.animate().alpha(0.0f).setDuration(100L).setListener(new AnimatorListenerAdapter() { // from class: com.microsoft.xbox.toolkit.ui.TextureManager.2.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                xLEImageView.setFinal(true);
                                TextureManager.this.setImage(xLEImageView, xLEBitmap);
                                xLEImageView.animate().alpha(alpha).setDuration(100L).setListener(null);
                            }
                        });
                    } else {
                        TextureManager.this.setImage(xLEImageView, xLEBitmap);
                    }
                    synchronized (TextureManager.this.listLock) {
                        TextureManager.this.waitingForImage.removeValue(xLEImageView);
                    }
                }
            }
        });
    }

    private static boolean validResizeDimention(int i, int i2) {
        if (i == 0 || i2 == 0) {
            throw new UnsupportedOperationException();
        }
        return i > 0 && i2 > 0;
    }

    public void bindToView(int i, ImageView imageView, int i2, int i3) {
        bindToView(i, imageView, i2, i3, null);
    }

    public void bindToView(int i, ImageView imageView, int i2, int i3, OnBitmapSetListener onBitmapSetListener) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        XLEBitmap loadResource = loadResource(i);
        XLEAssert.assertTrue(loadResource != null);
        if (imageView instanceof XLEImageView) {
            ((XLEImageView) imageView).TEST_loadingOrLoadedImageUrl = Integer.toString(i);
        }
        setImage(imageView, loadResource);
    }

    public void bindToView(URI uri, ImageView imageView, int i, int i2) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (i == 0 || i2 == 0) {
            throw new UnsupportedOperationException();
        }
        bindToViewInternal(uri == null ? null : uri.toString(), imageView, new TextureBindingOption(i, i2));
    }

    public void bindToView(URI uri, ImageView imageView, TextureBindingOption textureBindingOption) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        bindToViewInternal(uri == null ? null : uri.toString(), imageView, textureBindingOption);
    }

    public void bindToViewFromFile(String str, ImageView imageView, int i, int i2) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (i == 0 || i2 == 0) {
            throw new UnsupportedOperationException();
        }
        bindToViewInternal(str, imageView, new TextureBindingOption(i, i2));
    }

    public void bindToViewFromFile(String str, ImageView imageView, TextureBindingOption textureBindingOption) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        bindToViewInternal(str, imageView, textureBindingOption);
    }

    public boolean isBusy() {
        boolean z;
        synchronized (this.listLock) {
            z = !this.inProgress.isEmpty();
        }
        return z;
    }

    public XLEBitmap loadResource(int i) {
        TextureManagerScaledResourceBitmapRequest textureManagerScaledResourceBitmapRequest = new TextureManagerScaledResourceBitmapRequest(i);
        XLEBitmap xLEBitmap = this.resourceBitmapCache.get(textureManagerScaledResourceBitmapRequest);
        if (xLEBitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(XboxTcuiSdk.getResources(), textureManagerScaledResourceBitmapRequest.resourceId, options);
            xLEBitmap = XLEBitmap.decodeResource(XboxTcuiSdk.getResources(), textureManagerScaledResourceBitmapRequest.resourceId);
            this.resourceBitmapCache.put(textureManagerScaledResourceBitmapRequest, xLEBitmap);
        }
        XLEAssert.assertNotNull(xLEBitmap);
        return xLEBitmap;
    }

    public XLEBitmap.XLEBitmapDrawable loadScaledResourceDrawable(int i) {
        XLEBitmap loadResource = loadResource(i);
        if (loadResource == null) {
            return null;
        }
        return loadResource.getDrawable();
    }

    public void purgeResourceBitmapCache() {
        this.resourceBitmapCache.clear();
    }

    public void setCachingEnabled(boolean z) {
        this.bitmapCache = new XLEMemoryCache<>(z ? getNetworkBitmapCacheSizeInMB() : 0, BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES);
        this.bitmapFileCache = XLEFileCacheManager.createCache(BMP_FILE_CACHE_DIR_NAME, 2000, z);
        this.resourceBitmapCache = new HashMap<>();
    }
}

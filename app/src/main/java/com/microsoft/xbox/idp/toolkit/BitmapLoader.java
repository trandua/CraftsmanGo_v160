package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.microsoft.xbox.idp.toolkit.WorkerLoader;
import java.net.MalformedURLException;
import java.net.URL;

/* loaded from: classes3.dex */
public class BitmapLoader extends WorkerLoader<BitmapLoader.Result> {
    public static final String TAG = "BitmapLoader";

    /* loaded from: classes3.dex */
    public interface Cache {
        void clear();

        Bitmap get(Object obj);

        Bitmap put(Object obj, Bitmap bitmap);

        Bitmap remove(Object obj);
    }

    /* loaded from: classes3.dex */
    private static class MyWorker implements WorkerLoader.Worker<Result> {
        static final boolean $assertionsDisabled = false;
        public final Cache cache;
        public final Object resultKey;
        public final String urlString;

        @Override // com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker
        public void cancel() {
        }

        private MyWorker(Cache cache, Object obj, String str) {
            this.cache = cache;
            this.resultKey = obj;
            this.urlString = str;
        }

        public boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        @Override // com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker
        public void start(final WorkerLoader.ResultListener<Result> resultListener) {
            final Bitmap bitmap;
            if (hasCache()) {
                synchronized (this.cache) {
                    bitmap = this.cache.get(this.resultKey);
                }
                if (bitmap != null) {
                    Log.d(BitmapLoader.TAG, "Successfully retrieved Bitmap from BitmapLoader.Cache");
                    new Thread(new Runnable() { // from class: com.microsoft.xbox.idp.toolkit.BitmapLoader.MyWorker.1
                        @Override // java.lang.Runnable
                        public void run() {
                            resultListener.onResult(new Result(bitmap));
                        }
                    }).start();
                    return;
                }
            }
            new Thread(new Runnable() { // from class: com.microsoft.xbox.idp.toolkit.BitmapLoader.MyWorker.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        URL url = new URL(MyWorker.this.urlString);
                        Log.d(BitmapLoader.TAG, "url created: " + url);
                        Bitmap decodeStream = BitmapFactory.decodeStream(url.openStream());
                        if (MyWorker.this.hasCache()) {
                            synchronized (MyWorker.this.cache) {
                                MyWorker.this.cache.put(MyWorker.this.resultKey, decodeStream);
                            }
                        }
                        resultListener.onResult(new Result(decodeStream));
                    } catch (MalformedURLException e) {
                        Log.e(BitmapLoader.TAG, "Received malformed URL: " + MyWorker.this.urlString);
                        resultListener.onResult(new Result(e));
                    } catch (Exception e2) {
                        resultListener.onResult(new Result(e2));
                    }
                }
            }).start();
        }
    }

    /* loaded from: classes3.dex */
    public static class Result extends LoaderResult<Bitmap> {
        protected Result(Bitmap bitmap) {
            super(bitmap, null);
        }

        protected Result(Exception exc) {
            super(exc);
        }

        @Override // com.microsoft.xbox.idp.toolkit.LoaderResult
        public boolean isReleased() {
            return hasData() && getData().isRecycled();
        }

        @Override // com.microsoft.xbox.idp.toolkit.LoaderResult
        public void release() {
            if (hasData()) {
                getData().recycle();
            }
        }
    }

    public BitmapLoader(Context context, Cache cache, Object obj, String str) {
        super(context, new MyWorker(cache, obj, str));
    }

    public BitmapLoader(Context context, String str) {
        this(context, null, null, str);
    }

    @Override // com.microsoft.xbox.idp.toolkit.WorkerLoader
    public boolean isDataReleased(Result result) {
        return result.isReleased();
    }

    @Override // com.microsoft.xbox.idp.toolkit.WorkerLoader
    public void releaseData(Result result) {
        result.release();
    }
}

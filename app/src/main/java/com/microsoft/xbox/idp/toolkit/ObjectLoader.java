package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import com.google.gson.Gson;
import com.microsoft.xbox.idp.toolkit.WorkerLoader;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpHeaders;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

/* loaded from: classes3.dex */
public class ObjectLoader<T> extends WorkerLoader<ObjectLoader.Result<T>> {
    private static final String TAG = "ObjectLoader";

    /* loaded from: classes3.dex */
    public interface Cache {
        void clear();

        <T> Result<T> get(Object obj);

        <T> Result<?> put(Object obj, Result<T> result);

        <T> Result<T> remove(Object obj);
    }

//    @Override // com.microsoft.xbox.idp.toolkit.WorkerLoader
//    public /* bridge */ /* synthetic */ boolean isDataReleased(Object obj) {
//        return isDataReleased((Result) ((Result) obj));
//    }

//    @Override // com.microsoft.xbox.idp.toolkit.WorkerLoader
//    public /* bridge */ /* synthetic */ void releaseData(Object obj) {
//        releaseData((Result) ((Result) obj));
//    }

    /* loaded from: classes3.dex */
    private static class MyWorker<T> implements WorkerLoader.Worker<Result<T>> {
        public final Cache cache;
        public final Class<T> cls;
        public final Gson gson;
        private final HttpCall httpCall;
        public final Object resultKey;

        @Override // com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker
        public void cancel() {
        }

        private MyWorker(Cache cache, Object obj, Class<T> cls, Gson gson, HttpCall httpCall) {
            this.cache = cache;
            this.resultKey = obj;
            this.cls = cls;
            this.gson = gson;
            this.httpCall = httpCall;
        }

        public boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        @Override // com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker
        public void start(final WorkerLoader.ResultListener<Result<T>> resultListener) {
            Result<T> result;
            if (hasCache()) {
                synchronized (this.cache) {
                    result = this.cache.get(this.resultKey);
                }
                if (result != null) {
                    resultListener.onResult(result);
                    return;
                }
            }
            this.httpCall.getResponseAsync(new HttpCall.Callback() { // from class: com.microsoft.xbox.idp.toolkit.ObjectLoader.MyWorker.1
                @Override // com.microsoft.xbox.idp.util.HttpCall.Callback
                public void processResponse(int i, InputStream inputStream, HttpHeaders httpHeaders) throws Exception {
                    if (i < 200 || i > 299) {
                        Result<T> result2 = new Result<>(new HttpError(i, i, inputStream));
                        if (MyWorker.this.hasCache()) {
                            synchronized (MyWorker.this.cache) {
                                MyWorker.this.cache.put(MyWorker.this.resultKey, result2);
                            }
                        }
                        resultListener.onResult(result2);
                    } else if (MyWorker.this.cls == Void.class) {
                        resultListener.onResult(new Result((HttpError) null));
                    } else {
                        StringWriter stringWriter = new StringWriter();
                        try {
                            InputStreamReader inputStreamReader = new InputStreamReader(new BufferedInputStream(inputStream));
                            Result<T> result3 = new Result(MyWorker.this.gson.fromJson((Reader) inputStreamReader, (Class<Object>) MyWorker.this.cls));
                            if (MyWorker.this.hasCache()) {
                                synchronized (MyWorker.this.cache) {
                                    MyWorker.this.cache.put(MyWorker.this.resultKey, result3);
                                }
                            }
                            resultListener.onResult(result3);
                            inputStreamReader.close();
                        } finally {
                            stringWriter.close();
                        }
                    }
                }
            });
        }
    }

    /* loaded from: classes3.dex */
    public static class Result<T> extends LoaderResult<T> {
        @Override // com.microsoft.xbox.idp.toolkit.LoaderResult
        public boolean isReleased() {
            return true;
        }

        @Override // com.microsoft.xbox.idp.toolkit.LoaderResult
        public void release() {
        }

        protected Result(HttpError httpError) {
            super(null, httpError);
        }

        protected Result(T t) {
            super(t, null);
        }
    }

    public ObjectLoader(Context context, Cache cache, Object obj, Class<T> cls, Gson gson, HttpCall httpCall) {
        super(context, new MyWorker(cache, obj, cls, gson, httpCall));
    }

    public ObjectLoader(Context context, Class<T> cls, Gson gson, HttpCall httpCall) {
        this(context, null, null, cls, gson, httpCall);
    }

    public boolean isDataReleased(Result<T> result) {
        return result.isReleased();
    }

    public void releaseData(Result<T> result) {
        result.release();
    }
}

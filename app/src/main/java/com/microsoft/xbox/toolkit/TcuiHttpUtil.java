package com.microsoft.xbox.toolkit;

import android.util.Pair;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpHeaders;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes3.dex */
public class TcuiHttpUtil {
    public static String getResponseBodySync(HttpCall httpCall) throws XLEException {
        final AtomicReference atomicReference = new AtomicReference();
        atomicReference.set(new Pair(false, null));
        httpCall.getResponseAsync(new HttpCall.Callback() { // from class: com.microsoft.xbox.toolkit.TcuiHttpUtil.1
            @Override // com.microsoft.xbox.idp.util.HttpCall.Callback
            public void processResponse(int i, InputStream inputStream, HttpHeaders httpHeaders) throws Exception {
                String str = null;
                if (i < 200 || i > 299) {
                    synchronized (atomicReference) {
                        atomicReference.set(new Pair(true, null));
                        atomicReference.notify();
                    }
                    return;
                }
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 4096);
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        sb.append(readLine + "\n");
                    }
                    str = sb.toString();
                } catch (IOException e) {
                    XLEAssert.assertTrue("Failed to read ShortCircuitProfileMessage string - " + e.getMessage(), false);
                }
                synchronized (atomicReference) {
                    atomicReference.set(new Pair(true, str));
                    atomicReference.notify();
                }
            }
        });
        synchronized (atomicReference) {
            while (!((Boolean) ((Pair) atomicReference.get()).first).booleanValue()) {
                try {
                    atomicReference.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
        return (String) ((Pair) atomicReference.get()).second;
    }

    public static <T> T getResponseSync(HttpCall httpCall, final Class<T> cls) throws XLEException {
        final AtomicReference atomicReference = new AtomicReference();
        atomicReference.set(new Pair(false, null));
        httpCall.getResponseAsync(new HttpCall.Callback() { // from class: com.microsoft.xbox.toolkit.TcuiHttpUtil.2
            @Override // com.microsoft.xbox.idp.util.HttpCall.Callback
            public void processResponse(int i, InputStream inputStream, HttpHeaders httpHeaders) throws Exception {
                Object deserializeJson = (i >= 200 || i <= 299) ? GsonUtil.deserializeJson(inputStream, cls) : null;
                synchronized (atomicReference) {
                    atomicReference.set(new Pair(true, deserializeJson));
                    atomicReference.notify();
                }
            }
        });
        synchronized (atomicReference) {
            while (!((Boolean) ((Pair) atomicReference.get()).first).booleanValue()) {
                try {
                    atomicReference.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
        return (T) ((Pair) atomicReference.get()).second;
    }

    public static boolean getResponseSyncSucceeded(HttpCall httpCall, final List<Integer> list) {
        final AtomicReference atomicReference = new AtomicReference();
        httpCall.getResponseAsync(new HttpCall.Callback() { // from class: com.microsoft.xbox.toolkit.TcuiHttpUtil.3
            @Override // com.microsoft.xbox.idp.util.HttpCall.Callback
            public void processResponse(int i, InputStream inputStream, HttpHeaders httpHeaders) throws Exception {
                synchronized (atomicReference) {
                    atomicReference.set(Boolean.valueOf(list.contains(Integer.valueOf(i))));
                    atomicReference.notify();
                }
            }
        });
        synchronized (atomicReference) {
            while (atomicReference.get() == null) {
                try {
                    atomicReference.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
        return ((Boolean) atomicReference.get()).booleanValue();
    }

    public static <T> void throwIfNullOrFalse(T t) throws XLEException {
        if (t == null && !Boolean.getBoolean(t.toString())) {
            throw new XLEException(2L);
        }
    }
}

package com.microsoft.xbox.service.network.managers.xblshared;

/* loaded from: classes3.dex */
public class ProtectedRunnable implements Runnable {
    private static final String TAG = "ProtectedRunnable";
    private final Runnable runnable;

    public ProtectedRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override // java.lang.Runnable
    public void run() {
        boolean z = false;
        for (int i = 0; !z && i < 10; i++) {
            try {
                this.runnable.run();
                z = true;
            } catch (LinkageError unused) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException unused2) {
                }
            }
        }
    }
}

package com.microsoft.xbox.toolkit;

import android.os.SystemClock;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

/* loaded from: classes3.dex */
public class BackgroundThreadWaitor {
    private static BackgroundThreadWaitor instance = new BackgroundThreadWaitor();
    private BackgroundThreadWaitorChangedCallback blockingChangedCallback = null;
    private Hashtable<WaitType, WaitObject> blockingTable = new Hashtable<>();
    private Ready waitReady = new Ready();
    private ArrayList<Runnable> waitingRunnables = new ArrayList<>();

    /* loaded from: classes3.dex */
    public interface BackgroundThreadWaitorChangedCallback {
        void run(EnumSet<WaitType> enumSet, boolean z);
    }

    /* loaded from: classes3.dex */
    public enum WaitType {
        Navigation,
        ApplicationBar,
        ListScroll,
        ListLayout,
        PivotScroll
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class WaitObject {
        private long expires;
        final BackgroundThreadWaitor this$0;
        public WaitType type;

        public WaitObject(BackgroundThreadWaitor backgroundThreadWaitor, WaitType waitType, long j) {
            this.this$0 = backgroundThreadWaitor;
            this.type = waitType;
            this.expires = SystemClock.uptimeMillis() + j;
        }

        public boolean isExpired() {
            return this.expires < SystemClock.uptimeMillis();
        }
    }

    private void drainWaitingRunnables() {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        Iterator<Runnable> it = this.waitingRunnables.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.waitingRunnables.clear();
    }

    public static BackgroundThreadWaitor getInstance() {
        if (instance == null) {
            instance = new BackgroundThreadWaitor();
        }
        return instance;
    }

    public void updateWaitReady() {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        HashSet hashSet = new HashSet();
        EnumSet<WaitType> noneOf = EnumSet.noneOf(WaitType.class);
        Enumeration<WaitObject> elements = this.blockingTable.elements();
        while (elements.hasMoreElements()) {
            WaitObject nextElement = elements.nextElement();
            boolean isExpired = nextElement.isExpired();
            WaitType waitType = nextElement.type;
            if (isExpired) {
                hashSet.add(waitType);
            } else {
                noneOf.add(waitType);
            }
        }
        final Hashtable<WaitType, WaitObject> hashtable = this.blockingTable;
        Objects.requireNonNull(hashtable);
        hashSet.forEach(new Consumer() { // from class: com.microsoft.xbox.toolkit.BackgroundThreadWaitor$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                hashtable.remove((BackgroundThreadWaitor.WaitType) obj);
            }
        });
        boolean z = !noneOf.isEmpty();
        if (!z) {
            this.waitReady.setReady();
            drainWaitingRunnables();
        } else {
            this.waitReady.reset();
        }
        BackgroundThreadWaitorChangedCallback backgroundThreadWaitorChangedCallback = this.blockingChangedCallback;
        if (backgroundThreadWaitorChangedCallback != null) {
            backgroundThreadWaitorChangedCallback.run(noneOf, z);
        }
    }

    public void clearBlocking(WaitType waitType) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        this.blockingTable.remove(waitType);
        updateWaitReady();
    }

    public boolean isBlocking() {
        return !this.waitReady.getIsReady();
    }

    public void postRunnableAfterReady(Runnable runnable) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        if (runnable != null) {
            if (!isBlocking()) {
                runnable.run();
            } else {
                this.waitingRunnables.add(runnable);
            }
        }
    }

    public void setBlocking(WaitType waitType, int i) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        this.blockingTable.put(waitType, new WaitObject(this, waitType, i));
        updateWaitReady();
    }

    public void setChangedCallback(BackgroundThreadWaitorChangedCallback backgroundThreadWaitorChangedCallback) {
        this.blockingChangedCallback = backgroundThreadWaitorChangedCallback;
    }

    public void waitForReady(int i) {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        ThreadManager.UIThreadPost(new Runnable() { // from class: com.microsoft.xbox.toolkit.BackgroundThreadWaitor.1
            @Override // java.lang.Runnable
            public void run() {
                BackgroundThreadWaitor.this.updateWaitReady();
            }
        });
        this.waitReady.waitForReady(i);
    }
}

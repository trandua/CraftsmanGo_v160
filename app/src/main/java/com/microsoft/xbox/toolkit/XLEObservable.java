package com.microsoft.xbox.toolkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: classes3.dex */
public abstract class XLEObservable<T> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private HashSet<XLEObserver<T>> data = new HashSet<>();

    public void addObserver(XLEObserver<T> xLEObserver) {
        synchronized (this) {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            this.data.add(xLEObserver);
        }
    }

    public void addUniqueObserver(XLEObserver<T> xLEObserver) {
        synchronized (this) {
            if (!this.data.contains(xLEObserver)) {
                addObserver(xLEObserver);
            }
        }
    }

    public void clearObserver() {
        synchronized (this) {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            this.data.clear();
        }
    }

    public ArrayList<XLEObserver<T>> getObservers() {
        ArrayList<XLEObserver<T>> arrayList;
        synchronized (this) {
            arrayList = new ArrayList<>(this.data);
        }
        return arrayList;
    }

    public void notifyObservers(AsyncResult<T> asyncResult) {
        synchronized (this) {
            Iterator it = new ArrayList(this.data).iterator();
            while (it.hasNext()) {
                ((XLEObserver) it.next()).update(asyncResult);
            }
        }
    }

    public void removeObserver(XLEObserver<T> xLEObserver) {
        synchronized (this) {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            this.data.remove(xLEObserver);
        }
    }
}

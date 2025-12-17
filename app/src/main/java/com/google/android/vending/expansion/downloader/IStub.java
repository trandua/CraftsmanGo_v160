package com.google.android.vending.expansion.downloader;

import android.content.Context;
import android.os.Messenger;

/* loaded from: classes7.dex */
public interface IStub {
    void connect(Context c);

    void disconnect(Context c);

    Messenger getMessenger();
}

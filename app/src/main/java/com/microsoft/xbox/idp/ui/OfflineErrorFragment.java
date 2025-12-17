package com.microsoft.xbox.idp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xboxtcui.C5528R;

/* loaded from: classes3.dex */
public class OfflineErrorFragment extends BaseFragment {
    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C5528R.layout.xbid_fragment_error_offline, viewGroup, false);
    }
}

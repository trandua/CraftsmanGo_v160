package com.microsoft.xbox.idp.compat;

import android.app.Activity;
import android.os.Bundle;

/* loaded from: classes3.dex */
public abstract class BaseActivity extends Activity {
    public void addFragment(int i, BaseFragment baseFragment) {
        getFragmentManager().beginTransaction().add(i, baseFragment).commit();
    }

    public boolean hasFragment(int i) {
        return getFragmentManager().findFragmentById(i) != null;
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setOrientation();
    }

    public void setOrientation() {
        if ((getApplicationContext().getResources().getConfiguration().screenLayout & 15) < 3) {
            setRequestedOrientation(1);
        }
    }
}

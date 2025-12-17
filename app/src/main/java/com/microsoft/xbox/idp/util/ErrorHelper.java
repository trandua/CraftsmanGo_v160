package com.microsoft.xbox.idp.util;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.microsoft.xbox.idp.ui.ErrorActivity;

/* loaded from: classes3.dex */
public final class ErrorHelper implements Parcelable {
    public static final Parcelable.Creator<ErrorHelper> CREATOR = new Parcelable.Creator<ErrorHelper>() { // from class: com.microsoft.xbox.idp.util.ErrorHelper.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ErrorHelper createFromParcel(Parcel parcel) {
            return new ErrorHelper(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ErrorHelper[] newArray(int i) {
            return new ErrorHelper[i];
        }
    };
    public static final String KEY_RESULT_KEY = "KEY_RESULT_KEY";
    public static final int LOADER_NONE = -1;
    public static final int RC_ERROR_SCREEN = 63;
    private static final String TAG = "ErrorHelper";
    private ActivityContext activityContext;
    public Bundle loaderArgs;
    public int loaderId;

    /* loaded from: classes3.dex */
    public interface ActivityContext {
        Activity getActivity();

        LoaderInfo getLoaderInfo(int i);

        LoaderManager getLoaderManager();

        void startActivityForResult(Intent intent, int i);
    }

    /* loaded from: classes3.dex */
    public interface LoaderInfo {
        void clearCache(Object obj);

        LoaderManager.LoaderCallbacks<?> getLoaderCallbacks();

        boolean hasCachedData(Object obj);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes3.dex */
    public static class ActivityResult {
        private final boolean tryAgain;

        public ActivityResult(boolean z) {
            this.tryAgain = z;
        }

        public boolean isTryAgain() {
            return this.tryAgain;
        }
    }

    public ErrorHelper() {
        this.loaderId = -1;
        this.loaderArgs = null;
    }

    protected ErrorHelper(Parcel parcel) {
        this.loaderId = parcel.readInt();
        this.loaderArgs = parcel.readBundle();
    }

    private boolean isConnected() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.activityContext.getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void deleteLoader() {
        if (this.loaderId != -1) {
            this.activityContext.getLoaderManager().destroyLoader(this.loaderId);
            Bundle bundle = this.loaderArgs;
            Object obj = bundle == null ? null : bundle.get(KEY_RESULT_KEY);
            if (obj != null) {
                this.activityContext.getLoaderInfo(this.loaderId).clearCache(obj);
            }
            this.loaderId = -1;
            this.loaderArgs = null;
        }
    }

    public ActivityResult getActivityResult(int i, int i2, Intent intent) {
        if (i != 63) {
            return null;
        }
        return new ActivityResult(i2 == 1);
    }

    public <D> boolean initLoader(int i, Bundle bundle) {
        return initLoader(i, bundle, true);
    }

    public <D> boolean initLoader(int i, Bundle bundle, boolean z) {
        Log.d(TAG, "initLoader");
        if (i != -1) {
            this.loaderId = i;
            this.loaderArgs = bundle;
            LoaderManager loaderManager = this.activityContext.getLoaderManager();
            LoaderInfo loaderInfo = this.activityContext.getLoaderInfo(this.loaderId);
            Bundle bundle2 = this.loaderArgs;
            Object obj = bundle2 == null ? null : bundle2.get(KEY_RESULT_KEY);
            if ((obj != null && loaderInfo.hasCachedData(obj)) || loaderManager.getLoader(i) != null || !z || isConnected()) {
                Log.d(TAG, "initializing loader #" + this.loaderId);
                loaderManager.initLoader(i, bundle, loaderInfo.getLoaderCallbacks());
                return true;
            }
            Log.e(TAG, "Starting error activity: OFFLINE");
            startErrorActivity(ErrorActivity.ErrorScreen.OFFLINE);
            return false;
        }
        Log.e(TAG, "LOADER_NONE");
        return false;
    }

    public <D> boolean restartLoader() {
        if (this.loaderId != -1) {
            if (isConnected()) {
                LoaderManager loaderManager = this.activityContext.getLoaderManager();
                int i = this.loaderId;
                loaderManager.restartLoader(i, this.loaderArgs, this.activityContext.getLoaderInfo(i).getLoaderCallbacks());
                return true;
            }
            startErrorActivity(ErrorActivity.ErrorScreen.OFFLINE);
            return false;
        }
        return false;
    }

    public <D> boolean restartLoader(int i, Bundle bundle) {
        if (i != -1) {
            this.loaderId = i;
            this.loaderArgs = bundle;
            if (isConnected()) {
                LoaderManager loaderManager = this.activityContext.getLoaderManager();
                int i2 = this.loaderId;
                loaderManager.restartLoader(i2, this.loaderArgs, this.activityContext.getLoaderInfo(i2).getLoaderCallbacks());
                return true;
            }
            startErrorActivity(ErrorActivity.ErrorScreen.OFFLINE);
            return false;
        }
        return false;
    }

    public void setActivityContext(ActivityContext activityContext) {
        this.activityContext = activityContext;
    }

    public void startErrorActivity(ErrorActivity.ErrorScreen errorScreen) {
        Intent intent = new Intent(this.activityContext.getActivity(), ErrorActivity.class);
        intent.putExtra(ErrorActivity.ARG_ERROR_TYPE, errorScreen.type.getId());
        this.activityContext.startActivityForResult(intent, 63);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.loaderId);
        parcel.writeBundle(this.loaderArgs);
    }
}

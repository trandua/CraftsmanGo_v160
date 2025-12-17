package com.microsoft.xbox.xle.model;

import android.os.Build;
import com.microsoft.xbox.service.model.ModelBase;
import com.microsoft.xbox.service.model.serialization.Version;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.xle.app.SmartglassSettings;
import java.util.HashSet;

/* loaded from: classes3.dex */
public class SystemSettingsModel extends ModelBase<Version> {
    private final HashSet<String> hiddenMruItems;
    private int latestVersion;
    private String marketUrl;
    private int minRequiredOSVersion;
    private int minVersion;
    private int[] remoteControlSpecialTitleIds;
    private SmartglassSettings smartglassSettings;
    private final SingleEntryLoadingStatus smartglassSettingsLoadingStatus;
    private OnUpdateExistListener updateExistListener;

    /* loaded from: classes3.dex */
    public interface OnUpdateExistListener {
        void onMustUpdate();

        void onOptionalUpdate();
    }

    /* loaded from: classes3.dex */
    private class GetSmartglassSettingsRunner extends IDataLoaderRunnable<SmartglassSettings> {
        private final SystemSettingsModel caller;
        final SystemSettingsModel this$0;

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public SmartglassSettings buildData() throws XLEException {
            return null;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_SETTINGS;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public GetSmartglassSettingsRunner(SystemSettingsModel systemSettingsModel, SystemSettingsModel systemSettingsModel2) {
            this.this$0 = systemSettingsModel;
            this.caller = systemSettingsModel2;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<SmartglassSettings> asyncResult) {
            this.caller.onGetSmartglassSettingsCompleted(asyncResult);
        }
    }

    /* loaded from: classes3.dex */
    private static class SystemSettingsModelContainer {
        public static SystemSettingsModel instance = new SystemSettingsModel();

        private SystemSettingsModelContainer() {
        }
    }

    private SystemSettingsModel() {
        this.minRequiredOSVersion = 0;
        this.minVersion = 0;
        this.latestVersion = 0;
        this.hiddenMruItems = new HashSet<>();
        this.smartglassSettingsLoadingStatus = new SingleEntryLoadingStatus();
    }

    public static SystemSettingsModel getInstance() {
        return SystemSettingsModelContainer.instance;
    }

    private int getMinimumVersion() {
        return this.minVersion;
    }

    public void onGetSmartglassSettingsCompleted(AsyncResult<SmartglassSettings> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            SmartglassSettings result = asyncResult.getResult();
            this.smartglassSettings = result;
            if (result != null) {
                this.minRequiredOSVersion = result.ANDROID_VERSIONMINOS;
                this.minVersion = this.smartglassSettings.ANDROID_VERSIONMIN;
                this.latestVersion = this.smartglassSettings.ANDROID_VERSIONLATEST;
                this.marketUrl = this.smartglassSettings.ANDROID_VERSIONURL;
                populateHiddenMruItems(this.smartglassSettings.HIDDEN_MRU_ITEMS);
                populateRemoteControlSpecialTitleIds(this.smartglassSettings.REMOTE_CONTROL_SPECIALS);
                if (this.updateExistListener == null) {
                    return;
                }
                if (getMustUpdate(ProjectSpecificDataProvider.getInstance().getVersionCode())) {
                    this.updateExistListener.onMustUpdate();
                } else if (getHasUpdate(ProjectSpecificDataProvider.getInstance().getVersionCode())) {
                    this.updateExistListener.onOptionalUpdate();
                }
            }
        }
    }

    private void populateHiddenMruItems(String str) {
        String[] split;
        this.hiddenMruItems.clear();
        if (str == null || (split = str.split(",")) == null) {
            return;
        }
        for (String str2 : split) {
            this.hiddenMruItems.add(str2);
        }
    }

    private void populateRemoteControlSpecialTitleIds(String str) {
        String[] split;
        int i;
        if (str == null || (split = str.split(",")) == null) {
            return;
        }
        this.remoteControlSpecialTitleIds = new int[split.length];
        int length = split.length;
        int i2 = 0;
        int i3 = 0;
        while (i2 < length) {
            try {
                i = Integer.parseInt(split[i2]);
            } catch (NumberFormatException unused) {
                i = 0;
            }
            this.remoteControlSpecialTitleIds[i3] = i;
            i2++;
            i3++;
        }
    }

    public boolean getHasUpdate(int i) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return Build.VERSION.SDK_INT >= this.minRequiredOSVersion && getLatestVersion() > i;
    }

    public int getLatestVersion() {
        return this.latestVersion;
    }

    public String getMarketUrl() {
        return this.marketUrl;
    }

    public boolean getMustUpdate(int i) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return Build.VERSION.SDK_INT >= this.minRequiredOSVersion && getMinimumVersion() > i;
    }

    public int[] getRemoteControlSpecialTitleIds() {
        return this.remoteControlSpecialTitleIds;
    }

    public boolean isInHiddenMruItems(String str) {
        return this.hiddenMruItems.contains(str);
    }

    public void loadAsync(boolean z) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        DataLoadUtil.StartLoadFromUI(z, this.lifetime, null, this.smartglassSettingsLoadingStatus, new GetSmartglassSettingsRunner(this, this));
    }

    public AsyncResult<SmartglassSettings> loadSystemSettings(boolean z) {
        return DataLoadUtil.load(z, this.lifetime, null, this.smartglassSettingsLoadingStatus, new GetSmartglassSettingsRunner(this, this));
    }

    public void setOnUpdateExistListener(OnUpdateExistListener onUpdateExistListener) {
        this.updateExistListener = onUpdateExistListener;
    }
}

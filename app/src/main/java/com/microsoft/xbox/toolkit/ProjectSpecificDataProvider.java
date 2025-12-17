package com.microsoft.xbox.toolkit;

/* loaded from: classes3.dex */
public class ProjectSpecificDataProvider implements IProjectSpecificDataProvider {
    private static ProjectSpecificDataProvider instance = new ProjectSpecificDataProvider();
    private IProjectSpecificDataProvider provider;

    private void checkProvider() {
    }

    public static ProjectSpecificDataProvider getInstance() {
        return instance;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public boolean getAllowExplicitContent() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getAllowExplicitContent();
        }
        return false;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getAutoSuggestdDataSource() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getAutoSuggestdDataSource();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getCombinedContentRating() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getCombinedContentRating();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getConnectedLocale() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getConnectedLocale();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getConnectedLocale(boolean z) {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getConnectedLocale(z);
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getContentRestrictions() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getContentRestrictions();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getCurrentSandboxID() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getCurrentSandboxID();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public boolean getInitializeComplete() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getInitializeComplete();
        }
        return false;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public boolean getIsForXboxOne() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getIsForXboxOne();
        }
        return false;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public boolean getIsFreeAccount() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getIsFreeAccount();
        }
        return true;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public boolean getIsXboxMusicSupported() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getIsXboxMusicSupported();
        }
        return false;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getLegalLocale() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getLegalLocale();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getMembershipLevel() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getMembershipLevel();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getPrivileges() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        return iProjectSpecificDataProvider != null ? iProjectSpecificDataProvider.getPrivileges() : "";
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getRegion() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getRegion();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getSCDRpsTicket() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getSCDRpsTicket();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getVersionCheckUrl() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getVersionCheckUrl();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public int getVersionCode() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getVersionCode();
        }
        return 0;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getWindowsLiveClientId() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getWindowsLiveClientId();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public String getXuidString() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.getXuidString();
        }
        return null;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public boolean isDeviceLocaleKnown() {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            return iProjectSpecificDataProvider.isDeviceLocaleKnown();
        }
        return true;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public void resetModels(boolean z) {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            iProjectSpecificDataProvider.resetModels(z);
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public void setPrivileges(String str) {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            iProjectSpecificDataProvider.setPrivileges(str);
        }
    }

    public void setProvider(IProjectSpecificDataProvider iProjectSpecificDataProvider) {
        this.provider = iProjectSpecificDataProvider;
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public void setSCDRpsTicket(String str) {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            iProjectSpecificDataProvider.setSCDRpsTicket(str);
        }
    }

    @Override // com.microsoft.xbox.toolkit.IProjectSpecificDataProvider
    public void setXuidString(String str) {
        checkProvider();
        IProjectSpecificDataProvider iProjectSpecificDataProvider = this.provider;
        if (iProjectSpecificDataProvider != null) {
            iProjectSpecificDataProvider.setXuidString(str);
        }
    }
}

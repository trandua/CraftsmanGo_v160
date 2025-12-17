package com.microsoft.xbox.toolkit.network;

import com.microsoft.xbox.toolkit.XLEAssert;

/* loaded from: classes3.dex */
public class XboxLiveEnvironment {
    public static final String NEVER_LIST_CONTRACT_VERSION = "1";
    public static final String SHARE_IDENTITY_CONTRACT_VERSION = "4";
    public static final String SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION = "1";
    public static final String USER_PROFILE_CONTRACT_VERSION = "3";
    public static final String USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION = "4";
    private static XboxLiveEnvironment instance = new XboxLiveEnvironment();
    private Environment environment = Environment.PROD;
    private final boolean useProxy = false;

    /* loaded from: classes3.dex */
    public enum Environment {
        STUB,
        VINT,
        CERTNET,
        PARTNERNET,
        PROD,
        DNET
    }

    public String getFriendFinderSettingsUrl() {
        return "https://settings.xboxlive.com/settings/feature/friendfinder/settings";
    }

    public String getMutedServiceUrlFormat() {
        return "https://privacy.xboxlive.com/users/xuid(%s)/people/mute";
    }

    public String getPeopleHubFriendFinderStateUrlFormat() {
        return "https://peoplehub.xboxlive.com/users/me/friendfinder";
    }

    public String getPeopleHubRecommendationsUrlFormat() {
        return "https://peoplehub.xboxlive.com/users/me/people/recommendations";
    }

    public String getProfileSettingUrlFormat() {
        return "https://privacy.xboxlive.com/users/me/privacy/settings/%s";
    }

    public boolean getProxyEnabled() {
        return false;
    }

    public String getSetFriendFinderOptInStatusUrlFormat() {
        return "https://friendfinder.xboxlive.com/users/me/networks/%s/optin";
    }

    public String getShortCircuitProfileUrlFormat() {
        return "https://pf.directory.live.com/profile/mine/System.ShortCircuitProfile.json";
    }

    public String getSubmitFeedbackUrlFormat() {
        return "https://reputation.xboxlive.com/users/xuid(%s)/feedback";
    }

    public String getTenureWatermarkUrlFormat() {
        return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/tenure/%s.png";
    }

    public String getUpdateThirdPartyTokenUrlFormat() {
        return "https://thirdpartytokens.xboxlive.com/users/me/networks/%s/token";
    }

    public String getUploadingPhoneContactsUrlFormat() {
        return "https://people.directory.live.com/people/ExternalSCDLookup";
    }

    public String getUserProfileSettingUrlFormat() {
        return "https://privacy.xboxlive.com/users/me/privacy/settings";
    }

    /* loaded from: classes3.dex */
    static class C54821 {
        static final int[] f12620xa155190f;

        C54821() {
        }

        static {
            int[] iArr = new int[Environment.values().length];
            f12620xa155190f = iArr;
            try {
                iArr[Environment.VINT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f12620xa155190f[Environment.DNET.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f12620xa155190f[Environment.PARTNERNET.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                f12620xa155190f[Environment.PROD.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public static XboxLiveEnvironment Instance() {
        return instance;
    }

    public String getAddFriendsToShareIdentityUrlFormat() {
        int i = C54821.f12620xa155190f[this.environment.ordinal()];
        if (i == 1 || i == 2) {
            return "https://social.dnet.xboxlive.com/users/xuid(%s)/people/identityshared/xuids?method=add";
        }
        if (i == 4) {
            return "https://social.xboxlive.com/users/xuid(%s)/people/identityshared/xuids?method=add";
        }
        throw new UnsupportedOperationException();
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public String getGamertagSearchUrlFormat() {
        int i = C54821.f12620xa155190f[this.environment.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            return "https://profile.dnet.xboxlive.com/users/gt(%s)/profile/settings?settings=AppDisplayName,DisplayPic,Gamerscore,Gamertag,PublicGamerpic,XboxOneRep";
        }
        if (i == 4) {
            return "https://profile.xboxlive.com/users/gt(%s)/profile/settings?settings=AppDisplayName,DisplayPic,Gamerscore,Gamertag,PublicGamerpic,XboxOneRep";
        }
        throw new UnsupportedOperationException();
    }

    public String getProfileFavoriteListUrl() {
        int i = C54821.f12620xa155190f[this.environment.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            return "https://social.dnet.xboxlive.com/users/me/people/favorites/xuids?method=%s";
        }
        if (i == 4) {
            return "https://social.xboxlive.com/users/me/people/favorites/xuids?method=%s";
        }
        throw new UnsupportedOperationException();
    }

    public String getProfileNeverListUrlFormat() {
        int i = C54821.f12620xa155190f[this.environment.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            return "https://privacy.dnet.xboxlive.com/users/xuid(%s)/people/never";
        }
        if (i == 4) {
            return "https://privacy.xboxlive.com/users/xuid(%s)/people/never";
        }
        throw new UnsupportedOperationException();
    }

    public String getProfileSummaryUrlFormat() {
        int i = C54821.f12620xa155190f[this.environment.ordinal()];
        if (i == 1 || i == 2) {
            return "https://social.dnet.xboxlive.com/users/xuid(%s)/summary";
        }
        if (i == 4) {
            return "https://social.xboxlive.com/users/xuid(%s)/summary";
        }
        throw new UnsupportedOperationException();
    }

    public String getRemoveUsersFromShareIdentityUrlFormat() {
        int i = C54821.f12620xa155190f[this.environment.ordinal()];
        if (i == 1 || i == 2) {
            return "https://social.dnet.xboxlive.com/users/xuid(%s)/people/identityshared/xuids?method=remove";
        }
        if (i == 4) {
            return "https://social.xboxlive.com/users/xuid(%s)/people/identityshared/xuids?method=remove";
        }
        throw new UnsupportedOperationException();
    }

    public String getUserProfileInfoUrl() {
        int i = C54821.f12620xa155190f[this.environment.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            return "https://profile.dnet.xboxlive.com/users/batch/profile/settings";
        }
        if (i == 4) {
            return "https://profile.xboxlive.com/users/batch/profile/settings";
        }
        throw new UnsupportedOperationException();
    }

    public String getWatermarkUrl(String str) {
        String lowerCase = str.toLowerCase();
        lowerCase.hashCode();
        lowerCase.hashCode();
        lowerCase.hashCode();
        char c = 65535;
        switch (lowerCase.hashCode()) {
            case -1921480520:
                if (lowerCase.equals("nxeteam")) {
                    c = 0;
                    break;
                }
                break;
            case -69693424:
                if (lowerCase.equals("xboxoneteam")) {
                    c = 1;
                    break;
                }
                break;
            case 467871267:
                if (lowerCase.equals("kinectteam")) {
                    c = 2;
                    break;
                }
                break;
            case 547378320:
                if (lowerCase.equals("launchteam")) {
                    c = 3;
                    break;
                }
                break;
            case 742262976:
                if (lowerCase.equals("cheater")) {
                    c = 4;
                    break;
                }
                break;
            case 949652176:
                if (lowerCase.equals("xboxnxoeteam")) {
                    c = 5;
                    break;
                }
                break;
            case 1584505217:
                if (lowerCase.equals("xboxoriginalteam")) {
                    c = 6;
                    break;
                }
                break;
            case 2056113039:
                if (lowerCase.equals("xboxlivelaunchteam")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/nxeteam.png";
            case 1:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/xboxoneteam.png";
            case 2:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/kinectteam.png";
            case 3:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/launchteam.png";
            case 4:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/cheater.png";
            case 5:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/xboxnxoeteam.png";
            case 6:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/xboxoriginalteam.png";
            case 7:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/xboxlivelaunchteam.png";
            default:
                XLEAssert.fail("Unsupported watermark value: " + str);
                return "";
        }
    }

    public String updateProfileFollowingListUrl() {
        int i = C54821.f12620xa155190f[this.environment.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            return "https://social.dnet.xboxlive.com/users/me/people/xuids?method=%s";
        }
        if (i == 4) {
            return "https://social.xboxlive.com/users/me/people/xuids?method=%s";
        }
        throw new UnsupportedOperationException();
    }
}

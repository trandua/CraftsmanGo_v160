package com.microsoft.xbox.service.network.managers.xblshared;

import android.util.Log;
import androidx.core.util.Pair;
import androidx.exifinterface.media.ExifInterface;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpHeaders;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.service.model.privacy.PrivacySettings;
import com.microsoft.xbox.service.model.privacy.PrivacySettingsResult;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer;
import com.microsoft.xbox.service.network.managers.FamilySettings;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.service.network.managers.IUserProfileResult;
import com.microsoft.xbox.service.network.managers.MutedListResultContainer;
import com.microsoft.xbox.service.network.managers.NeverListResultContainer;
import com.microsoft.xbox.service.network.managers.ProfilePreferredColor;
import com.microsoft.xbox.service.network.managers.ProfileSummaryResultContainer;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.TcuiHttpUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;

/* loaded from: classes3.dex */
public class SLSXsapiServiceManager implements ISLSServiceManager {
    private static final String TAG = "SLSXsapiServiceManager";

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public FamilySettings getFamilySettings(String str) throws XLEException {
        return null;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public int[] getXTokenPrivileges() throws XLEException {
        return new int[0];
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public IUserProfileResult.UserProfileResult SearchGamertag(String str) throws XLEException {
        Log.i(TAG, "SearchGamertag");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        try {
            IUserProfileResult.UserProfileResult userProfileResult = (IUserProfileResult.UserProfileResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", String.format(XboxLiveEnvironment.Instance().getGamertagSearchUrlFormat(), URLEncoder.encode(str.toLowerCase(), "utf-8")), ""), "3"), IUserProfileResult.UserProfileResult.class);
            TcuiHttpUtil.throwIfNullOrFalse(userProfileResult);
            return userProfileResult;
        } catch (UnsupportedEncodingException e) {
            throw new XLEException(15L, e);
        }
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean addFriendToShareIdentitySetting(String str, String str2) throws XLEException {
        Log.i(TAG, "addFriendToShareIdentitySetting");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().getAddFriendsToShareIdentityUrlFormat(), str), ""), "4");
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(204));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean addUserToFavoriteList(String str) throws XLEException {
        Log.i(TAG, "addUserToFavoriteList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().getProfileFavoriteListUrl(), "add"), ""), "1");
        appendCommonParameters.setRequestBody(str);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(204));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public AddFollowingUserResponseContainer.AddFollowingUserResponse addUserToFollowingList(String str) throws XLEException {
        Log.i(TAG, "addUserToFollowingList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().updateProfileFollowingListUrl(), "add"), ""), "1");
        appendCommonParameters.setRequestBody(str);
        final AddFollowingUserResponseContainer.AddFollowingUserResponse addFollowingUserResponse = new AddFollowingUserResponseContainer.AddFollowingUserResponse();
        final AtomicReference atomicReference = new AtomicReference();
        atomicReference.set(new Pair(false, null));
        appendCommonParameters.getResponseAsync(new HttpCall.Callback() { // from class: com.microsoft.xbox.service.network.managers.xblshared.SLSXsapiServiceManager.1
            @Override // com.microsoft.xbox.idp.util.HttpCall.Callback
            public void processResponse(int i, InputStream inputStream, HttpHeaders httpHeaders) throws Exception {
                synchronized (atomicReference) {
                    if (i >= 200 || i <= 299) {
                        addFollowingUserResponse.setAddFollowingRequestStatus(true);
                        atomicReference.set(new Pair(true, addFollowingUserResponse));
                    } else {
                        atomicReference.set(new Pair(true, (AddFollowingUserResponseContainer.AddFollowingUserResponse) GsonUtil.deserializeJson(inputStream, AddFollowingUserResponseContainer.AddFollowingUserResponse.class)));
                    }
                    atomicReference.notify();
                }
            }
        });
        synchronized (atomicReference) {
            while (!((Boolean) ((Pair) atomicReference.get()).first).booleanValue()) {
                try {
                    atomicReference.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
        TcuiHttpUtil.throwIfNullOrFalse(((Pair) atomicReference.get()).second);
        return (AddFollowingUserResponseContainer.AddFollowingUserResponse) ((Pair) atomicReference.get()).second;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean addUserToMutedList(String str, String str2) throws XLEException {
        Log.i(TAG, "addUserToMutedList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpPut.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), str), ""), "1");
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean addUserToNeverList(String str, String str2) throws XLEException {
        Log.i(TAG, "addUserToNeverList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpPut.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), str), ""), "1");
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public MutedListResultContainer.MutedListResult getMutedListInfo(String str) throws XLEException {
        Log.i(TAG, "getMutedListInfo");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(str));
        MutedListResultContainer.MutedListResult mutedListResult = (MutedListResultContainer.MutedListResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), str), ""), "1"), MutedListResultContainer.MutedListResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(mutedListResult);
        return mutedListResult;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public NeverListResultContainer.NeverListResult getNeverListInfo(String str) throws XLEException {
        Log.i(TAG, "getNeverListInfo");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(str));
        NeverListResultContainer.NeverListResult neverListResult = (NeverListResultContainer.NeverListResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), str), ""), "1"), NeverListResultContainer.NeverListResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(neverListResult);
        return neverListResult;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public IPeopleHubResult.PeopleHubPeopleSummary getPeopleHubRecommendations() throws XLEException {
        Log.i(TAG, "getPeopleHubRecommendations");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall("GET", XboxLiveEnvironment.Instance().getPeopleHubRecommendationsUrlFormat(), ""), "1");
        appendCommonParameters.setCustomHeader("Accept-Language", ProjectSpecificDataProvider.getInstance().getLegalLocale());
        appendCommonParameters.setCustomHeader("X-XBL-Contract-Version", "1");
        appendCommonParameters.setCustomHeader("X-XBL-Market", ProjectSpecificDataProvider.getInstance().getRegion());
        IPeopleHubResult.PeopleHubPeopleSummary peopleHubPeopleSummary = (IPeopleHubResult.PeopleHubPeopleSummary) TcuiHttpUtil.getResponseSync(appendCommonParameters, IPeopleHubResult.PeopleHubPeopleSummary.class);
        TcuiHttpUtil.throwIfNullOrFalse(peopleHubPeopleSummary);
        return peopleHubPeopleSummary;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public PrivacySettings.PrivacySetting getPrivacySetting(PrivacySettings.PrivacySettingId privacySettingId) throws XLEException {
        Log.i(TAG, "getPrivacySetting");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        PrivacySettings.PrivacySetting privacySetting = (PrivacySettings.PrivacySetting) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", String.format(XboxLiveEnvironment.Instance().getProfileSettingUrlFormat(), privacySettingId.name()), ""), "4"), PrivacySettings.PrivacySetting.class);
        TcuiHttpUtil.throwIfNullOrFalse(privacySetting);
        return privacySetting;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public ProfilePreferredColor getProfilePreferredColor(String str) throws XLEException {
        Log.i(TAG, "getProfilePreferredColor");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        ProfilePreferredColor profilePreferredColor = (ProfilePreferredColor) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", str, ""), ExifInterface.GPS_MEASUREMENT_2D), ProfilePreferredColor.class);
        TcuiHttpUtil.throwIfNullOrFalse(profilePreferredColor);
        return profilePreferredColor;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public ProfileSummaryResultContainer.ProfileSummaryResult getProfileSummaryInfo(String str) throws XLEException {
        Log.i(TAG, "getProfileSummaryInfo");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(str));
        ProfileSummaryResultContainer.ProfileSummaryResult profileSummaryResult = (ProfileSummaryResultContainer.ProfileSummaryResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", String.format(XboxLiveEnvironment.Instance().getProfileSummaryUrlFormat(), str), ""), ExifInterface.GPS_MEASUREMENT_2D), ProfileSummaryResultContainer.ProfileSummaryResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(profileSummaryResult);
        return profileSummaryResult;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public IUserProfileResult.UserProfileResult getUserProfileInfo(String str) throws XLEException {
        Log.i(TAG, "getUserProfileInfo");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall httpCall = new HttpCall("POST", XboxLiveEnvironment.Instance().getUserProfileInfoUrl(), "");
        HttpUtil.appendCommonParameters(httpCall, "3");
        httpCall.setRequestBody(str);
        IUserProfileResult.UserProfileResult userProfileResult = (IUserProfileResult.UserProfileResult) TcuiHttpUtil.getResponseSync(httpCall, IUserProfileResult.UserProfileResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(userProfileResult);
        return userProfileResult;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public PrivacySettingsResult getUserProfilePrivacySettings() throws XLEException {
        Log.i(TAG, "getUserProfilePrivacySettings");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        PrivacySettingsResult privacySettingsResult = (PrivacySettingsResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", XboxLiveEnvironment.Instance().getUserProfileSettingUrlFormat(), ""), "4"), PrivacySettingsResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(privacySettingsResult);
        return privacySettingsResult;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean removeFriendFromShareIdentitySetting(String str, String str2) throws XLEException {
        Log.i(TAG, "removeFriendFromShareIdentitySetting");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().getRemoveUsersFromShareIdentityUrlFormat(), str), ""), "4");
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(204));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean removeUserFromFavoriteList(String str) throws XLEException {
        Log.i(TAG, "removeUserFromFavoriteList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().getProfileFavoriteListUrl(), "remove"), ""), "1");
        appendCommonParameters.setRequestBody(str);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(204));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean removeUserFromFollowingList(String str) throws XLEException {
        Log.i(TAG, "removeUserFromFollowingList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().updateProfileFollowingListUrl(), "remove"), ""), "1");
        appendCommonParameters.setRequestBody(str);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(204));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean removeUserFromMutedList(String str, String str2) throws XLEException {
        Log.i(TAG, "removeUserFromMutedList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpDelete.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), str), ""), "1");
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean removeUserFromNeverList(String str, String str2) throws XLEException {
        Log.i(TAG, "removeUserFromNeverList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpDelete.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), str), ""), "1");
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean setPrivacySettings(PrivacySettingsResult privacySettingsResult) throws XLEException {
        Log.i(TAG, "setPrivacySettings");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpPut.METHOD_NAME, XboxLiveEnvironment.Instance().getUserProfileSettingUrlFormat(), ""), "4");
        appendCommonParameters.setRequestBody(PrivacySettingsResult.getPrivacySettingRequestBody(privacySettingsResult));
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(Integer.valueOf((int) HttpStatus.SC_CREATED)));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    @Override // com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager
    public boolean submitFeedback(String str, String str2) throws XLEException {
        Log.i(TAG, "submitFeedback");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().getSubmitFeedbackUrlFormat(), str), ""), "101");
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, new ArrayList((int) HttpStatus.SC_ACCEPTED));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }
}

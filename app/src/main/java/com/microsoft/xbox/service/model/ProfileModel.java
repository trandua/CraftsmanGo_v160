package com.microsoft.xbox.service.model;

import android.util.Log;
import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.privacy.PrivacySettingsResult;
import com.microsoft.xbox.service.model.sls.AddShareIdentityRequest;
import com.microsoft.xbox.service.model.sls.FavoriteListRequest;
import com.microsoft.xbox.service.model.sls.FeedbackType;
import com.microsoft.xbox.service.model.sls.MutedListRequest;
import com.microsoft.xbox.service.model.sls.NeverListRequest;
import com.microsoft.xbox.service.model.sls.SubmitFeedbackRequest;
import com.microsoft.xbox.service.model.sls.UserProfileRequest;
import com.microsoft.xbox.service.model.sls.UserProfileSetting;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer;
import com.microsoft.xbox.service.network.managers.FamilySettings;
import com.microsoft.xbox.service.network.managers.FollowingSummaryResult;
import com.microsoft.xbox.service.network.managers.IFollowerPresenceResult;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.service.network.managers.IUserProfileResult;
import com.microsoft.xbox.service.network.managers.MutedListResultContainer;
import com.microsoft.xbox.service.network.managers.NeverListResultContainer;
import com.microsoft.xbox.service.network.managers.ProfileSummaryResultContainer;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.ShareRealNameSettingFilter;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

/* loaded from: classes3.dex */
public class ProfileModel extends ModelBase<ProfileData> {
    private static final int MAX_PROFILE_MODELS = 20;
    private static final long friendsDataLifetime = 180000;
    private static ProfileModel meProfileInstance = null;
    private static ThreadSafeFixedSizeHashtable<String, ProfileModel> profileModelCache = new ThreadSafeFixedSizeHashtable<>(20);
    private static final long profilePresenceDataLifetime = 180000;
    private AddFollowingUserResponseContainer.AddFollowingUserResponse addUserToFollowingResponse;
    private ArrayList<FollowersData> favorites;
    private String firstName;
    private ArrayList<FollowersData> following;
    private ArrayList<FollowingSummaryResult.People> followingSummaries;
    private String lastName;
    private Date lastRefreshMutedList;
    private Date lastRefreshNeverList;
    private Date lastRefreshPeopleHubRecommendations;
    private Date lastRefreshPresenceData;
    private Date lastRefreshProfileSummary;
    private MutedListResultContainer.MutedListResult mutedList;
    private NeverListResultContainer.NeverListResult neverList;
    private IPeopleHubResult.PeopleHubPersonSummary peopleHubPersonSummary;
    private ArrayList<FollowersData> peopleHubRecommendations;
    private IPeopleHubResult.PeopleHubPeopleSummary peopleHubRecommendationsRaw;
    private IFollowerPresenceResult.UserPresence presenceData;
    private SingleEntryLoadingStatus presenceDataLoadingStatus;
    private String profileImageUrl;
    private ProfileSummaryResultContainer.ProfileSummaryResult profileSummary;
    private SingleEntryLoadingStatus profileSummaryLoadingStatus;
    private IUserProfileResult.ProfileUser profileUser;
    private boolean shareRealName;
    private String shareRealNameStatus;
    private boolean sharingRealNameTransitively;
    public String xuid;
    private SingleEntryLoadingStatus addingUserToFavoriteListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToFollowingListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToMutedListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToNeverListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToShareIdentityListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus mutedListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus neverListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromFavoriteListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromFollowingListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromMutedListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromNeverListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromShareIdentityListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus submitFeedbackForUserLoadingStatus = new SingleEntryLoadingStatus();

    public void onSubmitFeedbackForUserCompleted(AsyncResult<Boolean> asyncResult) {
    }

    /* loaded from: classes3.dex */
    private class AddUserToFavoriteListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String favoriteUserXuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ADD_USER_TO_FAVORITELIST;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public AddUserToFavoriteListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.favoriteUserXuid = str;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public Boolean buildData() throws XLEException {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.favoriteUserXuid);
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToFavoriteList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(arrayList))));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onAddUserToFavoriteListCompleted(asyncResult, this.favoriteUserXuid);
        }
    }

    /* loaded from: classes3.dex */
    private class AddUserToFollowingListRunner extends IDataLoaderRunnable<AddFollowingUserResponseContainer.AddFollowingUserResponse> {
        private ProfileModel caller;
        private String followingUserXuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ADD_FRIEND;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public AddUserToFollowingListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.followingUserXuid = str;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public AddFollowingUserResponseContainer.AddFollowingUserResponse buildData() throws XLEException {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.followingUserXuid);
            return ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToFollowingList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(arrayList)));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<AddFollowingUserResponseContainer.AddFollowingUserResponse> asyncResult) {
            this.caller.onAddUserToFollowingListCompleted(asyncResult, this.followingUserXuid);
        }
    }

    /* loaded from: classes3.dex */
    private class AddUsersToShareIdentityListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private ArrayList<String> userIds;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ADD_TO_SHARE_IDENTIY;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public AddUsersToShareIdentityListRunner(ProfileModel profileModel, ArrayList<String> arrayList) {
            this.caller = profileModel;
            this.userIds = arrayList;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addFriendToShareIdentitySetting(this.caller.xuid, AddShareIdentityRequest.getAddShareIdentityRequestBody(new AddShareIdentityRequest(this.userIds))));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onAddUserToShareIdentityCompleted(asyncResult, this.userIds);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class FollowingAndFavoritesComparator implements Comparator<FollowersData> {
        private FollowingAndFavoritesComparator() {
        }

        @Override // java.util.Comparator
        public int compare(FollowersData followersData, FollowersData followersData2) {
            return followersData.userProfileData.appDisplayName.compareToIgnoreCase(followersData2.userProfileData.appDisplayName);
        }
    }

    /* loaded from: classes3.dex */
    private class GetMutedListRunner extends IDataLoaderRunnable<MutedListResultContainer.MutedListResult> {
        private ProfileModel caller;
        private String xuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_MUTED_LIST;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public GetMutedListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.xuid = str;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public MutedListResultContainer.MutedListResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getMutedListInfo(this.xuid);
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<MutedListResultContainer.MutedListResult> asyncResult) {
            this.caller.onGetMutedListCompleted(asyncResult);
        }
    }

    /* loaded from: classes3.dex */
    private class GetNeverListRunner extends IDataLoaderRunnable<NeverListResultContainer.NeverListResult> {
        private ProfileModel caller;
        private String xuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_NEVERLIST_DATA;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public GetNeverListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.xuid = str;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public NeverListResultContainer.NeverListResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getNeverListInfo(this.xuid);
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<NeverListResultContainer.NeverListResult> asyncResult) {
            this.caller.onGetNeverListCompleted(asyncResult);
        }
    }

    /* loaded from: classes3.dex */
    private class GetPeopleHubRecommendationRunner extends IDataLoaderRunnable<IPeopleHubResult.PeopleHubPeopleSummary> {
        private ProfileModel caller;
        private String xuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return 11L;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public GetPeopleHubRecommendationRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.xuid = str;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public IPeopleHubResult.PeopleHubPeopleSummary buildData() throws XLEException {
            IPeopleHubResult.PeopleHubPeopleSummary peopleHubPeopleSummary = new IPeopleHubResult.PeopleHubPeopleSummary();
            return (JavaUtil.isNullOrEmpty(this.xuid) || !this.xuid.equalsIgnoreCase(ProjectSpecificDataProvider.getInstance().getXuidString())) ? peopleHubPeopleSummary : ServiceManagerFactory.getInstance().getSLSServiceManager().getPeopleHubRecommendations();
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<IPeopleHubResult.PeopleHubPeopleSummary> asyncResult) {
            this.caller.onGetPeopleHubRecommendationsCompleted(asyncResult);
        }
    }

    /* loaded from: classes3.dex */
    private class GetPresenceDataRunner extends IDataLoaderRunnable<IFollowerPresenceResult.UserPresence> {
        private ProfileModel caller;
        private String xuid;

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public IFollowerPresenceResult.UserPresence buildData() throws XLEException {
            return null;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_PROFILE_PRESENCE_DATA;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public GetPresenceDataRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.xuid = str;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<IFollowerPresenceResult.UserPresence> asyncResult) {
            this.caller.onGetPresenceDataCompleted(asyncResult);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class GetProfileRunner extends IDataLoaderRunnable<ProfileData> {
        private ProfileModel caller;
        private boolean loadEssentialsOnly;
        public String xuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_USER_PROFILE_INFO;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public GetProfileRunner(ProfileModel profileModel, String str, boolean z) {
            this.caller = profileModel;
            this.xuid = str;
            this.loadEssentialsOnly = z;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public ProfileData buildData() throws XLEException {
            boolean z = false;
            final ISLSServiceManager sLSServiceManager = ServiceManagerFactory.getInstance().getSLSServiceManager();
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.xuid);
            IUserProfileResult.UserProfileResult userProfileInfo = sLSServiceManager.getUserProfileInfo(UserProfileRequest.getUserProfileRequestBody(new UserProfileRequest(arrayList, this.loadEssentialsOnly)));
            boolean z2 = false;
            if (ProjectSpecificDataProvider.getInstance().getXuidString().equalsIgnoreCase(this.xuid)) {
                if (userProfileInfo != null && userProfileInfo.profileUsers != null && userProfileInfo.profileUsers.size() > 0) {
                    final IUserProfileResult.ProfileUser profileUser = userProfileInfo.profileUsers.get(0);
                    profileUser.setPrivilieges(sLSServiceManager.getXTokenPrivileges());
                    try {
                        String settingValue = profileUser.getSettingValue(UserProfileSetting.PreferredColor);
                        if (settingValue != null && settingValue.length() > 0) {
                            profileUser.colors = sLSServiceManager.getProfilePreferredColor(settingValue);
                        }
                    } catch (Throwable unused) {
                    }
                    XLEThreadPool.networkOperationsThreadPool.run(new Runnable() { // from class: com.microsoft.xbox.service.model.ProfileModel.GetProfileRunner.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                FamilySettings familySettings = sLSServiceManager.getFamilySettings(GetProfileRunner.this.xuid);
                                if (familySettings == null || familySettings.familyUsers == null) {
                                    return;
                                }
                                for (int i = 0; i < familySettings.familyUsers.size(); i++) {
                                    if (familySettings.familyUsers.get(i).xuid.equalsIgnoreCase(GetProfileRunner.this.xuid)) {
                                        profileUser.canViewTVAdultContent = familySettings.familyUsers.get(i).canViewTVAdultContent;
                                        profileUser.setmaturityLevel(familySettings.familyUsers.get(i).maturityLevel);
                                        return;
                                    }
                                }
                            } catch (Throwable unused2) {
                            }
                        }
                    });
                }
            } else if (userProfileInfo != null && userProfileInfo.profileUsers != null && userProfileInfo.profileUsers.size() > 0) {
                IUserProfileResult.ProfileUser profileUser2 = userProfileInfo.profileUsers.get(0);
                try {
                    String settingValue2 = profileUser2.getSettingValue(UserProfileSetting.PreferredColor);
                    if (settingValue2 != null && settingValue2.length() > 0) {
                        profileUser2.colors = sLSServiceManager.getProfilePreferredColor(settingValue2);
                    }
                } catch (Throwable unused2) {
                }
            }
            String str = this.xuid;
            String str2 = null;
            if (str != null && str.compareToIgnoreCase(ProjectSpecificDataProvider.getInstance().getXuidString()) == 0) {
                try {
                    PrivacySettingsResult userProfilePrivacySettings = sLSServiceManager.getUserProfilePrivacySettings();
                    str2 = userProfilePrivacySettings.getShareRealNameStatus();
                    boolean z3 = ShareRealNameSettingFilter.Blocked.toString().compareTo(str2) != 0;
                    try {
                        z2 = userProfilePrivacySettings.getSharingRealNameTransitively();
                    } catch (Exception unused3) {
                    }
                    z = z2;
                    z2 = z3;
                } catch (Exception unused4) {
                }
                return new ProfileData(userProfileInfo, z2, str2, z);
            }
            z = false;
            return new ProfileData(userProfileInfo, z2, str2, z);
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<ProfileData> asyncResult) {
            this.caller.updateWithProfileData(asyncResult, this.loadEssentialsOnly);
        }
    }

    /* loaded from: classes3.dex */
    private class GetProfileSummaryRunner extends IDataLoaderRunnable<ProfileSummaryResultContainer.ProfileSummaryResult> {
        private ProfileModel caller;
        private String xuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_USER_PROFILE_INFO;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public GetProfileSummaryRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.xuid = str;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public ProfileSummaryResultContainer.ProfileSummaryResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getProfileSummaryInfo(this.xuid);
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<ProfileSummaryResultContainer.ProfileSummaryResult> asyncResult) {
            this.caller.onGetProfileSummaryCompleted(asyncResult);
        }
    }

    /* loaded from: classes3.dex */
    private class PutUserToMutedListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String mutedUserXuid;
        private String xuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_MUTE_USER;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public PutUserToMutedListRunner(ProfileModel profileModel, String str, String str2) {
            this.caller = profileModel;
            this.xuid = str;
            this.mutedUserXuid = str2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToMutedList(this.xuid, MutedListRequest.getNeverListRequestBody(new MutedListRequest(Long.parseLong(this.mutedUserXuid)))));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onPutUserToMutedListCompleted(asyncResult, this.mutedUserXuid);
        }
    }

    /* loaded from: classes3.dex */
    private class PutUserToNeverListRunner extends IDataLoaderRunnable<Boolean> {
        private String blockUserXuid;
        private ProfileModel caller;
        private String xuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_BLOCK_USER;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public PutUserToNeverListRunner(ProfileModel profileModel, String str, String str2) {
            this.caller = profileModel;
            this.xuid = str;
            this.blockUserXuid = str2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToNeverList(this.xuid, NeverListRequest.getNeverListRequestBody(new NeverListRequest(Long.parseLong(this.blockUserXuid)))));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onPutUserToNeverListCompleted(asyncResult, this.blockUserXuid);
        }
    }

    /* loaded from: classes3.dex */
    private class RemoveUserFromFavoriteListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String favoriteUserXuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_USER_FROM_FAVORITELIST;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public RemoveUserFromFavoriteListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.favoriteUserXuid = str;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public Boolean buildData() throws XLEException {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.favoriteUserXuid);
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromFavoriteList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(arrayList))));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onRemoveUserFromFavoriteListCompleted(asyncResult, this.favoriteUserXuid);
        }
    }

    /* loaded from: classes3.dex */
    private class RemoveUserFromFollowingListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String followingUserXuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_FRIEND;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public RemoveUserFromFollowingListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.followingUserXuid = str;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public Boolean buildData() throws XLEException {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.followingUserXuid);
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromFollowingList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(arrayList))));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onRemoveUserFromFollowingListCompleted(asyncResult, this.followingUserXuid);
        }
    }

    /* loaded from: classes3.dex */
    private class RemoveUserFromMutedListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String unmutedUserXuid;
        private String xuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_UNMUTE_USER;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public RemoveUserFromMutedListRunner(ProfileModel profileModel, String str, String str2) {
            this.caller = profileModel;
            this.xuid = str;
            this.unmutedUserXuid = str2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromMutedList(this.xuid, MutedListRequest.getNeverListRequestBody(new MutedListRequest(Long.parseLong(this.unmutedUserXuid)))));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onRemoveUserFromMutedListCompleted(asyncResult, this.unmutedUserXuid);
        }
    }

    /* loaded from: classes3.dex */
    private class RemoveUserFromNeverListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String unblockUserXuid;
        private String xuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_USER_FROM_NEVERLIST;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public RemoveUserFromNeverListRunner(ProfileModel profileModel, String str, String str2) {
            this.caller = profileModel;
            this.xuid = str;
            this.unblockUserXuid = str2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromNeverList(this.xuid, NeverListRequest.getNeverListRequestBody(new NeverListRequest(Long.parseLong(this.unblockUserXuid)))));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onRemoveUserFromNeverListCompleted(asyncResult, this.unblockUserXuid);
        }
    }

    /* loaded from: classes3.dex */
    private class RemoveUsersFromShareIdentityListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private ArrayList<String> userIds;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_FROM_SHARE_IDENTIY;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public RemoveUsersFromShareIdentityListRunner(ProfileModel profileModel, ArrayList<String> arrayList) {
            this.caller = profileModel;
            this.userIds = arrayList;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeFriendFromShareIdentitySetting(this.caller.xuid, AddShareIdentityRequest.getAddShareIdentityRequestBody(new AddShareIdentityRequest(this.userIds))));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onRemoveUserFromShareIdentityCompleted(asyncResult, this.userIds);
        }
    }

    /* loaded from: classes3.dex */
    private class SubmitFeedbackForUserRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private FeedbackType feedbackType;
        private String textReason;
        private String xuid;

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_SUBMIT_FEEDBACK;
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPreExecute() {
        }

        public SubmitFeedbackForUserRunner(ProfileModel profileModel, String str, FeedbackType feedbackType, String str2) {
            this.caller = profileModel;
            this.xuid = str;
            this.feedbackType = feedbackType;
            this.textReason = str2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().submitFeedback(this.xuid, SubmitFeedbackRequest.getSubmitFeedbackRequestBody(new SubmitFeedbackRequest(Long.parseLong(this.xuid), null, this.feedbackType, this.textReason, null, null))));
        }

        @Override // com.microsoft.xbox.toolkit.network.IDataLoaderRunnable
        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onSubmitFeedbackForUserCompleted(asyncResult);
        }
    }

    private ProfileModel(String str) {
        this.xuid = str;
    }

    private void buildRecommendationsList(boolean z) {
        ArrayList<FollowersData> arrayList = new ArrayList<>();
        this.peopleHubRecommendations = arrayList;
        if (z) {
            arrayList.add(0, new RecommendationsPeopleData(true, FollowersData.DummyType.DUMMY_LINK_TO_FACEBOOK));
        }
        IPeopleHubResult.PeopleHubPeopleSummary peopleHubPeopleSummary = this.peopleHubRecommendationsRaw;
        if (peopleHubPeopleSummary == null || XLEUtil.isNullOrEmpty(peopleHubPeopleSummary.people)) {
            return;
        }
        Iterator<IPeopleHubResult.PeopleHubPersonSummary> it = this.peopleHubRecommendationsRaw.people.iterator();
        while (it.hasNext()) {
            this.peopleHubRecommendations.add(new RecommendationsPeopleData(it.next()));
        }
    }

    public static int getDefaultColor() {
        return XboxTcuiSdk.getResources().getColor(XLERValueHelper.getColorRValue("XboxOneGreen"));
    }

    public static ProfileModel getMeProfileModel() {
        if (ProjectSpecificDataProvider.getInstance().getXuidString() == null) {
            return null;
        }
        if (meProfileInstance == null) {
            meProfileInstance = new ProfileModel(ProjectSpecificDataProvider.getInstance().getXuidString());
        }
        return meProfileInstance;
    }

    private String getProfileImageUrl() {
        String str = this.profileImageUrl;
        if (str != null) {
            return str;
        }
        String profileSettingValue = getProfileSettingValue(UserProfileSetting.GameDisplayPicRaw);
        this.profileImageUrl = profileSettingValue;
        return profileSettingValue;
    }

    public static ProfileModel getProfileModel(String str) {
        if (JavaUtil.isNullOrEmpty(str)) {
            throw new IllegalArgumentException();
        }
        if (JavaUtil.stringsEqualCaseInsensitive(str, ProjectSpecificDataProvider.getInstance().getXuidString())) {
            if (meProfileInstance == null) {
                meProfileInstance = new ProfileModel(str);
            }
            return meProfileInstance;
        }
        ProfileModel profileModel = profileModelCache.get(str);
        if (profileModel != null) {
            return profileModel;
        }
        ProfileModel profileModel2 = new ProfileModel(str);
        profileModelCache.put(str, profileModel2);
        return profileModel2;
    }

    private String getProfileSettingValue(UserProfileSetting userProfileSetting) {
        IUserProfileResult.ProfileUser profileUser = this.profileUser;
        if (profileUser == null || profileUser.settings == null) {
            return null;
        }
        Iterator<IUserProfileResult.Settings> it = this.profileUser.settings.iterator();
        while (it.hasNext()) {
            IUserProfileResult.Settings next = it.next();
            if (next.f12612id != null && next.f12612id.equals(userProfileSetting.toString())) {
                return next.value;
            }
        }
        return null;
    }

    private static boolean hasPrivilege(String str) {
        String privileges = ProjectSpecificDataProvider.getInstance().getPrivileges();
        return !JavaUtil.isNullOrEmpty(privileges) && privileges.contains(str);
    }

    public static boolean hasPrivilegeToAddFriend() {
        return hasPrivilege(XPrivilegeConstants.XPRIVILEGE_ADD_FRIEND);
    }

    public static boolean hasPrivilegeToSendMessage() {
        return hasPrivilege(XPrivilegeConstants.XPRIVILEGE_COMMUNICATIONS);
    }

    public static boolean isMeXuid(String str) {
        String xuidString = ProjectSpecificDataProvider.getInstance().getXuidString();
        return (xuidString == null || str == null || str.compareToIgnoreCase(xuidString) != 0) ? false : true;
    }

    public void onAddUserToFavoriteListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && asyncResult.getResult().booleanValue() && this.following != null) {
            ArrayList<FollowersData> arrayList = new ArrayList<>();
            Iterator<FollowersData> it = this.following.iterator();
            while (it.hasNext()) {
                FollowersData next = it.next();
                if (next.xuid.equals(str)) {
                    next.isFavorite = true;
                }
                if (next.isFavorite) {
                    arrayList.add(next);
                }
            }
            Collections.sort(arrayList, new FollowingAndFavoritesComparator());
            this.favorites = arrayList;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, null));
        }
    }

    public void onAddUserToFollowingListCompleted(AsyncResult<AddFollowingUserResponseContainer.AddFollowingUserResponse> asyncResult, String str) {
        AddFollowingUserResponseContainer.AddFollowingUserResponse addFollowingUserResponse;
        boolean z;
        ProfileModel profileModel = getProfileModel(str);
        XLEAssert.assertNotNull(profileModel);
        this.addUserToFollowingResponse = asyncResult.getResult();
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && (addFollowingUserResponse = this.addUserToFollowingResponse) != null && addFollowingUserResponse.getAddFollowingRequestStatus()) {
            ArrayList<FollowersData> arrayList = new ArrayList<>();
            ArrayList<FollowersData> arrayList2 = this.following;
            if (arrayList2 != null) {
                Iterator<FollowersData> it = arrayList2.iterator();
                z = false;
                while (it.hasNext()) {
                    FollowersData next = it.next();
                    arrayList.add(next);
                    if (next.xuid.equals(str)) {
                        z = true;
                    }
                }
            } else {
                z = false;
            }
            if (!z) {
                FollowersData followersData = new FollowersData();
                followersData.xuid = str;
                followersData.isFavorite = false;
                followersData.status = UserStatus.Offline;
                followersData.userProfileData = new UserProfileData();
                followersData.userProfileData.accountTier = profileModel.getAccountTier();
                followersData.userProfileData.appDisplayName = profileModel.getAppDisplayName();
                followersData.userProfileData.gamerScore = profileModel.getGamerScore();
                followersData.userProfileData.gamerTag = profileModel.getGamerTag();
                followersData.userProfileData.profileImageUrl = profileModel.getProfileImageUrl();
                arrayList.add(followersData);
                Collections.sort(arrayList, new FollowingAndFavoritesComparator());
            }
            this.following = arrayList;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, null));
        } else if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && (this.addUserToFollowingResponse.code == 1028 || this.addUserToFollowingResponse.getAddFollowingRequestStatus())) {
        } else {
            this.addUserToFollowingResponse = null;
        }
    }

    public void onAddUserToShareIdentityCompleted(AsyncResult<Boolean> asyncResult, ArrayList<String> arrayList) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && asyncResult.getResult().booleanValue()) {
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                ProfileSummaryResultContainer.ProfileSummaryResult profileSummaryData = getProfileModel(it.next()).getProfileSummaryData();
                if (profileSummaryData != null) {
                    profileSummaryData.hasCallerMarkedTargetAsIdentityShared = true;
                }
            }
            ProfileModel meProfileModel = getMeProfileModel();
            ArrayList<FollowingSummaryResult.People> profileFollowingSummaryData = meProfileModel.getProfileFollowingSummaryData();
            if (XLEUtil.isNullOrEmpty(profileFollowingSummaryData)) {
                return;
            }
            Iterator<String> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                String next = it2.next();
                Iterator<FollowingSummaryResult.People> it3 = profileFollowingSummaryData.iterator();
                while (true) {
                    if (!it3.hasNext()) {
                        break;
                    }
                    FollowingSummaryResult.People next2 = it3.next();
                    if (next2.xuid.equalsIgnoreCase(next)) {
                        next2.isIdentityShared = true;
                        break;
                    }
                }
            }
            meProfileModel.setProfileFollowingSummaryData(profileFollowingSummaryData);
        }
    }

    public void onGetMutedListCompleted(AsyncResult<MutedListResultContainer.MutedListResult> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            MutedListResultContainer.MutedListResult result = asyncResult.getResult();
            this.lastRefreshMutedList = new Date();
            if (result != null) {
                this.mutedList = result;
            } else {
                this.mutedList = new MutedListResultContainer.MutedListResult();
            }
        }
    }

    public void onGetNeverListCompleted(AsyncResult<NeverListResultContainer.NeverListResult> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            NeverListResultContainer.NeverListResult result = asyncResult.getResult();
            this.lastRefreshNeverList = new Date();
            if (result != null) {
                this.neverList = result;
            } else {
                this.neverList = new NeverListResultContainer.NeverListResult();
            }
        }
    }

    private void onGetPeopleHubPersonDataCompleted(AsyncResult<IPeopleHubResult.PeopleHubPersonSummary> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            this.peopleHubPersonSummary = asyncResult.getResult();
        }
    }

    public void onGetPeopleHubRecommendationsCompleted(AsyncResult<IPeopleHubResult.PeopleHubPeopleSummary> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            IPeopleHubResult.PeopleHubPeopleSummary result = asyncResult.getResult();
            if (result == null) {
                this.peopleHubRecommendationsRaw = null;
                this.peopleHubRecommendations = null;
                return;
            }
            this.peopleHubRecommendationsRaw = result;
            this.lastRefreshPeopleHubRecommendations = new Date();
        }
    }

    public void onGetPresenceDataCompleted(AsyncResult<IFollowerPresenceResult.UserPresence> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            this.lastRefreshPresenceData = new Date();
            this.presenceData = asyncResult.getResult();
        }
    }

    public void onGetProfileSummaryCompleted(AsyncResult<ProfileSummaryResultContainer.ProfileSummaryResult> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            this.lastRefreshProfileSummary = new Date();
            this.profileSummary = asyncResult.getResult();
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.ActivityAlertsSummary, true), this, null));
        }
    }

    public void onPutUserToMutedListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && asyncResult.getResult().booleanValue()) {
            if (this.mutedList == null) {
                this.mutedList = new MutedListResultContainer.MutedListResult();
            }
            if (this.mutedList.contains(str)) {
                return;
            }
            this.mutedList.add(str);
        }
    }

    public void onPutUserToNeverListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && asyncResult.getResult().booleanValue()) {
            if (this.neverList == null) {
                this.neverList = new NeverListResultContainer.NeverListResult();
            }
            if (this.neverList.contains(str)) {
                return;
            }
            this.neverList.add(str);
        }
    }

    public void onRemoveUserFromFavoriteListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && asyncResult.getResult().booleanValue() && this.following != null) {
            ArrayList<FollowersData> arrayList = new ArrayList<>();
            Iterator<FollowersData> it = this.following.iterator();
            while (it.hasNext()) {
                FollowersData next = it.next();
                if (next.xuid.equals(str)) {
                    next.isFavorite = false;
                }
                if (next.isFavorite) {
                    arrayList.add(next);
                }
            }
            this.favorites = arrayList;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, null));
        }
    }

    public void onRemoveUserFromFollowingListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && asyncResult.getResult().booleanValue() && this.following != null) {
            ArrayList<FollowersData> arrayList = new ArrayList<>();
            ArrayList<FollowersData> arrayList2 = new ArrayList<>();
            Iterator<FollowersData> it = this.following.iterator();
            while (it.hasNext()) {
                FollowersData next = it.next();
                if (!next.xuid.equals(str)) {
                    arrayList.add(next);
                    if (next.isFavorite) {
                        arrayList2.add(next);
                    }
                }
            }
            this.following = arrayList;
            this.favorites = arrayList2;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, null));
        }
    }

    public void onRemoveUserFromMutedListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        MutedListResultContainer.MutedListResult mutedListResult;
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && asyncResult.getResult().booleanValue() && (mutedListResult = this.mutedList) != null && mutedListResult.contains(str)) {
            this.mutedList.remove(str);
        }
    }

    public void onRemoveUserFromNeverListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        NeverListResultContainer.NeverListResult neverListResult;
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && asyncResult.getResult().booleanValue() && (neverListResult = this.neverList) != null && neverListResult.contains(str)) {
            this.neverList.remove(str);
        }
    }

    public void onRemoveUserFromShareIdentityCompleted(AsyncResult<Boolean> asyncResult, ArrayList<String> arrayList) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && asyncResult.getResult().booleanValue()) {
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                ProfileSummaryResultContainer.ProfileSummaryResult profileSummaryData = getProfileModel(it.next()).getProfileSummaryData();
                if (profileSummaryData != null) {
                    profileSummaryData.hasCallerMarkedTargetAsIdentityShared = false;
                }
            }
            ProfileModel meProfileModel = getMeProfileModel();
            ArrayList<FollowingSummaryResult.People> profileFollowingSummaryData = meProfileModel.getProfileFollowingSummaryData();
            if (XLEUtil.isNullOrEmpty(profileFollowingSummaryData)) {
                return;
            }
            Iterator<String> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                String next = it2.next();
                Iterator<FollowingSummaryResult.People> it3 = profileFollowingSummaryData.iterator();
                while (true) {
                    if (!it3.hasNext()) {
                        break;
                    }
                    FollowingSummaryResult.People next2 = it3.next();
                    if (next2.xuid.equalsIgnoreCase(next)) {
                        next2.isIdentityShared = false;
                        break;
                    }
                }
            }
            meProfileModel.setProfileFollowingSummaryData(profileFollowingSummaryData);
        }
    }

    public static void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        Enumeration<ProfileModel> elements = profileModelCache.elements();
        while (elements.hasMoreElements()) {
            elements.nextElement().clearObserver();
        }
        ProfileModel profileModel = meProfileInstance;
        if (profileModel != null) {
            profileModel.clearObserver();
            meProfileInstance = null;
        }
        profileModelCache = new ThreadSafeFixedSizeHashtable<>(20);
    }

    public void updateWithProfileData(AsyncResult<ProfileData> asyncResult, boolean z) {
        updateWithNewData(asyncResult);
        if (z) {
            invalidateData();
        }
    }

    public AsyncResult<Boolean> addUserToFavoriteList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.load(z, this.lifetime, null, this.addingUserToFavoriteListLoadingStatus, new AddUserToFavoriteListRunner(this, str));
    }

    public AsyncResult<AddFollowingUserResponseContainer.AddFollowingUserResponse> addUserToFollowingList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.load(z, this.lifetime, null, this.addingUserToFollowingListLoadingStatus, new AddUserToFollowingListRunner(this, str));
    }

    public AsyncResult<Boolean> addUserToMutedList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.load(z, this.lifetime, null, this.addingUserToMutedListLoadingStatus, new PutUserToMutedListRunner(this, this.xuid, str));
    }

    public AsyncResult<Boolean> addUserToNeverList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.load(z, this.lifetime, null, this.addingUserToNeverListLoadingStatus, new PutUserToNeverListRunner(this, this.xuid, str));
    }

    public AsyncResult<Boolean> addUserToShareIdentity(boolean z, ArrayList<String> arrayList) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.load(z, this.lifetime, null, this.addingUserToShareIdentityListLoadingStatus, new AddUsersToShareIdentityListRunner(this, arrayList));
    }

    public String getAccountTier() {
        return getProfileSettingValue(UserProfileSetting.AccountTier);
    }

    public AddFollowingUserResponseContainer.AddFollowingUserResponse getAddUserToFollowingResult() {
        return this.addUserToFollowingResponse;
    }

    public String getAppDisplayName() {
        return getProfileSettingValue(UserProfileSetting.AppDisplayName);
    }

    public String getBio() {
        return getProfileSettingValue(UserProfileSetting.Bio);
    }

    public ArrayList<FollowersData> getFavorites() {
        return this.favorites;
    }

    public ArrayList<FollowersData> getFollowingData() {
        return this.following;
    }

    public String getGamerPicImageUrl() {
        return getProfileImageUrl();
    }

    public String getGamerScore() {
        return getProfileSettingValue(UserProfileSetting.Gamerscore);
    }

    public String getGamerTag() {
        return getProfileSettingValue(UserProfileSetting.Gamertag);
    }

    public String getLocation() {
        return getProfileSettingValue(UserProfileSetting.Location);
    }

    public int getMaturityLevel() {
        IUserProfileResult.ProfileUser profileUser = this.profileUser;
        if (profileUser != null) {
            return profileUser.getMaturityLevel();
        }
        return 0;
    }

    public MutedListResultContainer.MutedListResult getMutedList() {
        return this.mutedList;
    }

    public NeverListResultContainer.NeverListResult getNeverListData() {
        return this.neverList;
    }

    public int getNumberOfFollowers() {
        ProfileSummaryResultContainer.ProfileSummaryResult profileSummaryResult = this.profileSummary;
        if (profileSummaryResult != null) {
            return profileSummaryResult.targetFollowerCount;
        }
        return 0;
    }

    public int getNumberOfFollowing() {
        ProfileSummaryResultContainer.ProfileSummaryResult profileSummaryResult = this.profileSummary;
        if (profileSummaryResult != null) {
            return profileSummaryResult.targetFollowingCount;
        }
        return 0;
    }

    public IPeopleHubResult.PeopleHubPersonSummary getPeopleHubPersonSummary() {
        return this.peopleHubPersonSummary;
    }

    public IPeopleHubResult.PeopleHubPeopleSummary getPeopleHubRecommendationsRawData() {
        return this.peopleHubRecommendationsRaw;
    }

    public int getPreferedColor() {
        IUserProfileResult.ProfileUser profileUser = this.profileUser;
        return (profileUser == null || profileUser.colors == null) ? getDefaultColor() : this.profileUser.colors.getPrimaryColor();
    }

    public IFollowerPresenceResult.UserPresence getPresenceData() {
        return this.presenceData;
    }

    public ArrayList<FollowingSummaryResult.People> getProfileFollowingSummaryData() {
        return this.followingSummaries;
    }

    public ProfileSummaryResultContainer.ProfileSummaryResult getProfileSummaryData() {
        return this.profileSummary;
    }

    public String getRealName() {
        if (this.shareRealName) {
            return getProfileSettingValue(UserProfileSetting.RealName);
        }
        return null;
    }

    public String getShareRealNameStatus() {
        return this.shareRealNameStatus;
    }

    public ArrayList<URI> getWatermarkUris() {
        String[] split;
        ArrayList<URI> arrayList = new ArrayList<>();
        String profileSettingValue = getProfileSettingValue(UserProfileSetting.TenureLevel);
        if (!JavaUtil.isNullOrEmpty(profileSettingValue) && !profileSettingValue.equalsIgnoreCase("0")) {
            try {
                String tenureWatermarkUrlFormat = XboxLiveEnvironment.Instance().getTenureWatermarkUrlFormat();
                if (profileSettingValue.length() == 1) {
                    profileSettingValue = "0" + profileSettingValue;
                }
                arrayList.add(new URI(String.format(tenureWatermarkUrlFormat, profileSettingValue)));
            } catch (URISyntaxException e) {
                XLEAssert.fail("Failed to create URI for tenure watermark: " + e.toString());
            }
        }
        String profileSettingValue2 = getProfileSettingValue(UserProfileSetting.Watermarks);
        if (!JavaUtil.isNullOrEmpty(profileSettingValue2)) {
            for (String str : profileSettingValue2.split("\\|")) {
                try {
                    arrayList.add(new URI(XboxLiveEnvironment.Instance().getWatermarkUrl(str)));
                } catch (URISyntaxException e2) {
                    XLEAssert.fail("Failed to create URI for watermark " + str + " : " + e2.toString());
                }
            }
        }
        return arrayList;
    }

    public String getXuid() {
        return this.xuid;
    }

    public boolean hasCallerMarkedTargetAsFavorite() {
        ProfileSummaryResultContainer.ProfileSummaryResult profileSummaryResult = this.profileSummary;
        return profileSummaryResult != null && profileSummaryResult.hasCallerMarkedTargetAsFavorite;
    }

    public boolean hasCallerMarkedTargetAsIdentityShared() {
        ProfileSummaryResultContainer.ProfileSummaryResult profileSummaryResult = this.profileSummary;
        return profileSummaryResult != null && profileSummaryResult.hasCallerMarkedTargetAsIdentityShared;
    }

    public boolean isCallerFollowingTarget() {
        ProfileSummaryResultContainer.ProfileSummaryResult profileSummaryResult = this.profileSummary;
        return profileSummaryResult != null && profileSummaryResult.isCallerFollowingTarget;
    }

    public boolean isMeProfile() {
        return isMeXuid(this.xuid);
    }

    public boolean isTargetFollowingCaller() {
        ProfileSummaryResultContainer.ProfileSummaryResult profileSummaryResult = this.profileSummary;
        return profileSummaryResult != null && profileSummaryResult.isTargetFollowingCaller;
    }

    public void loadAsync(boolean z) {
        loadInternal(z, UpdateType.MeProfileData, new GetProfileRunner(this, this.xuid, false));
    }

    public AsyncResult<IPeopleHubResult.PeopleHubPeopleSummary> loadPeopleHubRecommendations(boolean z) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.load(z, 180000L, this.lastRefreshPeopleHubRecommendations, new SingleEntryLoadingStatus(), new GetPeopleHubRecommendationRunner(this, this.xuid));
    }

    public AsyncResult<IFollowerPresenceResult.UserPresence> loadPresenceData(boolean z) {
        if (this.presenceDataLoadingStatus == null) {
            this.presenceDataLoadingStatus = new SingleEntryLoadingStatus();
        }
        return DataLoadUtil.load(z, 180000L, this.lastRefreshPresenceData, this.presenceDataLoadingStatus, new GetPresenceDataRunner(this, this.xuid));
    }

    public AsyncResult<ProfileSummaryResultContainer.ProfileSummaryResult> loadProfileSummary(boolean z) {
        if (this.profileSummaryLoadingStatus == null) {
            this.profileSummaryLoadingStatus = new SingleEntryLoadingStatus();
        }
        return DataLoadUtil.load(z, this.lifetime, this.lastRefreshProfileSummary, this.profileSummaryLoadingStatus, new GetProfileSummaryRunner(this, this.xuid));
    }

    public AsyncResult<ProfileData> loadSync(boolean z) {
        return loadSync(z, false);
    }

    public AsyncResult<ProfileData> loadSync(boolean z, boolean z2) {
        return super.loadData(z, new GetProfileRunner(this, this.xuid, z2));
    }

    public AsyncResult<MutedListResultContainer.MutedListResult> loadUserMutedList(boolean z) {
        return DataLoadUtil.load(z, this.lifetime, this.lastRefreshMutedList, this.mutedListLoadingStatus, new GetMutedListRunner(this, this.xuid));
    }

    public AsyncResult<NeverListResultContainer.NeverListResult> loadUserNeverList(boolean z) {
        return DataLoadUtil.load(z, this.lifetime, this.lastRefreshNeverList, this.neverListLoadingStatus, new GetNeverListRunner(this, this.xuid));
    }

    public AsyncResult<Boolean> removeUserFromFavoriteList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.load(z, this.lifetime, null, this.removingUserFromFavoriteListLoadingStatus, new RemoveUserFromFavoriteListRunner(this, str));
    }

    public AsyncResult<Boolean> removeUserFromFollowingList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.load(z, this.lifetime, null, this.removingUserFromFollowingListLoadingStatus, new RemoveUserFromFollowingListRunner(this, str));
    }

    public AsyncResult<Boolean> removeUserFromMutedList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.load(z, this.lifetime, null, this.removingUserFromMutedListLoadingStatus, new RemoveUserFromMutedListRunner(this, this.xuid, str));
    }

    public AsyncResult<Boolean> removeUserFromNeverList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.load(z, this.lifetime, null, this.removingUserFromNeverListLoadingStatus, new RemoveUserFromNeverListRunner(this, this.xuid, str));
    }

    public AsyncResult<Boolean> removeUserFromShareIdentity(boolean z, ArrayList<String> arrayList) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.load(z, this.lifetime, null, this.removingUserFromShareIdentityListLoadingStatus, new RemoveUsersFromShareIdentityListRunner(this, arrayList));
    }

    public void setFirstName(String str) {
        this.firstName = str;
    }

    public void setLastName(String str) {
        this.lastName = str;
    }

    public void setProfileFollowingSummaryData(ArrayList<FollowingSummaryResult.People> arrayList) {
        this.followingSummaries = arrayList;
    }

    public boolean shouldRefreshPresenceData() {
        return XLEUtil.shouldRefresh(this.lastRefreshPresenceData, this.lifetime);
    }

    public boolean shouldRefreshProfileSummary() {
        return XLEUtil.shouldRefresh(this.lastRefreshProfileSummary, this.lifetime);
    }

    public AsyncResult<Boolean> submitFeedbackForUser(boolean z, FeedbackType feedbackType, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.load(z, this.lifetime, null, this.submitFeedbackForUserLoadingStatus, new SubmitFeedbackForUserRunner(this, this.xuid, feedbackType, str));
    }

    @Override // com.microsoft.xbox.service.model.ModelBase, com.microsoft.xbox.toolkit.ModelData
    public void updateWithNewData(AsyncResult<ProfileData> asyncResult) {
        ProfileData result;
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        super.updateWithNewData(asyncResult);
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && (result = asyncResult.getResult()) != null) {
            this.shareRealName = isMeProfile() ? result.getShareRealName() : true;
            this.shareRealNameStatus = result.getShareRealNameStatus();
            Log.i("ProfileModel", "shareRealNameStatus: " + this.shareRealNameStatus);
            this.sharingRealNameTransitively = result.getSharingRealNameTransitively();
            IUserProfileResult.UserProfileResult profileResult = result.getProfileResult();
            if (profileResult != null && profileResult.profileUsers != null) {
                this.profileUser = profileResult.profileUsers.get(0);
                this.profileImageUrl = null;
            }
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.ProfileData, true), this, asyncResult.getException()));
    }
}

package com.microsoft.xbox.xle.app.activity.Profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
//import com.crafting.minecrafting.lokicraft.R;
import com.craftsman.go.R;
import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer;
import com.microsoft.xbox.service.network.managers.MutedListResultContainer;
import com.microsoft.xbox.service.network.managers.NeverListResultContainer;
import com.microsoft.xbox.telemetry.helpers.UTCChangeRelationship;
import com.microsoft.xbox.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.SGProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.activity.ReportUserScreen;
import com.microsoft.xbox.xle.app.adapter.ProfileScreenAdapter;
import com.microsoft.xbox.xle.viewmodel.ChangeFriendshipDialogViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import com.microsoft.xboxtcui.XboxAppDeepLinker;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: classes3.dex */
public class ProfileScreenViewModel extends ViewModelBase {
    private static final String TAG = "ProfileScreenViewModel";
    private AddUserToFollowingListAsyncTask addUserToFollowingListAsyncTask;
    private AddUserToMutedListAsyncTask addUserToMutedListAsyncTask;
    private AddUserToNeverListAsyncTask addUserToNeverListAsyncTask;
    private AddUserToShareIdentityListAsyncTask addUserToShareIdentityListAsyncTask;
    private FollowersData basicData;
    private ChangeFriendshipDialogViewModel changeFriendshipDialogViewModel;
    private HashSet<ChangeFriendshipFormOptions> changeFriendshipForm;
    public boolean isAddingUserToBlockList;
    public boolean isAddingUserToFollowingList;
    public boolean isAddingUserToMutedList;
    public boolean isAddingUserToShareIdentityList;
    private boolean isBlocked;
    private boolean isFavorite;
    private boolean isFollowing;
    public boolean isLoadingUserMutedList;
    public boolean isLoadingUserNeverList;
    public boolean isLoadingUserProfile;
    private boolean isMuted;
    public boolean isRemovingUserFromBlockList;
    public boolean isRemovingUserFromMutedList;
    private boolean isShowingFailureDialog;
    private LoadUserProfileAsyncTask loadMeProfileTask;
    private LoadUserMutedListAsyncTask loadUserMutedListTask;
    private LoadUserNeverListAsyncTask loadUserNeverListTask;
    private LoadUserProfileAsyncTask loadUserProfileTask;
    protected ProfileModel model;
    private RemoveUserFromMutedListAsyncTask removeUserFromMutedListAsyncTask;
    private RemoveUserToNeverListAsyncTask removeUserToNeverListAsyncTask;

    /* loaded from: classes3.dex */
    public enum ChangeFriendshipFormOptions {
        ShouldAddUserToFriendList,
        ShouldRemoveUserFromFriendList,
        ShouldAddUserToFavoriteList,
        ShouldRemoveUserFromFavoriteList,
        ShouldAddUserToShareIdentityList,
        ShouldRemoveUserFromShareIdentityList
    }

    public boolean isFacebookFriend() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class C02554 {
        static final int[] $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus;

        C02554() {
        }

        static {
            int[] iArr = new int[AsyncActionStatus.values().length];
            $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus = iArr;
            try {
                iArr[AsyncActionStatus.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.NO_CHANGE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.NO_OP_SUCCESS.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.FAIL.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.NO_OP_FAIL.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    /* loaded from: classes3.dex */
    private class AddUserToFollowingListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String followingUserXuid;
        private boolean isFollowingUser = false;

        public AddUserToFollowingListAsyncTask(String str) {
            this.followingUserXuid = str;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            if (meProfileModel == null) {
                return AsyncActionStatus.FAIL;
            }
            AsyncActionStatus status = meProfileModel.addUserToFollowingList(this.forceLoad, this.followingUserXuid).getStatus();
            if (!AsyncActionStatus.getIsFail(status)) {
                AddFollowingUserResponseContainer.AddFollowingUserResponse addUserToFollowingResult = meProfileModel.getAddUserToFollowingResult();
                if (addUserToFollowingResult != null && !addUserToFollowingResult.getAddFollowingRequestStatus() && addUserToFollowingResult.code == 1028) {
                    return AsyncActionStatus.FAIL;
                }
                ProfileScreenViewModel.this.model.loadProfileSummary(true);
                meProfileModel.loadProfileSummary(true);
                ArrayList<FollowersData> followingData = meProfileModel.getFollowingData();
                if (followingData != null) {
                    Iterator<FollowersData> it = followingData.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (it.next().xuid.equals(this.followingUserXuid)) {
                            this.isFollowingUser = true;
                            break;
                        }
                    }
                }
            }
            return status;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onAddUserToFollowingListCompleted(AsyncActionStatus.NO_CHANGE, this.isFollowingUser);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onAddUserToFollowingListCompleted(asyncActionStatus, this.isFollowingUser);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isAddingUserToFollowingList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    /* loaded from: classes3.dex */
    private class AddUserToMutedListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String mutedUserXuid;

        public AddUserToMutedListAsyncTask(String str) {
            this.mutedUserXuid = str;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.addUserToMutedList(this.forceLoad, this.mutedUserXuid).getStatus() : AsyncActionStatus.FAIL;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onAddUserToMutedListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onAddUserToMutedListCompleted(asyncActionStatus);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isAddingUserToMutedList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class AddUserToNeverListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String blockUserXuid;

        public AddUserToNeverListAsyncTask(String str) {
            this.blockUserXuid = str;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.addUserToNeverList(this.forceLoad, this.blockUserXuid).getStatus() : AsyncActionStatus.FAIL;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onAddUserToBlockListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onAddUserToBlockListCompleted(asyncActionStatus);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isAddingUserToBlockList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    /* loaded from: classes3.dex */
    private class AddUserToShareIdentityListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ArrayList<String> usersToAdd;

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public boolean checkShouldExecute() {
            return true;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public void onNoAction() {
        }

        public AddUserToShareIdentityListAsyncTask(ArrayList<String> arrayList) {
            this.usersToAdd = arrayList;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.addUserToShareIdentity(this.forceLoad, this.usersToAdd).getStatus() : AsyncActionStatus.FAIL;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onAddUseToShareIdentityListCompleted(asyncActionStatus);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isAddingUserToShareIdentityList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    /* loaded from: classes3.dex */
    private class LoadUserMutedListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ProfileModel model;

        private LoadUserMutedListAsyncTask(ProfileModel profileModel) {
            this.model = profileModel;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return this.model.shouldRefresh();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(this.model);
            AsyncActionStatus status = this.model.loadSync(this.forceLoad).getStatus();
            return (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) ? this.model.loadUserMutedList(true).getStatus() : status;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onLoadUserProfileCompleted(AsyncActionStatus.NO_CHANGE);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onLoadUserMutedListCompleted(asyncActionStatus);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isLoadingUserMutedList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    /* loaded from: classes3.dex */
    private class LoadUserNeverListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ProfileModel model;

        private LoadUserNeverListAsyncTask(ProfileModel profileModel) {
            this.model = profileModel;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return this.model.shouldRefresh();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(this.model);
            AsyncActionStatus status = this.model.loadSync(this.forceLoad).getStatus();
            return (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) ? this.model.loadUserNeverList(true).getStatus() : status;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onLoadUserProfileCompleted(AsyncActionStatus.NO_CHANGE);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onLoadUserNeverListCompleted(asyncActionStatus);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isLoadingUserNeverList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    /* loaded from: classes3.dex */
    private class LoadUserProfileAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ProfileModel model;

        private LoadUserProfileAsyncTask(ProfileModel profileModel) {
            this.model = profileModel;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return this.model.shouldRefresh() || this.model.shouldRefreshProfileSummary();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(this.model);
            AsyncActionStatus status = this.model.loadSync(this.forceLoad).getStatus();
            return (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) ? this.model.loadProfileSummary(this.forceLoad).getStatus() : status;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onLoadUserProfileCompleted(AsyncActionStatus.NO_CHANGE);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onLoadUserProfileCompleted(asyncActionStatus);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isLoadingUserProfile = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    /* loaded from: classes3.dex */
    private class RemoveUserFromMutedListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String mutedUserXuid;

        public RemoveUserFromMutedListAsyncTask(String str) {
            this.mutedUserXuid = str;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.removeUserFromMutedList(this.forceLoad, this.mutedUserXuid).getStatus() : AsyncActionStatus.FAIL;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onRemoveUserFromMutedListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onRemoveUserFromMutedListCompleted(asyncActionStatus);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isRemovingUserFromMutedList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    /* loaded from: classes3.dex */
    private class RemoveUserToNeverListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String unblockUserXuid;

        public RemoveUserToNeverListAsyncTask(String str) {
            this.unblockUserXuid = str;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.removeUserFromNeverList(this.forceLoad, this.unblockUserXuid).getStatus() : AsyncActionStatus.FAIL;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onRemoveUserFromBlockListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onRemoveUserFromBlockListCompleted(asyncActionStatus);
        }

        @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isRemovingUserFromBlockList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    public ProfileScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        this.changeFriendshipForm = new HashSet<>();
        this.isBlocked = false;
        this.isFavorite = false;
        this.isFollowing = false;
        this.isMuted = false;
        this.model = ProfileModel.getProfileModel(NavigationManager.getInstance().getActivityParameters().getSelectedProfile());
        this.adapter = new ProfileScreenAdapter(this);
    }

    private void notifyDialogAsyncTaskCompleted() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskCompleted();
    }

    private void notifyDialogAsyncTaskFailed(String str) {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskFailed(str);
    }

    public void onAddUseToShareIdentityListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isAddingUserToShareIdentityList = false;
        int i = C02554.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            notifyDialogAsyncTaskCompleted();
        } else if (i == 4 || i == 5) {
            notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
        }
        updateAdapter();
    }

    public void onAddUserToBlockListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isAddingUserToBlockList = false;
        int i = C02554.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            if (meProfileModel != null) {
                this.isBlocked = false;
                NeverListResultContainer.NeverListResult neverListData = meProfileModel.getNeverListData();
                if (neverListData != null) {
                    this.isBlocked = neverListData.contains(this.model.getXuid());
                }
                this.isFollowing = false;
            }
        } else if (i == 4 || i == 5) {
            showError(R.string.Messages_Error_FailedToBlockUser);
        }
        updateAdapter();
    }

    public void onAddUserToFollowingListCompleted(AsyncActionStatus asyncActionStatus, boolean z) {
        this.isAddingUserToFollowingList = false;
        int i = C02554.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            this.isFollowing = z;
            XLEGlobalData.getInstance().AddForceRefresh(ProfileScreenViewModel.class);
            notifyDialogAsyncTaskCompleted();
        } else if (i == 4 || i == 5) {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            AddFollowingUserResponseContainer.AddFollowingUserResponse addUserToFollowingResult = meProfileModel != null ? meProfileModel.getAddUserToFollowingResult() : null;
            if (addUserToFollowingResult == null || addUserToFollowingResult.getAddFollowingRequestStatus() || addUserToFollowingResult.code != 1028) {
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorAddingFriend));
            } else {
                notifyDialogAsyncTaskFailed(addUserToFollowingResult.description);
            }
        }
        updateAdapter();
    }

    public void onAddUserToMutedListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isAddingUserToMutedList = false;
        int i = C02554.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            if (ProfileModel.getMeProfileModel() != null) {
                this.isMuted = true;
            }
        } else if (i == 4 || i == 5) {
            showError(R.string.Messages_Error_FailedToMuteUser);
        }
        updateAdapter();
    }

    public void onRemoveUserFromBlockListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isRemovingUserFromBlockList = false;
        int i = C02554.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            if (meProfileModel != null) {
                this.isBlocked = false;
                NeverListResultContainer.NeverListResult neverListData = meProfileModel.getNeverListData();
                if (neverListData != null) {
                    this.isBlocked = neverListData.contains(this.model.getXuid());
                }
            }
        } else if (i == 4 || i == 5) {
            showError(R.string.Messages_Error_FailedToUnblockUser);
        }
        updateAdapter();
    }

    public void onRemoveUserFromMutedListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isRemovingUserFromMutedList = false;
        int i = C02554.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            if (ProfileModel.getMeProfileModel() != null) {
                this.isMuted = false;
            }
        } else if (i == 4 || i == 5) {
            showError(R.string.Messages_Error_FailedToUnmuteUser);
        }
        updateAdapter();
    }

    public void addFollowingUser() {
        if (ProfileModel.hasPrivilegeToAddFriend()) {
            AddUserToFollowingListAsyncTask addUserToFollowingListAsyncTask = this.addUserToFollowingListAsyncTask;
            if (addUserToFollowingListAsyncTask != null) {
                addUserToFollowingListAsyncTask.cancel();
            }
            AddUserToFollowingListAsyncTask addUserToFollowingListAsyncTask2 = new AddUserToFollowingListAsyncTask(this.model.getXuid());
            this.addUserToFollowingListAsyncTask = addUserToFollowingListAsyncTask2;
            addUserToFollowingListAsyncTask2.load(true);
            return;
        }
        showError(R.string.Global_MissingPrivilegeError_DialogBody);
    }

    public void addUserToShareIdentityList() {
        AddUserToShareIdentityListAsyncTask addUserToShareIdentityListAsyncTask = this.addUserToShareIdentityListAsyncTask;
        if (addUserToShareIdentityListAsyncTask != null) {
            addUserToShareIdentityListAsyncTask.cancel();
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.model.getXuid());
        AddUserToShareIdentityListAsyncTask addUserToShareIdentityListAsyncTask2 = new AddUserToShareIdentityListAsyncTask(arrayList);
        this.addUserToShareIdentityListAsyncTask = addUserToShareIdentityListAsyncTask2;
        addUserToShareIdentityListAsyncTask2.load(true);
    }

    public void blockUser() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        showOkCancelDialog(XboxTcuiSdk.getResources().getString(R.string.Messages_BlockUserConfirmation_DialogTitle), XboxTcuiSdk.getResources().getString(R.string.Messages_BlockUserConfirmation_DialogBody), XboxTcuiSdk.getResources().getString(R.string.OK_Text), new Runnable() { // from class: com.microsoft.xbox.xle.app.activity.Profile.ProfileScreenViewModel.1
            @Override // java.lang.Runnable
            public void run() {
                ProfileScreenViewModel.this.blockUserInternal();
            }
        }, XboxTcuiSdk.getResources().getString(R.string.MessageDialog_Cancel), null);
        updateAdapter();
    }

    public void blockUserInternal() {
        UTCPeopleHub.trackBlockDialogComplete();
        AddUserToNeverListAsyncTask addUserToNeverListAsyncTask = this.addUserToNeverListAsyncTask;
        if (addUserToNeverListAsyncTask != null) {
            addUserToNeverListAsyncTask.cancel();
        }
        AddUserToNeverListAsyncTask addUserToNeverListAsyncTask2 = new AddUserToNeverListAsyncTask(this.model.getXuid());
        this.addUserToNeverListAsyncTask = addUserToNeverListAsyncTask2;
        addUserToNeverListAsyncTask2.load(true);
    }

    public String getGamerPicUrl() {
        return this.model.getGamerPicImageUrl();
    }

    public String getGamerScore() {
        return this.model.getGamerScore();
    }

    public String getGamerTag() {
        return this.model.getGamerTag();
    }

    public boolean getIsAddingUserToBlockList() {
        return this.isAddingUserToBlockList;
    }

    public boolean getIsAddingUserToMutedList() {
        return this.isAddingUserToMutedList;
    }

    public boolean getIsBlocked() {
        return this.isBlocked;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public boolean getIsMuted() {
        return this.isMuted;
    }

    public boolean getIsRemovingUserFromBlockList() {
        return this.isRemovingUserFromBlockList;
    }

    public boolean getIsRemovingUserFromMutedList() {
        return this.isRemovingUserFromMutedList;
    }

    public int getPreferredColor() {
        return this.model.getPreferedColor();
    }

    public String getRealName() {
        return this.model.getRealName();
    }

    public String getXuid() {
        return this.model.getXuid();
    }

    @Override // com.microsoft.xbox.xle.viewmodel.ViewModelBase
    public boolean isBusy() {
        return this.isLoadingUserProfile || this.isLoadingUserNeverList || this.isLoadingUserMutedList || this.isAddingUserToFollowingList || this.isAddingUserToShareIdentityList || this.isRemovingUserFromBlockList || this.isAddingUserToBlockList || this.isAddingUserToMutedList || this.isRemovingUserFromMutedList;
    }

    public boolean isCallerFollowingTarget() {
        return this.isFollowing;
    }

    public boolean isMeProfile() {
        return this.model.isMeProfile();
    }

    public void launchXboxApp() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        showOkCancelDialog(XboxTcuiSdk.getResources().getString(R.string.Messages_BlockUserConfirmation_DialogTitle), XboxTcuiSdk.getResources().getString(R.string.Messages_ViewInXboxApp_DialogBody), XboxTcuiSdk.getResources().getString(R.string.ConnectDialog_ContinueAsGuest), new Runnable() { // from class: com.microsoft.xbox.xle.app.activity.Profile.ProfileScreenViewModel.2
            @Override // java.lang.Runnable
            public void run() {
                UTCPeopleHub.trackViewInXboxAppDialogComplete();
                XboxAppDeepLinker.showUserProfile(XboxTcuiSdk.getActivity(), ProfileScreenViewModel.this.model.getXuid());
            }
        }, XboxTcuiSdk.getResources().getString(R.string.MessageDialog_Cancel), null);
        updateAdapter();
    }

    @Override // com.microsoft.xbox.xle.viewmodel.ViewModelBase
    public void load(boolean z) {
        LoadUserProfileAsyncTask loadUserProfileAsyncTask = this.loadUserProfileTask;
        if (loadUserProfileAsyncTask != null) {
            loadUserProfileAsyncTask.cancel();
        }
        LoadUserProfileAsyncTask loadUserProfileAsyncTask2 = new LoadUserProfileAsyncTask(ProfileModel.getMeProfileModel());
        this.loadMeProfileTask = loadUserProfileAsyncTask2;
        loadUserProfileAsyncTask2.load(true);
        if (isMeProfile()) {
            return;
        }
        LoadUserNeverListAsyncTask loadUserNeverListAsyncTask = this.loadUserNeverListTask;
        if (loadUserNeverListAsyncTask != null) {
            loadUserNeverListAsyncTask.cancel();
        }
        LoadUserNeverListAsyncTask loadUserNeverListAsyncTask2 = new LoadUserNeverListAsyncTask(ProfileModel.getMeProfileModel());
        this.loadUserNeverListTask = loadUserNeverListAsyncTask2;
        loadUserNeverListAsyncTask2.load(true);
        LoadUserMutedListAsyncTask loadUserMutedListAsyncTask = this.loadUserMutedListTask;
        if (loadUserMutedListAsyncTask != null) {
            loadUserMutedListAsyncTask.cancel();
        }
        LoadUserMutedListAsyncTask loadUserMutedListAsyncTask2 = new LoadUserMutedListAsyncTask(ProfileModel.getMeProfileModel());
        this.loadUserMutedListTask = loadUserMutedListAsyncTask2;
        loadUserMutedListAsyncTask2.load(true);
        LoadUserProfileAsyncTask loadUserProfileAsyncTask3 = new LoadUserProfileAsyncTask(this.model);
        this.loadUserProfileTask = loadUserProfileAsyncTask3;
        loadUserProfileAsyncTask3.load(true);
    }

    public void muteUser() {
        AddUserToMutedListAsyncTask addUserToMutedListAsyncTask = this.addUserToMutedListAsyncTask;
        if (addUserToMutedListAsyncTask != null) {
            addUserToMutedListAsyncTask.cancel();
        }
        AddUserToMutedListAsyncTask addUserToMutedListAsyncTask2 = new AddUserToMutedListAsyncTask(this.model.getXuid());
        this.addUserToMutedListAsyncTask = addUserToMutedListAsyncTask2;
        addUserToMutedListAsyncTask2.load(true);
    }

    public void navigateToChangeRelationship() {
        if (ProfileModel.hasPrivilegeToAddFriend()) {
            UTCChangeRelationship.trackChangeRelationshipAction(getScreen().getName(), getXuid(), isCallerFollowingTarget(), isFacebookFriend());
            showChangeFriendshipDialog();
            return;
        }
        showError(R.string.Global_MissingPrivilegeError_DialogBody);
    }

    public void onLoadUserMutedListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isLoadingUserMutedList = false;
        int i = C02554.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            if (!isMeProfile() && meProfileModel != null) {
                this.isMuted = false;
                MutedListResultContainer.MutedListResult mutedList = meProfileModel.getMutedList();
                if (mutedList != null) {
                    this.isMuted = mutedList.contains(this.model.getXuid());
                }
            }
        }
        updateAdapter();
    }

    public void onLoadUserNeverListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isLoadingUserNeverList = false;
        int i = C02554.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            if (!isMeProfile() && meProfileModel != null) {
                this.isBlocked = false;
                NeverListResultContainer.NeverListResult neverListData = meProfileModel.getNeverListData();
                if (neverListData != null) {
                    this.isBlocked = neverListData.contains(this.model.getXuid());
                }
            }
        }
        updateAdapter();
    }

    public void onLoadUserProfileCompleted(AsyncActionStatus asyncActionStatus) {
        this.isLoadingUserProfile = false;
        int i = C02554.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()];
        if (i == 1 || i == 2 || i == 3) {
            if (!isMeProfile() && ProfileModel.getMeProfileModel() != null) {
                this.isFollowing = this.model.isCallerFollowingTarget();
            }
        } else if ((i == 4 || i == 5) && !this.isShowingFailureDialog) {
            this.isShowingFailureDialog = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(XboxTcuiSdk.getActivity());
            builder.setMessage(R.string.Service_ErrorText);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.OK_Text, new DialogInterface.OnClickListener() { // from class: com.microsoft.xbox.xle.app.activity.Profile.ProfileScreenViewModel.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    try {
                        NavigationManager.getInstance().PopAllScreens();
                    } catch (XLEException unused) {
                    }
                }
            });
            builder.create().show();
        }
        updateAdapter();
    }

    @Override // com.microsoft.xbox.xle.viewmodel.ViewModelBase
    public void onRehydrate() {
        this.adapter = new ProfileScreenAdapter(this);
    }

    @Override // com.microsoft.xbox.xle.viewmodel.ViewModelBase
    public void onStartOverride() {
        this.isShowingFailureDialog = false;
    }

    @Override // com.microsoft.xbox.xle.viewmodel.ViewModelBase
    public void onStopOverride() {
        LoadUserProfileAsyncTask loadUserProfileAsyncTask = this.loadMeProfileTask;
        if (loadUserProfileAsyncTask != null) {
            loadUserProfileAsyncTask.cancel();
        }
        LoadUserNeverListAsyncTask loadUserNeverListAsyncTask = this.loadUserNeverListTask;
        if (loadUserNeverListAsyncTask != null) {
            loadUserNeverListAsyncTask.cancel();
        }
        LoadUserMutedListAsyncTask loadUserMutedListAsyncTask = this.loadUserMutedListTask;
        if (loadUserMutedListAsyncTask != null) {
            loadUserMutedListAsyncTask.cancel();
        }
        LoadUserProfileAsyncTask loadUserProfileAsyncTask2 = this.loadUserProfileTask;
        if (loadUserProfileAsyncTask2 != null) {
            loadUserProfileAsyncTask2.cancel();
        }
        AddUserToFollowingListAsyncTask addUserToFollowingListAsyncTask = this.addUserToFollowingListAsyncTask;
        if (addUserToFollowingListAsyncTask != null) {
            addUserToFollowingListAsyncTask.cancel();
        }
        AddUserToShareIdentityListAsyncTask addUserToShareIdentityListAsyncTask = this.addUserToShareIdentityListAsyncTask;
        if (addUserToShareIdentityListAsyncTask != null) {
            addUserToShareIdentityListAsyncTask.cancel();
        }
        AddUserToNeverListAsyncTask addUserToNeverListAsyncTask = this.addUserToNeverListAsyncTask;
        if (addUserToNeverListAsyncTask != null) {
            addUserToNeverListAsyncTask.cancel();
        }
        RemoveUserToNeverListAsyncTask removeUserToNeverListAsyncTask = this.removeUserToNeverListAsyncTask;
        if (removeUserToNeverListAsyncTask != null) {
            removeUserToNeverListAsyncTask.cancel();
        }
        AddUserToMutedListAsyncTask addUserToMutedListAsyncTask = this.addUserToMutedListAsyncTask;
        if (addUserToMutedListAsyncTask != null) {
            addUserToMutedListAsyncTask.cancel();
        }
        RemoveUserFromMutedListAsyncTask removeUserFromMutedListAsyncTask = this.removeUserFromMutedListAsyncTask;
        if (removeUserFromMutedListAsyncTask != null) {
            removeUserFromMutedListAsyncTask.cancel();
        }
    }

    public void showChangeFriendshipDialog() {
        if (this.changeFriendshipDialogViewModel == null) {
            this.changeFriendshipDialogViewModel = new ChangeFriendshipDialogViewModel(this.model);
        }
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).showChangeFriendshipDialog(this.changeFriendshipDialogViewModel, this);
    }

    public void showReportDialog() {
        try {
            NavigationManager.getInstance().PopScreensAndReplace(0, ReportUserScreen.class, false, false, false, NavigationManager.getInstance().getActivityParameters());
        } catch (XLEException unused) {
        }
    }

    public void unblockUser() {
        RemoveUserToNeverListAsyncTask removeUserToNeverListAsyncTask = this.removeUserToNeverListAsyncTask;
        if (removeUserToNeverListAsyncTask != null) {
            removeUserToNeverListAsyncTask.cancel();
        }
        RemoveUserToNeverListAsyncTask removeUserToNeverListAsyncTask2 = new RemoveUserToNeverListAsyncTask(this.model.getXuid());
        this.removeUserToNeverListAsyncTask = removeUserToNeverListAsyncTask2;
        removeUserToNeverListAsyncTask2.load(true);
    }

    public void unmuteUser() {
        RemoveUserFromMutedListAsyncTask removeUserFromMutedListAsyncTask = this.removeUserFromMutedListAsyncTask;
        if (removeUserFromMutedListAsyncTask != null) {
            removeUserFromMutedListAsyncTask.cancel();
        }
        RemoveUserFromMutedListAsyncTask removeUserFromMutedListAsyncTask2 = new RemoveUserFromMutedListAsyncTask(this.model.getXuid());
        this.removeUserFromMutedListAsyncTask = removeUserFromMutedListAsyncTask2;
        removeUserFromMutedListAsyncTask2.load(true);
    }
}

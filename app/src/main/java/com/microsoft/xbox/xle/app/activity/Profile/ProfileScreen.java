package com.microsoft.xbox.xle.app.activity.Profile;

import android.content.Context;
import android.util.AttributeSet;
//import com.crafting.minecrafting.lokicraft.R;
import com.craftsman.go.R;
import com.microsoft.xbox.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xbox.xle.app.activity.ActivityBase;

/* loaded from: classes3.dex */
public class ProfileScreen extends ActivityBase {
    @Override // com.microsoft.xbox.xle.app.activity.ActivityBase
    public String getActivityName() {
        return "PeopleHub Info";
    }

    public ProfileScreen() {
    }

    public ProfileScreen(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.microsoft.xbox.toolkit.ui.ScreenLayout
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        ProfileScreenViewModel profileScreenViewModel = new ProfileScreenViewModel(this);
        this.viewModel = profileScreenViewModel;
        UTCPeopleHub.trackPeopleHubView(getActivityName(), profileScreenViewModel.getXuid(), profileScreenViewModel.isMeProfile());
    }

    @Override // com.microsoft.xbox.xle.app.activity.ActivityBase
    public void onCreateContentView() {
        setContentView(R.layout.profile_screen);
    }
}

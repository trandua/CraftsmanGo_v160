package com.microsoft.xbox.idp.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
//import com.crafting.minecrafting.lokicraft.R;
import com.craftsman.go.R;
import com.microsoft.xbox.idp.compat.BaseActivity;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.model.Const;
import com.microsoft.xbox.idp.ui.ErrorButtonsFragment;
import com.microsoft.xbox.idp.ui.HeaderFragment;
import com.microsoft.xbox.telemetry.helpers.UTCError;
import com.microsoft.xbox.telemetry.helpers.UTCPageView;
import com.microsoft.xboxtcui.C5528R;

/* loaded from: classes3.dex */
public class ErrorActivity extends BaseActivity implements HeaderFragment.Callbacks, ErrorButtonsFragment.Callbacks {
    public static final String ARG_ERROR_TYPE = "ARG_ERROR_TYPE";
    public static final String ARG_GAMER_TAG = "ARG_GAMER_TAG";
    public static final int RESULT_TRY_AGAIN = 1;
    private static final String TAG = "ErrorActivity";
    private int activityResult = 0;

    /* loaded from: classes3.dex */
    public enum ErrorScreen {
        BAN(Interop.ErrorType.BAN, BanErrorFragment.class, C5528R.string.xbid_more_info),
        CREATION(Interop.ErrorType.CREATION, CreationErrorFragment.class, C5528R.string.xbid_try_again),
        OFFLINE(Interop.ErrorType.OFFLINE, OfflineErrorFragment.class, C5528R.string.xbid_try_again),
        CATCHALL(Interop.ErrorType.CATCHALL, CatchAllErrorFragment.class, C5528R.string.xbid_try_again);
        
        public final Class<? extends BaseFragment> errorFragmentClass;
        public final int leftButtonTextId;
        public final Interop.ErrorType type;

        ErrorScreen(Interop.ErrorType errorType, Class cls, int i) {
            this.type = errorType;
            this.errorFragmentClass = cls;
            this.leftButtonTextId = i;
        }

        public static ErrorScreen fromId(int i) {
            ErrorScreen[] values;
            for (ErrorScreen errorScreen : values()) {
                if (errorScreen.type.getId() == i) {
                    return errorScreen;
                }
            }
            return null;
        }
    }

    @Override // android.app.Activity
    public void finish() {
        UTCPageView.removePage();
        super.finish();
    }

    @Override // com.microsoft.xbox.idp.ui.HeaderFragment.Callbacks
    public void onClickCloseHeader() {
        Log.d(TAG, "onClickCloseHeader");
        UTCError.trackClose(ErrorScreen.fromId(getIntent().getIntExtra(ARG_ERROR_TYPE, -1)), getTitle());
        finish();
    }

    @Override // com.microsoft.xbox.idp.ui.ErrorButtonsFragment.Callbacks
    public void onClickedLeftButton() {
        Log.d(TAG, "onClickedLeftButton");
        ErrorScreen fromId = ErrorScreen.fromId(getIntent().getIntExtra(ARG_ERROR_TYPE, -1));
        if (fromId == ErrorScreen.BAN) {
            UTCError.trackGoToEnforcement(fromId, getTitle());
            try {
                startActivity(new Intent("android.intent.action.VIEW", Const.URL_ENFORCEMENT_XBOX_COM));
                return;
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, e.getMessage());
                return;
            }
        }
        UTCError.trackTryAgain(fromId, getTitle());
        this.activityResult = 1;
        setResult(1);
        finish();
    }

    @Override // com.microsoft.xbox.idp.ui.ErrorButtonsFragment.Callbacks
    public void onClickedRightButton() {
        Log.d(TAG, "onClickedRightButton");
        UTCError.trackRightButton(ErrorScreen.fromId(getIntent().getIntExtra(ARG_ERROR_TYPE, -1)), getTitle());
        finish();
    }

    @Override // com.microsoft.xbox.idp.compat.BaseActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        String str;
        Log.d(TAG, "onCreate");
        super.onCreate(bundle);
        setContentView(C5528R.layout.xbid_activity_error);
        Intent intent = getIntent();
        UiUtil.ensureHeaderFragment(this, R.id.xbid_header_fragment, intent.getExtras());
        if (intent.hasExtra(ARG_ERROR_TYPE)) {
            ErrorScreen fromId = ErrorScreen.fromId(intent.getIntExtra(ARG_ERROR_TYPE, -1));
            if (fromId != null) {
                UiUtil.ensureErrorFragment(this, fromId);
                UiUtil.ensureErrorButtonsFragment(this, fromId);
                UTCError.trackPageView(fromId, getTitle());
                return;
            }
            str = "Incorrect error type was provided";
        } else {
            str = "No error type was provided";
        }
        Log.e(TAG, str);
    }
}

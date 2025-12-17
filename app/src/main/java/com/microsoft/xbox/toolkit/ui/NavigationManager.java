package com.microsoft.xbox.toolkit.ui;

import android.view.KeyEvent;
import android.view.View;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.Iterator;
import java.util.Stack;

/* loaded from: classes3.dex */
public class NavigationManager implements View.OnKeyListener {
    private static final String TAG = "NavigationManager";
    private NavigationManagerAnimationState animationState;
    final Runnable callAfterAnimation;
    public boolean cannotNavigateTripwire;
    private XLEAnimationPackage currentAnimation;
    private boolean goingBack;
    private NavigationCallbacks navigationCallbacks;
    public OnNavigatedListener navigationListener;
    public final Stack<ActivityParameters> navigationParameters;
    public final Stack<ScreenLayout> navigationStack;
    private boolean transitionAnimate;
    private Runnable transitionLambda;

    /* loaded from: classes3.dex */
    public interface NavigationCallbacks {
        void addContentViewXLE(ScreenLayout screenLayout);

        void onBeforeNavigatingIn();

        void removeContentViewXLE(ScreenLayout screenLayout);

        void setAnimationBlocking(boolean z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public enum NavigationManagerAnimationState {
        NONE,
        ANIMATING_IN,
        ANIMATING_OUT,
        COUNT
    }

    /* loaded from: classes3.dex */
    public interface OnNavigatedListener {
        void onPageNavigated(ScreenLayout screenLayout, ScreenLayout screenLayout2);

        void onPageRestarted(ScreenLayout screenLayout);
    }

    public boolean TEST_isAnimatingIn() {
        return false;
    }

    public boolean TEST_isAnimatingOut() {
        return false;
    }

    /* loaded from: classes3.dex */
    public static class C54853 {
        static final int[] f12621xfa317669;

        static {
            int[] iArr = new int[NavigationManagerAnimationState.values().length];
            f12621xfa317669 = iArr;
            try {
                iArr[NavigationManagerAnimationState.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f12621xfa317669[NavigationManagerAnimationState.ANIMATING_IN.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f12621xfa317669[NavigationManagerAnimationState.ANIMATING_OUT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class NavigationManagerHolder {
        public static final NavigationManager instance = new NavigationManager();

        private NavigationManagerHolder() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class RestartRunner implements Runnable {
        private  ActivityParameters params;
        NavigationManager this$0;

        public RestartRunner(NavigationManager navigationManager, ActivityParameters activityParameters) {
            this.this$0 = navigationManager;
            this.params = activityParameters;
        }

        public RestartRunner(ActivityParameters screenParameters) {

        }

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.cannotNavigateTripwire = true;
            ScreenLayout currentActivity = this.this$0.getCurrentActivity();
            XLEAssert.assertNotNull(currentActivity);
            this.this$0.getCurrentActivity().onSetInactive();
            this.this$0.getCurrentActivity().onPause();
            this.this$0.getCurrentActivity().onStop();
            XLEAssert.assertTrue("navigationParameters cannot be empty!", true ^ this.this$0.navigationParameters.isEmpty());
            this.this$0.navigationParameters.pop();
            this.this$0.navigationParameters.push(this.params);
            this.this$0.getCurrentActivity().onStart();
            this.this$0.getCurrentActivity().onResume();
            this.this$0.getCurrentActivity().onSetActive();
            this.this$0.getCurrentActivity().onAnimateInStarted();
            XboxTcuiSdk.getActivity().invalidateOptionsMenu();
            if (this.this$0.navigationListener != null) {
                this.this$0.navigationListener.onPageRestarted(currentActivity);
            }
            this.this$0.cannotNavigateTripwire = false;
        }
    }

    private NavigationManager() {
        this.navigationParameters = new Stack<>();
        this.navigationStack = new Stack<>();
        this.currentAnimation = null;
        this.animationState = NavigationManagerAnimationState.NONE;
        this.transitionLambda = null;
        this.goingBack = false;
        this.transitionAnimate = true;
        this.cannotNavigateTripwire = false;
        this.callAfterAnimation = new Runnable() { // from class: com.microsoft.xbox.toolkit.ui.NavigationManager.1
            @Override // java.lang.Runnable
            public void run() {
                NavigationManager.this.OnAnimationEnd();
            }
        };
        XLEAssert.assertTrue("You must access navigation manager on UI thread.", Thread.currentThread() == ThreadManager.UIThread);
    }

    public void OnAnimationEnd() {
        int i = C54853.f12621xfa317669[this.animationState.ordinal()];
        if (i == 2) {
            NavigationCallbacks navigationCallbacks = this.navigationCallbacks;
            if (navigationCallbacks != null) {
                navigationCallbacks.setAnimationBlocking(false);
            }
            this.animationState = NavigationManagerAnimationState.NONE;
            if (getCurrentActivity() != null) {
                getCurrentActivity().onAnimateInCompleted();
            }
        } else if (i == 3) {
            this.transitionLambda.run();
            XLEAnimationPackage animateIn = getCurrentActivity() != null ? getCurrentActivity().getAnimateIn(this.goingBack) : null;
            NavigationCallbacks navigationCallbacks2 = this.navigationCallbacks;
            if (navigationCallbacks2 != null) {
                navigationCallbacks2.onBeforeNavigatingIn();
            }
            startAnimation(animateIn, NavigationManagerAnimationState.ANIMATING_IN);
        }
    }

    private void ReplaceOnAnimationEnd(boolean z, Runnable runnable, boolean z2) {
        XLEAssert.assertTrue(this.animationState == NavigationManagerAnimationState.ANIMATING_OUT || this.animationState == NavigationManagerAnimationState.ANIMATING_IN);
        this.animationState = NavigationManagerAnimationState.ANIMATING_OUT;
        this.transitionLambda = runnable;
        this.transitionAnimate = z2;
        this.goingBack = z;
    }

    private int Size() {
        return this.navigationStack.size();
    }

    private void Transition(boolean z, Runnable runnable, boolean z2) {
        this.transitionLambda = runnable;
        this.transitionAnimate = z2;
        this.goingBack = z;
        XLEAnimationPackage animateOut = getCurrentActivity() == null ? null : getCurrentActivity().getAnimateOut(z);
        this.currentAnimation = animateOut;
        startAnimation(animateOut, NavigationManagerAnimationState.ANIMATING_OUT);
    }

    public static NavigationManager getInstance() {
        return NavigationManagerHolder.instance;
    }

    private void startAnimation(XLEAnimationPackage xLEAnimationPackage, NavigationManagerAnimationState navigationManagerAnimationState) {
        this.animationState = navigationManagerAnimationState;
        this.currentAnimation = xLEAnimationPackage;
        NavigationCallbacks navigationCallbacks = this.navigationCallbacks;
        if (navigationCallbacks != null) {
            navigationCallbacks.setAnimationBlocking(true);
        }
        if (!this.transitionAnimate || xLEAnimationPackage == null) {
            this.callAfterAnimation.run();
            return;
        }
        xLEAnimationPackage.setOnAnimationEndRunnable(this.callAfterAnimation);
        xLEAnimationPackage.startAnimation();
    }

    public int CountPopsToScreen(Class<? extends ScreenLayout> cls) {
        int size = this.navigationStack.size() - 1;
        for (int i = size; i >= 0; i--) {
            if (this.navigationStack.get(i).getClass().equals(cls)) {
                return size - i;
            }
        }
        return -1;
    }

    public void GotoScreenWithPop(ActivityParameters activityParameters, Class<? extends ScreenLayout> cls, Class<? extends ScreenLayout>... clsArr) throws XLEException {
        Class<? extends ScreenLayout> cls2;
        int i;
        int size = this.navigationStack.size() - 1;
        int i2 = size;
        loop0: while (true) {
            if (i2 < 0) {
                cls2 = null;
                break;
            }
            Class<?> cls3 = this.navigationStack.get(i2).getClass();
            for (Class<? extends ScreenLayout> cls4 : clsArr) {
                if (cls4 == cls3) {
                    cls2 = cls4;
                    break loop0;
                }
            }
            i2--;
        }
        if (cls2 == null) {
            i = Size();
        } else if (cls2 != cls) {
            i = size - i2;
        } else if (i2 == size) {
            RestartCurrentScreen(activityParameters, false);
            return;
        } else {
            i = size - i2;
            PopScreensAndReplace(i, null, true, true, false, activityParameters);
        }
        PopScreensAndReplace(i, cls, true, true, false, activityParameters);
    }

    public void GotoScreenWithPop(Class<? extends ScreenLayout> cls) throws XLEException {
        int CountPopsToScreen = CountPopsToScreen(cls);
        if (CountPopsToScreen > 0) {
            PopScreensAndReplace(CountPopsToScreen, null, true, false, false);
        } else if (CountPopsToScreen < 0) {
            PopScreensAndReplace(Size(), cls, true, false, false);
        } else {
            RestartCurrentScreen(true);
        }
    }

    public void GotoScreenWithPush(Class<? extends ScreenLayout> cls) throws XLEException {
        int CountPopsToScreen = CountPopsToScreen(cls);
        if (CountPopsToScreen > 0) {
            PopScreensAndReplace(CountPopsToScreen, null, true, false, false);
        } else if (CountPopsToScreen < 0) {
            PopScreensAndReplace(0, cls, true, false, false);
        } else {
            RestartCurrentScreen(true);
        }
    }

    public void GotoScreenWithPush(Class<? extends ScreenLayout> cls, ActivityParameters activityParameters) throws XLEException {
        int CountPopsToScreen = CountPopsToScreen(cls);
        if (CountPopsToScreen > 0) {
            PopScreensAndReplace(CountPopsToScreen, null, true, false, false, activityParameters);
        } else if (CountPopsToScreen < 0) {
            PopScreensAndReplace(0, cls, true, false, false, activityParameters);
        } else {
            RestartCurrentScreen(true);
        }
    }

    public boolean IsScreenOnStack(Class<? extends ScreenLayout> cls) {
        Iterator<ScreenLayout> it = this.navigationStack.iterator();
        while (it.hasNext()) {
            if (it.next().getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    public void NavigateTo(Class<? extends ScreenLayout> cls, boolean z) {
        NavigateTo(cls, z, null);
    }

    public void NavigateTo(Class<? extends ScreenLayout> cls, boolean z, ActivityParameters activityParameters) {
        if (z) {
            try {
                PushScreen(cls, activityParameters);
                return;
            } catch (XLEException unused) {
                return;
            }
        }
        try {
            PopScreensAndReplace(1, cls, activityParameters);
        } catch (XLEException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean OnBackButtonPressed() {
        boolean ShouldBackCloseApp = ShouldBackCloseApp();
        if (getCurrentActivity() != null && !getCurrentActivity().onBackButtonPressed()) {
            if (ShouldBackCloseApp) {
                try {
                    Class cls = null;
                    PopScreensAndReplace(1, null, false, false, false);
                } catch (XLEException unused) {
                }
            } else {
                try {
                    PopScreen();
                } catch (XLEException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return ShouldBackCloseApp;
    }

    public void PopAllScreens() throws XLEException {
        if (Size() > 0) {
            PopScreensAndReplace(Size(), null, false, false, false);
        }
    }

    public void PopScreen() throws XLEException {
        PopScreens(1);
    }

    public void PopScreens(int i) throws XLEException {
        PopScreensAndReplace(i, null);
    }

    public void PopScreensAndReplace(int i, Class<? extends ScreenLayout> cls) throws XLEException {
        PopScreensAndReplace(i, cls, null);
    }

    public void PopScreensAndReplace(int i, Class<? extends ScreenLayout> cls, ActivityParameters activityParameters) throws XLEException {
        PopScreensAndReplace(i, cls, true, true, false, activityParameters);
    }

    public void PopScreensAndReplace(int i, Class<? extends ScreenLayout> cls, boolean z, boolean z2) throws XLEException {
        PopScreensAndReplace(i, cls, z, true, z2);
    }

    public void PopScreensAndReplace(int i, Class<? extends ScreenLayout> cls, boolean z, boolean z2, boolean z3) throws XLEException {
        PopScreensAndReplace(i, cls, z, z2, z3, null);
    }

    public void PopScreensAndReplace(final int popCount, Class<? extends ScreenLayout> newScreenClass, boolean animate, boolean goingBack, boolean isRestart, ActivityParameters activityParameters) throws XLEException {
//        ScreenLayout screenLayout;
//        boolean z4;
//        boolean z5;
//        boolean z6 = false;
//        XLEAssert.assertTrue("You must access navigation manager on UI thread.", Thread.currentThread() == ThreadManager.UIThread);
//        if (!this.cannotNavigateTripwire) {
//            if (cls == null || z3) {
//                screenLayout = null;
//                z4 = z;
//            } else {
//                try {
//                    ScreenLayout newInstance = cls.getConstructor(new Class[0]).newInstance(new Object[0]);
//                    if (z) {
//                        if (newInstance.isAnimateOnPush()) {
//                            z4 = true;
//                            screenLayout = newInstance;
//                        }
//                    }
//                    z4 = false;
//                    screenLayout = newInstance;
//                } catch (Exception e) {
//                    throw new XLEException(19L, "FIXME: Failed to create a screen of type " + cls.getName(), e);
//                }
//            }
//            if (getCurrentActivity() != null) {
//                if (z4 && getCurrentActivity().isAnimateOnPop()) {
//                    z6 = true;
//                }
//                z5 = z6;
//            } else {
//                z5 = z4;
//            }
//            ActivityParameters activityParameters2 = activityParameters == null ? new ActivityParameters() : activityParameters;
//            NavigationCallbacks navigationCallbacks = this.navigationCallbacks;
//            XLEAssert.assertNotNull(navigationCallbacks);
//            Runnable restartRunner = z3 ? new RestartRunner(this, activityParameters2) : new Runnable(activityParameters2, i, navigationCallbacks, _screenLayout) { // from class: com.microsoft.xbox.toolkit.ui.NavigationManager.2
//                final NavigationManager this$0;
//                final /* synthetic */ ActivityParameters val$activityParameters2;
//                final NavigationCallbacks val$callbacks;
//                final /* synthetic */ int val$i;
//                final /* synthetic */ NavigationCallbacks val$navigationCallbacks2;
//                final ScreenLayout val$newScreen;
//                final int val$popCount;
//                final /* synthetic */ ScreenLayout val$screenLayout;
//                final ActivityParameters val$screenParameters;
//
//                {
//                    this.val$activityParameters2 = activityParameters2;
//                    this.val$i = i;
//                    this.val$navigationCallbacks2 = navigationCallbacks;
//                    this.val$screenLayout = screenLayout;
//                    this.this$0 = NavigationManager.this;
//                    this.val$screenParameters = activityParameters2;
//                    this.val$popCount = i;
//                    this.val$callbacks = navigationCallbacks;
//                    this.val$newScreen = screenLayout;
//                }
//
//                @Override // java.lang.Runnable
//                public void run() {
//                    ScreenLayout screenLayout2;
//                    this.this$0.cannotNavigateTripwire = true;
//                    ScreenLayout currentActivity = this.this$0.getCurrentActivity();
//                    this.val$screenParameters.putFromScreen(currentActivity);
//                    this.val$screenParameters.putSourcePage(this.this$0.getCurrentActivityName());
//                    if (this.this$0.getCurrentActivity() != null) {
//                        this.this$0.getCurrentActivity().onSetInactive();
//                        this.this$0.getCurrentActivity().onPause();
//                        this.this$0.getCurrentActivity().onStop();
//                    }
//                    for (int i2 = 0; i2 < this.val$popCount; i2++) {
//                        this.this$0.getCurrentActivity().onDestroy();
//                        this.val$callbacks.removeContentViewXLE(this.this$0.navigationStack.pop());
//                        this.this$0.navigationParameters.pop();
//                    }
//                    TextureManager.Instance().purgeResourceBitmapCache();
//                    if (this.val$newScreen != null) {
//                        if (this.this$0.getCurrentActivity() != null && !this.val$newScreen.isKeepPreviousScreen()) {
//                            this.this$0.getCurrentActivity().onTombstone();
//                        }
//                        this.val$callbacks.addContentViewXLE(this.this$0.navigationStack.push(this.val$newScreen));
//                        this.this$0.navigationParameters.push(this.val$screenParameters);
//                        this.this$0.getCurrentActivity().onCreate();
//                    } else if (this.this$0.getCurrentActivity() != null) {
//                        this.val$callbacks.addContentViewXLE(this.this$0.getCurrentActivity());
//                        if (this.this$0.getCurrentActivity().getIsTombstoned()) {
//                            this.this$0.getCurrentActivity().onRehydrate();
//                        }
//                    }
//                    if (this.this$0.getCurrentActivity() != null) {
//                        this.this$0.getCurrentActivity().onStart();
//                        this.this$0.getCurrentActivity().onResume();
//                        this.this$0.getCurrentActivity().onSetActive();
//                        this.this$0.getCurrentActivity().onAnimateInStarted();
//                        XboxTcuiSdk.getActivity().invalidateOptionsMenu();
//                        screenLayout2 = this.this$0.getCurrentActivity();
//                    } else {
//                        screenLayout2 = null;
//                    }
//                    if (this.this$0.navigationListener != null) {
//                        this.this$0.navigationListener.onPageNavigated(currentActivity, screenLayout2);
//                    }
//                    this.this$0.cannotNavigateTripwire = false;
//                }
//            };
//            if (C54853.f12621xfa317669[this.animationState.ordinal()] != 1) {
//                ReplaceOnAnimationEnd(z2, restartRunner, z5);
//                return;
//            } else {
//                Transition(z2, restartRunner, z5);
//                return;
//            }
//        }
//        throw new UnsupportedOperationException("NavigationManager: attempted to execute a recursive navigation in the OnStop/OnStart method.  This is forbidden.");
        final ScreenLayout newScreen;
        Runnable popAndReplaceRunnable;
        XLEAssert.assertTrue("You must access navigation manager on UI thread.", Thread.currentThread() == ThreadManager.UIThread);
        if (this.cannotNavigateTripwire) {
            throw new UnsupportedOperationException("NavigationManager: attempted to execute a recursive navigation in the OnStop/OnStart method.  This is forbidden.");
        }
        if (newScreenClass == null || isRestart) {
            newScreen = null;
        } else {
            try {
                newScreen = (ScreenLayout) newScreenClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                if (animate) {
                    if (newScreen.isAnimateOnPush()) {
                        animate = true;
                    }
                }
                animate = false;
            } catch (Exception e) {
                throw new XLEException(19L, "FIXME: Failed to create a screen of type " + newScreenClass.getName(), e);
            }
        }
        if (getCurrentActivity() != null) {
            animate = animate && getCurrentActivity().isAnimateOnPop();
        }
        final ActivityParameters screenParameters = activityParameters == null ? new ActivityParameters() : activityParameters;
        final NavigationCallbacks callbacks = this.navigationCallbacks;
        XLEAssert.assertNotNull(callbacks);
        if (isRestart) {
            popAndReplaceRunnable = new RestartRunner(screenParameters);
        } else {
            popAndReplaceRunnable = new Runnable() { // from class: com.microsoft.xbox.toolkit.ui.NavigationManager.1
                @Override // java.lang.Runnable
                public void run() {
                    NavigationManager.this.cannotNavigateTripwire = true;
                    ScreenLayout from = NavigationManager.this.getCurrentActivity();
                    screenParameters.putFromScreen(from);
                    screenParameters.putSourcePage(NavigationManager.this.getCurrentActivityName());
                    if (NavigationManager.this.getCurrentActivity() != null) {
                        NavigationManager.this.getCurrentActivity().onSetInactive();
                        NavigationManager.this.getCurrentActivity().onPause();
                        NavigationManager.this.getCurrentActivity().onStop();
                    }
                    for (int i = 0; i < popCount; i++) {
                        NavigationManager.this.getCurrentActivity().onDestroy();
                        callbacks.removeContentViewXLE((ScreenLayout) NavigationManager.this.navigationStack.pop());
                        NavigationManager.this.navigationParameters.pop();
                    }
                    TextureManager.Instance().purgeResourceBitmapCache();
                    ScreenLayout to = null;
                    if (newScreen != null) {
                        if (NavigationManager.this.getCurrentActivity() != null && !newScreen.isKeepPreviousScreen()) {
                            NavigationManager.this.getCurrentActivity().onTombstone();
                        }
                        callbacks.addContentViewXLE((ScreenLayout) NavigationManager.this.navigationStack.push(newScreen));
                        NavigationManager.this.navigationParameters.push(screenParameters);
                        NavigationManager.this.getCurrentActivity().onCreate();
                    } else if (NavigationManager.this.getCurrentActivity() != null) {
                        callbacks.addContentViewXLE(NavigationManager.this.getCurrentActivity());
                        if (NavigationManager.this.getCurrentActivity().getIsTombstoned()) {
                            NavigationManager.this.getCurrentActivity().onRehydrate();
                        }
                    }
                    if (NavigationManager.this.getCurrentActivity() != null) {
                        NavigationManager.this.getCurrentActivity().onStart();
                        NavigationManager.this.getCurrentActivity().onResume();
                        NavigationManager.this.getCurrentActivity().onSetActive();
                        NavigationManager.this.getCurrentActivity().onAnimateInStarted();
                        XboxTcuiSdk.getActivity().invalidateOptionsMenu();
                        to = NavigationManager.this.getCurrentActivity();
                    }
                    if (NavigationManager.this.navigationListener != null) {
                        NavigationManager.this.navigationListener.onPageNavigated(from, to);
                    }
                    NavigationManager.this.cannotNavigateTripwire = false;
                }
            };
        }
        switch (this.animationState) {
            case NONE:
                Transition(goingBack, popAndReplaceRunnable, animate);
                return;
            default:
                ReplaceOnAnimationEnd(goingBack, popAndReplaceRunnable, animate);
                return;
        }
    }

    public void PopTillScreenThenPush(Class<? extends ScreenLayout> cls, Class<? extends ScreenLayout> cls2) throws XLEException {
        PopTillScreenThenPush(cls, cls2, null);
    }

    public void PopTillScreenThenPush(Class<? extends ScreenLayout> cls, Class<? extends ScreenLayout> cls2, ActivityParameters activityParameters) throws XLEException {
        int i;
        boolean z;
        int CountPopsToScreen = CountPopsToScreen(cls);
        if (CountPopsToScreen > 0) {
            i = CountPopsToScreen;
            z = true;
        } else if (CountPopsToScreen < 0) {
            PopScreensAndReplace(0, cls2, true, false, false, activityParameters);
            return;
        } else {
            i = 0;
            z = false;
        }
        PopScreensAndReplace(i, cls2, true, z, false, activityParameters);
    }

    public void PushScreen(Class<? extends ScreenLayout> cls) throws XLEException {
        PushScreen(cls, null);
    }

    public void PushScreen(Class<? extends ScreenLayout> cls, ActivityParameters activityParameters) throws XLEException {
        PopScreensAndReplace(0, cls, true, false, false, activityParameters);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void RestartCurrentScreen(ActivityParameters activityParameters, boolean z) throws XLEException {
        if (this.animationState == NavigationManagerAnimationState.ANIMATING_OUT) {
            OnAnimationEnd();
            return;
        }
        if (this.animationState == NavigationManagerAnimationState.ANIMATING_IN) {
            OnAnimationEnd();
        }
        PopScreensAndReplace(1, getCurrentActivity().getClass(), z, true, true, activityParameters);
    }

    public void RestartCurrentScreen(boolean z) throws XLEException {
        RestartCurrentScreen(null, z);
    }

    public boolean ShouldBackCloseApp() {
        return Size() <= 1 && this.animationState == NavigationManagerAnimationState.NONE;
    }

    public ActivityParameters getActivityParameters() {
        return getActivityParameters(0);
    }

    public ActivityParameters getActivityParameters(int i) {
        XLEAssert.assertTrue(i >= 0 && i < this.navigationParameters.size());
        Stack<ActivityParameters> stack = this.navigationParameters;
        return stack.get((stack.size() - i) - 1);
    }

    public ScreenLayout getCurrentActivity() {
        if (this.navigationStack.empty()) {
            return null;
        }
        return this.navigationStack.peek();
    }

    public String getCurrentActivityName() {
        ScreenLayout currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            return currentActivity.getName();
        }
        return null;
    }

    public ScreenLayout getPreviousActivity() {
        if (this.navigationStack.empty() || this.navigationStack.size() <= 1) {
            return null;
        }
        Stack<ScreenLayout> stack = this.navigationStack;
        return stack.get(stack.size() - 2);
    }

    public boolean isAnimating() {
        return this.animationState != NavigationManagerAnimationState.NONE;
    }

    public void onApplicationPause() {
        for (int i = 0; i < this.navigationStack.size(); i++) {
            this.navigationStack.get(i).onApplicationPause();
        }
    }

    public void onApplicationResume() {
        for (int i = 0; i < this.navigationStack.size(); i++) {
            this.navigationStack.get(i).onApplicationResume();
        }
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == 4 && keyEvent.getAction() == 1) {
            if (!OnBackButtonPressed()) {
                return true;
            }
            removeNavigationCallbacks();
            removeNaviationListener();
        }
        return false;
    }

    public void removeNaviationListener() {
        this.navigationListener = null;
    }

    public void removeNavigationCallbacks() {
        this.navigationCallbacks = null;
    }

    public void setAnimationBlocking(boolean z) {
        NavigationCallbacks navigationCallbacks = this.navigationCallbacks;
        if (navigationCallbacks != null) {
            navigationCallbacks.setAnimationBlocking(z);
        }
    }

    public void setNavigationCallbacks(NavigationCallbacks navigationCallbacks) {
        this.navigationCallbacks = navigationCallbacks;
    }

    public void setOnNavigatedListener(OnNavigatedListener onNavigatedListener) {
        this.navigationListener = onNavigatedListener;
    }
}

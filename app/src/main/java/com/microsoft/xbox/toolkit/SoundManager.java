package com.microsoft.xbox.toolkit;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import com.microsoft.xboxtcui.XboxTcuiSdk;
//import com.unity3d.services.core.device.MimeTypes;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes3.dex */
public class SoundManager {
    private static final int MAX_STREAM_SIZE = 14;
    private static final int NO_LOOP = 0;
    private AudioManager audioManager;
    private Context context;
    private boolean isEnabled;
    private ArrayList<Integer> recentlyPlayedResourceIds;
    private HashMap<Integer, Integer> resourceSoundIdMap;
    private SoundPool soundPool;

    public void clearMostRecentlyPlayedResourceIds() {
    }

    public Integer[] getMostRecentlyPlayedResourceIds() {
        return new Integer[0];
    }

    /* loaded from: classes3.dex */
    private static class SoundManagerHolder {
        public static final SoundManager instance = new SoundManager();

        private SoundManagerHolder() {
        }
    }

    private SoundManager() {
        this.resourceSoundIdMap = new HashMap<>();
        this.recentlyPlayedResourceIds = new ArrayList<>();
        this.isEnabled = false;
        XLEAssert.assertTrue("You must access sound manager on UI thread.", Thread.currentThread() == ThreadManager.UIThread);
        this.context = XboxTcuiSdk.getApplicationContext();
        this.soundPool = new SoundPool(14, 3, 0);
        this.audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static SoundManager getInstance() {
        return SoundManagerHolder.instance;
    }

    public void loadSound(int i) {
        if (this.resourceSoundIdMap.containsKey(Integer.valueOf(i))) {
            return;
        }
        this.resourceSoundIdMap.put(Integer.valueOf(i), Integer.valueOf(this.soundPool.load(this.context, i, 1)));
    }

    public void playSound(int i) {
        int intValue;
        if (this.isEnabled) {
            if (!this.resourceSoundIdMap.containsKey(Integer.valueOf(i))) {
                intValue = this.soundPool.load(this.context, i, 1);
                this.resourceSoundIdMap.put(Integer.valueOf(i), Integer.valueOf(intValue));
            } else {
                intValue = this.resourceSoundIdMap.get(Integer.valueOf(i)).intValue();
            }
            float streamVolume = this.audioManager.getStreamVolume(3) / this.audioManager.getStreamMaxVolume(3);
            this.soundPool.play(intValue, streamVolume, streamVolume, 1, 0, 1.0f);
        }
    }

    public void setEnabled(boolean z) {
        if (this.isEnabled != z) {
            this.isEnabled = z;
        }
    }
}

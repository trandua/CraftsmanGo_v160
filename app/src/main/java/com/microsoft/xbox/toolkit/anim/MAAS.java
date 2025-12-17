package com.microsoft.xbox.toolkit.anim;

import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

/* loaded from: classes3.dex */
public class MAAS {
    private static MAAS instance = new MAAS();
    private final String ASSET_FILENAME = "animation/%sAnimation.xml";
    private final String SDCARD_FILENAME = "/sdcard/bishop/maas/%sAnimation.xml";
    private Hashtable<String, MAASAnimation> maasFileCache = new Hashtable<>();
    private boolean usingSdcard = false;

    /* loaded from: classes3.dex */
    public enum MAASAnimationType {
        ANIMATE_IN,
        ANIMATE_OUT
    }

    public static MAAS getInstance() {
        return instance;
    }

    private MAASAnimation getMAASFile(String str) {
        MAASAnimation loadMAASFile;
        if (!this.maasFileCache.containsKey(str) && (loadMAASFile = loadMAASFile(str)) != null) {
            this.maasFileCache.put(str, loadMAASFile);
        }
        return this.maasFileCache.get(str);
    }

    private MAASAnimation loadMAASFile(String str) {
        InputStream open;
        try {
            if (this.usingSdcard) {
                open = new FileInputStream(new File(String.format("/sdcard/bishop/maas/%sAnimation.xml", str)));
            } else {
                open = XboxTcuiSdk.getAssetManager().open(String.format("animation/%sAnimation.xml", str));
            }
            return (MAASAnimation) XMLHelper.instance().load(open, MAASAnimation.class);
        } catch (Exception unused) {
            return null;
        }
    }

    public MAASAnimation getAnimation(String str) {
        if (str != null) {
            return getMAASFile(str);
        }
        throw new IllegalArgumentException();
    }
}

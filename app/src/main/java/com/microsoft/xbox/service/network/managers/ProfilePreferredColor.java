package com.microsoft.xbox.service.network.managers;

import androidx.core.view.ViewCompat;
import com.google.gson.annotations.SerializedName;

/* loaded from: classes3.dex */
public class ProfilePreferredColor {
    @SerializedName("primaryColor")
    private String primaryColorString;
    @SerializedName("secondaryColor")
    private String secondaryColorString;
    @SerializedName("tertiaryColor")
    private String tertiaryColorString;
    private int primary = -1;
    private int secondary = -1;
    private int tertiary = -1;

    public static int convertColorFromString(String str) {
        if (str == null) {
            return 0;
        }
        if (str.startsWith("#")) {
            str = str.substring(1);
        }
        int parseInt = Integer.parseInt(str, 16);
        return (parseInt >> 24) == 0 ? parseInt | ViewCompat.MEASURED_STATE_MASK : parseInt;
    }

    public int getPrimaryColor() {
        if (this.primary < 0) {
            this.primary = convertColorFromString(this.primaryColorString);
        }
        return this.primary;
    }

    public int getSecondaryColor() {
        if (this.secondary < 0) {
            this.secondary = convertColorFromString(this.secondaryColorString);
        }
        return this.secondary;
    }

    public int getTertiaryColor() {
        if (this.tertiary < 0) {
            this.tertiary = convertColorFromString(this.tertiaryColorString);
        }
        return this.tertiary;
    }
}

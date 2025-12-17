package com.microsoft.xbox.toolkit;

import android.view.View;
//import com.crafting.minecrafting.lokicraft.R;
import com.craftsman.go.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.lang.reflect.Field;

/* loaded from: classes3.dex */
public class XLERValueHelper {
    public static int findDimensionIdByName(String str) {
        Field field;
        try {
            field = R.dimen.class.getField(str);
        } catch (NoSuchFieldException unused) {
            field = null;
        }
        if (field == null) {
            return -1;
        }
        try {
            return field.getInt(null);
        } catch (IllegalAccessException unused2) {
            return -1;
        }
    }

    public static View findViewByString(String str) {
        Field field;
        int i = -1;
        try {
            field = R.id.class.getField(str);
        } catch (NoSuchFieldException unused) {
            field = null;
        }
        if (field != null) {
            try {
                i = field.getInt(null);
            } catch (IllegalAccessException unused2) {
            }
            return XboxTcuiSdk.getActivity().findViewById(i);
        }
        i = -1;
        return XboxTcuiSdk.getActivity().findViewById(i);
    }

    protected static Class getColorRClass() {
        return R.color.class;
    }

    public static int getColorRValue(String str) {
        try {
            return getColorRClass().getDeclaredField(str).getInt(null);
        } catch (Exception unused) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getDimenRClass() {
        return R.dimen.class;
    }

    public static int getDimenRValue(String str) {
        try {
            return getDimenRClass().getDeclaredField(str).getInt(null);
        } catch (Exception unused) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getDrawableRClass() {
        return R.drawable.class;
    }

    public static int getDrawableRValue(String str) {
        try {
            return getDrawableRClass().getDeclaredField(str).getInt(null);
        } catch (Exception unused) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getIdRClass() {
        return R.id.class;
    }

    public static int getIdRValue(String str) {
        try {
            return getIdRClass().getDeclaredField(str).getInt(null);
        } catch (Exception unused) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getLayoutRClass() {
        return R.layout.class;
    }

    public static int getLayoutRValue(String str) {
        try {
            return getLayoutRClass().getDeclaredField(str).getInt(null);
        } catch (Exception unused) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getStringRClass() {
        return R.string.class;
    }

    public static int getStringRValue(String str) {
        try {
            return getStringRClass().getDeclaredField(str).getInt(null);
        } catch (Exception unused) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getStyleRClass() {
        return R.style.class;
    }

    public static int getStyleRValue(String str) {
        try {
            return getStyleRClass().getDeclaredField(str).getInt(null);
        } catch (Exception unused) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    protected static Class getStyleableRClass() {
        return R.styleable.class;
    }

    public static int getStyleableRValue(String str) {
        try {
            return getStyleableRClass().getDeclaredField(str).getInt(null);
        } catch (Exception unused) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return -1;
        }
    }

    public static int[] getStyleableRValueArray(String str) {
        try {
            return (int[]) getStyleableRClass().getDeclaredField(str).get(null);
        } catch (Exception unused) {
            XLEAssert.assertTrue("Can't find " + str, false);
            return null;
        }
    }
}

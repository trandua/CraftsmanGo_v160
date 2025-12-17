package com.mojang.minecraftpe.input;

import android.view.InputDevice;
//import com.crafting.minecrafting.lokicraft.StringFog;
//import com.craftsman.go.StringFog;

import java.io.File;

/* loaded from: classes3.dex */
public class InputCharacteristics {
    public static boolean allControllersHaveDoubleTriggers() {
        boolean z = false;
        for (int i : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(i);
            if (device != null && !device.isVirtual() && device.getControllerNumber() > 0 && (device.getSources() & 1025) != 0) {
                boolean[] hasKeys = device.hasKeys(102, 103, 104, 105);
                boolean z2 = true;
                boolean z3 = hasKeys.length == 4;
                int i2 = 0;
                while (true) {
                    if (i2 >= hasKeys.length) {
                        break;
                    } else if (!hasKeys[i2]) {
                        z3 = false;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (!z3 && hasKeys[0] && hasKeys[1]) {
                    if ((device.getMotionRange(17) == null && device.getMotionRange(23) == null) || (device.getMotionRange(18) == null && device.getMotionRange(22) == null)) {
                        z2 = false;
                    }
                    z3 = z2;
                }
                z = (z3 && device.getName().contains("EI-GP20")) ? false : z3;
                if (!z) {
                    break;
                }
            }
        }
        return z;
    }

    public static boolean isCreteController(int i) {
        InputDevice device = InputDevice.getDevice(i);
        if (device == null || device.isVirtual() || device.getControllerNumber() <= 0 || (device.getSources() & 1025) == 0 || device.getVendorId() != 1118 || device.getProductId() != 736) {
            return false;
        }
        String[] strArr = {"/system/usr/keylayout/Vendor_045e_Product_02e0.kl", "/data/system/devices/keylayout/Vendor_045e_Product_02e0.kl"};
        for (int i2 = 0; i2 < 2; i2++) {
            if (new File(strArr[i2]).exists()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isXboxController(int i) {
        InputDevice device = InputDevice.getDevice(i);
        return device != null && (device.getSources() & 1025) == 1025 && device.getVendorId() == 1118;
    }

    public static boolean isPlaystationController(int i) {
        InputDevice device = InputDevice.getDevice(i);
        return device != null && (device.getSources() & 1025) == 1025 && device.getVendorId() == 1356;
    }
}

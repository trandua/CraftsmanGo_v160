package com.mojang.minecraftpe.input;

import android.content.Context;
import android.hardware.input.InputManager;
//import com.craftsman.go.StringFog;

/* loaded from: classes3.dex */
public class JellyBeanDeviceManager extends InputDeviceManager implements InputManager.InputDeviceListener {
    private final InputManager inputManager;

    public native void onInputDeviceAddedNative(int i);

    public native void onInputDeviceChangedNative(int i);

    public native void onInputDeviceRemovedNative(int i);

    public native void setCreteControllerNative(int i, boolean z);

    public native void setDoubleTriggersSupportedNative(boolean z);

    public native void setFoundPlaystationControllerNative(boolean z);

    public native void setFoundXboxControllerNative(boolean z);

    public JellyBeanDeviceManager(Context context) {
        this.inputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
    }

    @Override // com.mojang.minecraftpe.input.InputDeviceManager
    public void register() {
        int[] inputDeviceIds = this.inputManager.getInputDeviceIds();
        this.inputManager.registerInputDeviceListener(this, null);
        setDoubleTriggersSupportedNative(InputCharacteristics.allControllersHaveDoubleTriggers());
        for (int i : inputDeviceIds) {
            setCreteControllerNative(i, InputCharacteristics.isCreteController(i));
        }
        checkForXboxAndPlaystationController();
    }

    @Override // com.mojang.minecraftpe.input.InputDeviceManager
    public void unregister() {
        this.inputManager.unregisterInputDeviceListener(this);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(int i) {
        onInputDeviceAddedNative(i);
        setDoubleTriggersSupportedNative(InputCharacteristics.allControllersHaveDoubleTriggers());
        setCreteControllerNative(i, InputCharacteristics.isCreteController(i));
        if (InputCharacteristics.isXboxController(i)) {
            setFoundXboxControllerNative(true);
        } else if (InputCharacteristics.isPlaystationController(i)) {
            setFoundPlaystationControllerNative(true);
        }
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int i) {
        onInputDeviceChangedNative(i);
        setDoubleTriggersSupportedNative(InputCharacteristics.allControllersHaveDoubleTriggers());
        setCreteControllerNative(i, InputCharacteristics.isCreteController(i));
        checkForXboxAndPlaystationController();
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int i) {
        onInputDeviceRemovedNative(i);
        setDoubleTriggersSupportedNative(InputCharacteristics.allControllersHaveDoubleTriggers());
        setCreteControllerNative(i, InputCharacteristics.isCreteController(i));
        checkForXboxAndPlaystationController();
    }

    public void checkForXboxAndPlaystationController() {
        int[] inputDeviceIds;
        boolean z = false;
        boolean z2 = false;
        for (int i : this.inputManager.getInputDeviceIds()) {
            z |= InputCharacteristics.isXboxController(i);
            z2 |= InputCharacteristics.isPlaystationController(i);
            if (z && z2) {
                break;
            }
        }
        setFoundXboxControllerNative(z);
        setFoundPlaystationControllerNative(z2);
    }
}

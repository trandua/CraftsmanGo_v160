package com.microsoft.xbox.toolkit.ui;

/* loaded from: classes3.dex */
public class TextureManagerScaledResourceBitmapRequest {
    public final TextureBindingOption bindingOption;
    public final int resourceId;

    public TextureManagerScaledResourceBitmapRequest(int i) {
        this(i, new TextureBindingOption());
    }

    public TextureManagerScaledResourceBitmapRequest(int i, TextureBindingOption textureBindingOption) {
        this.resourceId = i;
        this.bindingOption = textureBindingOption;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TextureManagerScaledResourceBitmapRequest) {
            TextureManagerScaledResourceBitmapRequest textureManagerScaledResourceBitmapRequest = (TextureManagerScaledResourceBitmapRequest) obj;
            return this.resourceId == textureManagerScaledResourceBitmapRequest.resourceId && this.bindingOption.equals(textureManagerScaledResourceBitmapRequest.bindingOption);
        }
        return false;
    }

    public int hashCode() {
        return this.resourceId;
    }
}

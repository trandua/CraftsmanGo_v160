package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import java.net.URI;

/* loaded from: classes3.dex */
public class XLEImageViewFast extends XLEImageView {
    private TextureBindingOption option;
    protected int pendingBitmapResourceId;
    private String pendingFilePath;
    protected URI pendingUri;
    private boolean useFileCache;

    public XLEImageViewFast(Context context) {
        super(context);
        this.pendingBitmapResourceId = -1;
        this.pendingFilePath = null;
        this.pendingUri = null;
        this.useFileCache = true;
        setSoundEffectsEnabled(false);
    }

    public XLEImageViewFast(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.pendingBitmapResourceId = -1;
        this.pendingFilePath = null;
        this.pendingUri = null;
        this.useFileCache = true;
        if (isInEditMode()) {
            return;
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("XLEImageViewFast"));
        setImageResource(obtainStyledAttributes.getResourceId(XLERValueHelper.getStyleableRValue("XLEImageViewFast_src"), -1));
        obtainStyledAttributes.recycle();
        setSoundEffectsEnabled(false);
    }

    private void bindToFilePath(String str) {
        this.pendingFilePath = null;
        TextureManager.Instance().bindToViewFromFile(str, this, getWidth(), getHeight());
    }

    private void bindToResourceId(int i) {
        this.pendingBitmapResourceId = -1;
        TextureManager.Instance().bindToView(i, this, getWidth(), getHeight());
    }

    private void bindToUri(URI uri, TextureBindingOption textureBindingOption) {
        this.pendingUri = null;
        this.option = null;
        TextureManager.Instance().bindToView(uri, this, textureBindingOption);
    }

    public void bindToUri(URI uri) {
        this.pendingUri = null;
        bindToUri(uri, new TextureBindingOption(getWidth(), getHeight(), this.useFileCache));
    }

    public boolean hasSize() {
        return getWidth() > 0 && getHeight() > 0;
    }

    @Override // android.widget.ImageView, android.view.View
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(resolveSize(0, i), resolveSize(0, i2));
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (hasSize()) {
            int i5 = this.pendingBitmapResourceId;
            if (i5 >= 0) {
                bindToResourceId(i5);
            }
            URI uri = this.pendingUri;
            if (uri != null || (uri == null && this.option != null)) {
                if (this.option != null) {
                    bindToUri(uri, new TextureBindingOption(getWidth(), getHeight(), this.option.resourceIdForLoading, this.option.resourceIdForError, this.option.useFileCache));
                } else {
                    bindToUri(uri);
                }
            }
            String str = this.pendingFilePath;
            if (str != null) {
                bindToFilePath(str);
            }
        }
    }

    public void setImageFilePath(String str) {
        if (hasSize()) {
            bindToFilePath(str);
        } else {
            this.pendingFilePath = str;
        }
    }

    @Override // android.widget.ImageView
    public void setImageResource(int i) {
        if (hasSize()) {
            bindToResourceId(i);
        } else {
            this.pendingBitmapResourceId = i;
        }
    }

    @Override // android.widget.ImageView
    public void setImageURI(Uri uri) {
        throw new UnsupportedOperationException();
    }

    public void setImageURI2(URI uri) {
        if (hasSize()) {
            bindToUri(uri);
        } else {
            this.pendingUri = uri;
        }
    }

    public void setImageURI2(URI uri, int i, int i2) {
        this.option = new TextureBindingOption(getWidth(), getHeight(), i, i2, this.useFileCache);
        if (hasSize()) {
            bindToUri(uri, this.option);
        } else {
            this.pendingUri = uri;
        }
    }

    public void setImageURI2(URI uri, boolean z) {
        this.useFileCache = z;
        this.option = new TextureBindingOption(getWidth(), getHeight(), this.useFileCache);
        if (hasSize()) {
            bindToUri(uri, this.option);
        } else {
            this.pendingUri = uri;
        }
    }

    @Override // com.microsoft.xbox.toolkit.ui.XLEImageView, android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        super.setOnClickListener(TouchUtil.createOnClickListener(onClickListener));
    }
}

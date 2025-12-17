package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.ui.XLETextArg;
import java.net.URI;
import java.net.URISyntaxException;

/* loaded from: classes3.dex */
public class XLEUniversalImageView extends XLEImageView {
    private static final int JELLY_BEAN_MR1 = 17;
    private static final String TAG = "XLEUniversalImageView";
    private boolean adjustViewBounds;
    public Params arg;
    private final View.OnLayoutChangeListener listener;
    private int maxHeight;
    private int maxWidth;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class C54942 {
        static final int[] f12622x6e56e010;

        C54942() {
        }

        static {
            int[] iArr = new int[TypefaceXml.values().length];
            f12622x6e56e010 = iArr;
            try {
                iArr[TypefaceXml.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f12622x6e56e010[TypefaceXml.SANS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f12622x6e56e010[TypefaceXml.SERIF.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                f12622x6e56e010[TypefaceXml.MONOSPACE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class Params {
        private final XLETextArg argText;
        private final XLEURIArg argUri;
        private final boolean hasSrc;

        public Params() {
            this(new XLETextArg(new XLETextArg.Params()), null, false);
        }

        public Params(XLETextArg xLETextArg, XLEURIArg xLEURIArg) {
            this(xLETextArg, xLEURIArg, false);
        }

        private Params(XLETextArg xLETextArg, XLEURIArg xLEURIArg, boolean z) {
            this.argText = xLETextArg;
            this.argUri = xLEURIArg;
            this.hasSrc = z;
        }

        public Params(XLETextArg xLETextArg, boolean z) {
            this(xLETextArg, null, z);
        }

        public Params cloneWithText(String str) {
            return new Params(new XLETextArg(str, this.argText.getParams()), null, this.hasSrc);
        }

        public Params cloneEmpty() {
            return new Params(new XLETextArg(this.argText.getParams()), null, false);
        }

        public Params cloneWithSrc(boolean z) {
            return new Params(new XLETextArg(this.argText.getParams()), null, z);
        }

        public Params cloneWithUri(URI uri) {
            XLEURIArg xLEURIArg = this.argUri;
            int loadingResourceId = xLEURIArg == null ? -1 : xLEURIArg.getLoadingResourceId();
            XLEURIArg xLEURIArg2 = this.argUri;
            return cloneWithUri(uri, loadingResourceId, xLEURIArg2 != null ? xLEURIArg2.getErrorResourceId() : -1);
        }

        public Params cloneWithUri(URI uri, int i, int i2) {
            return new Params(new XLETextArg(this.argText.getParams()), new XLEURIArg(uri, i, i2), this.hasSrc);
        }

        public XLETextArg getArgText() {
            return this.argText;
        }

        public XLEURIArg getArgUri() {
            return this.argUri;
        }

        public boolean hasArgUri() {
            return this.argUri != null;
        }

        public boolean hasSrc() {
            return this.hasSrc;
        }

        public boolean hasText() {
            return this.argText.hasText();
        }
    }

    /* loaded from: classes3.dex */
    public enum TypefaceXml {
        NORMAL,
        SANS,
        SERIF,
        MONOSPACE;

        public static TypefaceXml fromIndex(int i) {
            TypefaceXml[] values = values();
            if (i < 0 || i >= values.length) {
                return null;
            }
            return values[i];
        }

        public static Typeface typefaceFromIndex(int i) {
            TypefaceXml fromIndex = fromIndex(i);
            if (fromIndex == null) {
                return null;
            }
            int i2 = C54942.f12622x6e56e010[fromIndex.ordinal()];
            if (i2 == 2) {
                return Typeface.SANS_SERIF;
            }
            if (i2 == 3) {
                return Typeface.SERIF;
            }
            if (i2 != 4) {
                return null;
            }
            return Typeface.MONOSPACE;
        }
    }

    public XLEUniversalImageView(Context context) {
        this(context, new Params());
    }

    public XLEUniversalImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.listener = new View.OnLayoutChangeListener() { // from class: com.microsoft.xbox.toolkit.ui.XLEUniversalImageView.1
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                if (!(i3 - i == i7 - i5 && i4 - i2 == i8 - i6) && XLEUniversalImageView.this.arg.hasText()) {
                    new XLETextTask(XLEUniversalImageView.this).execute(XLEUniversalImageView.this.arg.getArgText());
                }
            }
        };
        this.arg = initializeAttributes(context, attributeSet, 0);
        updateImage();
    }

    public XLEUniversalImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.listener = new View.OnLayoutChangeListener() { // from class: com.microsoft.xbox.toolkit.ui.XLEUniversalImageView.2
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                if (!(i4 - i2 == i8 - i6 && i5 - i3 == i9 - i7) && XLEUniversalImageView.this.arg.hasText()) {
                    new XLETextTask(XLEUniversalImageView.this).execute(XLEUniversalImageView.this.arg.getArgText());
                }
            }
        };
        this.arg = initializeAttributes(context, attributeSet, i);
        updateImage();
    }

    public XLEUniversalImageView(Context context, Params params) {
        super(context);
        this.listener = new View.OnLayoutChangeListener() { // from class: com.microsoft.xbox.toolkit.ui.XLEUniversalImageView.3
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                if (!(i3 - i == i7 - i5 && i4 - i2 == i8 - i6) && XLEUniversalImageView.this.arg.hasText()) {
                    new XLETextTask(XLEUniversalImageView.this).execute(XLEUniversalImageView.this.arg.getArgText());
                }
            }
        };
        setMaxWidth(Integer.MAX_VALUE);
        setMaxHeight(Integer.MAX_VALUE);
        this.arg = params;
    }

    private Params initializeAttributes(Context context, AttributeSet attributeSet, int i) {
        Params params;
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray(TAG), i, 0);
        String str = null;
        try {
            float dimension = obtainStyledAttributes.getDimension(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_textSize"), context.getResources().getDisplayMetrics().scaledDensity * 8.0f);
            int color = obtainStyledAttributes.getColor(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_textColor"), 0);
            int i2 = obtainStyledAttributes.getInt(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_typeface"), -1);
            int i3 = obtainStyledAttributes.getInt(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_textStyle"), 0);
            String string = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_typefaceSource"));
            Typeface create = string == null ? Typeface.create(TypefaceXml.typefaceFromIndex(i2), i3) : FontManager.Instance().getTypeface(context, string);
            int color2 = obtainStyledAttributes.getColor(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_eraseColor"), 0);
            boolean z = obtainStyledAttributes.getBoolean(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_adjustForImageSize"), false);
            boolean hasValue = obtainStyledAttributes.hasValue(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_src"));
            XLETextArg.Params params2 = new XLETextArg.Params(dimension, color, create, color2, z, obtainStyledAttributes.hasValue(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_textAspectRatio")) ? Float.valueOf(obtainStyledAttributes.getFloat(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_textAspectRatio"), 0.0f)) : null);
            String string2 = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_android_text"));
            if (string2 != null) {
                params = new Params(new XLETextArg(string2, params2), false);
            } else {
                str = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("XLEUniversalImageView_uri"));
                params = str != null ? new Params(new XLETextArg(params2), new XLEURIArg(new URI(str))) : new Params(new XLETextArg(params2), hasValue);
            }
            if (z) {
                addOnLayoutChangeListener(this.listener);
            }
            obtainStyledAttributes.recycle();
            return params;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error parsing URI '" + str + "'", e);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    private int resolveAdjustedSize(int i, int i2, int i3) {
        int mode = View.MeasureSpec.getMode(i3);
        int size = View.MeasureSpec.getSize(i3);
        if (mode == Integer.MIN_VALUE) {
            i = Math.min(i, size);
        } else if (mode != 0) {
            return mode != 1073741824 ? i : size;
        }
        return Math.min(i, i2);
    }

    private void updateImage() {
        if (this.arg.hasText()) {
            new XLETextTask(this).execute(this.arg.getArgText());
        } else if (this.arg.hasArgUri()) {
            TextureManager.Instance().bindToView(this.arg.getArgUri().getUri(), this, this.arg.getArgUri().getTextureBindingOption());
        } else if (this.arg.hasSrc()) {
        } else {
            setImageDrawable(null);
        }
    }

    public void clearImage() {
        this.arg = this.arg.cloneEmpty();
        updateImage();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        Drawable drawable = getDrawable();
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth <= 0) {
                intrinsicWidth = 1;
            }
            if (intrinsicHeight <= 0) {
                intrinsicHeight = 1;
            }
            if (this.adjustViewBounds) {
                boolean z = mode != 1073741824;
                boolean z2 = mode2 != 1073741824;
                if (z && !z2) {
                    setMeasuredDimension(View.MeasureSpec.getSize(i), (int) Math.ceil(intrinsicHeight * (View.MeasureSpec.getSize(i) / intrinsicWidth)));
                } else if (!z && z2) {
                    setMeasuredDimension((int) Math.ceil(intrinsicWidth * (View.MeasureSpec.getSize(i2) / intrinsicHeight)), View.MeasureSpec.getSize(i2));
                } else {
                    super.onMeasure(i, i2);
                }
            }
        }
    }

    @Override // android.widget.ImageView
    public void setAdjustViewBounds(boolean z) {
        this.adjustViewBounds = z;
        super.setAdjustViewBounds(z);
    }

    public void setImageURI2(URI uri) {
        this.arg = this.arg.cloneWithUri(uri);
        updateImage();
    }

    public void setImageURI2(URI uri, int i, int i2) {
        this.arg = this.arg.cloneWithUri(uri, i, i2);
        updateImage();
    }

    @Override // android.widget.ImageView
    public void setMaxHeight(int i) {
        super.setMaxHeight(i);
        this.maxHeight = i;
    }

    @Override // android.widget.ImageView
    public void setMaxWidth(int i) {
        super.setMaxWidth(i);
        this.maxWidth = i;
    }

    public void setText(int i) {
        setText(getResources().getString(i));
    }

    public void setText(String str) {
        if (TextUtils.equals(str, this.arg.getArgText().getText())) {
            return;
        }
        this.arg = this.arg.cloneWithText(str);
        updateImage();
    }
}

package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/* loaded from: classes3.dex */
public class CircleImageView extends ImageView {
    static final boolean $assertionsDisabled = false;

    public CircleImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CircleImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private Bitmap createBitmap(Drawable drawable) {
        Bitmap createBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    private Bitmap createRoundBitmap(Bitmap bitmap, int i) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle((bitmap.getWidth() / 2) + 0.7f, (bitmap.getHeight() / 2) + 0.7f, (bitmap.getWidth() / 2) + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    private void drawBitmap(Canvas canvas, Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        int min = Math.min(getWidth(), getHeight());
        if (bitmap.getWidth() == min && bitmap.getHeight() == min) {
            drawRoundBitmap(canvas, bitmap, min);
            return;
        }
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, min, min, false);
        try {
            drawRoundBitmap(canvas, createScaledBitmap, min);
        } finally {
            createScaledBitmap.recycle();
        }
    }

    private void drawRoundBitmap(Canvas canvas, Bitmap bitmap, int i) {
        Bitmap createRoundBitmap = createRoundBitmap(bitmap, i);
        try {
            Paint paint = null;
            canvas.drawBitmap(createRoundBitmap, 0.0f, 0.0f, (Paint) null);
        } finally {
            createRoundBitmap.recycle();
        }
    }

    @Override // android.widget.ImageView, android.view.View
    public void onDraw(Canvas canvas) {
        Drawable drawable;
        if (getWidth() == 0 || getHeight() == 0 || (drawable = getDrawable()) == null) {
            return;
        }
        if (drawable instanceof BitmapDrawable) {
            drawBitmap(canvas, ((BitmapDrawable) drawable).getBitmap());
            return;
        }
        Bitmap createBitmap = createBitmap(drawable);
        try {
            drawBitmap(canvas, createBitmap);
        } finally {
            createBitmap.recycle();
        }
    }
}

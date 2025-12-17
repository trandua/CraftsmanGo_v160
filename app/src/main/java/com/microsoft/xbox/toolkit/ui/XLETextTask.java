package com.microsoft.xbox.toolkit.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.text.TextPaint;
import android.widget.ImageView;
import com.microsoft.xbox.toolkit.ui.XLETextArg;
import java.lang.ref.WeakReference;

/* loaded from: classes3.dex */
public class XLETextTask extends AsyncTask<XLETextArg, Void, Bitmap> {
    private static final String TAG = "XLETextTask";
    private final WeakReference<ImageView> img;
    private final int imgHeight;
    private final int imgWidth;

    public XLETextTask(ImageView imageView) {
        this.img = new WeakReference<>(imageView);
        this.imgWidth = imageView.getWidth();
        this.imgHeight = imageView.getHeight();
    }

    @Override // android.os.AsyncTask
    public Bitmap doInBackground(XLETextArg... xLETextArgArr) {
        int i;
        int i2;
        if (xLETextArgArr.length <= 0) {
            return null;
        }
        XLETextArg xLETextArg = xLETextArgArr[0];
        XLETextArg.Params params = xLETextArg.getParams();
        String text = xLETextArg.getText();
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(params.getTextSize());
        textPaint.setAntiAlias(true);
        textPaint.setColor(params.getColor());
        textPaint.setTypeface(params.getTypeface());
        int round = Math.round(textPaint.measureText(text));
        int round2 = Math.round(textPaint.descent() - textPaint.ascent());
        if (params.isAdjustForImageSize()) {
            i = Math.max(round, this.imgWidth);
            i2 = Math.max(round2, this.imgHeight);
        } else {
            i = round;
            i2 = round2;
        }
        if (params.hasTextAspectRatio()) {
            float floatValue = params.getTextAspectRatio().floatValue();
            if (floatValue > 0.0f) {
                float f = i2;
                float f2 = i * floatValue;
                if (f > f2) {
                    i = (int) (f / floatValue);
                } else {
                    i2 = (int) f2;
                }
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        if (params.hasEraseColor()) {
            createBitmap.eraseColor(params.getEraseColor());
        }
        new Canvas(createBitmap).drawText(text, (Math.max(0, i - round) / 2) + 0, (-textPaint.ascent()) + (Math.max(0, i2 - round2) / 2), textPaint);
        return createBitmap;
    }

    @Override // android.os.AsyncTask
    public void onPostExecute(Bitmap bitmap) {
        ImageView imageView = this.img.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}

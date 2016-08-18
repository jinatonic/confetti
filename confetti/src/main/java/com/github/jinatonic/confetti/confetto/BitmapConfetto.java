package com.github.jinatonic.confetti.confetto;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class BitmapConfetto extends Confetto {
    private final Bitmap bitmap;
    private final float bitmapCenterX, bitmapCenterY;

    public BitmapConfetto(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.bitmapCenterX = bitmap.getWidth() / 2f;
        this.bitmapCenterY = bitmap.getHeight() / 2f;
    }

    @Override
    protected void drawInternal(Canvas canvas, Matrix matrix, Paint paint, float x, float y,
            float rotation) {
        matrix.preTranslate(x, y);
        matrix.preRotate(rotation, bitmapCenterX, bitmapCenterY);
        canvas.drawBitmap(bitmap, matrix, paint);
    }
}

package com.github.jinatonic.confetti.confetti;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * A lightly more optimal way to draw a circle shape that doesn't require the use of a bitmap.
 */
public class CircleConfetti extends Confetti {
    private final int color;
    private final float radius;

    public CircleConfetti(int color, float radius) {
        this.color = color;
        this.radius = radius;
    }

    @Override
    protected void configurePaint(Paint paint) {
        super.configurePaint(paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }

    @Override
    protected void drawInternal(Canvas canvas, Matrix matrix, Paint paint, float x, float y,
            float rotation) {
        canvas.drawCircle(x + radius, y + radius, radius, paint);
    }
}

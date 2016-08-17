package com.github.jinatonic.confetti.confetti;

import android.animation.ArgbEvaluator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.SystemClock;

import java.util.Random;

public class ShimmeringConfetti extends BitmapConfetti {
    private final ArgbEvaluator evaluator = new ArgbEvaluator();
    private final int fromColor, toColor;
    private final long waveLength, halfWaveLength;
    private final long randomStart;

    public ShimmeringConfetti(Bitmap bitmap, int fromColor, int toColor, long waveLength,
            Random random) {
        super(bitmap);
        this.fromColor = fromColor;
        this.toColor = toColor;
        this.waveLength = waveLength;
        this.halfWaveLength = waveLength / 2;

        final long currentTime = SystemClock.elapsedRealtime();
        this.randomStart = currentTime - random.nextInt((int) currentTime);
    }

    @Override
    protected void drawInternal(Canvas canvas, Matrix matrix, Paint paint, float x, float y,
            float rotation) {
        final long currTime = SystemClock.elapsedRealtime();
        final long fraction = (currTime - randomStart) % waveLength;
        final float animated = fraction < halfWaveLength
                ? (float) fraction / halfWaveLength
                : ((float) waveLength - fraction) / halfWaveLength;

        final int color = (int) evaluator.evaluate(animated, fromColor, toColor);
        final ColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(colorFilter);
        super.drawInternal(canvas, matrix, paint, x, y, rotation);
    }
}

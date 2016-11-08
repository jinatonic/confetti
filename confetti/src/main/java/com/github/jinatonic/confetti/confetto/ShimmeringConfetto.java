/**
 * Copyright (C) 2016 Robinhood Markets, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jinatonic.confetti.confetto;

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

public class ShimmeringConfetto extends BitmapConfetto {
    private final ArgbEvaluator evaluator = new ArgbEvaluator();
    private final int fromColor, toColor;
    private final long waveLength, halfWaveLength;
    private final long randomStart;

    public ShimmeringConfetto(Bitmap bitmap, int fromColor, int toColor, long waveLength,
            Random random) {
        super(bitmap);
        this.fromColor = fromColor;
        this.toColor = toColor;
        this.waveLength = waveLength;
        this.halfWaveLength = waveLength / 2;

        final int currentTime = Math.abs((int) SystemClock.elapsedRealtime());
        this.randomStart = currentTime - random.nextInt(currentTime);
    }

    @Override
    protected void drawInternal(Canvas canvas, Matrix matrix, Paint paint, float x, float y,
            float rotation, float percentageAnimated) {
        final long currTime = SystemClock.elapsedRealtime();
        final long fraction = (currTime - randomStart) % waveLength;
        final float animated = fraction < halfWaveLength
                ? (float) fraction / halfWaveLength
                : ((float) waveLength - fraction) / halfWaveLength;

        final int color = (int) evaluator.evaluate(animated, fromColor, toColor);
        final ColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(colorFilter);
        super.drawInternal(canvas, matrix, paint, x, y, rotation, percentageAnimated);
    }
}

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

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * A lightly more optimal way to draw a circle shape that doesn't require the use of a bitmap.
 */
public class CircleConfetto extends Confetto {
    private final int color;
    private final float radius;
    private final int diamater;

    public CircleConfetto(int color, float radius) {
        this.color = color;
        this.radius = radius;
        this.diamater = (int) (this.radius * 2);
    }

    @Override
    public int getWidth() {
        return diamater;
    }

    @Override
    public int getHeight() {
        return diamater;
    }

    @Override
    protected void configurePaint(Paint paint) {
        super.configurePaint(paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }

    @Override
    protected void drawInternal(Canvas canvas, Matrix matrix, Paint paint, float x, float y,
            float rotation, float percentageAnimated) {
        canvas.drawCircle(x + radius, y + radius, radius, paint);
    }
}

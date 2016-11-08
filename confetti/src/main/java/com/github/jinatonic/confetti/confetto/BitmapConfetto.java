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
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    protected void drawInternal(Canvas canvas, Matrix matrix, Paint paint, float x, float y,
            float rotation, float percentageAnimated) {
        matrix.preTranslate(x, y);
        matrix.preRotate(rotation, bitmapCenterX, bitmapCenterY);
        canvas.drawBitmap(bitmap, matrix, paint);
    }
}

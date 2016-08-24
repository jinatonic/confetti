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

package com.github.jinatonic.confetti;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final Paint PAINT = new Paint();
    static {
        PAINT.setStyle(Paint.Style.FILL);
    }

    private static Interpolator defaultAlphaInterpolator;
    public static Interpolator getDefaultAlphaInterpolator() {
        if (defaultAlphaInterpolator == null) {
            defaultAlphaInterpolator = new Interpolator() {
                @Override
                public float getInterpolation(float v) {
                    return v >= 0.9f ? 1f - (v - 0.9f) * 10f : 1f;
                }
            };
        }
        return defaultAlphaInterpolator;
    }

    public static List<Bitmap> generateConfettiBitmaps(int[] colors, int size) {
        final List<Bitmap> bitmaps = new ArrayList<>();
        for (int color : colors) {
            bitmaps.add(createCircleBitmap(color, size));
            bitmaps.add(createSquareBitmap(color, size));
            bitmaps.add(createTriangleBitmap(color, size));
        }
        return bitmaps;
    }

    public static Bitmap createCircleBitmap(int color, int size) {
        final Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        PAINT.setColor(color);

        final float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, PAINT);
        return bitmap;
    }

    public static Bitmap createSquareBitmap(int color, int size) {
        final Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        PAINT.setColor(color);

        final Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(size, 0);
        path.lineTo(size, size);
        path.lineTo(0, size);
        path.close();

        canvas.drawPath(path, PAINT);
        return bitmap;
    }

    public static Bitmap createTriangleBitmap(int color, int size) {
        final Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        PAINT.setColor(color);

        // Generate equilateral triangle (http://mathworld.wolfram.com/EquilateralTriangle.html).
        final Path path = new Path();
        final float point = (float) Math.tan(15f / 180f * Math.PI) * size;
        path.moveTo(0, 0);
        path.lineTo(size, point);
        path.lineTo(point, size);
        path.close();

        canvas.drawPath(path, PAINT);
        return bitmap;
    }
}

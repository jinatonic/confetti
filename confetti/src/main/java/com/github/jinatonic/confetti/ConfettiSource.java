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

/**
 * The source from which confetti will appear. This can be either a line or a point.
 *
 * <p>Please note that the specified source represents the top left corner of the drawn
 * confetti. If you want the confetti to appear from off-screen, you'll have to offset it
 * with the confetti's size.
 */
public class ConfettiSource {
    public final int x0, y0, x1, y1;

    /**
     * Specifies a point source from which all confetti will emit from.
     *
     * @param x x-coordinate of the point relative to the {@link ConfettiView}'s parent.
     * @param y y-coordinate of the point relative to the {@link ConfettiView}'s parent.
     */
    public ConfettiSource(int x, int y) {
        this(x, y, x, y);
    }

    /**
     * Specifies a line source from which all confetti will emit from.
     *
     * @param x0 x-coordinate of the first point relative to the {@link ConfettiView}'s parent.
     * @param y0 y-coordinate of the first point relative to the {@link ConfettiView}'s parent.
     * @param x1 x-coordinate of the second point relative to the {@link ConfettiView}'s parent.
     * @param y1 y-coordinate of the second point relative to the {@link ConfettiView}'s parent.
     */
    public ConfettiSource(int x0, int y0, int x1, int y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    protected float getInitialX(float random) {
        return x0 + (x1 - x0) * random;
    }

    protected float getInitialY(float random) {
        return y0 + (y1 - y0) * random;
    }
}

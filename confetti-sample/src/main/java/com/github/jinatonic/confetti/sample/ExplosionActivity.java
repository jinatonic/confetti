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

package com.github.jinatonic.confetti.sample;

import android.graphics.Rect;

import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.Utils;

public class ExplosionActivity extends AbstractActivity {
    @Override
    public ConfettiManager getConfettiManager() {
        final int centerX = container.getWidth() / 2;
        final int centerY = container.getHeight() / 5 * 2;
        final int explosionRadius =
                getResources().getDimensionPixelOffset(R.dimen.explosion_radius);

        final ConfettiSource confettiSource = new ConfettiSource(centerX, centerY);
        return new ConfettiManager(this, this, confettiSource, container)
                .setTTL(1000)
                .setBound(new Rect(
                        centerX - explosionRadius, centerY - explosionRadius,
                        centerX + explosionRadius, centerY + explosionRadius
                ))
                .setVelocityX(0, velocityFast)
                .setVelocityY(0, velocityFast)
                .enableFadeOut(Utils.getDefaultAlphaInterpolator())
                .setInitialRotation(180, 180)
                .setRotationalAcceleration(360, 180)
                .setTargetRotationalVelocity(360);
    }
}

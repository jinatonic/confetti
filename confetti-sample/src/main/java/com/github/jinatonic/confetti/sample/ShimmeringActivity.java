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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.Utils;
import com.github.jinatonic.confetti.confetto.Confetto;
import com.github.jinatonic.confetti.confetto.ShimmeringConfetto;

import java.util.List;
import java.util.Random;

public class ShimmeringActivity extends AbstractActivity implements ConfettoGenerator {
    private int size;
    private int velocitySlow, velocityNormal;
    private List<Bitmap> confettoBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Resources res = getResources();
        size = res.getDimensionPixelSize(R.dimen.default_confetti_size);
        velocitySlow = res.getDimensionPixelOffset(R.dimen.default_velocity_slow);
        velocityNormal = res.getDimensionPixelOffset(R.dimen.default_velocity_normal);

        // The color here doesn't matter, it's simply needed to generate the bitmaps
        final int[] colors = { Color.BLACK };
        confettoBitmaps = Utils.generateConfettiBitmaps(colors, size);
    }

    @Override
    protected void generateOnce() {
        getConfettiManager().setNumInitialCount(100)
                .setEmissionDuration(0)
                .animate();
    }

    @Override
    protected void generateStream() {
        getConfettiManager().setNumInitialCount(0)
                .setEmissionDuration(3000)
                .setEmissionRate(50)
                .animate();
    }

    @Override
    protected void generateInfinite() {
        getConfettiManager().setNumInitialCount(0)
                .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
                .setEmissionRate(50)
                .animate();
    }

    private ConfettiManager getConfettiManager() {
        final ConfettiSource confettiSource = new ConfettiSource(0, -size, container.getWidth(),
                -size);
        return new ConfettiManager(this, this, confettiSource, container)
                .setVelocityX(0, velocitySlow)
                .setVelocityY(velocityNormal, velocitySlow)
                .setInitialRotation(180, 180)
                .setRotationalAcceleration(360, 180)
                .setTargetRotationalVelocity(360);
    }

    @Override
    public Confetto generateConfetto(Random random) {
        return new ShimmeringConfetto(
                confettoBitmaps.get(random.nextInt(confettoBitmaps.size())),
                goldLight, goldDark, 1000, random);
    }
}

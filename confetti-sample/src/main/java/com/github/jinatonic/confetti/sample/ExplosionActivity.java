package com.github.jinatonic.confetti.sample;

import android.graphics.Rect;

import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.Utils;

public class ExplosionActivity extends AbstractActivity {
    private ConfettiManager confettiManager;

    @Override
    public ConfettiManager getConfettiManager() {
        if (confettiManager == null) {
            final int centerX = container.getWidth() / 2;
            final int centerY = container.getHeight() / 5 * 2;
            final int explosionRadius =
                    getResources().getDimensionPixelOffset(R.dimen.explosion_radius);
            final ConfettiManager.ConfettiSource confettiSource =
                    new ConfettiManager.ConfettiSource(centerX, centerY);
            confettiManager = new ConfettiManager(this, this, confettiSource, container)
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
        return confettiManager;
    }
}

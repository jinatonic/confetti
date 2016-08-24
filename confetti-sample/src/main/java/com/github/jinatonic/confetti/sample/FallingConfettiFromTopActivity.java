package com.github.jinatonic.confetti.sample;

import com.github.jinatonic.confetti.ConfettiManager;

public class FallingConfettiFromTopActivity extends AbstractActivity {
    @Override
    public ConfettiManager getConfettiManager() {
        final int width = container.getWidth();
        final ConfettiManager.ConfettiSource confettiSource =
                new ConfettiManager.ConfettiSource(0, -confettiSize, width, -confettiSize);
        return new ConfettiManager(this, this, confettiSource, container)
                .setVelocityX(0, velocitySlow)
                .setVelocityY(velocityNormal, velocitySlow)
                .setInitialRotation(180, 180)
                .setRotationalAcceleration(360, 180)
                .setTargetRotationalVelocity(360);
    }
}

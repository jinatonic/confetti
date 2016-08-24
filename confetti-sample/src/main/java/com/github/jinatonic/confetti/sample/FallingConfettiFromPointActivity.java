package com.github.jinatonic.confetti.sample;

import com.github.jinatonic.confetti.ConfettiManager;

public class FallingConfettiFromPointActivity extends AbstractActivity {
    @Override
    public ConfettiManager getConfettiManager() {
        final ConfettiManager.ConfettiSource confettiSource =
                new ConfettiManager.ConfettiSource(-confettiSize, -confettiSize);
        return new ConfettiManager(this, this, confettiSource, container)
                .setVelocityX(velocityFast, velocityNormal)
                .setAccelerationX(-velocityNormal, velocitySlow)
                .setTargetVelocityX(0, velocitySuperSlow)
                .setVelocityY(velocityNormal, velocitySlow)
                .setInitialRotation(180, 180)
                .setRotationalAcceleration(360, 180)
                .setTargetRotationalVelocity(360);
    }
}

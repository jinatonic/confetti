package com.github.jinatonic.confetti.sample;

import com.github.jinatonic.confetti.ConfettiManager;

public class FallingConfettiFromPointActivity extends AbstractActivity {
    private ConfettiManager confettiManager;

    @Override
    public ConfettiManager getConfettiManager() {
        if (confettiManager == null) {
            final ConfettiManager.ConfettiSource confettiSource =
                    new ConfettiManager.ConfettiSource(-confettiSize, -confettiSize);
            confettiManager = new ConfettiManager(this, this, confettiSource, container)
                    .setVelocityX(velocityFast, velocityNormal)
                    .setAccelerationX(-velocityNormal, velocitySlow)
                    .setTargetVelocityX(0, velocitySuperSlow)
                    .setVelocityY(velocityNormal, velocitySlow)
                    .setInitialRotation(180, 180)
                    .setRotationalAcceleration(360, 180)
                    .setTargetRotationalVelocity(360);
        }
        return confettiManager;
    }
}

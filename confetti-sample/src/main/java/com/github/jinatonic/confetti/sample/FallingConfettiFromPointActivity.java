package com.github.jinatonic.confetti.sample;

import com.github.jinatonic.confetti.ConfettiManager;

public class FallingConfettiFromPointActivity extends AbstractActivity {
    private ConfettiManager confettiManager;

    private void ensureConfettiManager() {
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
    }

    @Override
    protected void generateOnce() {
        ensureConfettiManager();
        confettiManager.setNumInitialCount(100)
                .setEmissionDuration(0)
                .animate();
    }

    @Override
    protected void generateStream() {
        ensureConfettiManager();
        confettiManager.setNumInitialCount(0)
                .setEmissionDuration(3000)
                .setEmissionRate(100)
                .animate();
    }

    @Override
    protected void generateInfinite() {
        ensureConfettiManager();
        confettiManager.setNumInitialCount(0)
                .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
                .setEmissionRate(50)
                .animate();
    }
}

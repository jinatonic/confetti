package com.github.jinatonic.confetti.sample;

import com.github.jinatonic.confetti.ConfettiManager;

public class FallingConfettiFromTopActivity extends AbstractActivity {
    private ConfettiManager confettiManager;

    private void ensureConfettiManager() {
        if (confettiManager == null) {
            final int width = container.getWidth();
            final ConfettiManager.ConfettiSource confettiSource =
                    new ConfettiManager.ConfettiSource(0, -confettiSize, width, -confettiSize);
            confettiManager = new ConfettiManager(this, this, confettiSource, container)
                    .setVelocityX(0, velocitySlow)
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

package com.github.jinatonic.confetti.sample;

import com.github.jinatonic.confetti.ConfettiManager;

public class FallingConfettiFromTopActivity extends AbstractActivity {
    private ConfettiManager confettiManager;

    @Override
    public ConfettiManager getConfettiManager() {
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
        return confettiManager;
    }
}

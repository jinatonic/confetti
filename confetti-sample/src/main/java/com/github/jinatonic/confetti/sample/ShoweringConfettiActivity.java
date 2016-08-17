package com.github.jinatonic.confetti.sample;

import android.view.View;

import com.github.jinatonic.confetti.ConfettiManager;

public class ShoweringConfettiActivity extends AbstractActivity {
    @Override
    public void onClick(View view) {
        new ConfettiManager(getResources(), this, 300)
                .setDuration(3000)
                .setVelocityX(0, velocityNormal)
                .setVelocityY(velocityNormal, 0f)
                .setAccelerationY(0f, 0f)
                .setMaximumVelocityY(defaultMaximumVelocityY)
                .setRotationalAcceleration(360, 180)
                .setMaximumRotationalVelocity(360)
                .show(new ConfettiManager.ConfettiSource(0, 0, container.getWidth(), 0), container);
    }
}

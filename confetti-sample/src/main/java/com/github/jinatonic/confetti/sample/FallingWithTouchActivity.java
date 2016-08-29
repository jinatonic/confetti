package com.github.jinatonic.confetti.sample;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.github.jinatonic.confetti.confetto.Confetto;

import java.util.Random;

public class FallingWithTouchActivity extends AbstractActivity implements ConfettoGenerator {
    private int size;
    private int velocitySlow, velocityNormal;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Resources res = getResources();
        size = res.getDimensionPixelSize(R.dimen.big_confetti_size);
        velocitySlow = res.getDimensionPixelOffset(R.dimen.default_velocity_slow);
        velocityNormal = res.getDimensionPixelOffset(R.dimen.default_velocity_normal);

        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.snowflake),
                size, size, false);
    }

    @Override
    protected void generateOnce() {
        getConfettiManager().setNumInitialCount(20)
                .setEmissionDuration(0)
                .animate();
    }

    @Override
    protected void generateStream() {
        getConfettiManager().setNumInitialCount(0)
                .setEmissionDuration(3000)
                .setEmissionRate(20)
                .animate();
    }

    @Override
    protected void generateInfinite() {
        getConfettiManager().setNumInitialCount(0)
                .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
                .setEmissionRate(20)
                .animate();
    }

    private ConfettiManager getConfettiManager() {
        final ConfettiSource source = new ConfettiSource(0, -size, container.getWidth(), -size);
        return new ConfettiManager(this, this, source, container)
                .setVelocityX(0, velocitySlow)
                .setVelocityY(velocityNormal, velocitySlow)
                .setRotationalVelocity(180, 90)
                .setTouchEnabled(true);
    }

    @Override
    public Confetto generateConfetto(Random random) {
        return new BitmapConfetto(bitmap);
    }
}

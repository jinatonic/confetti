package com.github.jinatonic.confetti.sample;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.github.jinatonic.confetti.confetto.Confetto;

import java.util.Random;

public class FallingConfettiWithListenerActivity extends AbstractActivity
        implements ConfettoGenerator, ConfettiManager.ConfettiAnimationListener {

    private TextView numConfettiTxt;
    private int numConfettiOnScreen;

    private int size;
    private int velocitySlow, velocityNormal;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        numConfettiTxt = (TextView) findViewById(R.id.num_confetti_txt);

        final Resources res = getResources();
        size = res.getDimensionPixelSize(R.dimen.big_confetti_size);
        velocitySlow = res.getDimensionPixelOffset(R.dimen.default_velocity_slow);
        velocityNormal = res.getDimensionPixelOffset(R.dimen.default_velocity_normal);

        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.snowflake),
                size, size, false);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_confetti_with_listener;
    }

    @Override
    protected ConfettiManager generateOnce() {
        return getConfettiManager().setNumInitialCount(20)
                .setEmissionDuration(0)
                .setConfettiAnimationListener(this)
                .animate();
    }

    @Override
    protected ConfettiManager generateStream() {
        return getConfettiManager().setNumInitialCount(0)
                .setEmissionDuration(3000)
                .setEmissionRate(20)
                .setConfettiAnimationListener(this)
                .animate();
    }

    @Override
    protected ConfettiManager generateInfinite() {
        return getConfettiManager().setNumInitialCount(0)
                .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
                .setEmissionRate(20)
                .setConfettiAnimationListener(this)
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

    @Override
    public void onAnimationStart(ConfettiManager confettiManager) {
        Toast.makeText(this, "Starting confetti animation", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAnimationEnd(ConfettiManager confettiManager) {
        numConfettiOnScreen = 0;
        updateNumConfettiTxt();
        Toast.makeText(this, "Ending confetti animation", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfettoEnter(Confetto confetto) {
        numConfettiOnScreen++;
        updateNumConfettiTxt();
    }

    @Override
    public void onConfettoExit(Confetto confetto) {
        numConfettiOnScreen--;
        updateNumConfettiTxt();
    }

    private void updateNumConfettiTxt() {
        numConfettiTxt.setText(getString(R.string.num_confetti_desc, numConfettiOnScreen));
    }
}

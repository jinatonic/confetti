package com.github.jinatonic.confetti.sample;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.Utils;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.github.jinatonic.confetti.confetto.Confetto;

import java.util.List;
import java.util.Random;

public abstract class AbstractActivity extends AppCompatActivity implements
        ConfettiManager.ConfettoGenerator, View.OnClickListener {
    protected ViewGroup container;

    protected int confettiSize;
    protected float velocitySuperSlow, velocitySlow, velocityNormal, velocityFast;
    private int[] colors;
    private List<Bitmap> bitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confetti);

        container = (ViewGroup) findViewById(R.id.container);
        findViewById(R.id.generate_confetti_once_btn).setOnClickListener(this);
        findViewById(R.id.generate_confetti_stream_btn).setOnClickListener(this);
        findViewById(R.id.generate_confetti_infinite_btn).setOnClickListener(this);

        final Resources res = getResources();
        confettiSize = res.getDimensionPixelSize(R.dimen.default_confetti_size);
        velocitySuperSlow = res.getDimensionPixelOffset(R.dimen.default_velocity_super_slow);
        velocitySlow = res.getDimensionPixelSize(R.dimen.default_velocity_slow);
        velocityNormal = res.getDimensionPixelSize(R.dimen.default_velocity_normal);
        velocityFast = res.getDimensionPixelSize(R.dimen.default_velocity_fast);

        colors = new int[] {
                res.getColor(R.color.gold_dark),
                res.getColor(R.color.gold_med),
                res.getColor(R.color.gold),
                res.getColor(R.color.gold_light),
        };
        bitmaps = Utils.generateConfettiBitmaps(colors, confettiSize);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.generate_confetti_once_btn) {
            generateOnce();
        } else if (id == R.id.generate_confetti_stream_btn) {
            generateStream();
        } else {
            generateInfinite();
        }
    }

    protected void generateOnce() {
        getConfettiManager().setNumInitialCount(100)
                .setEmissionDuration(0)
                .animate();
    }

    protected void generateStream() {
        getConfettiManager().setNumInitialCount(0)
                .setEmissionDuration(3000)
                .setEmissionRate(100)
                .animate();
    }
    protected void generateInfinite() {
        getConfettiManager().setNumInitialCount(0)
                .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
                .setEmissionRate(50)
                .animate();
    }

    protected abstract ConfettiManager getConfettiManager();

    @Override
    public Confetto generateConfetto(Random random) {
        return new BitmapConfetto(bitmaps.get(random.nextInt(bitmaps.size())));
    }
}

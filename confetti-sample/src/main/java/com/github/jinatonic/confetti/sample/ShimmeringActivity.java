package com.github.jinatonic.confetti.sample;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import com.github.jinatonic.confetti.Utils;
import com.github.jinatonic.confetti.confetto.Confetto;
import com.github.jinatonic.confetti.confetto.ShimmeringConfetto;

import java.util.List;
import java.util.Random;

public class ShimmeringActivity extends FallingConfettiFromTopActivity {
    private List<Bitmap> confettoBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int[] colors = {Color.BLACK };
        confettoBitmaps = Utils.generateConfettiBitmaps(colors, confettiSize);
    }

    @Override
    public Confetto generateConfetto(Random random) {
        return new ShimmeringConfetto(
                confettoBitmaps.get(random.nextInt(confettoBitmaps.size())),
                goldLight, goldDark, 1000, random);
    }
}

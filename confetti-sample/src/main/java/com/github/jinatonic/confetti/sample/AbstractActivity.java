/**
 * Copyright (C) 2016 Robinhood Markets, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jinatonic.confetti.sample;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.github.jinatonic.confetti.ConfettiManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

public abstract class AbstractActivity extends AppCompatActivity implements View.OnClickListener {
    protected ViewGroup container;

    protected int goldDark, goldMed, gold, goldLight;
    protected int[] colors;

    private final List<ConfettiManager> activeConfettiManagers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        container = findViewById(R.id.container);
        findViewById(R.id.generate_confetti_once_btn).setOnClickListener(this);
        findViewById(R.id.generate_confetti_stream_btn).setOnClickListener(this);
        findViewById(R.id.generate_confetti_infinite_btn).setOnClickListener(this);
        findViewById(R.id.generate_confetti_stop_btn).setOnClickListener(this);

        final Resources res = getResources();
        goldDark = res.getColor(R.color.gold_dark);
        goldMed = res.getColor(R.color.gold_med);
        gold = res.getColor(R.color.gold);
        goldLight = res.getColor(R.color.gold_light);
        colors = new int[] { goldDark, goldMed, gold, goldLight };
    }

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.activity_confetti;
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.generate_confetti_once_btn) {
            activeConfettiManagers.add(generateOnce());
        } else if (id == R.id.generate_confetti_stream_btn) {
            activeConfettiManagers.add(generateStream());
        } else if (id == R.id.generate_confetti_infinite_btn) {
            activeConfettiManagers.add(generateInfinite());
        } else {
            for (ConfettiManager confettiManager : activeConfettiManagers) {
                confettiManager.terminate();
            }
            activeConfettiManagers.clear();
        }
    }

    protected abstract ConfettiManager generateOnce();
    protected abstract ConfettiManager generateStream();
    protected abstract ConfettiManager generateInfinite();
}

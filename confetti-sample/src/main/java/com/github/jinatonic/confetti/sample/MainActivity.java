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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final ConfettiSample[] SAMPLES = {
            new ConfettiSample(
                    R.string.falling_confetti_from_top,
                    FallingConfettiFromTopActivity.class
            ),
            new ConfettiSample(
                    R.string.falling_confetti_from_point,
                    FallingConfettiFromPointActivity.class
            ),
            new ConfettiSample(
                    R.string.explosion_confetti,
                    ExplosionActivity.class
            ),
            new ConfettiSample(
                    R.string.shimmering_confetti,
                    ShimmeringActivity.class
            ),
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(android.R.id.list);
        final ListAdapter adapter = new ArrayAdapter<ConfettiSample>(this,
                R.layout.item_confetti_sample, SAMPLES) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);
                final ConfettiSample sample = getItem(position);
                ((TextView) view).setText(sample.nameResId);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, sample.targetActivityClass));
                    }
                });

                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    private static class ConfettiSample {
        final int nameResId;
        final Class<? extends Activity> targetActivityClass;

        private ConfettiSample(int nameResId, Class<? extends Activity> targetActivityClass) {
            this.nameResId = nameResId;
            this.targetActivityClass = targetActivityClass;
        }
    }
}

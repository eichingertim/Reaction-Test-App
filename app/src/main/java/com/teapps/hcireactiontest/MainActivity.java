package com.teapps.hcireactiontest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.teapps.hcireactiontest.reaction_tests.AcousticReactionTestActivity;
import com.teapps.hcireactiontest.reaction_tests.DesicionReactionTestActivity;
import com.teapps.hcireactiontest.reaction_tests.FilterReactionTestActivity;
import com.teapps.hcireactiontest.reaction_tests.VisualReactionTestActivity;

import java.io.BufferedReader;

public class MainActivity extends AppCompatActivity {

    private Button btnVisual, btnAcoustic, btnDesicions, btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initObjects();
        setOnClickListeners();

    }

    private void setOnClickListeners() {
        setOnClickListener(VisualReactionTestActivity.class, btnVisual);
        setOnClickListener(AcousticReactionTestActivity.class, btnAcoustic);
        setOnClickListener(DesicionReactionTestActivity.class, btnDesicions);
        setOnClickListener(FilterReactionTestActivity.class, btnFilter);
    }

    private void setOnClickListener(final Class cls, Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, cls));
            }
        });
    }

    private void initObjects() {
        btnVisual = findViewById(R.id.btnVisual);
        btnAcoustic = findViewById(R.id.btnAcustics);
        btnDesicions = findViewById(R.id.btnDesicions);
        btnFilter = findViewById(R.id.btnFilter);
    }
}

package com.yan.inflaterautotest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yan.inflaterauto.InflaterAuto;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(SkinTextView.class.getSimpleName()));
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.ll, new InflaterFragment()).commit();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(InflaterAuto.wrap(base));
    }
}

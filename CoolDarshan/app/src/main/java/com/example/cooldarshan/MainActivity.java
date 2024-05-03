package com.example.cooldarshan;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    Button hideButton;
    ImageView greenBotImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideButton = (Button) findViewById(R.id.button1);
        greenBotImageView = (ImageView) findViewById(R.id.ImageView1);
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation hideAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim);
                greenBotImageView.startAnimation(hideAnim);
            }
        });

    }
}

package com.example.propertiesdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class PropertyAnimationDemo extends Activity implements View.OnClickListener {

    private RelativeLayout parent;
    private ObjectAnimator colorAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(this);
        LayoutTransition layoutTransition = new LayoutTransition();
        parent = (RelativeLayout) findViewById(R.id.parent);
        parent.setLayoutTransition(layoutTransition);
        colorAnimator = ObjectAnimator.ofInt(parent, "backgroundColor",
                Color.RED, Color.BLUE).setDuration(1000);
    }

    @Override
    public void onClick(View v) {
        colorAnimator.start();
    }
}

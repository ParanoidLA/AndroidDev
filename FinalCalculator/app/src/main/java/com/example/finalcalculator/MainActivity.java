package com.example.finalcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = (EditText) findViewById(R.id.firstint);


        EditText editText2 = (EditText) findViewById(R.id.secondint);


        b1=findViewById(R.id.adderbutton);

        TextView v=findViewById(R.id.resultforaddition);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                int value1 = Integer.parseInt(editText.getText().toString());
                int value2 = Integer.parseInt(editText2.getText().toString());
                int result=value1+value2;
                v.setText(String.valueOf(result));

            }
        });

    }
}
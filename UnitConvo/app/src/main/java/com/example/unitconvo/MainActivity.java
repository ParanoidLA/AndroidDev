package com.example.unitconvo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private Button button;
    private EditText editTextTextPersonName;
    private TextView textview2;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button= findViewById(R.id.button);
        textview2=findViewById(R.id.textView2);
        editTextTextPersonName=findViewById(R.id.editTextTextPersonName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(MainActivity.this, "On click listener is wirking", Toast.LENGTH_SHORT).show();
                String s= editTextTextPersonName.getText().toString();
                int kg=Integer.parseInt(s);
                double pound= 2.205 * kg;
                textview2.setText("The corresponding value in pound is  "+ pound);
            }
        });

    }
}
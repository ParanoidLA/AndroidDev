package com.example.filehandling1;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText ed1;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed1 = findViewById(R.id.data);
        b1 = findViewById(R.id.submitbtn);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = ed1.getText().toString();
//                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

                FileWriter fileWriter=null;
                try {
                    File file = new File(getApplicationContext().getFilesDir(), "./abc.txt");
                    FileWriter f= new FileWriter(file);
                    // read each character from string and write
                    // into FileWriter
                    f.write(text);
                    f.flush();
                    f.close();
                    // Display a toast message to indicate successful writing
                    Toast.makeText(MainActivity.this, "Successfully written", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace(); // Print error message to console
                    // Optionally, you can show a message to indicate failure in file writing
                    // Toast.makeText(MainActivity.this, "Error writing to file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button btnScan, btnSubmit;
    private EditText editTextVoterID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Welcome text
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome to Elections");

        // EditText for manual entry of voter ID
        editTextVoterID = findViewById(R.id.editTextVoterID);

        // Button to submit manually entered voter ID
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            String voterID = editTextVoterID.getText().toString().trim();
            if (!voterID.isEmpty()) {
                // Send manually entered voter ID to next activity
                Intent intent = new Intent(MainActivity.this, VoterInfo.class);
                intent.putExtra("scannedData", voterID);
                startActivity(intent);
            }
        });

        // Set up button for scanning QR or barcode
        btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            // Start QR/Barcode scanner
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setPrompt("Scan QR code or Barcode");
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        });
    }

    // Handle the result of the scan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Send scanned data to next activity
                Intent intent = new Intent(MainActivity.this, VoterInfo.class);
                intent.putExtra("scannedData", result.getContents());
                startActivity(intent);
            }
        }
    }
}
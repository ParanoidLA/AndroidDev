package com.example.voters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NextActivity extends AppCompatActivity {

    private TextView serialNumberTextView, voterIdTextView, uidaiIdTextView, nameTextView, ageTextView, genderTextView, constituencyCodeTextView, stateCodeTextView, electionIdTextView;
    private Button fingerprintButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        // Initialize TextViews
        serialNumberTextView = findViewById(R.id.serialNumberTextView);
        voterIdTextView = findViewById(R.id.voterIdTextView);
        uidaiIdTextView = findViewById(R.id.uidaiIdTextView);
        nameTextView = findViewById(R.id.nameTextView);
        ageTextView = findViewById(R.id.ageTextView);
        genderTextView = findViewById(R.id.genderTextView);
        constituencyCodeTextView = findViewById(R.id.constituencyCodeTextView);
        stateCodeTextView = findViewById(R.id.stateCodeTextView);
        electionIdTextView = findViewById(R.id.electionIdTextView);

        // Initialize fingerprint button
        fingerprintButton = findViewById(R.id.fingerprintButton);
        fingerprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NextActivity.this, "Fingerprint device not connected", Toast.LENGTH_SHORT).show();
            }
        });

        // Receive data from previous activity
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                // Extract data from extras
                String serialNumber = extras.getString("SerialNumber");
                String voterId = extras.getString("VoterID");
                String uidaiId = extras.getString("UIDAIId");
                String name = extras.getString("Name");
                int age = extras.getInt("Age");
                String gender = extras.getString("Gender");
                String constituencyCode = extras.getString("ConstituencyCode");
                String stateCode = extras.getString("StateCode");
                String electionID = extras.getString("ElectionID");

                // Display data in TextViews
                serialNumberTextView.setText("Serial Number: " + serialNumber);
                voterIdTextView.setText("Voter ID: " + voterId);
                uidaiIdTextView.setText("UIDAI ID: " + uidaiId);
                nameTextView.setText("Name: " + name);
                ageTextView.setText("Age: " + age);
                genderTextView.setText("Gender: " + gender);
                constituencyCodeTextView.setText("Constituency Code: " + constituencyCode);
                stateCodeTextView.setText("State Code: " + stateCode);
                electionIdTextView.setText("Election ID: " + electionID);
            }
        }

        // Set onClickListener for "Problem with fingerprint?" text
        TextView problemWithFingerprintTextView = findViewById(R.id.problemWithFingerprintTextView);
        problemWithFingerprintTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMasterKeyPopup();
            }
        });
    }

    private void showMasterKeyPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.master_key_dialog, null);
        builder.setView(dialogView);

        final TextView masterKeyEditText = dialogView.findViewById(R.id.masterKeyEditText);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String masterKey = masterKeyEditText.getText().toString();
                String correctMasterKey = "29082003143"; // Correct master key

                if (masterKey.equals(correctMasterKey)) {
                    // Redirect to VotingActivity if master key is correct
                    Intent intent = new Intent(NextActivity.this, VotingActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(NextActivity.this, "Incorrect master key", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

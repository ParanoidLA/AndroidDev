package com.example.voters;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voters.DBHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextSerialNumber, editTextVoterID, editTextUIDAIId, editTextName, editTextAge, editTextGender, editTextConstituencyCode, editTextStateCode, editTextElectionID;
    private Button buttonRegister;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize EditText fields
        editTextSerialNumber = findViewById(R.id.editTextSerialNumber);
        editTextVoterID = findViewById(R.id.editTextVoterID);
        editTextUIDAIId = findViewById(R.id.editTextUIDAIId);
        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextGender = findViewById(R.id.editTextGender);
        editTextConstituencyCode = findViewById(R.id.editTextConstituencyCode);
        editTextStateCode = findViewById(R.id.editTextStateCode);
        editTextElectionID = findViewById(R.id.editTextElectionID);

        // Initialize Register button
        buttonRegister = findViewById(R.id.buttonSubmit);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to handle registration
                registerUser();
            }
        });

        // Initialize DBHelper
        dbHelper = new DBHelper(this,"1");
    }

    private void registerUser() {
        // Retrieve input values from EditText fields
        String serialNumber = editTextSerialNumber.getText().toString().trim();
        String voterID = editTextVoterID.getText().toString().trim();
        String uidaiId = editTextUIDAIId.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        int age = Integer.parseInt(editTextAge.getText().toString().trim());
        String gender = editTextGender.getText().toString().trim();
        String constituencyCode = editTextConstituencyCode.getText().toString().trim();
        String stateCode = editTextStateCode.getText().toString().trim();
        String electionID = editTextElectionID.getText().toString().trim();

        // Validate input fields
        if (serialNumber.isEmpty() || voterID.isEmpty() || uidaiId.isEmpty() || name.isEmpty() || gender.isEmpty() || constituencyCode.isEmpty() || stateCode.isEmpty()) {
            // Display error message if any required field is empty
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        } else {
            // Insert user data into database
            dbHelper.insertUserData(serialNumber, voterID, uidaiId, name, age, gender, constituencyCode, stateCode, electionID, null); // Assuming fingerprint data is null for now

            // Display success message
            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();

            // Optionally, clear EditText fields after registration
            clearFields();
        }
    }

    private void clearFields() {
        editTextSerialNumber.setText("");
        editTextVoterID.setText("");
        editTextUIDAIId.setText("");
        editTextName.setText("");
        editTextAge.setText("");
        editTextGender.setText("");
        editTextConstituencyCode.setText("");
        editTextStateCode.setText("");
        editTextElectionID.setText("");
    }
}

package com.example.voters;

        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;

        import com.google.zxing.integration.android.IntentIntegrator;
        import com.google.zxing.integration.android.IntentResult;

public class ElectionActivity extends AppCompatActivity {

    private Button buttonScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election);

        // Initialize Scan button
        buttonScan = findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initiate barcode scanning
                scanBarcode();
            }
        });
    }

    private void scanBarcode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true); // Lock orientation to portrait
        integrator.setPrompt("Scan a barcode");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String scannedText = result.getContents();
                // Check VoterID in the database
                boolean userFound = checkUser(scannedText);
                if (userFound) {
                    // Navigate to the next screen
                    Intent intent = new Intent(this, NextActivity.class);
                    startActivity(intent);
                } else {
                    // Show toast if user not found
                    Toast.makeText(this, "User data for \"" + scannedText + "\" is not found", Toast.LENGTH_SHORT).show();

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean checkUser(String voterID) {
        DBHelper dbHelper = new DBHelper(this, null); // Initialize DBHelper
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "Users",                // Table name
                null,                   // Columns to return (all columns)
                "VoterID = ?",          // Selection criteria
                new String[]{voterID},  // Selection args
                null,                   // Group by
                null,                   // Having
                null                    // Order by
        );

        boolean userExists = cursor != null && cursor.moveToFirst(); // Move cursor to first row if it exists

        if (userExists) {
            // Extract user details from the cursor
            // Extract user details from the cursor
            // Ensure the cursor is not null and move to the first row
            String serialNumber = null;
            String voterId = null;
            String uidaiId = null;
            String name = null;
            int age = 0;
            String gender = null;
            String constituencyCode = null;
            String stateCode = null;
            String electionID = null;
            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve column values using column indexes
                int serialNumberIndex = cursor.getColumnIndex("SerialNumber");
                serialNumber = (serialNumberIndex != -1) ? cursor.getString(serialNumberIndex) : "";

                int voterIdIndex = cursor.getColumnIndex("VoterID");
                voterId = (voterIdIndex != -1) ? cursor.getString(voterIdIndex) : "";

                int uidaiIdIndex = cursor.getColumnIndex("UIDAIId");
                uidaiId = (uidaiIdIndex != -1) ? cursor.getString(uidaiIdIndex) : "";

                int nameIndex = cursor.getColumnIndex("Name");
                name = (nameIndex != -1) ? cursor.getString(nameIndex) : "";

                int ageIndex = cursor.getColumnIndex("Age");
                age = (ageIndex != -1) ? cursor.getInt(ageIndex) : 0;

                int genderIndex = cursor.getColumnIndex("Gender");
                gender = (genderIndex != -1) ? cursor.getString(genderIndex) : "";

                int constituencyCodeIndex = cursor.getColumnIndex("ConstituencyCode");
                constituencyCode = (constituencyCodeIndex != -1) ? cursor.getString(constituencyCodeIndex) : "";

                int stateCodeIndex = cursor.getColumnIndex("StateCode");
                stateCode = (stateCodeIndex != -1) ? cursor.getString(stateCodeIndex) : "";

                int electionIDIndex = cursor.getColumnIndex("ElectionID");
                electionID = (electionIDIndex != -1) ? cursor.getString(electionIDIndex) : "";

                // Proceed with the retrieved data
            }


            // Create an intent to start the next activity
            Intent intent = new Intent(this, NextActivity.class);

            // Pass user details as extras with the intent
            intent.putExtra("SerialNumber", serialNumber);
            intent.putExtra("VoterID", voterId);
            intent.putExtra("UIDAIId", uidaiId);
            intent.putExtra("Name", name);
            intent.putExtra("Age", age);
            intent.putExtra("Gender", gender);
            intent.putExtra("ConstituencyCode", constituencyCode);
            intent.putExtra("StateCode", stateCode);
            intent.putExtra("ElectionID", electionID);

            // Start the next activity
            startActivity(intent);
        }

        // Close cursor and database
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return userExists;
    }


}

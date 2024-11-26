package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VoterInfo extends AppCompatActivity implements MFS100Event {

    private FirebaseFirestore db;
    private MFS100 mfs100;
    private TextView resultText;
    private Button btnMatch;
    private byte[] storedTemplate;
    private String name;
    private String gender;
    private String constituencyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voterinfo);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize MFS100 fingerprint scanner
        mfs100 = new MFS100(this);
        mfs100.SetApplicationContext(VoterInfo.this);

        // Retrieve the scanned data from the intent
        String scannedData = getIntent().getStringExtra("scannedData");

        // Initialize UI components
        resultText = findViewById(R.id.resultText);
        btnMatch = findViewById(R.id.btnMatch);

        if (scannedData != null && !scannedData.isEmpty()) {
            // Fetch user data and display information
            fetchUserData(scannedData);
        } else {
            Toast.makeText(this, "No scanned data found", Toast.LENGTH_LONG).show();
        }

        // Set up button click listener
        btnMatch.setOnClickListener(v -> matchFingerprint());
    }

    private void fetchUserData(String userId) {
        // Fetch the user record from Firebase
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the stored fingerprint template and user details
                        String storedFingerprint = documentSnapshot.getString("fingerprint");
                        name = documentSnapshot.getString("name");
                        gender = documentSnapshot.getString("gender");
                        constituencyCode = documentSnapshot.getString("constituencyCode");

                        storedTemplate = Base64.decode(storedFingerprint, Base64.DEFAULT);

                        // Fetch constituency name
                        db.collection("constituency").document(constituencyCode).get()
                                .addOnSuccessListener(constituencySnapshot -> {
                                    if (constituencySnapshot.exists()) {
                                        String constituencyName = constituencySnapshot.getString("ConstituencyName");
                                        resultText.setText("Hello " + name + " from " + constituencyName + "\nYou are a " + gender + " voter");

                                        // Check for ongoing or upcoming elections
                                        checkElections(constituencyCode);
                                    } else {
                                        Toast.makeText(VoterInfo.this, "Constituency not found", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirebaseError", "Error fetching constituency", e);
                                    Toast.makeText(VoterInfo.this, "Error fetching constituency data", Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Toast.makeText(VoterInfo.this, "User not found", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Error fetching user", e);
                    Toast.makeText(VoterInfo.this, "Error fetching user data", Toast.LENGTH_LONG).show();
                });
    }
    private String ongoingElectionID = null; // Store ongoing election ID

    private void checkElections(String constituencyCode) {
        // Fetch the election record from Firebase
        db.collection("election").whereEqualTo("ConstituencyID", constituencyCode).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean foundElection = false;
                    String upcomingElectionDate = null;

                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String electionID = document.getId();
                        String startDate = document.getString("StartDate");
                        String endDate = document.getString("EndDate");

                        if (startDate != null && endDate != null) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                Date start = sdf.parse(startDate);
                                Date end = sdf.parse(endDate);
                                Date now = new Date();

                                if (now.after(start) && now.before(end)) {
                                    // Ongoing election
                                    foundElection = true;
                                    ongoingElectionID = electionID; // Store the ongoing election ID
                                    runOnUiThread(() -> {
                                        resultText.append("\nAn election is ongoing.");
                                        btnMatch.setVisibility(Button.VISIBLE); // Make the button visible
                                    });
                                    break; // Exit loop since we found an ongoing election
                                } else if (now.before(start)) {
                                    // Upcoming election
                                    if (upcomingElectionDate == null || start.before(sdf.parse(upcomingElectionDate))) {
                                        upcomingElectionDate = startDate;
                                    }
                                }
                            } catch (ParseException e) {
                                Log.e("DateParseError", "Error parsing election dates", e);
                            }
                        }
                    }

                    if (!foundElection && upcomingElectionDate != null) {
                        String finalUpcomingElectionDate = upcomingElectionDate;
                        runOnUiThread(() -> resultText.append("\nUpcoming election starting on: " + finalUpcomingElectionDate));
                    } else if (!foundElection) {
                        runOnUiThread(() -> resultText.append("\nNo upcoming elections."));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Error fetching elections", e);
                    Toast.makeText(VoterInfo.this, "Error fetching election data", Toast.LENGTH_LONG).show();
                });
    }
    private void matchFingerprint() {
        if (storedTemplate == null) {
            Toast.makeText(this, "No fingerprint data to match", Toast.LENGTH_LONG).show();
            return;
        }

        if (ongoingElectionID == null) {
            Toast.makeText(this, "No ongoing election found.", Toast.LENGTH_LONG).show();
            return; // Stop the process if no ongoing election
        }

        // Start capturing the fingerprint
        new Thread(() -> {
            FingerData fingerData = new FingerData();
            int ret = mfs100.AutoCapture(fingerData, 10000, true); // Capture new fingerprint
            if (ret != 0) {
                runOnUiThread(() -> Toast.makeText(VoterInfo.this, "Capture failed: " + mfs100.GetErrorMsg(ret), Toast.LENGTH_LONG).show());
            } else {
                // Match captured fingerprint with stored fingerprint
                int matchResult = mfs100.MatchISO(storedTemplate, fingerData.ISOTemplate());
                runOnUiThread(() -> {
                    if (matchResult >= 600) {
                        Toast.makeText(VoterInfo.this, "Fingerprint matched!", Toast.LENGTH_LONG).show();
                        // Send election ID to next screen since the fingerprint matched and election is ongoing
                        sendToNextScreen(ongoingElectionID);
                    } else {
                        Toast.makeText(VoterInfo.this, "Fingerprint did not match", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }
    private void sendToNextScreen(String electionID) {
        Intent intent = new Intent(VoterInfo.this, NextActivity.class);
        intent.putExtra("electionID", electionID); // Send the ongoing election ID
        startActivity(intent);
    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        if (hasPermission) {
            mfs100.Init(true);
        }
    }

    @Override
    public void OnDeviceDetached() {
        mfs100.UnInit();
    }

    @Override
    public void OnHostCheckFailed(String err) {
        runOnUiThread(() -> resultText.setText("Host check failed: " + err));
    }

    @Override
    public void OnMFS100Preview(byte[] bytes, int i, String s) {
        // Optional preview handling can go here
    }

    // Implement other MFS100Event methods as needed
}
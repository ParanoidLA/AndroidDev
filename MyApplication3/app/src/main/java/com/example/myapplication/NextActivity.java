package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class NextActivity extends AppCompatActivity {

    private TextView electionIdTextView;
    private LinearLayout candidateButtonsLayout;
    private FirebaseFirestore db;
    private boolean voteCasted = false; // Ensure vote is cast only once

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        // Initialize UI components
        electionIdTextView = findViewById(R.id.electionIdTextView);
        candidateButtonsLayout = findViewById(R.id.candidateButtonsLayout);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get the election ID from the intent
        String electionID = getIntent().getStringExtra("electionID");

        // Log the election ID for debugging
        Log.d("NextActivity", "Received electionID: " + electionID);

        // Display the election ID
        if (electionID != null) {
            electionIdTextView.setText("Election ID: " + electionID);
            fetchCandidates(electionID);
        } else {
            electionIdTextView.setText("No Election ID received");
        }
    }

    private void fetchCandidates(String electionID) {
        // Fetch candidate IDs from the votes collection where electionID matches
        db.collection("votes").document(electionID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> candidateVotes = documentSnapshot.getData();
                        if (candidateVotes != null) {
                            for (String candidateID : candidateVotes.keySet()) {
                                // If candidateID is not 0, fetch candidate details
                                if (!candidateID.equals("0")) {
                                    fetchCandidateDetails(candidateID, electionID);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(NextActivity.this, "No candidates found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Error fetching votes", e));
    }

    private void fetchCandidateDetails(String candidateID, String electionID) {
        // Fetch candidate name and party from the candidate collection
        db.collection("candidate").document(candidateID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String candidateName = documentSnapshot.getString("CandidateName");
                        String partyName = documentSnapshot.getString("Party");

                        // Create a button with candidate name and party
                        Button candidateButton = new Button(NextActivity.this);
                        String buttonText = candidateName + " (" + partyName + ")";
                        candidateButton.setText(buttonText);
                        candidateButton.setOnClickListener(v -> castVote(candidateID, electionID));
                        candidateButtonsLayout.addView(candidateButton);
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Error fetching candidate details", e));
    }

    private void castVote(String candidateID, String electionID) {
        if (voteCasted) {
            Toast.makeText(NextActivity.this, "You have already casted your vote", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update vote count in the votes collection for the selected candidateID
        DocumentReference voteRef = db.collection("votes").document(electionID);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(voteRef);
            long currentVotes = snapshot.getLong(candidateID);
            transaction.update(voteRef, candidateID, currentVotes + 1);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(NextActivity.this, "Vote casted successfully!", Toast.LENGTH_SHORT).show();
            voteCasted = true; // Set flag to ensure vote is only cast once
        }).addOnFailureListener(e -> {
            Log.e("FirebaseError", "Error casting vote", e);
            Toast.makeText(NextActivity.this, "Error casting vote", Toast.LENGTH_SHORT).show();
        });
    }
}
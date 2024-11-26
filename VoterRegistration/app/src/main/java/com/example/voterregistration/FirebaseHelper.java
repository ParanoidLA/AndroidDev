package com.example.voterregistration;

import android.util.Base64;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private FirebaseFirestore db;

    public FirebaseHelper() {
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
    }

    // Save user data to Firebase Firestore
    public void saveUser(String userId, String name, String gender, String constituencyCode, byte[] Enroll_Template, OnFirebaseSaveListener listener) {
        if (userId.isEmpty() || name.isEmpty() || gender.isEmpty() || constituencyCode.isEmpty() || Enroll_Template == null) {
            listener.onFailure("Please enter all details and capture fingerprint");
            return;
        }

        // Encode fingerprint data as Base64 string
        String fingerprintData = Base64.encodeToString(Enroll_Template, Base64.DEFAULT);

        // Create a new user record with additional fields
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("voterId", userId);
        user.put("gender", gender);
        user.put("constituencyCode", constituencyCode);
        user.put("fingerprint", fingerprintData);

        // Add user data to Firestore
        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> listener.onSuccess("User saved successfully"))
                .addOnFailureListener(e -> listener.onFailure("Error saving user: " + e.getMessage()));
    }

    // Interface for Firebase callbacks
    public interface OnFirebaseSaveListener {
        void onSuccess(String message);
        void onFailure(String error);
    }
}
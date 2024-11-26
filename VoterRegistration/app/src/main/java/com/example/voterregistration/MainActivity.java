package com.example.voterregistration;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements MFS100Event {

    Button btnCapture, btnMatch, btnSave;
    EditText txtUserId, txtName, txtGender, txtConstituencyCode;
    TextView lblMessage;
    ImageView imgFinger;

    MFS100 mfs100;
    FingerData lastCapFingerData = null;
    byte[] Enroll_Template;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        btnCapture = findViewById(R.id.btnCapture);
        btnMatch = findViewById(R.id.btnMatch);
        btnSave = findViewById(R.id.btnSave);
        txtUserId = findViewById(R.id.txtUserId);
        txtName = findViewById(R.id.txtName);
        txtGender = findViewById(R.id.txtGender);
        txtConstituencyCode = findViewById(R.id.txtConstituencyCode);
        lblMessage = findViewById(R.id.lblMessage);
        imgFinger = findViewById(R.id.imgFinger);

        // Initialize MFS100 fingerprint scanner
        mfs100 = new MFS100(this);
        mfs100.SetApplicationContext(MainActivity.this);

        // Set listeners for buttons
        btnCapture.setOnClickListener(v -> StartCapture());

        btnSave.setOnClickListener(v -> saveToFirebase());

        btnMatch.setOnClickListener(v -> matchFingerprint());
    }

    private void StartCapture() {
        new Thread(() -> {
            FingerData fingerData = new FingerData();
            int ret = mfs100.AutoCapture(fingerData, 10000, true);
            if (ret != 0) {
                setTextOnUIThread(mfs100.GetErrorMsg(ret));
            } else {
                lastCapFingerData = fingerData;
                Enroll_Template = fingerData.ISOTemplate();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(fingerData.FingerImage(), 0, fingerData.FingerImage().length);
                runOnUiThread(() -> imgFinger.setImageBitmap(bitmap));
                setTextOnUIThread("Capture Success");
            }
        }).start();
    }

    private void saveToFirebase() {
        String userId = txtUserId.getText().toString().trim();
        String name = txtName.getText().toString().trim();
        String gender = txtGender.getText().toString().trim();
        String constituencyCode = txtConstituencyCode.getText().toString().trim();

        if (userId.isEmpty() || name.isEmpty() || gender.isEmpty() || constituencyCode.isEmpty() || Enroll_Template == null) {
            Toast.makeText(this, "Please enter all details and capture fingerprint", Toast.LENGTH_LONG).show();
            Log.e("SaveToFirebase", "All fields are not filled or fingerprint is not captured.");
            return;
        }

        // Encode fingerprint data as Base64 string
        String fingerprintData = Base64.encodeToString(Enroll_Template, Base64.DEFAULT);

        // Create a new user record
        Map<String, Object> user = new HashMap<>();
        user.put("voterId", userId);
        user.put("name", name);
        user.put("gender", gender);
        user.put("constituencyCode", constituencyCode);
        user.put("fingerprint", fingerprintData);

        // Log the user data being sent to Firebase
        Log.d("SaveToFirebase", "User data: " + user.toString());

        // Add user data to Firestore
        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseSave", "User saved successfully");
                    Toast.makeText(MainActivity.this, "User saved successfully", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Error saving user", e);
                    Toast.makeText(MainActivity.this, "Error saving user", Toast.LENGTH_LONG).show();
                });
    }
    private void matchFingerprint() {
        String userId = txtUserId.getText().toString().trim();
        if (userId.isEmpty()) {
            Toast.makeText(this, "Enter User ID", Toast.LENGTH_LONG).show();
            return;
        }

        // Start capturing the fingerprint
        new Thread(() -> {
            FingerData fingerData = new FingerData();
            int ret = mfs100.AutoCapture(fingerData, 10000, true); // Capture new fingerprint
            if (ret != 0) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Capture failed: " + mfs100.GetErrorMsg(ret), Toast.LENGTH_LONG).show());
            } else {
                // Fetch the user record from Firebase
                db.collection("users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Get the stored fingerprint template
                                String storedFingerprint = documentSnapshot.getString("fingerprint");
                                byte[] storedTemplate = Base64.decode(storedFingerprint, Base64.DEFAULT);

                                // Match captured fingerprint with stored fingerprint
                                int matchResult = mfs100.MatchISO(storedTemplate, fingerData.ISOTemplate());
                                if (matchResult >= 600) {
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Fingerprint matched!", Toast.LENGTH_LONG).show());
                                } else {
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Fingerprint did not match", Toast.LENGTH_LONG).show());
                                }
                            } else {
                                runOnUiThread(() -> Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_LONG).show());
                            }
                        })
                        .addOnFailureListener(e -> Log.e("FirebaseError", "Error fetching user", e));
            }
        }).start();
    }
    private void setTextOnUIThread(final String message) {
        runOnUiThread(() -> lblMessage.setText(message));
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
        setTextOnUIThread("Host check failed: " + err);
    }

    @Override
    public void OnMFS100Preview(byte[] bytes, int i, String s) {
        // Optional preview handling can go here
    }
}
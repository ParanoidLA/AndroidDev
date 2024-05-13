package com.example.voters;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VotingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button option1Button, option2Button, option3Button, option4Button;
    private int selectedOption = -1; // Track the currently selected option

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        // Initialize buttons
        option1Button = findViewById(R.id.option1Button);
        option2Button = findViewById(R.id.option2Button);
        option3Button = findViewById(R.id.option3Button);
        option4Button = findViewById(R.id.option4Button);

        // Set click listeners
        option1Button.setOnClickListener(this);
        option2Button.setOnClickListener(this);
        option3Button.setOnClickListener(this);
        option4Button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Reset background color of previously selected option
        if (selectedOption != -1) {
            Button prevButton = getButtonByOption(selectedOption);
            if (prevButton != null) {
                prevButton.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        // Set background color of newly selected option
        if (v.getId() == R.id.option1Button) {
            selectedOption = 1;
        } else if (v.getId() == R.id.option2Button) {
            selectedOption = 2;
        } else if (v.getId() == R.id.option3Button) {
            selectedOption = 3;
        } else if (v.getId() == R.id.option4Button) {
            selectedOption = 4;
        }

        v.setBackgroundColor(Color.YELLOW); // First click, turn option yellow
    }


    public boolean onLongClick(View v) {
        // Change the color to green on long click
        v.setBackgroundColor(Color.GREEN);

        // Record the vote in the database
        Toast.makeText(VotingActivity.this, "Vote recorded successfully", Toast.LENGTH_SHORT).show();

        // Redirect back to ElectionActivity
        Intent intent = new Intent(this, ElectionActivity.class);
        startActivity(intent);

        return true;
    }

    // Utility method to get the button corresponding to an option
    private Button getButtonByOption(int option) {
        switch (option) {
            case 1:
                return option1Button;
            case 2:
                return option2Button;
            case 3:
                return option3Button;
            case 4:
                return option4Button;
            default:
                return null;
        }
    }
}

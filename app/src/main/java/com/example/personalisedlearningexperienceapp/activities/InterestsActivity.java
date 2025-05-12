package com.example.personalisedlearningexperienceapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalisedlearningexperienceapp.R;
import com.example.personalisedlearningexperienceapp.database.AppDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {

    private final ArrayList<String> selectedInterests = new ArrayList<>();
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        username = getIntent().getStringExtra("username");
        androidx.gridlayout.widget.GridLayout gridLayout = findViewById(R.id.interestsGrid);
        Button nextButton = findViewById(R.id.nextButton);

        List<String> interestList = Arrays.asList(
                "Algorithms", "Data Structures", "Web Development", "Testing",
                "UI/UX", "Databases", "AI", "Cybersecurity", "Java", "Python"
        );

        for (String interest : interestList) {
            Button btn = new Button(this);
            btn.setText(interest);
            btn.setTextColor(Color.BLACK);
            btn.setTextSize(14f);
            btn.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            btn.setPadding(16, 16, 16, 16);

            // Corrected LayoutParams for androidx.gridlayout.widget.GridLayout
            androidx.gridlayout.widget.GridLayout.LayoutParams params =
                    new androidx.gridlayout.widget.GridLayout.LayoutParams();
            params.width = 0;
            params.height = androidx.gridlayout.widget.GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = androidx.gridlayout.widget.GridLayout.spec(
                    androidx.gridlayout.widget.GridLayout.UNDEFINED, 1f);
            params.rowSpec = androidx.gridlayout.widget.GridLayout.spec(
                    androidx.gridlayout.widget.GridLayout.UNDEFINED);
            params.setMargins(8, 8, 8, 8);

            btn.setLayoutParams(params);
            btn.setAlpha(0.5f); // Initially unselected

            btn.setOnClickListener(v -> {
                if (selectedInterests.contains(interest)) {
                    selectedInterests.remove(interest);
                    btn.setAlpha(0.5f);
                } else {
                    if (selectedInterests.size() < 10) {
                        selectedInterests.add(interest);
                        btn.setAlpha(1f);
                    } else {
                        Toast.makeText(this, "You can only select up to 10 topics", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            gridLayout.addView(btn);
        }

        nextButton.setOnClickListener(v -> {
            if (selectedInterests.isEmpty()) {
                Toast.makeText(this, "Please select at least one interest", Toast.LENGTH_SHORT).show();
                return;
            }

            String interestsCsv = String.join(",", selectedInterests);

            new Thread(() -> {
                AppDatabase.getInstance(this).userDao().updateInterests(username, interestsCsv);
                runOnUiThread(() -> {
                    Intent intent = new Intent(InterestsActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            }).start();
        });
    }
}


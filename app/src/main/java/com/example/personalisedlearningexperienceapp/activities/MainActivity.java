package com.example.personalisedlearningexperienceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalisedlearningexperienceapp.R;
import com.example.personalisedlearningexperienceapp.database.AppDatabase;
import com.example.personalisedlearningexperienceapp.model.User;

public class MainActivity extends AppCompatActivity {

    TextView textWelcome;
    Button btnStartTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textWelcome = findViewById(R.id.textWelcome);
        btnStartTask = findViewById(R.id.btnStartTask);

        String username = getIntent().getStringExtra("username");

        if (username != null) {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                User user = db.userDao().findByUsername(username);

                runOnUiThread(() -> {
                    if (user != null) {
                        textWelcome.setText("Hello, " + user.firstName + "!");
                        btnStartTask.setOnClickListener(v -> {
                            Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                            intent.putExtra("interests", user.interests); // send interests to TaskActivity
                            intent.putExtra("username", user.username); //send username to TaskActivity
                            startActivity(intent);
                        });
                    }
                });
            }).start();
        }
    }
}

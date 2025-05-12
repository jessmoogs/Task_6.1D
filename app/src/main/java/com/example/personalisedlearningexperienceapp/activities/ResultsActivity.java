package com.example.personalisedlearningexperienceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.personalisedlearningexperienceapp.R;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        LinearLayout resultContainer = findViewById(R.id.resultContainer);
        TextView scoreText = findViewById(R.id.scoreText);
        Button continueButton = findViewById(R.id.continueButton);

        ArrayList<String> questions = getIntent().getStringArrayListExtra("questions");
        ArrayList<String> userAnswers = getIntent().getStringArrayListExtra("userAnswers");
        ArrayList<String> correctAnswers = getIntent().getStringArrayListExtra("correctAnswers");

        int score = 0;

        if (questions != null && userAnswers != null && correctAnswers != null) {
            for (int i = 0; i < questions.size(); i++) {
                String question = questions.get(i);
                String userAnswer = userAnswers.get(i);
                String correctAnswer = correctAnswers.get(i);
                boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);

                if (isCorrect) score++;

                // Create card
                CardView card = new CardView(this);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0, 0, 0, 24); // add space between cards
                card.setLayoutParams(cardParams);
                card.setCardElevation(8f);
                card.setRadius(12f);
                card.setCardBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

                // Card content
                LinearLayout cardContent = new LinearLayout(this);
                cardContent.setOrientation(LinearLayout.VERTICAL);
                cardContent.setPadding(24, 24, 24, 24);

                TextView resultText = new TextView(this);
                resultText.setTextSize(16f);
                resultText.setTextColor(getResources().getColor(android.R.color.white));
                resultText.setText("Q" + (i + 1) + ": " + question +
                        "\nYour Answer: " + userAnswer +
                        "\nCorrect Answer: " + correctAnswer +
                        "\nResult: " + (isCorrect ? "✅ Correct" : "❌ Incorrect"));

                cardContent.addView(resultText);
                card.addView(cardContent);
                resultContainer.addView(card);
            }

            scoreText.setText("You got " + score + " out of " + questions.size() + " correct!");
        }

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("username", getIntent().getStringExtra("username"));  // Pass username again
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }
}

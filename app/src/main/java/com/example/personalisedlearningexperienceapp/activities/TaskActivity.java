package com.example.personalisedlearningexperienceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.personalisedlearningexperienceapp.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class TaskActivity extends AppCompatActivity {

    private TextView questionText, topicSubtitle;
    private RadioGroup optionsGroup;
    private Button nextButton;

    private List<Question> questionList = new ArrayList<>();
    private List<String> selectedAnswers = new ArrayList<>();
    private int currentQuestionIndex = 0;

    private static class Question {
        String questionText;
        List<String> options;
        String correctAnswer;
        String topic;

        Question(String questionText, List<String> options, String correctAnswer, String topic) {
            this.questionText = questionText;
            this.options = options;
            this.correctAnswer = correctAnswer;
            this.topic = topic;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        questionText = findViewById(R.id.questionText);
        topicSubtitle = findViewById(R.id.questionTopic);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);

        String interestsCsv = getIntent().getStringExtra("interests");
        String username = getIntent().getStringExtra("username"); // ✅ capture username here

        if (interestsCsv != null) {
            fetchQuizFromApi(interestsCsv);
        }

        nextButton.setOnClickListener(v -> {
            int selectedId = optionsGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedOption = findViewById(selectedId);
            selectedAnswers.add(selectedOption.getText().toString());

            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                displayQuestion(currentQuestionIndex);
                if (currentQuestionIndex == questionList.size() - 1) {
                    nextButton.setText("Submit");
                }
            } else {
                ArrayList<String> questionTexts = new ArrayList<>();
                ArrayList<String> correctAnswers = new ArrayList<>();
                for (Question q : questionList) {
                    questionTexts.add(q.questionText);
                    correctAnswers.add(q.correctAnswer);
                }

                Intent intent = new Intent(TaskActivity.this, ResultsActivity.class);
                intent.putStringArrayListExtra("questions", questionTexts);
                intent.putStringArrayListExtra("userAnswers", new ArrayList<>(selectedAnswers));
                intent.putStringArrayListExtra("correctAnswers", correctAnswers);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchQuizFromApi(String topicCsv) {
        String url = "http://10.0.2.2:5001/getQuiz?topic=" + topicCsv;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray quizArray = response.getJSONArray("quiz");

                        for (int i = 0; i < quizArray.length(); i++) {
                            JSONObject qObj = quizArray.getJSONObject(i);
                            String question = qObj.getString("question");
                            JSONArray optionsArray = qObj.getJSONArray("options");
                            String correct = qObj.getString("correct_answer");
                            String topic = qObj.optString("topic", "General");

                            List<String> options = new ArrayList<>();
                            for (int j = 0; j < optionsArray.length(); j++) {
                                options.add(optionsArray.getString(j));
                            }

                            questionList.add(new Question(question, options, correct, topic));
                        }

                        Collections.shuffle(questionList);

                        if (questionList.size() > 6) {
                            questionList = new ArrayList<>(questionList.subList(0, 6));
                        }

                        if (!questionList.isEmpty()) {
                            displayQuestion(currentQuestionIndex);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing quiz: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to load quiz: " + error.getMessage(), Toast.LENGTH_LONG).show());

        queue.add(request);
    }

    private void displayQuestion(int index) {
        Question question = questionList.get(index);
        questionText.setText(question.questionText);
        topicSubtitle.setText("Topic: " + question.topic);
        optionsGroup.removeAllViews();

        for (String option : question.options) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            optionsGroup.addView(rb);
        }
    }
}

package com.example.dasboad.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dasboad.R;
import com.example.dasboad.adapter.QuestionAdapter;
import com.example.dasboad.models.Question;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private RecyclerView recyclerQuestions;
    private List<Question> questionList;
    private QuestionAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        recyclerQuestions = findViewById(R.id.recyclerViewQuestions);
        questionList = new ArrayList<>();
        adapter = new QuestionAdapter(this, questionList);
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(this));
        recyclerQuestions.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("questions")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (snapshots != null) {
                        questionList.clear();
                        for (var doc : snapshots.getDocuments()) {
                            Question q = doc.toObject(Question.class);
                            questionList.add(q);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}

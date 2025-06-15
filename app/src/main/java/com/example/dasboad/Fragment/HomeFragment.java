package com.example.dasboad.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.dasboad.R;
import com.example.dasboad.activities.AddQuestionActivity;
import com.example.dasboad.adapter.QuestionAdapter;
import com.example.dasboad.models.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerQuestions;
    private QuestionAdapter adapter;
    private ArrayList<Question> questionList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerQuestions = view.findViewById(R.id.recyclerQuestions);
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new QuestionAdapter(getContext(), questionList);
        recyclerQuestions.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadQuestions();

        FloatingActionButton fabAddQuestion = view.findViewById(R.id.fabAddQuestion);
        fabAddQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddQuestionActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadQuestions() {
        db.collection("questions")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    questionList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Question q = doc.toObject(Question.class);
                        questionList.add(q);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
package com.example.dasboad.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dasboad.R;
import com.example.dasboad.adapter.AnswerAdapter;
import com.example.dasboad.models.Answer;
import com.example.dasboad.models.Question;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class QuestionDetailActivity extends AppCompatActivity {
    private TextView tvQuestionUserName, tvQuestionTitle, tvQuestionContent, tvQuestionTimestamp, tvLikeCount, tvAnswerCount;
    private ImageButton btnLikeQuestion;
    private EditText edtAnswer;
    private Button btnSendAnswer,btnBack;
    private RecyclerView rvAnswers;
    private AnswerAdapter answerAdapter;
    private ArrayList<Answer> answerList = new ArrayList<>();

    private String questionId;
    private String currentUserId;
    private String currentUserName = "Bạn";

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        // Ánh xạ view
        tvQuestionUserName = findViewById(R.id.tvQuestionUserName);
        tvQuestionTitle = findViewById(R.id.tvQuestionTitle);
        tvQuestionContent = findViewById(R.id.tvQuestionContent);
        tvQuestionTimestamp = findViewById(R.id.tvQuestionTimestamp);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvAnswerCount = findViewById(R.id.tvAnswerCount);
        btnLikeQuestion = findViewById(R.id.btnLikeQuestion);
        edtAnswer = findViewById(R.id.edtAnswer);
        btnSendAnswer = findViewById(R.id.btnSendAnswer);
        rvAnswers = findViewById(R.id.rvAnswers);

        // Lấy id bài viết
        questionId = getIntent().getStringExtra("questionId");
        currentUserId = FirebaseAuth.getInstance().getUid();

        db = FirebaseFirestore.getInstance();

        // Load thông tin bài viết
        loadQuestionDetail();

        // Load danh sách câu trả lời
        setupAnswersRecyclerView();
        loadAnswers();

        // Gửi trả lời
        btnSendAnswer.setOnClickListener(v -> sendAnswer());

        // Like bài viết
        btnLikeQuestion.setOnClickListener(v -> likeQuestion());


        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadQuestionDetail() {
        if(questionId == null) return;
        db.collection("questions").document(questionId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Question question = documentSnapshot.toObject(Question.class);
                        tvQuestionTitle.setText(question.getTitle());
                        tvQuestionContent.setText(question.getContent());
                        tvLikeCount.setText(question.getLikeCount() + " lượt thích");
                        tvAnswerCount.setText(question.getAnswerCount() + " trả lời");

                        // Thời gian
                        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                .format(new Date(question.getTimestamp()));
                        tvQuestionTimestamp.setText(date);

                        // Lấy tên người đăng
                        db.collection("users").document(question.getUserId()).get()
                                .addOnSuccessListener(userDoc -> {
                                    String userName = userDoc.getString("username");
                                    if (userName == null) userName = "Ẩn danh";
                                    tvQuestionUserName.setText(userName);
                                });
                    } else {
                        Toast.makeText(this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupAnswersRecyclerView() {
        answerAdapter = new AnswerAdapter(this, answerList, questionId, currentUserId);
        rvAnswers.setLayoutManager(new LinearLayoutManager(this));
        rvAnswers.setAdapter(answerAdapter);
    }

    private void loadAnswers() {
        CollectionReference answersRef = db.collection("questions").document(questionId).collection("answers");
        answersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable com.google.firebase.firestore.FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    answerList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Answer answer = doc.toObject(Answer.class);
                        answer.setId(doc.getId());
                        answerList.add(answer);
                    }
                    answerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void sendAnswer() {
        String answerText = edtAnswer.getText().toString().trim();
        if (answerText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung trả lời!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy tên user hiện tại (nếu cần)
        db.collection("users").document(currentUserId).get().addOnSuccessListener(userDoc -> {
            String userName = userDoc.getString("username");
            if (userName == null) userName = "Bạn";

            Answer answer = new Answer();

            answer.setQuestionId(questionId);
            answer.setUserId(currentUserId);
            answer.setContent(answerText);
            answer.setTimestamp(System.currentTimeMillis());

            db.collection("questions").document(questionId)
                    .collection("answers")
                    .add(answer)
                    .addOnSuccessListener(documentReference -> {
                        edtAnswer.setText("");
                        String answerId = documentReference.getId();
                        documentReference.update("id", answerId);
                        Toast.makeText(this, "Đã gửi trả lời!", Toast.LENGTH_SHORT).show();
                        // Tăng answerCount cho question
                        db.collection("questions").document(questionId)
                                .update("answerCount", com.google.firebase.firestore.FieldValue.increment(1));
                    });
        });
    }

    private void likeQuestion() {
        // Like đơn giản: tăng likeCount (có thể check user đã like chưa nếu muốn)
        DocumentReference questionRef = db.collection("questions").document(questionId);
        questionRef.update("likeCount", com.google.firebase.firestore.FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Bạn đã thích bài viết!", Toast.LENGTH_SHORT).show());
    }
}
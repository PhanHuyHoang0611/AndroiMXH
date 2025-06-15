package com.example.dasboad.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dasboad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddQuestionActivity extends AppCompatActivity {
    private EditText edtTitle, edtContent, edtCategory;
    private Button btnPost, btnChooseImage, btnBack;
    private ImageView imgPreview;

    private Uri imageUri = null;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        edtCategory = findViewById(R.id.edtCategory);
        btnPost = findViewById(R.id.btnPost);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        imgPreview = findViewById(R.id.imgPreview);
        btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        btnChooseImage.setOnClickListener(v -> chooseImage());
        btnPost.setOnClickListener(v -> postQuestion());

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgPreview.setVisibility(ImageView.VISIBLE);
            imgPreview.setImageURI(imageUri);
        }
    }

    private void postQuestion() {
        String title = edtTitle.getText().toString().trim();
        String content = edtContent.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        long timestamp = System.currentTimeMillis();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String questionId = UUID.randomUUID().toString();

        if (title.isEmpty() || content.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            // Upload ảnh lên Firebase Storage trước
            StorageReference imgRef = storageRef.child("question_images/" + questionId + ".jpg");
            imgRef.putFile(imageUri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) throw task.getException();
                        return imgRef.getDownloadUrl();
                    })
                    .addOnSuccessListener(uri -> {
                        saveQuestion(questionId, title, content, category, userId, timestamp, uri.toString());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi upload ảnh!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Không có ảnh
            saveQuestion(questionId, title, content, category, userId, timestamp, "");
        }
    }

    private void saveQuestion(String questionId, String title, String content, String category, String userId, long timestamp, String imageUrl) {
        Map<String, Object> question = new HashMap<>();
        question.put("id", questionId);
        question.put("title", title);
        question.put("content", content);
        question.put("category", category);
        question.put("imageUrl", imageUrl);
        question.put("userId", userId);
        question.put("timestamp", timestamp);

        db.collection("questions")
                .document(questionId)
                .set(question)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi đăng bài!", Toast.LENGTH_SHORT).show();
                });
    }
}
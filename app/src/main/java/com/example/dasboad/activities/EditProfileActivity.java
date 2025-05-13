package com.example.dasboad.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dasboad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private EditText editUsername, editPhone, editAddress, editBio;
    private Button btnChooseImage, btnSave,btnBack;

    private Uri imageUri;
    private String avatarUrl = "";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        editUsername = findViewById(R.id.editUsername);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        editBio = findViewById(R.id.editBio);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        btnBack=findViewById(R.id.btnBack);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("avatars");

        loadUserProfile();

        btnChooseImage.setOnClickListener(v -> chooseImage());
        btnSave.setOnClickListener(v -> saveUserProfile());

        btnBack.setOnClickListener(v -> {
            // Quay lại màn hình trước đó
            finish(); // Đóng Activity hiện tại
        });
    }

    private void loadUserProfile() {
        String uid = auth.getCurrentUser().getUid(); // Lấy UID của người dùng hiện tại
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Ánh xạ dữ liệu từ Firestore vào giao diện
                        editUsername.setText(documentSnapshot.getString("username"));
                        editPhone.setText(documentSnapshot.getString("phone"));
                        editAddress.setText(documentSnapshot.getString("address"));
                        editBio.setText(documentSnapshot.getString("bio"));
                    } else {
                        Toast.makeText(this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);
        }
    }

    private void saveUserProfile() {
        String username = editUsername.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String bio = editBio.getText().toString().trim();

        if (imageUri != null) {
            storageRef.child(auth.getCurrentUser().getUid() + ".jpg").putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.child(auth.getCurrentUser().getUid() + ".jpg").getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                avatarUrl = uri.toString();
                                updateFirestore(username, phone, address, bio);
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()));
        } else {
            updateFirestore(username, phone, address, bio);
        }
    }

    private void updateFirestore(String username, String phone, String address, String bio) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("username", username);
        userUpdates.put("phone", phone);
        userUpdates.put("address", address);
        userUpdates.put("bio", bio);
        if (!avatarUrl.isEmpty()) {
            userUpdates.put("avatarUrl", avatarUrl);
        }

        db.collection("users").document(auth.getCurrentUser().getUid())
                .update(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile"+ e.getMessage(),Toast.LENGTH_SHORT).show());
    }

}
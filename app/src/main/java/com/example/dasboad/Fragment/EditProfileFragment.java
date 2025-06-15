package com.example.dasboad.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.dasboad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private ImageView imgAvatar;
    private EditText editUsername, editPhone, editAddress, editBio;
    private Button btnChooseImage, btnSave, btnBack;

    private Uri imageUri;
    private String avatarUrl = "";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp giao diện cho fragment này
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Khởi tạo các thành phần giao diện
        imgAvatar = view.findViewById(R.id.imgAvatar);
        editUsername = view.findViewById(R.id.editUsername);
        editPhone = view.findViewById(R.id.editPhone);
        editAddress = view.findViewById(R.id.editAddress);
        editBio = view.findViewById(R.id.editBio);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        btnSave = view.findViewById(R.id.btnSave);
        btnBack = view.findViewById(R.id.btnBack);

        // Khởi tạo Firebase Auth và Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("avatars");

        //Load thông tin người dùng
        loadUserProfile();

        // thiết lập btn
        btnChooseImage.setOnClickListener(v -> chooseImage());
        btnSave.setOnClickListener(v -> saveUserProfile());
        btnBack.setOnClickListener(v -> {
            // Quay lại màn hình trước đó bằng cách thay đổi Fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void loadUserProfile() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Đưa dữ liệu lênh lên giao diện
                        editUsername.setText(documentSnapshot.getString("username"));
                        editPhone.setText(documentSnapshot.getString("phone"));
                        editAddress.setText(documentSnapshot.getString("address"));
                        editBio.setText(documentSnapshot.getString("bio"));

                        String avatarUrl = documentSnapshot.getString("avatarUrl");
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(this).load(avatarUrl).into(imgAvatar);
                        }
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == getActivity().RESULT_OK && data != null) {
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
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()));
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
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Caap nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
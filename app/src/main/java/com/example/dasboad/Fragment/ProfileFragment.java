package com.example.dasboad.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.dasboad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView tvUsername, tvEmail, tvPhone, tvAddress, tvBio, tvRole, tvRank, tvJoinTime;
    private Button btnEditProfile;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Lấy view cho fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ các thành phần giao diện
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvBio = view.findViewById(R.id.tvBio);
        tvRole = view.findViewById(R.id.tvRole);
        tvRank = view.findViewById(R.id.tvRank);
        tvJoinTime = view.findViewById(R.id.tvJoinTime);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // Khởi tạo FirebaseAuth và FirebaseFirestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Hàm đưa thông tin người dùng lên giao diện
        loadUserProfile();

        btnEditProfile.setOnClickListener(v -> {
            // Sử dụng FragmentTransaction để thay thế Fragment
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new EditProfileFragment());
            transaction.addToBackStack(null); // Thêm vào BackStack để có thể quay lại
            transaction.commit();
        });

        return view;
    }

    private void loadUserProfile() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvUsername.setText(documentSnapshot.getString("username"));
                        tvEmail.setText("Email: " + documentSnapshot.getString("email"));
                        tvPhone.setText("Phone: " + documentSnapshot.getString("phone"));
                        tvAddress.setText("Address: " + documentSnapshot.getString("address"));
                        tvBio.setText("Bio: " + documentSnapshot.getString("bio"));
                        tvRole.setText("Role: " + documentSnapshot.getString("role"));
                        tvRank.setText("Rank: " + documentSnapshot.getLong("rank"));

                        Long joinTime = documentSnapshot.getLong("joinTime");
                        if (joinTime != null) {
                            // Kiểm tra nếu giá trị là tính bằng giây thay vì mili-giây
                            if (joinTime < 10000000000L) { // 10 chữ số (giây)
                                joinTime *= 1000; // Chuyển đổi từ giây sang mili-giây
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String formattedDate = sdf.format(new Date(joinTime));
                            tvJoinTime.setText("Joined: " + formattedDate);
                        } else {
                            tvJoinTime.setText("Joined: N/A");
                        }

                        String avatarUrl = documentSnapshot.getString("avatarUrl");
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(this).load(avatarUrl).into(imgAvatar);
                        }
                    }
                });
    }
}
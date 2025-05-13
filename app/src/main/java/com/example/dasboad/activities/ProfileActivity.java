package com.example.dasboad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dasboad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView tvUsername, tvEmail, tvPhone, tvAddress, tvBio, tvRole, tvRank, tvJoinTime;
    private Button btnEditProfile;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvBio = findViewById(R.id.tvBio);
        tvRole = findViewById(R.id.tvRole);
        tvRank = findViewById(R.id.tvRank);
        tvJoinTime = findViewById(R.id.tvJoinTime);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserProfile();

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
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

                        // Kiểm tra giá trị joinTime trước khi sử dụng
                        Long joinTime = documentSnapshot.getLong("joinTime");
                        if (joinTime != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String formattedDate = sdf.format(new Date(joinTime));
                            tvJoinTime.setText("Joined: " + formattedDate);
                        } else {
                            tvJoinTime.setText("Joined: N/A"); // Giá trị mặc định nếu joinTime bị null
                        }

                        String avatarUrl = documentSnapshot.getString("avatarUrl");
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(this).load(avatarUrl).into(imgAvatar);
                        }
                    }
                });
    }
}
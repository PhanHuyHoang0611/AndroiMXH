package com.example.dasboad.activities;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dasboad.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Thiết lập TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        String[] tabs = {"Chào", "Đặt câu", "Câu hỏi", "Các chủ", "Khám"};
        for (String tab : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }

        // Thiết lập RecyclerView
        RecyclerView postRecyclerView = findViewById(R.id.postRecyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Post> postList = getSamplePosts();
        PostAdapter adapter = new PostAdapter(postList);
        postRecyclerView.setAdapter(adapter);

        // Xử lý WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Dữ liệu mẫu
    private List<Post> getSamplePosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post("Tìm kiếm cụm trà lồi", "vừa nãy", "Quán nước này của bạn...", R.drawable.avata, null));
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.avata);
        imageList.add(R.drawable.avata);
        imageList.add(R.drawable.avata);
        posts.add(new Post("Cái đặt tài", "vừa nãy", "Đặt đồ ăn sáng...", null, imageList));
        return posts;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }
}
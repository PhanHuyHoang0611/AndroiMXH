package com.example.dasboad.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dasboad.R;
import com.example.dasboad.activities.QuestionDetailActivity;
import com.example.dasboad.models.Question;

import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private List<Question> questionList;
    private Map<String, String> userNameCache = new HashMap<>();
    private Context context;

    public QuestionAdapter(Context context, List<Question> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question q = questionList.get(position);
        holder.tvTitle.setText(q.getTitle());
        holder.tvContent.setText(q.getContent());
        // Hiển thị số lượt thích và số câu trả lời
        holder.tvLikeCount.setText(q.getLikeCount() + " lượt thích");
        holder.tvAnswerCount.setText(q.getAnswerCount() + " trả lời");

        // Lấy username từ cache hoặc Firestore
        String userId = q.getUserId();
        if (userNameCache.containsKey(userId)) {
            holder.tvUserName.setText(userNameCache.get(userId));
        } else {
            holder.tvUserName.setText("Đang tải");
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String username = documentSnapshot.getString("username");
                        if (username == null) username = "Ẩn danh";
                        userNameCache.put(userId, username);
                        holder.tvUserName.setText(username);
                    })
                    .addOnFailureListener(e -> {
                        holder.tvUserName.setText("Ẩn danh");
                    });
        }

        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(q.getTimestamp()));
        holder.tvTimestamp.setText(date);

        // Sự kiện click vào item để mở chi tiết bài viết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuestionDetailActivity.class);
            intent.putExtra("questionId", q.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTimestamp, tvUserName, tvLikeCount, tvAnswerCount;
        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvAnswerCount = itemView.findViewById(R.id.tvAnswerCount);
        }
    }
}
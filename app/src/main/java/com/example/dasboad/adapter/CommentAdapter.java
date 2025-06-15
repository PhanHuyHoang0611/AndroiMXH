package com.example.dasboad.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dasboad.R;
import com.example.dasboad.models.Comment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private String questionId;
    private String answerId;
    private String currentUserId;

    // Constructor đầy đủ
    public CommentAdapter(Context context, List<Comment> commentList, String questionId, String answerId, String currentUserId) {
        this.context = context;
        this.commentList = commentList;
        this.questionId = questionId;
        this.answerId = answerId;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Lấy tên người dùng từ Firestore
        if (comment.userId != null && !comment.userId.isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(comment.userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String userName = documentSnapshot.getString("username");
                        if (userName == null || userName.isEmpty()) userName = "Ẩn danh";
                        holder.tvCommentUserName.setText(userName);
                    })
                    .addOnFailureListener(e -> holder.tvCommentUserName.setText("Ẩn danh"));
        } else {
            holder.tvCommentUserName.setText("Ẩn danh");
        }

        holder.tvCommentContent.setText(comment.content != null ? comment.content : "");

        // Hiển thị thời gian bình luận
        if (comment.timestamp > 0) {
            String date = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
                    .format(new Date(comment.timestamp));
            holder.tvCommentTimestamp.setText(date);
            holder.tvCommentTimestamp.setVisibility(View.VISIBLE);
        } else {
            holder.tvCommentTimestamp.setVisibility(View.GONE);
        }

        // Chỉ chủ bình luận mới được sửa/xóa
        if (comment.userId != null && comment.userId.equals(currentUserId)) {
            holder.btnEditComment.setVisibility(View.VISIBLE);
            holder.btnDeleteComment.setVisibility(View.VISIBLE);
        } else {
            holder.btnEditComment.setVisibility(View.GONE);
            holder.btnDeleteComment.setVisibility(View.GONE);
        }

        // Xử lý sự kiện XÓA
        holder.btnDeleteComment.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa bình luận")
                    .setMessage("Bạn chắc chắn muốn xóa bình luận này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("questions").document(questionId)
                                .collection("answers").document(answerId)
                                .collection("comments").document(comment.id)
                                .delete()
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(context, "Đã xóa bình luận", Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // Xử lý sự kiện SỬA
        holder.btnEditComment.setOnClickListener(v -> {
            EditText editText = new EditText(context);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editText.setText(comment.content);
            new AlertDialog.Builder(context)
                    .setTitle("Sửa bình luận")
                    .setView(editText)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        String newContent = editText.getText().toString().trim();
                        if (!newContent.isEmpty() && !newContent.equals(comment.content)) {
                            FirebaseFirestore.getInstance()
                                    .collection("questions").document(questionId)
                                    .collection("answers").document(answerId)
                                    .collection("comments").document(comment.id)
                                    .update("content", newContent)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(context, "Đã sửa bình luận", Toast.LENGTH_SHORT).show()
                                    );
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentUserName, tvCommentContent, tvCommentTimestamp;
        ImageButton btnEditComment, btnDeleteComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentUserName = itemView.findViewById(R.id.tvCommentUserName);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            tvCommentTimestamp = itemView.findViewById(R.id.tvCommentTimestamp);
            btnEditComment = itemView.findViewById(R.id.btnEditComment);
            btnDeleteComment = itemView.findViewById(R.id.btnDeleteComment);
        }
    }
}
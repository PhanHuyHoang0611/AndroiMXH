package com.example.dasboad.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dasboad.R;
import com.example.dasboad.models.Answer;
import com.example.dasboad.models.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    private Context context;
    private List<Answer> answerList;
    private String questionId;
    private String currentUserId;



    public AnswerAdapter(Context context, List<Answer> answerList, String questionId, String currentUserId) {
        this.context = context;
        this.answerList = answerList;
        this.questionId = questionId;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        Answer answer = answerList.get(position);

        // Hiển thị nội dung answer
        holder.tvAnswerContent.setText(answer.content != null ? answer.content : "");
        holder.tvVoteCount.setText(String.valueOf(answer.voteCount));
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(answer.timestamp));
        holder.tvAnswerTimestamp.setText(date);

        // Lấy tên người trả lời
        fetchUserName(answer.userId, holder.tvAnswerUserName);

        // Reference tới comments của answer này
        CollectionReference commentsRef = FirebaseFirestore.getInstance()
                .collection("questions")
                .document(questionId)
                .collection("answers")
                .document(answer.id)
                .collection("comments");

        // Load comment cho answer này
        loadComments(holder, commentsRef);

        // Sự kiện gửi bình luận cho answer
        holder.btnSendAnswerComment.setOnClickListener(v -> {
            String commentText = holder.edtAnswerComment.getText().toString().trim();
            if (TextUtils.isEmpty(commentText)) {
                Toast.makeText(context, "Vui lòng nhập nội dung bình luận!", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = FirebaseAuth.getInstance().getUid();
            String userName = "Bạn"; // Có thể lấy từ Firestore nếu muốn

            // Tạo commentId từ Firestore
            String commentId = commentsRef.document().getId();

            // Nếu bạn không dùng ảnh thì để image là ""
            String imageUrl = "";

            Comment comment = new Comment();
            comment.setId(commentId);
            comment.setAnswerId(answer.id);
            comment.setUserId(userId);
            comment.setContent(commentText);
            comment.setImage(imageUrl);
            comment.setTimestamp(System.currentTimeMillis());

            commentsRef.document(commentId)
                    .set(comment)
                    .addOnSuccessListener(aVoid -> {
                        holder.edtAnswerComment.setText("");
                        Toast.makeText(context, "Đã gửi bình luận!", Toast.LENGTH_SHORT).show();
                    });
        });

        // Chỉ chủ bình luận mới được sửa/xóa
        if (answer.userId != null && answer.userId.equals(currentUserId)) {
            holder.btnEditAnswer.setVisibility(View.VISIBLE);
            holder.btnDeleteAnswer.setVisibility(View.VISIBLE);
        } else {
            holder.btnEditAnswer.setVisibility(View.GONE);
            holder.btnEditAnswer.setVisibility(View.GONE);
        }

        // SỬA Answer
        holder.btnEditAnswer.setOnClickListener(v -> {
            EditText editText = new EditText(context);
            editText.setText(answer.content);
            new AlertDialog.Builder(context)
                    .setTitle("Sửa câu trả lời")
                    .setView(editText)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        String newContent = editText.getText().toString().trim();
                        if (!newContent.isEmpty() && !newContent.equals(answer.content)) {
                            FirebaseFirestore.getInstance()
                                    .collection("questions").document(questionId)
                                    .collection("answers").document(answer.id)
                                    .update("content", newContent)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(context, "Đã sửa câu trả lời", Toast.LENGTH_SHORT).show()
                                    );
                        }
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

        // XOÁ Answer và tất cả comments của nó
        holder.btnDeleteAnswer.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xoá câu trả lời")
                    .setMessage("Bạn chắc chắn muốn xoá câu trả lời này? Tất cả bình luận của nó cũng sẽ bị xoá.")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("questions")
                                .document(questionId)
                                .collection("answers")
                                .document(answer.id)
                                .collection("comments")
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    for (DocumentSnapshot doc : querySnapshot) {
                                        doc.getReference().delete();
                                    }
                                    db.collection("questions")
                                            .document(questionId)
                                            .collection("answers")
                                            .document(answer.id)
                                            .delete()
                                            .addOnSuccessListener(aVoid ->
                                                    Toast.makeText(context, "Đã xoá câu trả lời", Toast.LENGTH_SHORT).show()
                                            );
                                });
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
        // Xử lý rating cho answer (nếu cần)
        holder.ratingBarVote.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                int vote = (int) rating;
                FirebaseFirestore.getInstance()
                        .collection("questions")
                        .document(questionId)
                        .collection("answers")
                        .document(answer.id)
                        .update("voteCount", vote)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Bạn đã đánh giá " + vote + " sao!", Toast.LENGTH_SHORT).show();
                            answer.voteCount = vote;
                            holder.tvVoteCount.setText(String.valueOf(vote));
                        });
            }
        });
    }
    private void fetchUserName(String userId, TextView tv) {
        if (userId == null) {
            tv.setText("Ẩn danh");
            return;
        }
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.getString("username");
                    if (userName == null) userName = userId;
                    tv.setText(userName);
                })
                .addOnFailureListener(e -> tv.setText("Ẩn danh"));
    }

    private void loadComments(AnswerViewHolder holder, CollectionReference commentsRef) {
        commentsRef.orderBy("timestamp")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    List<Comment> commentList = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Comment comment = doc.toObject(Comment.class);
                            commentList.add(comment);
                        }
                    }
                    CommentAdapter commentAdapter = new CommentAdapter(context, commentList, questionId, answerList.get(holder.getAdapterPosition()).id, FirebaseAuth.getInstance().getUid());
                    holder.rvAnswerComments.setAdapter(commentAdapter);
                });
    }

    @Override
    public int getItemCount() {
        return answerList == null ? 0 : answerList.size();
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnswerUserName, tvAnswerContent, tvAnswerTimestamp, tvVoteCount;
        RecyclerView rvAnswerComments;
        EditText edtAnswerComment;
        Button btnSendAnswerComment;
        ImageButton btnEditAnswer, btnDeleteAnswer;
        RatingBar ratingBarVote;
        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAnswerUserName = itemView.findViewById(R.id.tvAnswerUserName);
            tvAnswerContent = itemView.findViewById(R.id.tvAnswerContent);
            tvAnswerTimestamp = itemView.findViewById(R.id.tvAnswerTimestamp);
            tvVoteCount = itemView.findViewById(R.id.tvVoteCount);
            rvAnswerComments = itemView.findViewById(R.id.rvAnswerComments);
            edtAnswerComment = itemView.findViewById(R.id.edtAnswerComment);
            btnSendAnswerComment = itemView.findViewById(R.id.btnSendAnswerComment);
            ratingBarVote = itemView.findViewById(R.id.ratingBarVote);
            btnEditAnswer = itemView.findViewById(R.id.btnEditAnswer);
            btnDeleteAnswer = itemView.findViewById(R.id.btnDeleteAnswer);
            rvAnswerComments.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
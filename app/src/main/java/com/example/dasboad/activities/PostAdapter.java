package com.example.dasboad.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dasboad.R;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.usernameTextView.setText(post.getUsername());
        holder.timeTextView.setText(post.getTime());
        holder.contentTextView.setText(post.getContent());

        if (post.getSingleImage() != null) {
            holder.postImageView.setVisibility(View.VISIBLE);
            holder.postImageView.setImageResource(post.getSingleImage());
            holder.imageGridRecyclerView.setVisibility(View.GONE);
        } else if (post.getImageList() != null && !post.getImageList().isEmpty()) {
            holder.postImageView.setVisibility(View.GONE);
            holder.imageGridRecyclerView.setVisibility(View.VISIBLE);
            holder.imageGridRecyclerView.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 3));
            ImageAdapter imageAdapter = new ImageAdapter(post.getImageList());
            holder.imageGridRecyclerView.setAdapter(imageAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView, postImageView;
        TextView usernameTextView, timeTextView, contentTextView, likeTextView, commentTextView;
        RecyclerView imageGridRecyclerView;

        public PostViewHolder(View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            postImageView = itemView.findViewById(R.id.postImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            likeTextView = itemView.findViewById(R.id.likeTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            imageGridRecyclerView = itemView.findViewById(R.id.imageGridRecyclerView);
        }
    }
}
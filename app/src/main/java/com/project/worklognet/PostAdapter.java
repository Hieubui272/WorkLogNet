package com.project.worklognet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> postList;
    private final Context context;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvContent.setText(post.getContent());
        holder.tvLikeCount.setText(String.format("%d likes", post.getLikes()));

        // Hiển thị thời gian đăng bài
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String date = sdf.format(new Date(post.getTimestamp()));
        holder.tvTimestamp.setText(date);

        // Hiển thị ảnh bài viết (nếu có)
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(context).load(post.getImageUrl()).into(holder.ivPostImage);
            holder.ivPostImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }

        // Cập nhật biểu tượng Like/Unlike
        updateLikeIcon(holder.ivLike, post.isLiked());

        // Xử lý sự kiện khi nhấn nút "Like"
        holder.ivLike.setOnClickListener(v -> toggleLike(post, holder));

        // Xử lý sự kiện khi nhấn nút "Comment"
        holder.ivComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getPostId());
            context.startActivity(intent);
        });

        // Xử lý sự kiện khi nhấn nút "Share"
        holder.ivShare.setOnClickListener(v -> sharePost(post));

        // Xử lý sự kiện khi nhấn nút "Delete"
        holder.ivDelete.setOnClickListener(v -> deletePost(post, position));
    }

    private void toggleLike(Post post, PostViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postRef = db.collection("posts").document(post.getPostId());

        if (post.isLiked()) {
            // Bỏ like
            postRef.update("likes", Math.max(0, post.getLikes() - 1))
                    .addOnSuccessListener(aVoid -> {
                        post.setLiked(false);
                        post.setLikes(Math.max(0, post.getLikes() - 1));
                        updateLikeIcon(holder.ivLike, false);
                        holder.tvLikeCount.setText(String.format("%d likes", post.getLikes()));
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi bỏ thích bài viết", Toast.LENGTH_SHORT).show());
        } else {
            // Like
            postRef.update("likes", post.getLikes() + 1)
                    .addOnSuccessListener(aVoid -> {
                        post.setLiked(true);
                        post.setLikes(post.getLikes() + 1);
                        updateLikeIcon(holder.ivLike, true);
                        holder.tvLikeCount.setText(String.format("%d likes", post.getLikes()));
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi thích bài viết", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateLikeIcon(ImageView likeIcon, boolean isLiked) {
        if (isLiked) {
            likeIcon.setImageResource(R.drawable.ic_like_filled); // Đổi icon khi đã like
        } else {
            likeIcon.setImageResource(R.drawable.ic_like); // Đổi icon khi chưa like
        }
    }

    // Hàm chia sẻ bài viết
    private void sharePost(Post post) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = post.getContent();
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            shareText += "\n" + post.getImageUrl(); // Thêm link ảnh nếu có
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(shareIntent, "Share post via"));
    }

    // Hàm xóa bài viết
    private void deletePost(Post post, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference postRef = db.collection("posts").document(post.getPostId());

        postRef.delete()
                .addOnSuccessListener(aVoid -> {
                    postList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, postList.size());
                    Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvLikeCount, tvTimestamp;
        ImageView ivPostImage, ivLike, ivComment, ivShare, ivDelete;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivShare = itemView.findViewById(R.id.ivShare);
            ivDelete = itemView.findViewById(R.id.ivDelete); // Nút Delete
        }
    }
}

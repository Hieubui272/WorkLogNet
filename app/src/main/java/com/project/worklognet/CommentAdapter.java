package com.project.worklognet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final List<Comment> commentList;
    private final Context context;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Hiển thị tên người dùng
        holder.tvUserName.setText(comment.getUserName() != null ? comment.getUserName() : "Unknown User");

        // Hiển thị nội dung bình luận
        holder.tvCommentText.setText(comment.getCommentText() != null ? comment.getCommentText() : "No comment available");

        // Kiểm tra và hiển thị thời gian bình luận
        if (comment.getTimestamp() != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            holder.tvTimestamp.setText(sdf.format(comment.getTimestamp()));
        } else {
            holder.tvTimestamp.setText("N/A");
        }

        // Xử lý sự kiện xóa bình luận
        holder.ivDeleteComment.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("comments").document(comment.getCommentId())  // Đảm bảo Comment có trường commentId
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Xóa bình luận khỏi danh sách và cập nhật RecyclerView
                        commentList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, commentList.size());
                        Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvCommentText, tvTimestamp;
        ImageView ivDeleteComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivDeleteComment = itemView.findViewById(R.id.ivDeleteComment); // Tham chiếu tới nút xóa
        }
    }
}

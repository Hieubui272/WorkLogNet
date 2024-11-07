package com.project.worklognet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private EditText etComment;
    private Button btnSubmit;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private FirebaseFirestore db;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get postId from intent
        postId = getIntent().getStringExtra("postId");

        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmit);
        rvComments = findViewById(R.id.rvComments);

        // Set up RecyclerView
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, commentList);
        rvComments.setAdapter(commentAdapter);

        // Load existing comments
        loadComments();

        // Handle comment submission
        btnSubmit.setOnClickListener(v -> submitComment());
    }

    private void submitComment() {
        String commentText = etComment.getText().toString().trim();

        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        // Prepare comment data
        Map<String, Object> comment = new HashMap<>();
        comment.put("postId", postId);
        comment.put("userId", userId);
        comment.put("username", username);
        comment.put("commentText", commentText);
        comment.put("timestamp", System.currentTimeMillis());

        // Add comment to Firestore
        db.collection("comments")
                .add(comment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show();
                    etComment.setText("");
                    loadComments();  // Refresh comments after adding
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show());
    }

    private void loadComments() {
        db.collection("comments")
                .whereEqualTo("postId", postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        commentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comment comment = document.toObject(Comment.class);
                            comment.setCommentId(document.getId()); // Set comment ID
                            commentList.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

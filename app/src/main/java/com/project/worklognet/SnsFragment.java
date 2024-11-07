package com.project.worklognet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnsFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri selectedImageUri;

    private EditText etPostContent;
    private Button btnSelectImage, btnPost;
    private ImageView ivSelectedImage;
    private ProgressBar progressBar;
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();

    private final ActivityResultLauncher<Intent> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivSelectedImage.setImageURI(selectedImageUri);
                    ivSelectedImage.setVisibility(View.VISIBLE);
                }
            }
    );

    public SnsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sns, container, false);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        etPostContent = view.findViewById(R.id.etPostContent);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnPost = view.findViewById(R.id.btnPost);
        ivSelectedImage = view.findViewById(R.id.ivSelectedImage);
        progressBar = view.findViewById(R.id.progressBar);
        rvPosts = view.findViewById(R.id.rvPosts);

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnPost.setOnClickListener(v -> postContent());

        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), postList);
        rvPosts.setAdapter(postAdapter);
        loadPosts();
        return view;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }

    private void postContent() {
        String content = etPostContent.getText().toString().trim();

        if (selectedImageUri != null) {
            toggleLoading(true);
            uploadImageAndPost(content);
        } else {
            Toast.makeText(requireContext(), "Please select an image before posting.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageAndPost(String content) {
        StorageReference imageRef = storageRef.child("posts/" + System.currentTimeMillis() + ".jpg");
        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> postToFirestore(content, uri.toString())))
                .addOnFailureListener(e -> {
                    toggleLoading(false);
                    Toast.makeText(requireContext(), "Failed to upload image.", Toast.LENGTH_SHORT).show();
                });
    }

    private void postToFirestore(String content, String imageUrl) {
        Map<String, Object> post = new HashMap<>();
        post.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        post.put("content", content);
        post.put("imageUrl", imageUrl);
        post.put("timestamp", System.currentTimeMillis());
        post.put("likes", 0);

        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Posted successfully", Toast.LENGTH_SHORT).show();
                    resetUI();
                    loadPosts(); // Làm mới danh sách bài viết sau khi đăng thành công
                })
                .addOnFailureListener(e -> {
                    toggleLoading(false);
                    Toast.makeText(requireContext(), "Failed to post", Toast.LENGTH_SHORT).show();
                });
    }

    private void resetUI() {
        etPostContent.setText("");
        ivSelectedImage.setImageURI(null);
        ivSelectedImage.setVisibility(View.GONE);
        selectedImageUri = null;
        toggleLoading(false);
    }

    private void toggleLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnPost.setEnabled(!isLoading);
        btnSelectImage.setEnabled(!isLoading);
    }

    private void loadPosts() {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            post.setPostId(document.getId()); // Lưu postId
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Failed to load posts.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

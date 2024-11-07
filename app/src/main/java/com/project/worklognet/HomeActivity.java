package com.project.worklognet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    // DEF
    private TextView tvWelcome;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private int currentMenuItem = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Full screen and hide action bar
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // [End] Full screen and hide action bar

        // Get Instance
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // [End] Get Instance

        // Check User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            fetchUserInfo(currentUser.getUid());
        } else {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
        }
        // [End] Check User

        // Logout button
        // Button logoutButton = findViewById(R.id.logoutButton);
        // logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set mặc định hiển thị Fragment đầu tiên khi khởi động Activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        // Lắng nghe sự kiện khi người dùng chọn item từ BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int newMenuItem;

                // Dựa vào id của item để hiển thị Fragment tương ứng
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = new HomeFragment();
                        newMenuItem = 1;
                        break;
                    case R.id.navigation_analysis:
                        selectedFragment = new AnalysisFragment();
                        newMenuItem = 2;
                        break;
                    case R.id.navigation_add:
                        selectedFragment = new AddFragment();
                        newMenuItem = 3;
                        break;
                    case R.id.navigation_sns:
                        selectedFragment = new SnsFragment();
                        newMenuItem = 4;
                        break;
                    case R.id.navigation_my:
                        selectedFragment = new MyPageFragment();
                        newMenuItem = 5;
                        break;
                    default:
                        return false;
                }

                // Thay đổi Fragment
                // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                if (selectedFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    if (newMenuItem > currentMenuItem) {
                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                    }

                    transaction.replace(R.id.fragment_container, selectedFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    currentMenuItem = newMenuItem;
                }

                return true;
            }
        });
    }

    private void fetchUserInfo(String uid) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nickname = documentSnapshot.getString("nickname");
                        String email = documentSnapshot.getString("email");
                    } else {
                        Toast.makeText(HomeActivity.this, "User info not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Failed to fetch user info.", Toast.LENGTH_SHORT).show();
                });
    }

    // Show logout popup
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Do you want to logout?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

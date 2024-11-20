package com.project.worklognet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class MyPageFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picker
    private ImageView ivProfile;
    private FirebaseAuth mAuth;

    public MyPageFragment() {
        // Required empty public constructor
    }

    public static MyPageFragment newInstance(String param1, String param2) {
        MyPageFragment fragment = new MyPageFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle parameters if needed
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
        ivProfile = view.findViewById(R.id.ivProfile); // Bind ImageView

        // Load saved mobile and address
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE);
        String savedMobile = prefs.getString("mobile", "010-xxxx-xxxx");
        String savedAddress = prefs.getString("address", "SSU");

        // Update UI
        ((TextView) view.findViewById(R.id.tvMobile)).setText(savedMobile);
        ((TextView) view.findViewById(R.id.tvAddress)).setText(savedAddress);

        // Add listeners
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        view.findViewById(R.id.ivSettings).setOnClickListener(v -> openSettings());
        view.findViewById(R.id.ivNotification).setOnClickListener(v -> openNotifications());
        ivProfile.setOnClickListener(v -> changeBackgroundImage()); // Set image picker for profile image

        // Add click listeners for Password, Mobile, and Address
        view.findViewById(R.id.btnChangePassword).setOnClickListener(this::changePassword);
        view.findViewById(R.id.tvMobile).setOnClickListener(this::changeMobile);
        view.findViewById(R.id.tvAddress).setOnClickListener(this::changeAddress);

        return view;
    }

    // Method to handle logout
    public void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Logout");
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Open Settings Activity
    private void openSettings() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    // Open Notifications Activity
    private void openNotifications() {
        Intent intent = new Intent(getActivity(), NotificationsActivity.class);
        startActivity(intent);
    }

    // Open Image Picker for changing profile picture
    public void changeBackgroundImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                // Set selected image as profile picture
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                ivProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Change Password Dialog
    public void changePassword(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnConfirmPasswordChange).setOnClickListener(v -> {
            String oldPassword = ((EditText) dialogView.findViewById(R.id.etOldPassword)).getText().toString();
            String newPassword = ((EditText) dialogView.findViewById(R.id.etNewPassword)).getText().toString();

            // Perform password update logic here
            Toast.makeText(getContext(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    // Change Mobile Dialog
    public void changeMobile(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_mobile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnConfirmMobileChange).setOnClickListener(v -> {
            String newMobile = ((EditText) dialogView.findViewById(R.id.etNewMobile)).getText().toString();

            if (newMobile.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to SharedPreferences
            requireContext().getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE)
                    .edit()
                    .putString("mobile", newMobile)
                    .apply();

            // Update UI
            ((TextView) getView().findViewById(R.id.tvMobile)).setText(newMobile);
            Toast.makeText(getContext(), "Mobile number updated successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    // Change Address Dialog
    public void changeAddress(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_address, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnConfirmAddressChange).setOnClickListener(v -> {
            String newAddress = ((EditText) dialogView.findViewById(R.id.etNewAddress)).getText().toString();

            if (newAddress.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a valid address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to SharedPreferences
            requireContext().getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE)
                    .edit()
                    .putString("address", newAddress)
                    .apply();

            // Update UI
            ((TextView) getView().findViewById(R.id.tvAddress)).setText(newAddress);
            Toast.makeText(getContext(), "Address updated successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}

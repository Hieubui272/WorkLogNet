package com.project.worklognet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText etWorkPlace, etWagePerHour;
    private TextView tvStartTime, tvEndTime;
    private Button btnAdd;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Calendar startTime, endTime;
    private ProgressDialog progressDialog;


    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // UI Elements initialization
        etWorkPlace = view.findViewById(R.id.etWorkPlace);
        etWagePerHour = view.findViewById(R.id.etWagePerHour);
        tvStartTime = view.findViewById(R.id.tvStartTime);
        tvEndTime = view.findViewById(R.id.tvEndTime);
        btnAdd = view.findViewById(R.id.btnAdd);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<WorkEntry> workEntryList = new ArrayList<>();
        WorkEntryAdapter adapter = new WorkEntryAdapter(requireContext(), workEntryList,
                position -> showEditPopup(workEntryList.get(position), position),
                position -> deleteWorkLog(workEntryList.get(position).getDocumentId()));  // Handle delete


        recyclerView.setAdapter(adapter);

        // Initialize calendar for date-time picking
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();

        // Set default start and end time to current time
        updateDateTimeText(tvStartTime, startTime);
        updateDateTimeText(tvEndTime, endTime);

        // Start time picker
        tvStartTime.setOnClickListener(v -> showDateTimePicker(startTime, tvStartTime));

        // End time picker
        tvEndTime.setOnClickListener(v -> showDateTimePicker(endTime, tvEndTime));

        // Add button logic
        btnAdd.setOnClickListener(v -> addWorkLog());

        // Firebase instance
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ Firebase và cập nhật danh sách
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("workLogs")
                .orderBy("startTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    workEntryList.clear();  // Xóa danh sách cũ
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        WorkEntry entry = document.toObject(WorkEntry.class);
                        if (entry != null) {
                            entry.setDocumentId(document.getId());  // Gán documentId từ Firestore vào WorkEntry
                            workEntryList.add(entry);  // Thêm vào danh sách
                        }
                    }
                    adapter.notifyDataSetChanged();  // Cập nhật RecyclerView sau khi thay đổi dữ liệu
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


        return view;
    }

    private void deleteWorkLog(String documentId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this work log?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    showLoadingDialog();
                    String userId = mAuth.getCurrentUser().getUid();
                    db.collection("users").document(userId).collection("workLogs")
                            .document(documentId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                hideLoadingDialog();
                                Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                reloadFragment();
                            })
                            .addOnFailureListener(e -> {
                                hideLoadingDialog();
                                Toast.makeText(requireContext(), "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private void updateDateTimeText(TextView textView, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        textView.setText(sdf.format(calendar.getTime()));
    }

    private void showDateTimePicker(Calendar calendar, TextView textView) {
        // Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            // Time Picker after date is set
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateDateTimeText(textView, calendar);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void addWorkLog() {
        String workPlace = etWorkPlace.getText().toString().trim();
        String wagePerHourStr = etWagePerHour.getText().toString().trim();

        if (workPlace.isEmpty() || wagePerHourStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double wagePerHour = Double.parseDouble(wagePerHourStr);

        if (endTime.before(startTime)) {
            Toast.makeText(requireContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tính toán số giờ làm việc
        long diffMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        double hoursWorked = (double) diffMillis / (1000 * 60 * 60); // Số giờ làm việc
        if (hoursWorked < 0) {
            Toast.makeText(requireContext(), "Invalid time range", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalEarnings = hoursWorked * wagePerHour;

        // Confirm work log entry
        showConfirmationDialog(workPlace, hoursWorked, wagePerHour, totalEarnings);
    }


    private void showConfirmationDialog(String workPlace, double hoursWorked, double wagePerHour, double totalEarnings) {
        long hours = (long) hoursWorked;
        long minutes = Math.round((hoursWorked - hours) * 60);

        String timeWorkedText = String.format(Locale.getDefault(), "%d hours %d minutes", hours, minutes);
        String totalEarningsText = String.format(Locale.getDefault(), "%.2f", totalEarnings);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Work Log");
        builder.setMessage("You worked " + timeWorkedText +
                " at " + String.format(Locale.getDefault(), "%.2f", wagePerHour) + " per hour. Total: " + totalEarningsText);

        builder.setPositiveButton("Yes", (dialog, which) -> saveWorkLog(workPlace, hoursWorked, wagePerHour, totalEarnings));
        builder.setNegativeButton("Something Wrong", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void saveWorkLog(String workPlace, double hoursWorked, double wagePerHour, double totalEarnings) {
        showLoadingDialog();
        String uid = mAuth.getCurrentUser().getUid();
        Map<String, Object> workLog = new HashMap<>();
        workLog.put("workPlace", workPlace);
        workLog.put("startTime", startTime.getTime());
        workLog.put("endTime", endTime.getTime());
        workLog.put("hoursWorked", hoursWorked);
        workLog.put("wagePerHour", wagePerHour);
        workLog.put("totalEarnings", totalEarnings);

        db.collection("users").document(uid).collection("workLogs")
                .add(workLog)
                .addOnSuccessListener(documentReference -> {
                    hideLoadingDialog();
                    Toast.makeText(requireContext(), "Work log added successfully", Toast.LENGTH_SHORT).show();
                    reloadFragment();
                })
                .addOnFailureListener(e -> {
                    hideLoadingDialog();
                    Toast.makeText(requireContext(), "Failed to add work log", Toast.LENGTH_SHORT).show();
                });
    }

    private void reloadFragment() {
        AddFragment newFragment = AddFragment.newInstance(mParam1, mParam2);

        // Tạo một FragmentTransaction và set animation
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, newFragment) // Thay thế fragment hiện tại
                .commit();
    }

    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing ...");
        progressDialog.setCancelable(false);  // Không cho phép hủy khi nhấn ngoài dialog
        progressDialog.show();
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void showEditPopup(WorkEntry entry, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Work Log");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_work_entry, null);
        EditText etWorkPlace = view.findViewById(R.id.etWorkPlace);
        EditText etWagePerHour = view.findViewById(R.id.etWagePerHour);
        TextView tvStartTime = view.findViewById(R.id.tvStartTime);
        TextView tvEndTime = view.findViewById(R.id.tvEndTime);

        etWorkPlace.setText(entry.getWorkPlace());
        etWagePerHour.setText(String.valueOf(entry.getWagePerHour()));
        tvStartTime.setText(entry.getFormattedStartTime());
        tvEndTime.setText(entry.getFormattedEndTime());

        builder.setView(view);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String updatedWorkPlace = etWorkPlace.getText().toString().trim();
            double updatedWagePerHour = Double.parseDouble(etWagePerHour.getText().toString().trim());

            // Update Firestore entry
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("workPlace", updatedWorkPlace);
            updatedData.put("wagePerHour", updatedWagePerHour);

            db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .collection("workLogs").document(entry.getDocumentId())
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                        reloadFragment();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
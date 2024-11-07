package com.project.worklognet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;


public class AnalysisFragment extends Fragment {

    private TextView tvTotalIncome, tvWorkplaceIncome;
    private Button btnToday, btnWeek, btnMonth, btnYear, btnOption;
    private PieChart pieChart;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Calendar customStartDate, customEndDate;

    public AnalysisFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvWorkplaceIncome = view.findViewById(R.id.tvWorkplaceIncome);
        btnToday = view.findViewById(R.id.btnToday);
        btnWeek = view.findViewById(R.id.btnWeek);
        btnMonth = view.findViewById(R.id.btnMonth);
        btnYear = view.findViewById(R.id.btnYear);
        btnOption = view.findViewById(R.id.btnOption);
        pieChart = view.findViewById(R.id.pieChart);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        customStartDate = Calendar.getInstance();
        customEndDate = Calendar.getInstance();

        calculateIncome("today");

        btnToday.setOnClickListener(v -> calculateIncome("today"));
        btnWeek.setOnClickListener(v -> calculateIncome("week"));
        btnMonth.setOnClickListener(v -> calculateIncome("month"));
        btnYear.setOnClickListener(v -> calculateIncome("year"));
        btnOption.setOnClickListener(v -> showDateRangePicker());

        return view;
    }

    private void calculateIncome(String range) {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("workLogs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalIncome = 0.0;
                    Map<String, Double> workplaceIncomeMap = new HashMap<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        WorkEntry entry = document.toObject(WorkEntry.class);
                        if (isInRange(entry, range)) {
                            totalIncome += entry.getTotalEarnings();

                            String workplace = entry.getWorkPlace();
                            workplaceIncomeMap.put(workplace, workplaceIncomeMap.getOrDefault(workplace, 0.0) + entry.getTotalEarnings());
                        }
                    }

                    tvTotalIncome.setText(String.format(Locale.getDefault(), "Total income: %.2f", totalIncome));

                    StringBuilder workplaceIncomeText = new StringBuilder();
                    List<PieEntry> pieEntries = new ArrayList<>();

                    for (Map.Entry<String, Double> entry : workplaceIncomeMap.entrySet()) {
                        double income = entry.getValue();
                        double percentage = (income / totalIncome) * 100;
                        workplaceIncomeText.append("- ").append(entry.getKey()).append(": ")
                                .append(String.format(Locale.getDefault(), "%.2f", income))
                                .append(" (").append(String.format(Locale.getDefault(), "%.2f", percentage))
                                .append("%)\n");

                        pieEntries.add(new PieEntry((float) income, entry.getKey()));
                    }
                    tvWorkplaceIncome.setText(workplaceIncomeText.toString());

                    setupPieChart(pieEntries);

                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Lỗi khi lấy dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupPieChart(List<PieEntry> pieEntries) {
        PieDataSet dataSet = new PieDataSet(pieEntries, "Income by Workplace");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);  // Sử dụng màu mặc định
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Workplace Income");
        pieChart.setCenterTextSize(14f);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // Refresh the chart
    }

    private boolean isInRange(WorkEntry entry, String range) {
        Calendar entryDate = Calendar.getInstance();
        entryDate.setTime(entry.getStartTime().toDate());

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        switch (range) {
            case "today":
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);

                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                break;

            case "week":
                startDate.set(Calendar.DAY_OF_WEEK, startDate.getFirstDayOfWeek());
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);

                endDate.set(Calendar.DAY_OF_WEEK, startDate.getFirstDayOfWeek() + 6);
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                break;

            case "month":
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);

                endDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                break;

            case "year":
                startDate.set(Calendar.DAY_OF_YEAR, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);

                endDate.set(Calendar.DAY_OF_YEAR, startDate.getActualMaximum(Calendar.DAY_OF_YEAR));
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                break;

            case "custom":
                startDate = customStartDate;
                endDate = customEndDate;
                break;

            default:
                return false;
        }

        return !entryDate.before(startDate) && !entryDate.after(endDate);
    }

    private void showDateRangePicker() {
        DatePickerDialog startDatePicker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    customStartDate.set(Calendar.YEAR, year);
                    customStartDate.set(Calendar.MONTH, month);
                    customStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    DatePickerDialog endDatePicker = new DatePickerDialog(requireContext(),
                            (view1, year1, month1, dayOfMonth1) -> {
                                customEndDate.set(Calendar.YEAR, year1);
                                customEndDate.set(Calendar.MONTH, month1);
                                customEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth1);

                                if (customEndDate.before(customStartDate)) {
                                    Toast.makeText(requireContext(), "Ngày kết thúc phải sau ngày bắt đầu", Toast.LENGTH_SHORT).show();
                                } else {
                                    calculateIncome("custom");
                                }
                            }, customEndDate.get(Calendar.YEAR), customEndDate.get(Calendar.MONTH), customEndDate.get(Calendar.DAY_OF_MONTH));
                    endDatePicker.show();
                }, customStartDate.get(Calendar.YEAR), customStartDate.get(Calendar.MONTH), customStartDate.get(Calendar.DAY_OF_MONTH));
        startDatePicker.show();
    }
}

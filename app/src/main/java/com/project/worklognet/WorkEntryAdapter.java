package com.project.worklognet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class WorkEntryAdapter extends RecyclerView.Adapter<WorkEntryAdapter.ViewHolder> {

    private List<WorkEntry> workEntryList;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;
    private Context context;

    public WorkEntryAdapter(Context context, List<WorkEntry> workEntryList, OnEditClickListener editListener, OnDeleteClickListener deleteListener) {
        this.workEntryList = workEntryList;
        this.context = context;
        this.editClickListener = editListener;
        this.deleteClickListener = deleteListener;  // Initialize delete listener
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkEntry entry = workEntryList.get(position);

        holder.tvWorkplace.setText(entry.getWorkPlace());
        holder.tvTime.setText(entry.getFormattedStartTime() + " - " + entry.getFormattedEndTime());
        holder.tvTotalEarnings.setText("Total Earnings: $" + String.format(Locale.getDefault(), "%.2f", entry.getTotalEarnings()));


        holder.btnEdit.setOnClickListener(v -> {
            editClickListener.onEditClick(position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            deleteClickListener.onDeleteClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return workEntryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorkplace, tvTime, tvTotalEarnings;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkplace = itemView.findViewById(R.id.tvWorkplace);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTotalEarnings = itemView.findViewById(R.id.tvTotalEarnings);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnEditClickListener {
        void onEditClick(int position);
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}


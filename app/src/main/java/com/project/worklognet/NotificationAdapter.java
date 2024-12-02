package com.project.worklognet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<String> notifications;

    public NotificationAdapter(List<String> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.notificationText.setText(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView notificationText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationText = itemView.findViewById(R.id.tvNotificationContent);
        }
    }
}

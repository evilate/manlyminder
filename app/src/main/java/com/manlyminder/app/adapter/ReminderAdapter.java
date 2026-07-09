package com.manlyminder.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manlyminder.app.R;
import com.manlyminder.app.model.DashboardReminder;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    public interface ReminderListener {
        void onReminderClick(int position);
        void onDone(int position);
        void onSnooze(int position);
        void onDismiss(int position);
    }

    private final List<DashboardReminder> reminders;
    private final ReminderListener listener;

    public ReminderAdapter(
            List<DashboardReminder> reminders,
            ReminderListener listener
    ) {
        this.reminders = reminders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        DashboardReminder reminder =
                reminders.get(position);

        holder.txtTitle.setText(
                reminder.getTitle()
        );

        holder.txtSubtitle.setText(
                reminder.getSubtitle()
        );

        holder.itemView.setOnClickListener(v ->
                listener.onReminderClick(holder.getAdapterPosition())
        );

        holder.btnDone.setOnClickListener(v ->
                listener.onDone(holder.getAdapterPosition())
        );

        holder.btnSnooze.setOnClickListener(v ->
                listener.onSnooze(holder.getAdapterPosition())
        );

        holder.btnDismiss.setOnClickListener(v ->
                listener.onDismiss(holder.getAdapterPosition())
        );
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public DashboardReminder getReminderAt(int position) {
        if (position < 0 || position >= reminders.size()) {
            return null;
        }

        return reminders.get(position);
    }

    public void removeAt(int position) {
        if (position < 0 || position >= reminders.size()) {
            return;
        }

        reminders.remove(position);
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        TextView txtSubtitle;
        Button btnDone;
        Button btnSnooze;
        Button btnDismiss;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle =
                    itemView.findViewById(
                            R.id.txtReminderTitle
                    );

            txtSubtitle =
                    itemView.findViewById(
                            R.id.txtReminderSubtitle
                    );

            btnDone =
                    itemView.findViewById(
                            R.id.btnDone
                    );

            btnSnooze =
                    itemView.findViewById(
                            R.id.btnSnooze
                    );

            btnDismiss =
                    itemView.findViewById(
                            R.id.btnDismiss
                    );
        }
    }
}
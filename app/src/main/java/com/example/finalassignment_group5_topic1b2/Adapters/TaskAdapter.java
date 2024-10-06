package com.example.finalassignment_group5_topic1b2.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalassignment_group5_topic1b2.Model.Task;
import com.example.finalassignment_group5_topic1b2.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskDelete(int taskId);
    }

    public TaskAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.textViewOrdinalNumber.setText(String.valueOf(position + 1));
        holder.textViewTaskName.setText(task.getTaskName());
        holder.textViewEstimateDays.setText(String.valueOf(task.getEstimateDays()));
        holder.itemView.setOnClickListener(v -> listener.onTaskClick(task));

        // Thêm logic xóa nhiệm vụ
        holder.itemView.setOnLongClickListener(v -> {
            listener.onTaskDelete(task.getId());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrdinalNumber, textViewTaskName, textViewEstimateDays;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTaskName = itemView.findViewById(R.id.task_name);
            textViewOrdinalNumber = itemView.findViewById(R.id.ordinalNumbers);
            textViewEstimateDays = itemView.findViewById(R.id.estimate_days);
        }
    }
}

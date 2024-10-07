package com.example.finalassignment_group5_topic1b2.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.finalassignment_group5_topic1b2.Model.Project;
import com.example.finalassignment_group5_topic1b2.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    private List<Project> projectList;
    private List<String> taskNames;
    private List<Integer> estimateDays;
    private OnProjectClickListener listener;
    private Set<Integer> selectedProjects = new HashSet<>();
    private boolean isDeleteMode = false;

    public interface OnProjectClickListener {
        void onItemClick(Project project);
    }

    public ProjectAdapter(List<Project> projectList, List<String> taskNames, List<Integer> estimateDays, OnProjectClickListener listener) {
        this.projectList = projectList;
        this.taskNames = taskNames;
        this.estimateDays = estimateDays;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.taskName.setText(taskNames.size() > position ? taskNames.get(position) : "");
        holder.devName.setText(project.getDevName());
        holder.startDate.setText(project.getStartDate());
        holder.endDate.setText(project.getEndDate());
        holder.estimateDays.setText(estimateDays.size() > position ? estimateDays.get(position) + " days" : "");

        holder.itemView.setOnClickListener(v -> listener.onItemClick(project));

        // delete mode
        if (isDeleteMode) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(selectedProjects.contains(project.getId()));
            holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedProjects.add(project.getId());
                } else {
                    selectedProjects.remove(project.getId());
                }
            });
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.checkbox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, devName, startDate, endDate, estimateDays;
        CheckBox checkbox;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_name);
            devName = itemView.findViewById(R.id.dev_name);
            startDate = itemView.findViewById(R.id.start_date);
            endDate = itemView.findViewById(R.id.end_date);
            estimateDays = itemView.findViewById(R.id.estimate_days);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }

    public void setDeleteMode(boolean deleteMode) {
        isDeleteMode = deleteMode;
        notifyDataSetChanged(); // update UI
    }

    public Set<Integer> getSelectedProjects() {
        return new HashSet<>(selectedProjects);
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void updateData(List<Project> newProjects, List<String> newTaskNames, List<Integer> newEstimateDays) {
        this.projectList.clear();
        this.projectList.addAll(newProjects);
        this.taskNames.clear();
        this.taskNames.addAll(newTaskNames);
        this.estimateDays.clear();
        this.estimateDays.addAll(newEstimateDays);
        notifyDataSetChanged();
    }
}
package com.example.finalassignment_group5_topic1b2.UI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;

import com.example.finalassignment_group5_topic1b2.Adapters.TaskAdapter;
import com.example.finalassignment_group5_topic1b2.Database.TaskRepository;
import com.example.finalassignment_group5_topic1b2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.finalassignment_group5_topic1b2.Model.Task;

import java.util.List;

public class TaskFragment extends Fragment {

    private FloatingActionButton fabAddTask;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TaskRepository taskRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewTask);
        taskRepository = new TaskRepository(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadTasks();

        // Add new task
        fabAddTask = view.findViewById(R.id.fab_add_task);
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });

        return view;
    }

    private void loadTasks() {
        List<Task> tasks = taskRepository.getAllTasks();
        taskAdapter = new TaskAdapter(tasks, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(Task task) {
                showUpdateTaskDialog(task);
            }

            @Override
            public void onTaskDelete(int taskId) {
                taskRepository.deleteTask(taskId);
                loadTasks();
            }
        });
        recyclerView.setAdapter(taskAdapter);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Task");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText txtTaskName = new EditText(getContext());
        txtTaskName.setHint("Task Name");
        layout.addView(txtTaskName);

        final EditText txtEstimateDays = new EditText(getContext());
        txtEstimateDays.setHint("Estimate Days");
        txtEstimateDays.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(txtEstimateDays);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskName = txtTaskName.getText().toString();
            int estimateDays = Integer.parseInt(txtEstimateDays.getText().toString());
            Task newTask = new Task(0, taskName, estimateDays);
            taskRepository.addTask(newTask);
            loadTasks();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showUpdateTaskDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Task");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText txtTaskName = new EditText(getContext());
        txtTaskName.setText(task.getTaskName());
        layout.addView(txtTaskName);

        final EditText txtEstimateDays = new EditText(getContext());
        txtEstimateDays.setText(String.valueOf(task.getEstimateDays()));
        txtEstimateDays.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(txtEstimateDays);

        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String taskName = txtTaskName.getText().toString();
            int estimateDays = Integer.parseInt(txtEstimateDays.getText().toString());
            Task updatedTask = new Task(task.getId(), taskName, estimateDays);
            taskRepository.updateTask(updatedTask);
            loadTasks();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
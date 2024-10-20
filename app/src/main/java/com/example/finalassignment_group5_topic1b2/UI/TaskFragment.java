package com.example.finalassignment_group5_topic1b2.UI;

import android.graphics.Color;
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
import android.widget.Toast;

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

        builder.setPositiveButton("Add", null); // Don't set an OnClickListener yet

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String taskName = txtTaskName.getText().toString();
                String estimateDaysStr = txtEstimateDays.getText().toString();

                // Validate input
                if (taskName.isEmpty() || estimateDaysStr.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int estimateDays = Integer.parseInt(estimateDaysStr);

                // Check for duplicate task name
                if (taskRepository.getAllTasks().stream().anyMatch(task -> task.getTaskName().equalsIgnoreCase(taskName))) {
                    Toast.makeText(getContext(), "Task with this name already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Task newTask = new Task(0, taskName, estimateDays);
                taskRepository.addTask(newTask);
                loadTasks();
                dialog.dismiss();
            });
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialog.dismiss());
        dialog.show();
    }

//    private void showUpdateTaskDialog(Task task) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Update Task");
//
//        LinearLayout layout = new LinearLayout(getContext());
//        layout.setOrientation(LinearLayout.VERTICAL);
//
//        final EditText txtTaskName = new EditText(getContext());
//        txtTaskName.setText(task.getTaskName());
//        layout.addView(txtTaskName);
//
//        final EditText txtEstimateDays = new EditText(getContext());
//        txtEstimateDays.setText(String.valueOf(task.getEstimateDays()));
//        txtEstimateDays.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
//        layout.addView(txtEstimateDays);
//
//        builder.setView(layout);
//
//        builder.setPositiveButton("Update", null);
//
//        AlertDialog dialog = builder.create();
//        dialog.setOnShowListener(dialogInterface -> {
//            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//            button.setOnClickListener(v -> {
//                String taskName = txtTaskName.getText().toString();
//                String estimateDaysStr = txtEstimateDays.getText().toString();
//
//                // Validate input
//                if (taskName.isEmpty() || estimateDaysStr.isEmpty()) {
//                    Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                int estimateDays = Integer.parseInt(estimateDaysStr);
//
//                // Check for duplicate task name
//                if (taskRepository.getAllTasks().stream().anyMatch(t -> t.getTaskName().equalsIgnoreCase(taskName) && !taskName.equals(task.getTaskName()))) {
//                    Toast.makeText(getContext(), "Task with this name already exists.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Task updatedTask = new Task(task.getId(), taskName, estimateDays);
//                taskRepository.updateTask(updatedTask);
//                loadTasks();
//                dialog.dismiss();
//
//            });
//
//            // Add Delete button
//            Button deleteButton = new Button(getContext());
//            deleteButton.setText("Delete");
//            deleteButton.setOnClickListener(v -> {
//                new AlertDialog.Builder(getContext())
//                        .setTitle("Confirm Delete")
//                        .setMessage("Are you sure you want to delete this task?")
//                        .setPositiveButton("Yes", (dialogInterface, i) -> {
//                            taskRepository.deleteTask(task.getId());
//                            loadTasks();
//                            dialog.dismiss();
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
//            });
//
//            // Create a horizontal layout for buttons
//            LinearLayout buttonLayout = new LinearLayout(getContext());
//            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
//            buttonLayout.addView(updateButton);
//            buttonLayout.addView(deleteButton);
//
//            // Add the button layout to the main layout
//            layout.addView(buttonLayout);
//        });
//
////        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialog.dismiss());
//        dialog.show();
//    }

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

        Button updateButton = new Button(getContext());
        updateButton.setText("Update");

        Button deleteButton = new Button(getContext());
        deleteButton.setText("Delete");

        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams updateParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        updateParams.weight = 1;
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        deleteParams.weight = 1;

        buttonLayout.addView(updateButton, updateParams);
        buttonLayout.addView(deleteButton, deleteParams);

        layout.addView(buttonLayout);
        builder.setView(layout);

        // Tạo dialog
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            updateButton.setOnClickListener(v -> {
                String taskName = txtTaskName.getText().toString().trim();
                String estimateDaysStr = txtEstimateDays.getText().toString().trim();

                // Validate input
                if (taskName.isEmpty() || estimateDaysStr.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int estimateDays = Integer.parseInt(estimateDaysStr);

                // Kiểm tra trùng tên task
                if (taskRepository.getAllTasks().stream().anyMatch(t ->
                        t.getTaskName().equalsIgnoreCase(taskName) && !task.getTaskName().equals(taskName))) {
                    Toast.makeText(getContext(), "Task with this name already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Task updatedTask = new Task(task.getId(), taskName, estimateDays);
                taskRepository.updateTask(updatedTask);
                loadTasks();
                dialog.dismiss();
            });

            deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes", (dialogInterface1, i) -> {
                            taskRepository.deleteTask(task.getId());
                            loadTasks();
                            dialog.dismiss();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        });

        dialog.show();
    }
}
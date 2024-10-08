package com.example.finalassignment_group5_topic1b2.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalassignment_group5_topic1b2.R;
import com.example.finalassignment_group5_topic1b2.Adapters.ProjectAdapter;
import com.example.finalassignment_group5_topic1b2.Database.ProjectRepository;
import com.example.finalassignment_group5_topic1b2.Database.TaskRepository;
import com.example.finalassignment_group5_topic1b2.Model.Project;
import com.example.finalassignment_group5_topic1b2.Model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.example.finalassignment_group5_topic1b2.R;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private FloatingActionButton fabAddProject;
    private ProjectRepository projectRepository;
    private Button btnDelete;
    private LinearLayout deleteOptions;
    private Button btnConfirmDelete;
    private Button btnCancelDelete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAddProject = view.findViewById(R.id.fab_add_project);
        deleteOptions = view.findViewById(R.id.delete_options);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnConfirmDelete = view.findViewById(R.id.btn_confirm_delete);
        btnCancelDelete = view.findViewById(R.id.btn_cancel_delete);

        loadProjects();

        // show box to add
        fabAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProjectDialog();
            }
        });

        btnDelete.setOnClickListener(v -> {
            adapter.setDeleteMode(true);
            deleteOptions.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
        });

        btnConfirmDelete.setOnClickListener(v -> {
            Set<Integer> selectedProjects = adapter.getSelectedProjects();
            Log.d("check selectedProjects", "check selectedProjects" + selectedProjects);
            if (!selectedProjects.isEmpty()) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Thực hiện xóa
                            for (int projectId : selectedProjects) {
                                projectRepository.deleteProject(projectId);
                            }
                            loadProjects();
                            adapter.setDeleteMode(false);
                            deleteOptions.setVisibility(View.GONE);
                            btnDelete.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Project deleted successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Toast.makeText(getContext(), "No projects selected.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelDelete.setOnClickListener(v -> {
            adapter.setDeleteMode(false);
            deleteOptions.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
        });

        return view;
    }

    private void loadProjects() {
        projectRepository = new ProjectRepository(getContext());
        TaskRepository taskRepository = new TaskRepository(getContext());

        // get all projects
        List<Project> projects = projectRepository.getAllProjects();

        // initial task name and estimate days
        List<String> taskNames = new ArrayList<>();
        List<Integer> estimateDays = new ArrayList<>();

        for (Project project : projects) {
            Task task = taskRepository.getTaskById(project.getTaskId());
            if (task != null) {
                taskNames.add(task.getTaskName());
                estimateDays.add(task.getEstimateDays());
            } else {
                taskNames.add("Unknown Task");
                estimateDays.add(0);
            }
        }

        adapter = new ProjectAdapter(projects, taskNames, estimateDays, new ProjectAdapter.OnProjectClickListener() {
            @Override
            public void onItemClick(Project project) {
                showUpdateProjectDialog(project);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddProjectDialog() {
        showProjectDialog(null, false); // set null for add new function
    }

    private void showUpdateProjectDialog(Project project) {
        showProjectDialog(project, true);
    }

    private void showProjectDialog(Project project, boolean isUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(isUpdate ? "Update Project" : "Add New Project");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_project, null);
        builder.setView(dialogView);

        final EditText txtDevName = dialogView.findViewById(R.id.txtDevName);
        final Spinner spinnerTasks = dialogView.findViewById(R.id.spinnerTasks);
        final EditText txtStartDate = dialogView.findViewById(R.id.txtStartDate);
        final EditText txtEndDate = dialogView.findViewById(R.id.txtEndDate);

        // Spinner to select task
        TaskRepository taskRepository = new TaskRepository(getContext());
        // get all tasks
        List<Task> tasks = taskRepository.getAllTasks();
        List<String> taskNames = new ArrayList<>();

        for (Task task : tasks) {
            taskNames.add(task.getTaskName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, taskNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTasks.setAdapter(adapter);

        // if is update, select task has already in project
        if (isUpdate) {
            txtDevName.setText(project.getDevName());
            txtStartDate.setText(project.getStartDate());
            txtEndDate.setText(project.getEndDate());

            Integer currentTaskId = project.getTaskId();
            int taskPosition = taskNames.indexOf(taskRepository.getTaskById(currentTaskId).getTaskName());
            if (taskPosition >= 0) {
                spinnerTasks.setSelection(taskPosition);
            }
        }

        txtStartDate.setOnClickListener(v -> showDatePickerDialog(txtStartDate));
        txtEndDate.setOnClickListener(v -> showDatePickerDialog(txtEndDate));

        builder.setPositiveButton(isUpdate ? "Update" : "Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String devName = txtDevName.getText().toString();
                String startDate = txtStartDate.getText().toString();
                String endDate = txtEndDate.getText().toString();
                Integer taskIdSelected = findTaskByName(tasks, spinnerTasks.getSelectedItem().toString());
                if (taskIdSelected != null) {
                    if (isUpdate) {
                        // update project
                        Log.d("Check update", "Update" + project.getId());
                        updateProject(project.getId(), devName, taskIdSelected, startDate, endDate);
                        // check overlap
                        if (checkForOverlaps(project.getId(), startDate, endDate)) {
                            String message = String.format("Task %s causes an overlap to other tasks when updating at %s",
                                    devName, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                            showNotification(message);
                        }
                    } else {
                        // add new project
                        Project newProject = new Project(0, devName, taskIdSelected, startDate, endDate);
                        // check overlap
                        if (checkForOverlaps(newProject.getId(), startDate, endDate)) {
                            String message = String.format("Task %s causes an overlap to other tasks when updating at %s",
                                    devName, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                            showNotification(message);
                        }
                        projectRepository.addProject(newProject);
                        Toast.makeText(getContext(), "Project added successfully!", Toast.LENGTH_SHORT).show();
                    }
                    loadProjects();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateProject(int projectId, String devName, int taskId, String startDate, String endDate) {
        Project updatedProject = new Project(projectId, devName, taskId, startDate, endDate);
        projectRepository.updateProject(updatedProject);
        loadProjects();
        Toast.makeText(getContext(), "Project updated successfully!", Toast.LENGTH_SHORT).show();
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // date format
                    String date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    editText.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    private Integer findTaskByName(List<Task> tasks, String taskName) {
        for (Task task : tasks) {
            if (task.getTaskName().equals(taskName)) {
                return task.getId();
            }
        }
        return null;
    }

    private boolean checkForOverlaps(int projectId, String newStartDate, String newEndDate) {
        projectRepository = new ProjectRepository(getContext());
        List<Project> projects = projectRepository.getAllProjects();

        // convert date to Date object
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date newStart, newEnd;

        try {
            newStart = sdf.parse(newStartDate);
            newEnd = sdf.parse(newEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        Log.d("CheckOverlaps first", "Checking overlaps for projectId: " + projectId);
        Log.d("CheckOverlaps first", "Input Start Date: " + newStartDate + ", End Date: " + newEndDate);

        for (Project project : projects) {
            if (project.getId() != projectId) {
                Date existingStart;
                Date existingEnd;
                try {
                    existingStart = sdf.parse(project.getStartDate());
                    existingEnd = sdf.parse(project.getEndDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }

                // check overlap
                if (newStart.before(existingEnd) && newEnd.after(existingStart)) {
                    Log.d("Overlap detected", "Overlap exists with projectId: " + project.getId() + project.getStartDate() + project.getEndDate());
                    return true;
                }
            }
        }

        Log.d("CheckOverlaps", "No overlaps detected.");
        return false;
    }

    private void showNotification(String message) {
        // create and show noti
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "task_overlap_channel";
        String channelName = "Task Overlap Notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                .setSmallIcon(R.drawable.baseline_error_24)
                .setContentTitle("Task Overlap Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

}
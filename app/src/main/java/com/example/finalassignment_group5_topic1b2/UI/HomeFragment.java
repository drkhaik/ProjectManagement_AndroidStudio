package com.example.finalassignment_group5_topic1b2.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private TaskRepository taskRepository;
    private Button btnDelete;
    private LinearLayout deleteOptions;
    private Button btnConfirmDelete;
    private Button btnCancelDelete;
    private TextView estimateDaysHeader;
    private boolean hideEstimate;

    private EditText searchInput;
    private Button searchButton;
    private Button returnButton;
    private RecyclerView searchResults;
    private ProjectAdapter searchAdapter;


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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        hideEstimate = sharedPreferences.getBoolean("hide_estimate", false);

        taskRepository = new TaskRepository(getContext());

        searchInput = view.findViewById(R.id.search_input);
        searchButton = view.findViewById(R.id.search_button);
        returnButton = view.findViewById(R.id.return_button);

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAddProject = view.findViewById(R.id.fab_add_project);
        deleteOptions = view.findViewById(R.id.delete_options);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnConfirmDelete = view.findViewById(R.id.btn_confirm_delete);
        btnCancelDelete = view.findViewById(R.id.btn_cancel_delete);
        estimateDaysHeader = view.findViewById(R.id.estimate_days);

        estimateDaysHeader.setVisibility(hideEstimate ? View.INVISIBLE : View.VISIBLE);

        loadProjects();

        searchButton.setOnClickListener(v -> performSearch());

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
                            for (int projectId : selectedProjects) {
                                Integer taskId = projectRepository.getTaskIdByProjectId(projectId);
                                Log.d("check task ID", "cehck" + taskId);
                                if (taskId != null) {
                                    taskRepository.deleteTask(taskId);
                                }
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

        returnButton.setOnClickListener(v -> {
            loadProjects();
            returnButton.setVisibility(View.GONE);
        });

        return view;
    }

    private void loadProjects() {
        projectRepository = new ProjectRepository(getContext());
        TaskRepository taskRepository = new TaskRepository(getContext());

        List<Project> projects = projectRepository.getAllProjects();
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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean hideEstimate = sharedPreferences.getBoolean("hide_estimate", false);

        adapter = new ProjectAdapter(projects, taskNames, estimateDays,hideEstimate, new ProjectAdapter.OnProjectClickListener() {
            @Override
            public void onItemClick(Project project) {
                showUpdateProjectDialog(project);
            }
        });
        recyclerView.setAdapter(adapter);

        adapter.setEstimateDaysVisibility(hideEstimate ? View.GONE : View.VISIBLE);
    }

    private void performSearch() {
        String query = searchInput.getText().toString().trim();
        if (!query.isEmpty()) {
            List<Project> results = projectRepository.searchProjects(query);
            List<String> newTaskNames = new ArrayList<>();
            List<Integer> newEstimateDays = new ArrayList<>();
            for (Project project : results) {
                Task task = taskRepository.getTaskById(project.getTaskId());
                if (task != null) {
                    newTaskNames.add(task.getTaskName());
                    newEstimateDays.add(task.getEstimateDays());
                }
            }

            adapter.updateData(results, newTaskNames, newEstimateDays);
            returnButton.setVisibility(View.VISIBLE);

            if (results.isEmpty()) {
                Toast.makeText(getContext(), "No projects found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please enter a dev name or task name.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddProjectDialog() {
        showProjectDialog(null, false); // set null for add new function
    }

    private void showUpdateProjectDialog(Project project) {
        showProjectDialog(project, true);
    }

    private void showProjectDialog(Project project, boolean isUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(isUpdate ? "Detail Project Plan" : "Add New Project");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_project, null);
        builder.setView(dialogView);

        final EditText txtDevName = dialogView.findViewById(R.id.txtDevName);
        final EditText txtTaskName = dialogView.findViewById(R.id.txtTaskName);
        final EditText tvDialogEstimateDays = dialogView.findViewById(R.id.txtEstimateDay);
        final EditText txtStartDate = dialogView.findViewById(R.id.txtStartDate);
        final EditText txtEndDate = dialogView.findViewById(R.id.txtEndDate);

        // if is update, select task already have in project
        if (isUpdate) {
            txtDevName.setText(project.getDevName());
            txtStartDate.setText(project.getStartDate());
            txtEndDate.setText(project.getEndDate());

            Task currentTask = taskRepository.getTaskById(project.getTaskId());
            if (currentTask != null) {
                txtTaskName.setText(currentTask.getTaskName());
                tvDialogEstimateDays.setText(String.valueOf(currentTask.getEstimateDays()));
            }
        }

        txtStartDate.setOnClickListener(v -> showDatePickerDialog(txtStartDate));
        txtEndDate.setOnClickListener(v -> {
            showDatePickerDialog(txtEndDate);
            txtEndDate.setOnFocusChangeListener((v1, hasFocus) -> {
                if (!hasFocus) {
                    String startDate = txtStartDate.getText().toString();
                    String endDate = txtEndDate.getText().toString();
                    if (!startDate.isEmpty() && !endDate.isEmpty()) {
                        int estimateDays = calculateEstimateDays(startDate, endDate);
                        Log.d("check", "check estimate days" + estimateDays);
                        tvDialogEstimateDays.setText(String.valueOf(estimateDays));
                    } else {
                        tvDialogEstimateDays.setText("");
                    }
                }
            });
        });

        txtStartDate.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                String startDate = txtStartDate.getText().toString();
                String endDate = txtEndDate.getText().toString();
                if (!startDate.isEmpty() && !endDate.isEmpty()) {
                    int estimateDays = calculateEstimateDays(startDate, endDate);
                    Log.d("check", "check estimate days" + estimateDays);
                    tvDialogEstimateDays.setText(String.valueOf(estimateDays));
                } else {
                    tvDialogEstimateDays.setText("");
                }
            }
        });

        builder.setPositiveButton(isUpdate ? "Update" : "Add", null);

        AlertDialog dialog = builder.create();

        // Set the positive button action
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String devName = txtDevName.getText().toString().trim();
                String taskName = txtTaskName.getText().toString().trim();
                String startDate = txtStartDate.getText().toString().trim();
                String endDate = txtEndDate.getText().toString().trim();

                // calculate estimate days from start date and end date
                Integer estimateDays = calculateEstimateDays(startDate, endDate);

                // Validate input
                if (taskName.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidDateRange(startDate, endDate)) {
                    Toast.makeText(getContext(), "End Date must be after Start Date!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check the user has change estimateDays or not
                String estimateDaysInput = tvDialogEstimateDays.getText().toString().trim();
                if (!estimateDaysInput.isEmpty()) {
                    // if they input another value, get that value
                    try {
                        estimateDays = Integer.parseInt(estimateDaysInput);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Please enter a valid number for Estimate Days!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                    if (isUpdate) {
                        Integer taskId = project.getTaskId();
                        Task updatedTask = new Task(taskId, taskName, estimateDays);
                        taskRepository.updateTask(updatedTask);
                        updateProject(project.getId(), devName, taskId, startDate, endDate);
                        // Check overlap
                        if (checkForOverlaps(project.getId(), startDate, endDate)) {
                            String message = String.format("Task %s causes an overlap to other tasks when updating at %s",
                                    devName, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                            showNotification(message);
                        }
                    } else {
                        if (taskRepository.getAllTasks().stream().anyMatch(task -> task.getTaskName().equalsIgnoreCase(taskName))) {
                            Toast.makeText(getContext(), "Task with this name already exists.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Task newTask = new Task(0, taskName, estimateDays);
                        int newTaskId = taskRepository.addTask(newTask);
                        Project newProject = new Project(0, devName, newTaskId, startDate, endDate);
                        // Check overlap
                        if (checkForOverlaps(newProject.getId(), startDate, endDate)) {
                            String message = String.format("Task %s causes an overlap to other tasks when adding at %s",
                                    devName, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                            showNotification(message);
                        }
                        projectRepository.addProject(newProject);
                        Toast.makeText(getContext(), "Project added successfully!", Toast.LENGTH_SHORT).show();
                    }

                    loadProjects();
                    dialog.dismiss(); // Close the dialog only if everything is valid

            });
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialog.dismiss());
        dialog.show();
    }

    private void updateProject(int projectId, String devName, int taskId, String startDate, String endDate) {
        Project updatedProject = new Project(projectId, devName, taskId, startDate, endDate);
        projectRepository.updateProject(updatedProject);
        loadProjects();
        Toast.makeText(getContext(), "Project updated successfully!", Toast.LENGTH_SHORT).show();
    }

    // validate to make sure start date before end date
    private boolean isValidDateRange(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return end != null && start != null && end.after(start);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int calculateEstimateDays(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        // Tính số ngày ước tính
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
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
        String channelName = "Task Overlap!";

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
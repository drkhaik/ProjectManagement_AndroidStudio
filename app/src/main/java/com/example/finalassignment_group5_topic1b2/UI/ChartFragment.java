package com.example.finalassignment_group5_topic1b2.UI;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.net.ParseException;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalassignment_group5_topic1b2.Adapters.ChartAdapter;
import com.example.finalassignment_group5_topic1b2.Database.ProjectRepository;
import com.example.finalassignment_group5_topic1b2.Database.TaskRepository;
import com.example.finalassignment_group5_topic1b2.Model.Project;
import com.example.finalassignment_group5_topic1b2.Model.Task;
import com.example.finalassignment_group5_topic1b2.R;

import java.util.List;
import java.util.ArrayList;


public class ChartFragment extends Fragment {
    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;

    private RecyclerView recyclerView;
    private ChartAdapter chartAdapter;
    private List<Project> projectList;
    private List<String> taskNames;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadProjectData();

        return view;
    }

    private void loadProjectData() {
        projectRepository = new ProjectRepository(getContext());
        taskRepository = new TaskRepository(getContext());
        // get all projects
        projectList = projectRepository.getAllProjects();
        // initial task name
        taskNames = new ArrayList<>();

        for (Project project : projectList) {
            Task task = taskRepository.getTaskById(project.getTaskId());
            if (task != null) {
                taskNames.add(task.getTaskName());
            } else {
                taskNames.add("Unknown Task");
            }
        }

        // initial adapter and set to RecyclerView
        chartAdapter = new ChartAdapter(projectList, taskNames, null);
        recyclerView.setAdapter(chartAdapter);
    }

}
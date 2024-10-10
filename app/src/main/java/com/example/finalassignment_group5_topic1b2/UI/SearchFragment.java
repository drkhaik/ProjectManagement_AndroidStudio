package com.example.finalassignment_group5_topic1b2.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalassignment_group5_topic1b2.Adapters.ProjectAdapter;
import com.example.finalassignment_group5_topic1b2.Database.ProjectRepository;
import com.example.finalassignment_group5_topic1b2.Database.TaskRepository;
import com.example.finalassignment_group5_topic1b2.Model.Project;
import com.example.finalassignment_group5_topic1b2.Model.Task;
import com.example.finalassignment_group5_topic1b2.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private EditText searchInput;
    private Button searchButton;
    private RecyclerView searchResults;
    private ProjectAdapter projectAdapter;
    private ProjectRepository projectRepository;

    public SearchFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectRepository = new ProjectRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.search_input);
        searchButton = view.findViewById(R.id.search_button);
        searchResults = view.findViewById(R.id.search_results);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean hideEstimate = sharedPreferences.getBoolean("hide_estimate", false);

        searchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        projectAdapter = new ProjectAdapter(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), hideEstimate, project -> {});
        searchResults.setAdapter(projectAdapter);

        searchButton.setOnClickListener(v -> performSearch());

        return view;
    }

    private void performSearch() {
        String query = searchInput.getText().toString().trim();
        TaskRepository taskRepository = new TaskRepository(getContext());
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

            projectAdapter.updateData(results, newTaskNames, newEstimateDays);

            // If results is empty
            if (results.isEmpty()) {
                Toast.makeText(getContext(), "No projects found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please enter a search term.", Toast.LENGTH_SHORT).show();
        }
    }
}
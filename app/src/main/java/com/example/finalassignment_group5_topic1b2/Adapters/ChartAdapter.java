package com.example.finalassignment_group5_topic1b2.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.net.ParseException;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalassignment_group5_topic1b2.Model.Project;
import com.example.finalassignment_group5_topic1b2.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {
    private List<Project> projectList;
    private List<String> taskNames;

    public ChartAdapter(List<Project> projectList, List<String> taskNames, ProjectAdapter.OnProjectClickListener listener) {
        this.projectList = projectList;
        this.taskNames = taskNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_chart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.taskName.setText(taskNames.size() > position ? taskNames.get(position) : "");
        holder.devName.setText(project.getDevName());
        holder.date.setText(getFormattedDate(project));

        String startDate = project.getStartDate();
        String endDate = project.getEndDate();

        int duration = calculateDuration(startDate, endDate);
        int startOffset = calculateOffset(startDate);

        Log.d("Check duration", "check duration" + duration);
        Log.d("Check startOffset", "check startOffset" + startOffset);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(duration * 35, 30);
        holder.durationBar.setLayoutParams(params);

        LinearLayout.LayoutParams barParams = (LinearLayout.LayoutParams) holder.durationBar.getLayoutParams();
        barParams.setMargins(startOffset, 0, 0, 0); // start from left
        holder.durationBar.setLayoutParams(barParams);
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, devName, date;
        View durationBar;

        ViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_name);
            devName = itemView.findViewById(R.id.dev_name);
            date = itemView.findViewById(R.id.date);
            durationBar = itemView.findViewById(R.id.duration_bar);
        }
    }

    private int calculateOffset(String startDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Date today = new Date();
            long difference = start.getTime() - today.getTime(); // calculate the different between start and now
            return (int) (difference / (1000 * 60 * 60 * 24)) * 20; // divide mili second in 1 days and multiple it with 20dp
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int calculateDuration(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            long difference = end.getTime() - start.getTime();
            return (int) (difference / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFormattedDate(Project project) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // convert input
        SimpleDateFormat outputFormat = new SimpleDateFormat("d/M", Locale.getDefault()); // convert output

        String startDateString = project.getStartDate();
        String endDateString = project.getEndDate();

        Date startDate = null;
        Date endDate = null;

        try {
            if (startDateString != null) {
                startDate = inputFormat.parse(startDateString); // convert string to date
            }
            if (endDateString != null) {
                endDate = inputFormat.parse(endDateString); //convert date to string
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "N/A";
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }

        if (startDate == null || endDate == null) {
            return "N/A";
        }

        // return the format string like dd-MM
        return outputFormat.format(startDate) + "-" + outputFormat.format(endDate);
    }
}

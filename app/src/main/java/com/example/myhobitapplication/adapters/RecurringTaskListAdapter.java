package com.example.myhobitapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.viewModels.taskViewModels.RecurringTaskListViewModel; // Proveri ime

import java.util.List;

public class RecurringTaskListAdapter extends RecyclerView.Adapter<RecurringTaskListAdapter.ViewHolder> {

    private List<RecurringTaskDTO> recurringTaskDTOS;
    private final RecurringTaskListViewModel viewModel;

    public RecurringTaskListAdapter(List<RecurringTaskDTO> recurringTaskDTOS, RecurringTaskListViewModel viewModel) {
        this.recurringTaskDTOS = recurringTaskDTOS;
        this.viewModel = viewModel;
    }

    public void updateData(List<RecurringTaskDTO> newTasks) {
        this.recurringTaskDTOS = newTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_task_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecurringTaskDTO task = recurringTaskDTOS.get(position);

        holder.bind(task, viewModel);
    }

    @Override
    public int getItemCount() {
        return recurringTaskDTOS.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, time, status;
        LinearLayout card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.taskTitleTextView);
            date = itemView.findViewById(R.id.taskDateTextView);
            time = itemView.findViewById(R.id.taskTimeTextView);
            status = itemView.findViewById(R.id.taskStatusTextView);
            card = itemView.findViewById(R.id.detailsContainer);
        }

        public void bind(final RecurringTaskDTO task, final RecurringTaskListViewModel viewModel) {

            name.setText(task.getName());
            date.setText(task.getStartDate().toString());
            time.setText(task.getExecutionTime().toString());
            status.setText(String.valueOf(task.getStatus()));

            try {
                card.setBackgroundColor(Color.parseColor(task.getCategoryColour()));
            } catch (Exception e) {
                card.setBackgroundColor(Color.GRAY);
            }

            itemView.setOnClickListener(v -> {
                viewModel.onTaskClicked(task);
            });
        }
    }
}
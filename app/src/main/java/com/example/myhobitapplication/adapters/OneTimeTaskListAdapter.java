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
import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.viewModels.taskViewModels.OneTimeTaskListViewModel;
import com.example.myhobitapplication.viewModels.taskViewModels.RecurringTaskListViewModel;

import java.util.List;

public class OneTimeTaskListAdapter extends RecyclerView.Adapter<OneTimeTaskListAdapter.ViewHolder>{


    //private final RecurringTaskListAdapter.OnItemButtonClickListener listener;
    private  List<OneTimeTaskDTO> oneTimeTaskDTOS;
    OneTimeTaskListViewModel viewModel;


    public OneTimeTaskListAdapter(List<OneTimeTaskDTO> oneTimeTaskDTOS, OneTimeTaskListViewModel viewModel){
        this.oneTimeTaskDTOS = oneTimeTaskDTOS;
        this.viewModel = viewModel;
    }

    public void updateData(List<OneTimeTaskDTO> newTasks) {
        this.oneTimeTaskDTOS = newTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OneTimeTaskListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_task_item, parent, false);

        return new OneTimeTaskListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OneTimeTaskListAdapter.ViewHolder holder, int position) {
        OneTimeTaskDTO task = oneTimeTaskDTOS.get(position);

        holder.bind(task, viewModel);
    }

    @Override
    public int getItemCount() {
        return oneTimeTaskDTOS.size();
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

        public void bind(final OneTimeTaskDTO task, final OneTimeTaskListViewModel viewModel) {

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

package com.example.todolistapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.models.TaskModel;

import java.util.ArrayList;

public class TaskArrayAdapter extends RecyclerView.Adapter<TaskArrayAdapter.ViewHolder>{
    private int listItemLayout;
    private ArrayList<TaskModel> itemList;


    public TaskArrayAdapter(int layoutId, ArrayList<TaskModel> itemList){
        this.listItemLayout = layoutId;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView itemId = holder.itemId;
        TextView itemTitle = holder.itemTitle;
        TextView itemDate = holder.itemDate;

        itemTitle.setText(itemList.get(position).getTitle());
        itemDate.setText(itemList.get(position).getDate());
        itemId.setText(itemList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView itemId;
        public TextView itemTitle;
        public TextView itemDate;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            itemTitle = (TextView) itemView.findViewById(R.id.task_title);
            itemDate = (TextView) itemView.findViewById(R.id.date_task);
            itemId =  (TextView) itemView.findViewById(R.id.task_id);

        }


        @Override
        public void onClick(View v) {
            Intent call = new Intent(v.getContext(), TaskInfo.class);
            call.putExtra("taskId", itemId.getText().toString());
            v.getContext().startActivity(call);
        }

    }
}
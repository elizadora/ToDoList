package com.example.todolistapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.models.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TaskArrayAdapter extends RecyclerView.Adapter<TaskArrayAdapter.ViewHolder> {
    private int listItemLayout;
    private ArrayList<TaskModel> itemList;
    private ArrayList<String> itemIds;// Nova lista para armazenar IDs
    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    String idAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Construtor modificado para aceitar também a lista de IDs
    public TaskArrayAdapter(int layoutId, ArrayList<TaskModel> itemList, ArrayList<String> itemIds) {
        this.listItemLayout = layoutId;
        this.itemList = itemList;
        this.itemIds = itemIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView itemId = holder.itemId;
        TextView itemTitle = holder.itemTitle;
        TextView itemDate = holder.itemDate;
        CheckBox checkTask = holder.checkTask;

        itemTitle.setText(itemList.get(position).getTitle());
        itemDate.setText(itemList.get(position).getDate());
        itemId.setText(itemIds.get(position));

        if(itemList.get(position).getStatus() == 1){
            checkTask.setChecked(true);
            itemTitle.setPaintFlags(itemTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            checkTask.setChecked(false);
            itemTitle.setPaintFlags(itemTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    private void updateStatusTask(String id, int status){
        firestoreDB.collection("Users").document(idAuth).collection("Tasks").document(id).
                update("status", status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("Sucesso", "status atualizado");
                        }else{
                            Log.d("Erro ao", "atualizar status");
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemId;
        public TextView itemTitle;
        public TextView itemDate;

        private TaskArrayAdapter adapter;

        public CheckBox checkTask;

        public ViewHolder(View itemView, TaskArrayAdapter adapter) {
            super(itemView);
            this.adapter = adapter;

            itemView.setOnClickListener(this);
            itemTitle = itemView.findViewById(R.id.task_title);
            itemDate = itemView.findViewById(R.id.date_task);
            itemId = itemView.findViewById(R.id.task_id);
            checkTask = (CheckBox) itemView.findViewById(R.id.check_task);

            checkTask.setButtonTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.primary)));


            checkTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkTask.isChecked()){
                        itemTitle.setPaintFlags(itemTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        adapter.updateStatusTask(itemId.getText().toString(), 1);
                    }else{
                        itemTitle.setPaintFlags(itemTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        adapter.updateStatusTask(itemId.getText().toString(), 0);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent call = new Intent(v.getContext(), TaskInfo.class);
            // Passa o ID do documento para a próxima Activity
            call.putExtra("taskId", itemId.getText().toString());
            v.getContext().startActivity(call);
        }

    }
}

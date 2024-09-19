package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.models.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Tasks extends Fragment {
    private FirebaseFirestore firestoreDB;

    FloatingActionButton btnAddTask;
    RecyclerView rvTaks;
    TextView tvTask;

    ArrayList<TaskModel> taskList = new ArrayList<TaskModel>();
    ArrayList<String> tasksId = new ArrayList<String>();
    TaskArrayAdapter taskArrayAdapter;

    String userIDAuth;

    public Tasks(){

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.fragment_tasks, container, false);

        // get userId
        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDB = FirebaseFirestore.getInstance();

        tvTask = view.findViewById(R.id.tv_task);
        btnAddTask = view.findViewById(R.id.btn_add_task);
        btnAddTask.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.secondary));

        // implement recyclerview
        rvTaks = view.findViewById(R.id.rv_tasks);
        taskArrayAdapter = new TaskArrayAdapter(R.layout.task_layout, taskList, tasksId);

        rvTaks = (RecyclerView) view.findViewById(R.id.rv_tasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvTaks.setLayoutManager(layoutManager);

        rvTaks.setAdapter(taskArrayAdapter);

        loadTasks();

        
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTask.class);
                startActivity(intent);
            }
        });


        return view;
    }

    private void loadTasks(){
        // get from firestore
        firestoreDB.collection("Users").document(userIDAuth).
                collection("Tasks").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.e("Error", "Listen Tasks falied");
                            return;
                        }

                        taskList.clear();
                        tasksId.clear();

                        if(value != null){
                            for(DocumentSnapshot d : value){
                                TaskModel t = d.toObject(TaskModel.class);
                                taskList.add(t);
                                tasksId.add(d.getId());
                            }

                            if(taskList.isEmpty()){
                                tvTask.setVisibility(View.VISIBLE);
                                rvTaks.setVisibility(View.GONE);
                            }else{
                                tvTask.setVisibility(View.GONE);
                                rvTaks.setVisibility(View.VISIBLE);
                                taskArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}

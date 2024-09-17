package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.models.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Tasks extends Fragment {
    private FirebaseFirestore firestoreDB;

    FloatingActionButton btnAddTask;
    RecyclerView rvTaks;

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

        btnAddTask = view.findViewById(R.id.btn_add_task);

        // implement recyclerview
        rvTaks = view.findViewById(R.id.rv_tasks);
        taskArrayAdapter = new TaskArrayAdapter(R.layout.task_layout, taskList, tasksId);

        rvTaks = (RecyclerView) view.findViewById(R.id.rv_tasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvTaks.setLayoutManager(layoutManager);


        rvTaks.setAdapter(taskArrayAdapter);

        // get from firestore
        firestoreDB.collection("Users").document(userIDAuth).
                collection("Tasks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                           for(DocumentSnapshot d : task.getResult()){
                                TaskModel t = d.toObject(TaskModel.class);

                                tasksId.add(d.getId());
                                taskList.add(t);

                           }
                        }
                            taskArrayAdapter.notifyDataSetChanged();
                    }
                });



        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTask.class);
                startActivity(intent);
            }
        });


        return view;
    }
}

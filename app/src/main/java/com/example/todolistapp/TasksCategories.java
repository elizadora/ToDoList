package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class TasksCategories extends Fragment {
    private FirebaseFirestore firestoreDB;
    String userIDAuth;

    FloatingActionButton btnAddTask2;
    TextView tvTaskCategory;

    RecyclerView rvTasksCategories;
    TasksCategoriesArrayAdapter tasksCategoriesArrayAdapter;
    ArrayList<TaskModel> taskList = new ArrayList<TaskModel>();
    ArrayList<String> tasksId = new ArrayList<String>();

    String categoryId;

    public TasksCategories(){

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks_categories, container, false);

        Bundle bundle = getArguments();

        if(bundle != null){
            categoryId = bundle.getString("id");
        }

        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDB = FirebaseFirestore.getInstance();


        tvTaskCategory = view.findViewById(R.id.tv_task_category);
        btnAddTask2 = view.findViewById(R.id.btn_add_task_2);
        btnAddTask2.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.secondary));


        rvTasksCategories = view.findViewById(R.id.rv_tasks_categories);
        tasksCategoriesArrayAdapter = new TasksCategoriesArrayAdapter(R.layout.task_layout, taskList, tasksId);


        rvTasksCategories = (RecyclerView) view.findViewById(R.id.rv_tasks_categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvTasksCategories.setLayoutManager(layoutManager);

        rvTasksCategories.setAdapter(tasksCategoriesArrayAdapter);

        loadTasks();


        btnAddTask2.setOnClickListener(new View.OnClickListener() {
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
                collection("Tasks").whereEqualTo("category", categoryId).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                            if (taskList.isEmpty()) {
                                tvTaskCategory.setVisibility(View.VISIBLE);
                                rvTasksCategories.setVisibility(View.GONE);
                            } else {
                                tvTaskCategory.setVisibility(View.GONE);
                                rvTasksCategories.setVisibility(View.VISIBLE);
                                tasksCategoriesArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}

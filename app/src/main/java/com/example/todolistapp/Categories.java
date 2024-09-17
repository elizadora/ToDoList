package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.models.CategoryModel;
import com.example.todolistapp.models.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Categories extends Fragment {

    private FirebaseFirestore firestoreDB;

    FloatingActionButton btnAddCategory;

    RecyclerView rvCategories;
    ArrayList<CategoryModel> categoriesList = new ArrayList<CategoryModel>();
    ArrayList<String> categoriesId = new ArrayList<String>();
    CategoriesArrayAdapter categoriesArrayAdapter;

    String userIDAuth;

    public Categories(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        // get userId
        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDB = FirebaseFirestore.getInstance();


        btnAddCategory = view.findViewById(R.id.btn_add_category);

        // implement recyclerview
        rvCategories = view.findViewById(R.id.rv_categories);
        categoriesArrayAdapter = new CategoriesArrayAdapter(R.layout.category_layout, categoriesList, categoriesId);

        rvCategories = (RecyclerView) view.findViewById(R.id.rv_categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvCategories.setLayoutManager(layoutManager);

        rvCategories.setAdapter(categoriesArrayAdapter);

        // get from firestore
        firestoreDB.collection("Users").document(userIDAuth).
                collection("Categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot d : task.getResult()){
                                CategoryModel t = d.toObject(CategoryModel.class);

                                categoriesId.add(d.getId());
                                categoriesList.add(t);
                            }
                        }
                        categoriesArrayAdapter.notifyDataSetChanged();
                    }
                });


        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(v.getContext(), AddCategory.class);
                startActivity(call);
            }
        });

        return view;
    }
}

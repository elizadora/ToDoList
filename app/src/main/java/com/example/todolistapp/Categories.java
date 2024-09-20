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

import com.example.todolistapp.models.CategoryModel;
import com.example.todolistapp.models.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Categories extends Fragment {

    // db and authentication
    private FirebaseFirestore firestoreDB;
    String userIDAuth;

    // buttons
    FloatingActionButton btnAddCategory;

    // output
    TextView tvCategory;

    // recycler view
    RecyclerView rvCategories;

    // lists to store category data and their IDs
    ArrayList<CategoryModel> categoriesList = new ArrayList<CategoryModel>();
    ArrayList<String> categoriesId = new ArrayList<String>();

    // adapter
    CategoriesArrayAdapter categoriesArrayAdapter;


    public Categories(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        // get userId and instance db
        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDB = FirebaseFirestore.getInstance();

        // find button id and set color filter
        btnAddCategory = view.findViewById(R.id.btn_add_category);
        btnAddCategory.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.secondary));

        // find text id
        tvCategory = view.findViewById(R.id.tv_category);

        // initialize RecyclerView and Adapter
        rvCategories = view.findViewById(R.id.rv_categories);
        categoriesArrayAdapter = new CategoriesArrayAdapter(R.layout.category_layout, categoriesList, categoriesId);

        // set layout manager for RecyclerView (linear layout)
        rvCategories = (RecyclerView) view.findViewById(R.id.rv_categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvCategories.setLayoutManager(layoutManager);

        // set adapter for RecyclerView
        rvCategories.setAdapter(categoriesArrayAdapter);

        // load data from db
        loadData();

        // function click to start AddCategory activity
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(v.getContext(), AddCategory.class);
                startActivity(call);
            }
        });

        return view;
    }


    // function load categories from db
    private void loadData(){
        // get from firestore
        firestoreDB.collection("Users").document(userIDAuth).
                collection("Categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.e("Error", "Listen Categories falied");
                            return;
                        }

                        categoriesList.clear();
                        categoriesId.clear();

                        if(value != null){
                            for(DocumentSnapshot d : value){
                                CategoryModel c = d.toObject(CategoryModel.class);
                                categoriesId.add(d.getId());
                                categoriesList.add(c);
                            }

                            if (categoriesList.isEmpty()) {
                                tvCategory.setVisibility(View.VISIBLE);
                                rvCategories.setVisibility(View.GONE);
                            } else {
                                tvCategory.setVisibility(View.GONE);
                                rvCategories.setVisibility(View.VISIBLE);
                                categoriesArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

    }
}

package com.example.todolistapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Principal extends AppCompatActivity {

    // bottom menu and frameLayout
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);

        // find id menu and frame
        bottomNavigationView = findViewById(R.id.bottom_nav);
        frameLayout = findViewById(R.id.frameLayout);

        Intent intent = getIntent();

        if (intent != null && intent.getBooleanExtra("openCategoriesFragment", false)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Categories()).commit();
            bottomNavigationView.setSelectedItemId(R.id.bottom_categorys);

        } else {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Tasks()).commit();
            }
        }

        // set up the bottom navigation item selection listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.bottom_tasks){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Tasks()).commit();

                }else if (id == R.id.bottom_categorys){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Categories()).commit();

                }else if(id == R.id.bottom_perfil){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Perfil()).commit();
                }


                return true;
            }
        });

    }

}

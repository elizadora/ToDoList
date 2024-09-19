package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolistapp.models.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCategory extends AppCompatActivity {

    // db and authentication
    private FirebaseFirestore firestoreDB;
    String userIDAuth;


    ImageButton btnBackMain;
    Button btnRegisterCategory;


    EditText categoryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);


        // get userId
        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDB = FirebaseFirestore.getInstance();


        btnBackMain = findViewById(R.id.btn_back_mainC);
        btnRegisterCategory = findViewById(R.id.btn_register_category);

        categoryName = findViewById(R.id.name_category);



        btnRegisterCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = categoryName.getText().toString().trim();

                if(name.isEmpty()){
                    Toast.makeText(AddCategory.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();

                }else{
                    addCategory(name);
                }
            }
        });

        btnBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(AddCategory.this, Principal.class);
                call.putExtra("openCategoriesFragment", true);
                startActivity(call);
            }
        });
    }



    private void addCategory(String name){
        CategoryModel category = new CategoryModel(name);

        firestoreDB.collection("Users").document(userIDAuth).collection("Categories").add(category).
                addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AddCategory.this, "Categoria cadastrada com sucesso", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AddCategory.this, "Erro ao cadastrar categoria", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

}


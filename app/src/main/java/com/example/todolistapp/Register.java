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
import com.example.todolistapp.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {
    // db and authentication
    private FirebaseFirestore firestoreDB;
    private FirebaseAuth firebaseAuth;


    // inputs
    EditText registerName;
    EditText registerEmail;
    EditText registerPassword;
    EditText registerPasswordA;

    //button
    Button btnRegister;
    ImageButton backLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);


        // find
        registerName = findViewById(R.id.register_user);
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_pass);
        registerPasswordA =  findViewById(R.id.register_pass_again);

        btnRegister = findViewById(R.id.btn_register);
        backLogin = findViewById(R.id.back_login);


        // db
        firebaseAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();


        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = registerName.getText().toString().trim();
                String email = registerEmail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();
                String passwordA = registerPasswordA.getText().toString().trim();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordA.isEmpty()){
                    Toast.makeText(Register.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();

                }else if(!password.equals(passwordA)){
                    Toast.makeText(Register.this, "Senhas diferentes!", Toast.LENGTH_SHORT).show();

                }else if(password.length() < 6){
                    Toast.makeText(Register.this, "A senha precisa de no minímo 6 caracteres!", Toast.LENGTH_SHORT).show();

                }else{
                    addUser(name, email, password);
                }
            }
        });

    }

    private void addUser(String name, String email, String password){

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            UserModel user = new UserModel(name);
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            if(firebaseUser != null){
                                // add user info to firestore by id auth
                                firestoreDB.collection("Users").document(firebaseUser.getUid()).set(user);

                                // add categories default
                                String[] defaultCategories = {"Trabalho", "Estudos", "Pessoal"};

                                for (String categoryName : defaultCategories) {
                                    CategoryModel category = new CategoryModel(categoryName);
                                    firestoreDB.collection("Users").document(firebaseUser.getUid()).collection("Categories").add(category);
                                }

                                Toast.makeText(Register.this, "Usuário cadastrado!", Toast.LENGTH_SHORT).show();
                            }

                            updateUI();

                        }else{
                            Toast.makeText(Register.this, "Error ao criar usuario!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void updateUI(){
        registerPassword.setText("");
        registerEmail.setText("");
        registerPasswordA.setText("");
        registerName.setText("");
    }
}
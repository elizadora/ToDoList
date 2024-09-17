package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    // button and link
    TextView btnRegisterTl;
    TextView forgotPassword;
    Button btnLogin;

    //input
    EditText loginEmail;
    EditText loginPass;

    // authentication
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnRegisterTl = findViewById(R.id.btn_registertl);
        forgotPassword = findViewById(R.id.forgot_password);
        btnLogin = findViewById(R.id.btn_login);

        loginEmail = findViewById(R.id.login_email);
        loginPass = findViewById(R.id.login_pass);


        loginEmail.setText("elizadoradasilva2003@gmail.com");
        loginPass.setText("123456");


        //authetication
        firebaseAuth = FirebaseAuth.getInstance();


        btnRegisterTl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(MainActivity.this, Register.class);
                startActivity(call);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String pass = loginPass.getText().toString().trim();

                if(email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(MainActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else{
                    loginUser(email, pass);
                }
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(MainActivity.this, ResetPassword.class);
                startActivity(call);
            }
        });


    }

    private void loginUser(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent call = new Intent(MainActivity.this, Principal.class);
                            startActivity(call);

                        }else{
                            Toast.makeText(MainActivity.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    // button
    Button btnResetPassword;
    ImageButton btnBack;

    // input
    EditText emailPassword;

    //authetication
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        // authetication
        mAuth = FirebaseAuth.getInstance();

        // find id button
        btnResetPassword = findViewById(R.id.btn_reset_password);
        btnBack = findViewById(R.id.btn_back_main_4);

        // find id input
        emailPassword = findViewById(R.id.email_passord_reset);


        // function click reset password by email
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPasswords = emailPassword.getText().toString().trim();

                if(emailPasswords.isEmpty()){
                    Toast.makeText(ResetPassword.this, "Preencha o campo para enviar", Toast.LENGTH_SHORT).show();

                }else{
                    mAuth.sendPasswordResetEmail(emailPasswords).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPassword.this, "O link para resetar a senha foi enviado para o seu email", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(ResetPassword.this, "Error ao enviar link", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }

            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPassword.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
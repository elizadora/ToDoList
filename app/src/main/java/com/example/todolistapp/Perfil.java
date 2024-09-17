package com.example.todolistapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.todolistapp.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Perfil extends Fragment {
    private FirebaseFirestore firestoreDB;
    private String userIDAuth;
    FirebaseUser userFirebase;

    TextView profileName;
    TextView profileNameUI;
    TextView profileEmail;
    TextView profileTasksNumber;
    TextView profileTasksNumber0;
    TextView profileTasksNumber1;

    Button btnLogout;

    ImageButton btnEditUsername;
    ImageButton btnEditEmail;
    ImageButton btnEditPassword;

    public Perfil() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Obtenha o ID do usuário autenticado
        userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        userIDAuth = userFirebase.getUid();
        firestoreDB = FirebaseFirestore.getInstance();

        // Acessar os elementos da interface do usuário
        profileName = view.findViewById(R.id.profile_name);
        profileNameUI = view.findViewById(R.id.profile_nameUi);
        profileEmail = view.findViewById(R.id.profile_email);
        profileTasksNumber = view.findViewById(R.id.profile_tasks_number);
        profileTasksNumber0 = view.findViewById(R.id.profile_tasks_number_0);
        profileTasksNumber1 = view.findViewById(R.id.profile_tasks_number_1);


        btnEditEmail = view.findViewById(R.id.btn_edit_email);
        btnEditPassword = view.findViewById(R.id.btn_edit_password);
        btnEditUsername = view.findViewById(R.id.bnt_edit_username);
        btnLogout = view.findViewById(R.id.btn_logout);

        loadData();

        // Contar o número de tarefas do usuário
        firestoreDB.collection("Users").document(userIDAuth).collection("Tasks").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int taskQTD = task.getResult().size();
                        profileTasksNumber.setText(String.valueOf(taskQTD));
                    }
                });

        // Contar o número de tarefas com status 0
        firestoreDB.collection("Users").document(userIDAuth)
                .collection("Tasks").whereEqualTo("status", 0).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int taskQTD0 = task.getResult().size();
                        profileTasksNumber0.setText(String.valueOf(taskQTD0));
                    }
                });

        // Contar o número de tarefas com status 1
        firestoreDB.collection("Users").document(userIDAuth)
                .collection("Tasks").whereEqualTo("status", 1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int taskQTD1 = task.getResult().size();
                        profileTasksNumber1.setText(String.valueOf(taskQTD1));
                    }
                });




        btnEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(view.getContext());

                View dialogView = inflater.inflate(R.layout.dialog_edit_username, null);
                EditText editUsername = dialogView.findViewById(R.id.edit_username);
                editUsername.setText(profileName.getText().toString());

                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Editar")
                        .setView(dialogView)
                        .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newUsername = editUsername.getText().toString().trim();
                                editUsername(newUsername);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        // Alterar a cor do botão "Salvar" (positivo)
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(view.getContext().getResources().getColor(R.color.primary));

                        // Alterar a cor do botão "Cancelar" (negativo)
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(view.getContext().getResources().getColor(R.color.secondary));
                    }
                });

                dialog.show();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent call = new Intent(view.getContext(), MainActivity.class);
                startActivity(call);
            }
        });

        return view;
    }


    private void loadData(){
        // Carregar os dados do usuário do Firestore
        firestoreDB.collection("Users").document(userIDAuth).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserModel user = task.getResult().toObject(UserModel.class);
                        if (user != null) {
                            profileName.setText(user.getName());
                            profileNameUI.setText(user.getName());
                        }
                        profileEmail.setText(userFirebase.getEmail());
                    }
                });
    }


    private void editUsername(String username){
        firestoreDB.collection("Users").document(userIDAuth).update("name", username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    loadData();
                }else{

                }
            }
        });
    }


}

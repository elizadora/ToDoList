package com.example.todolistapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.todolistapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Perfil extends Fragment {
    private FirebaseFirestore firestoreDB;
    private String userIDAuth;

    TextView profileName;
    TextView profileNameUI;
    TextView profileEmail;
    TextView profileTasksNumber;
    TextView profileTasksNumber0;
    TextView profileTasksNumber1;

    public Perfil() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Obtenha o ID do usuário autenticado
        FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        userIDAuth = userFirebase.getUid();
        firestoreDB = FirebaseFirestore.getInstance();

        // Acessar os elementos da interface do usuário
        profileName = view.findViewById(R.id.profile_name);
        profileNameUI = view.findViewById(R.id.profile_nameUi);
        profileEmail = view.findViewById(R.id.profile_email);
        profileTasksNumber = view.findViewById(R.id.profile_tasks_number);
        profileTasksNumber0 = view.findViewById(R.id.profile_tasks_number_0);
        profileTasksNumber1 = view.findViewById(R.id.profile_tasks_number_1);

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

        return view;
    }
}

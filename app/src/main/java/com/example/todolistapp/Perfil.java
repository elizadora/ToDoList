package com.example.todolistapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.todolistapp.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Perfil extends Fragment {
    // db and authetication
    private FirebaseFirestore firestoreDB;
    private String userIDAuth;
    FirebaseUser userFirebase;

    //output
    TextView profileName;
    TextView profileNameUI;
    TextView profileEmail;
    TextView profileTasksNumber;
    TextView profileTasksNumber0;
    TextView profileTasksNumber1;

    // button
    Button btnLogout;
    ImageButton btnEditUsername;
    ImageButton btnEditPassword;

    public Perfil() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // get id user and instance firestore
        userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        userIDAuth = userFirebase.getUid();
        firestoreDB = FirebaseFirestore.getInstance();

        // find id output
        profileName = view.findViewById(R.id.profile_name);
        profileNameUI = view.findViewById(R.id.profile_nameUi);
        profileEmail = view.findViewById(R.id.profile_email);
        profileTasksNumber = view.findViewById(R.id.profile_tasks_number);
        profileTasksNumber0 = view.findViewById(R.id.profile_tasks_number_0);
        profileTasksNumber1 = view.findViewById(R.id.profile_tasks_number_1);


        // find id btn
        btnEditPassword = view.findViewById(R.id.btn_edit_password);
        btnEditUsername = view.findViewById(R.id.bnt_edit_username);
        btnLogout = view.findViewById(R.id.btn_logout);

        // load data from db
        loadData();

        // count number tasks for user
        firestoreDB.collection("Users").document(userIDAuth).collection("Tasks").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int taskQTD = task.getResult().size();
                        profileTasksNumber.setText(String.valueOf(taskQTD));

                    }else{
                        Log.d("Error", "Erro ao contar as tarefas");
                    }
                });

        // count number tasks status 0
        firestoreDB.collection("Users").document(userIDAuth)
                .collection("Tasks").whereEqualTo("status", 0).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int taskQTD0 = task.getResult().size();
                            profileTasksNumber0.setText(String.valueOf(taskQTD0));
                        }else{
                            Log.d("Error", "Erro ao contar as tarefas de status 1");
                        }
                    }
                });

        // count number tasks status 1
        firestoreDB.collection("Users").document(userIDAuth)
                .collection("Tasks").whereEqualTo("status", 1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int taskQTD1 = task.getResult().size();
                            profileTasksNumber1.setText(String.valueOf(taskQTD1));
                        }else{
                            Log.d("Error", "Erro ao contar as tarefas de status 1");
                        }
                    }
                });



        // function click edit username
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
                        // change button colors on dialog show
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(view.getContext().getResources().getColor(R.color.primary));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(view.getContext().getResources().getColor(R.color.secondary));
                    }
                });

                dialog.show();
            }
        });

        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(view.getContext());

                View dialogView = inflater.inflate(R.layout.dialog_edit_password, null);
                EditText editPasswordCurrent = dialogView.findViewById(R.id.edit_password_current);
                EditText editPasswordNew = dialogView.findViewById(R.id.edit_password_new);
                EditText editPasswordNewAgain = dialogView.findViewById(R.id.edit_password_new_again);

                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Editar")
                        .setView(dialogView)
                        .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String currentPassword = editPasswordCurrent.getText().toString().trim();
                                String newPassword = editPasswordNew.getText().toString().trim();
                                String newPasswordAgain = editPasswordNewAgain.getText().toString().trim();

                                editPassword(currentPassword, newPassword, newPasswordAgain);
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
                        // change button colors on dialog show
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(view.getContext().getResources().getColor(R.color.primary));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(view.getContext().getResources().getColor(R.color.secondary));
                    }
                });

                dialog.show();
            }
        });

        // function click logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmeLogout = new AlertDialog.Builder(view.getContext());
                confirmeLogout.setTitle("Atenção!");
                confirmeLogout.setMessage("Tem certeza que deseja sair?");
                confirmeLogout.setCancelable(false);
                confirmeLogout.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

                confirmeLogout.setNegativeButton("Não", null);
                confirmeLogout.create();

                AlertDialog dialog = confirmeLogout.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.secondary));
                    }
                });
                dialog.show();
            }
        });

        return view;
    }


    private void loadData(){
        firestoreDB.collection("Users").document(userIDAuth).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            UserModel user = task.getResult().toObject(UserModel.class);
                            if (user != null) {
                                profileName.setText(user.getName());
                                profileNameUI.setText(user.getName());
                            }
                            profileEmail.setText(userFirebase.getEmail());
                        }else{
                            Log.d("Error", "Error ao carregar dados do usuario");
                        }
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
                    Toast.makeText(getContext(), "Error para atualizar username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void editPassword(String passwordCurrent, String passwordNew, String passwordNewAgain){
        AuthCredential credential = EmailAuthProvider.getCredential(profileEmail.getText().toString(), passwordCurrent);

        userFirebase.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(!passwordNew.equals(passwordNewAgain)){
                        Toast.makeText(getContext(), "Senhas diferentes, tente novamente", Toast.LENGTH_SHORT).show();
                    }else{
                        userFirebase.updatePassword(passwordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(), "Senha atualizada com sucesso", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), "Erro ao atualizar senha", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }else{
                    Toast.makeText(getContext(), "Senha nao referente ao usuário", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

package com.example.todolistapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.todolistapp.models.CategoryModel;
import com.example.todolistapp.models.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskInfo extends AppCompatActivity {
    private FirebaseFirestore firestoreDB;

    String userIDAuth;

    EditText titleView;
    EditText descriptionView;
    Spinner categoryView;
    TextView dateView;
    TextView statusView;

    Button btnSaveEditTask;
    Button btnSelectDate;
    Button btnDeleteTask;
    Button btnReady;
    ImageButton btnBackMain2;

    String id;

    List<String> categoryIds = new ArrayList<>();
    List<String> categoryList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_info);

        // get userId
        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDB = FirebaseFirestore.getInstance();


         id = getIntent().getStringExtra("taskId");


        titleView = findViewById(R.id.textTask);
        descriptionView = findViewById(R.id.description_view_task);
        categoryView = findViewById(R.id.category_view_task);
        dateView = findViewById(R.id.date_view_task);
        statusView = findViewById(R.id.status_view_task);

        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSaveEditTask = findViewById(R.id.btn_save_edit);
        btnDeleteTask = findViewById(R.id.btn_delete_task);
        btnReady = findViewById(R.id.btn_ready);
        btnBackMain2 = findViewById(R.id.btn_back_main_2);


        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryView.setAdapter(adapter);


        firestoreDB.collection("Users").document(userIDAuth).
                collection("Categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                CategoryModel category = document.toObject(CategoryModel.class);
                                categoryList.add(category.getName());
                                categoryIds.add(document.getId());
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        loadData();


        btnDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmDelete = new AlertDialog.Builder(TaskInfo.this);
                confirmDelete.setTitle("Atenção!");
                confirmDelete.setMessage("Tem certeza que deseja excluir essa tarefa?");
                confirmDelete.setCancelable(false);
                confirmDelete.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTask();
                    }
                });

                confirmDelete.setNegativeButton("Não", null);
                confirmDelete.create();

                AlertDialog dialog = confirmDelete.create();

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


        btnSaveEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleView.getText().toString().trim();
                String description = descriptionView.getText().toString().trim();
                String date = dateView.getText().toString().trim();
                int status = 1;

                if(statusView.getText().toString().equals("Pendente")){
                    status = 0;
                }

                if(title.isEmpty()){
                    Toast.makeText(TaskInfo.this, "Campos vazios!!", Toast.LENGTH_SHORT).show();
                }else{
                    editTask(title, description, categoryIds.get(categoryView.getSelectedItemPosition()), date, status);
                }

            }
        });


        btnBackMain2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(TaskInfo.this, Principal.class);
                startActivity(call);
            }
        });


        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = 0;

                if (statusView.getText().toString().equals("Pendente")) {
                    status = 1;

                } else {
                    status = 0;
                }

                firestoreDB.collection("Users")
                        .document(userIDAuth)
                        .collection("Tasks")
                        .document(id)
                        .update("status", status);

                loadData();
            }
        });


        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    private void loadData(){
        // get from firestore
        firestoreDB.collection("Users").document(userIDAuth).
                collection("Tasks").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            TaskModel t = (task.getResult()).toObject(TaskModel.class);

                            titleView.setText(t.getTitle());
                            descriptionView.setText(t.getDescription());

                            String categoryFromF = t.getCategory();
                            int spinnerPosition = categoryIds.indexOf(categoryFromF);


                            categoryView.setSelection(spinnerPosition);
                            adapter.notifyDataSetChanged();

                            dateView.setText(t.getDate());
                            if(t.getStatus() == 0){
                                statusView.setText("Pendente");
                                btnReady.setText("Marcar como concluido");

                            }else{
                                statusView.setText("Concluido");
                                btnReady.setText("Marcar como pendente");
                            }
                        }
                    }
                });
    }

    private void editTask(String title, String description, String category, String date, int status){
        TaskModel updatedTask = new TaskModel(title, description, date, category, status);


        firestoreDB.collection("Users").document(userIDAuth).collection("Tasks").document(id).set(updatedTask).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(TaskInfo.this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(TaskInfo.this, "Error ao atualizar dados", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private void deleteTask(){
        firestoreDB.collection("Users").document(userIDAuth).collection("Tasks").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent call = new Intent(TaskInfo.this, Principal.class);
                    startActivity(call);
                }else{
                    Toast.makeText(TaskInfo.this, "Error ao deletar tarefa", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void openDialog(){
        Calendar calendar = Calendar.getInstance();
        int Uyear = calendar.get(Calendar.YEAR);
        int Umonth = calendar.get(Calendar.MONTH);
        int Uday = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateView.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            }
        }, Uyear, Umonth, Uday);

        dialog.show();
    }

}
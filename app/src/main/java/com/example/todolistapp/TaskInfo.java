package com.example.todolistapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
    // authetication and db
    private FirebaseFirestore firestoreDB;
    String userIDAuth;

    // inputs/outputs
    EditText titleView;
    EditText descriptionView;
    Spinner categoryView;
    TextView dateView;
    TextView statusView;

    // buttons
    Button btnSaveEditTask;
    Button btnSelectDate;
    Button btnDeleteTask;
    Button btnReady;
    ImageButton btnBackMain2;

    // id tasks
    String id;

    // lists to spinner
    List<String> categoryIds = new ArrayList<>();
    List<String> categoryList = new ArrayList<>();

    // adapter spinner
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_info);

        // get userId and firestore instance
        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDB = FirebaseFirestore.getInstance();

        // get putExtra id tasks
        id = getIntent().getStringExtra("taskId");

        // find id input/output
        titleView = findViewById(R.id.textTask);
        descriptionView = findViewById(R.id.description_view_task);
        categoryView = findViewById(R.id.category_view_task);
        dateView = findViewById(R.id.date_view_task);
        statusView = findViewById(R.id.status_view_task);

        // find id buttons
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSaveEditTask = findViewById(R.id.btn_save_edit);
        btnDeleteTask = findViewById(R.id.btn_delete_task);
        btnReady = findViewById(R.id.btn_ready);
        btnBackMain2 = findViewById(R.id.btn_back_main_2);

        // adapter
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // set adapter to spinner
        categoryView.setAdapter(adapter);

        // add hint and select category from task
        categoryList.add("Selecione uma categoria");
        categoryIds.add("0");
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


        // function click delete task
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


        // function click edit task
        btnSaveEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleView.getText().toString().trim();
                String description = descriptionView.getText().toString().trim();
                String date = dateView.getText().toString().trim();
                String category = categoryView.getSelectedItemPosition() == 0 ? "" : categoryIds.get(categoryView.getSelectedItemPosition());
                int status = statusView.getText().toString().equals("Pendente") ? 0 : 1;

                if(title.isEmpty()){
                    Toast.makeText(TaskInfo.this, "Campos vazios!!", Toast.LENGTH_SHORT).show();
                }else{
                    editTask(title, description, category, date, status);
                }

            }
        });

        // function click back to last view
        btnBackMain2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(TaskInfo.this, Principal.class);
                startActivity(call);
            }
        });


        // change status task
        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = statusView.getText().toString().equals("Pendente") ? 1 : 0;

                firestoreDB.collection("Users")
                        .document(userIDAuth)
                        .collection("Tasks")
                        .document(id)
                        .update("status", status);

                loadData();
            }
        });


        // function select date
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    // function load data from db
    private void loadData(){
        firestoreDB.collection("Users").document(userIDAuth).
                collection("Tasks").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            TaskModel t = (task.getResult()).toObject(TaskModel.class);

                            titleView.setText(t.getTitle());
                            descriptionView.setText(t.getDescription());

                            String categoryFromF = t.getCategory();
                            int spinnerPosition = categoryFromF.isEmpty() ? 0 : categoryIds.indexOf(categoryFromF);


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

    // function edit taks db
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

    // function delete task db
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



    // open dialog function
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

        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#227B94"));
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F5F5F5"));
    }

}
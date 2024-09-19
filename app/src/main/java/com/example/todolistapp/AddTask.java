package com.example.todolistapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTask extends AppCompatActivity {

    // db and authentication
    private FirebaseFirestore firestoreDB;


    // buttons
    Button btnDataPicker;
    Button btnRegisterTask;
    ImageButton btnBackMain;

    // id user
    String userIDAuth;

    List<String> categoryIds = new ArrayList<>();
    List<String> categoryList = new ArrayList<>();

    // inputs
    Spinner categoryTask;
    TextView dateTask;
    EditText titleTask;
    EditText descriptionTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);

        // get userId
        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // find elements
        categoryTask = findViewById(R.id.category_task);
        dateTask = findViewById(R.id.date_task);
        titleTask = findViewById(R.id.title_task);
        descriptionTask = findViewById(R.id.description_task);

        btnDataPicker = findViewById(R.id.date_picker);
        btnRegisterTask = findViewById(R.id.btn_register_task);
        btnBackMain = findViewById(R.id.btn_back_main);


        // set categories user in spinner
        firestoreDB = FirebaseFirestore.getInstance();


        categoryList.add("Selecione uma categoria");
        categoryIds.add("0");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryTask.setAdapter(adapter);
        categoryTask.setSelection(0);

        firestoreDB.collection("Users").document(userIDAuth)
                .collection("Categories").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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


        // set date
        dateTask.setText(String.format("%02d/%02d/%d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH),  Calendar.getInstance().get(Calendar.MONTH) + 1,  Calendar.getInstance().get(Calendar.YEAR)));


        btnDataPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });


        btnRegisterTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTask.getText().toString().trim();
                String description = descriptionTask.getText().toString().trim();
                String date = dateTask.getText().toString().trim();

                if (title.isEmpty() || categoryTask.getSelectedItemPosition() == 0){
                    Toast.makeText(AddTask.this, "Preencha os campos obrigatorios", Toast.LENGTH_SHORT).show();

                }else{
                    addTask(title, description, categoryIds.get(categoryTask.getSelectedItemPosition()), date);
                }

            }
        });


        btnBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTask.this, Principal.class);
                startActivity(intent);
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
                dateTask.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            }
        }, Uyear, Umonth, Uday);

        dialog.show();
    }


    private void addTask(String title, String descripition, String category, String date){
        TaskModel task1  = new TaskModel(title, descripition,date, category, 0);

        firestoreDB.collection("Users").document(userIDAuth).
                collection("Tasks").add(task1).
                addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AddTask.this, "Tarefa cadastrada com sucesso", Toast.LENGTH_SHORT).show();
                            updateUI();

                        }else{
                            Toast.makeText(AddTask.this, "Error ao cadastrar tarefa", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private void updateUI(){
        titleTask.setText("");
        descriptionTask.setText("");
        categoryTask.setSelection(0);
    }
}
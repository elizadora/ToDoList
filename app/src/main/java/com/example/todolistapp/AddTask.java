package com.example.todolistapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
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
    String userIDAuth;


    // buttons
    Button btnDataPicker;
    Button btnRegisterTask;
    ImageButton btnBackMain;


    // lists to spinner
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

        // get userId and instance db
        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDB = FirebaseFirestore.getInstance();

        // find id inputs
        categoryTask = findViewById(R.id.category_task);
        dateTask = findViewById(R.id.date_task);
        titleTask = findViewById(R.id.title_task);
        descriptionTask = findViewById(R.id.description_task);

        // find id buttons
        btnDataPicker = findViewById(R.id.date_picker);
        btnRegisterTask = findViewById(R.id.btn_register_task);
        btnBackMain = findViewById(R.id.btn_back_main);


        // set first category(hint) to spinner
        categoryList.add("Selecione uma categoria");
        categoryIds.add("0");

        // create adapter
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // set adapter to spinner
        categoryTask.setAdapter(adapter);
        categoryTask.setSelection(0);

        // populate spinner with categories from db
        firestoreDB.collection("Users").document(userIDAuth)
                .collection("Categories").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    // Add each category from the db to the spinner
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        CategoryModel category = document.toObject(CategoryModel.class);
                                        categoryList.add(category.getName());
                                        categoryIds.add(document.getId());

                                    }

                                    //Notify the adapter that the data set has changed
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        });


        // set date default to today
        dateTask.setText(String.format("%02d/%02d/%d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH),  Calendar.getInstance().get(Calendar.MONTH) + 1,  Calendar.getInstance().get(Calendar.YEAR)));


        // function click open a date picker dialog
        btnDataPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });


        // function click register task
        btnRegisterTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTask.getText().toString().trim();
                String description = descriptionTask.getText().toString().trim();
                String category = categoryTask.getSelectedItemPosition() == 0 ? "" : categoryIds.get(categoryTask.getSelectedItemPosition());
                String date = dateTask.getText().toString().trim();

                if (title.isEmpty()){
                    Toast.makeText(AddTask.this, "Preencha os campos obrigatorios", Toast.LENGTH_SHORT).show();

                }else{
                    addTask(title, description,category, date);
                }

            }
        });


        // function click btn back to last view
        btnBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTask.this, Principal.class);
                startActivity(intent);
            }
        });

    }

    // open a date picker dialog for the user to select a date
    private void openDialog(){
        Calendar calendar = Calendar.getInstance();
        int Uyear = calendar.get(Calendar.YEAR);
        int Umonth = calendar.get(Calendar.MONTH);
        int Uday = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // update the selected date in the TextView
                dateTask.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            }
        }, Uyear, Umonth, Uday);

        dialog.show();

        // set the colors of the positive and negative buttons in the dialog
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#227B94"));
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F5F5F5"));

    }

    // add a task to the db
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

    // reset the input fields in the UI after task is added
    private void updateUI(){
        titleTask.setText("");
        descriptionTask.setText("");
        categoryTask.setSelection(0);
    }
}
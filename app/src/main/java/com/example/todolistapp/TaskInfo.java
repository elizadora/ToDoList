package com.example.todolistapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolistapp.models.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class TaskInfo extends AppCompatActivity {
    private FirebaseFirestore firestoreDB;

    String userIDAuth;

    TextView titleView;
    TextView descriptionView;
    TextView categoryView;
    TextView dateView;
    TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_info);

        // get userId
        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDB = FirebaseFirestore.getInstance();


        String id = getIntent().getStringExtra("taskId");


        titleView = findViewById(R.id.textTask);
        descriptionView = findViewById(R.id.description_view_task);
        categoryView = findViewById(R.id.category_view_task);
        dateView = findViewById(R.id.date_view_task);
        statusView = findViewById(R.id.status_view_task);

        // get from firestore
        firestoreDB.collection("Users").document(userIDAuth).
                collection("Tasks").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            TaskModel t = (task.getResult()).toObject(TaskModel.class);

                            titleView.setText(t.getTitle());
                            descriptionView.setText(t.getDescription());
                            categoryView.setText(t.getCategory());
                            dateView.setText(t.getDate());
                            if(t.getStatus() == 0){
                                statusView.setText("Pendente");
                            }else{
                                statusView.setText("Concluido");
                            }
                        }
                    }
                });

    }
}
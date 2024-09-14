package com.example.todolistapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.models.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategoriesArrayAdapter extends RecyclerView.Adapter<CategoriesArrayAdapter.ViewHolder>{
    private int listItemLayout;
    private ArrayList<CategoryModel> itemList;
    private FirebaseFirestore firestoreDB;
    private String userIDAuth;


    public CategoriesArrayAdapter(int layoutId, ArrayList<CategoryModel> itemList){
        this.listItemLayout = layoutId;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        CategoriesArrayAdapter.ViewHolder myViewHolder = new CategoriesArrayAdapter.ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get userId
        userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDB = FirebaseFirestore.getInstance();

        TextView itemName = holder.itemName;
        TextView itemQtd = holder.itemQtd;

        itemName.setText(itemList.get(position).getName());


        firestoreDB.collection("Users").document(userIDAuth).collection("Tasks")
                .whereEqualTo("category", itemList.get(position).getName())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            Log.d("DDDDDD", String.valueOf(querySnapshot.size()));

                            if (querySnapshot != null) {
                                int total = querySnapshot.size();
                                itemQtd.setText(String.valueOf(total));
                            } else {
                                itemQtd.setText("0");
                            }
                        } else {
                            Log.e("FirestoreError", "Erro ao consultar documentos: ", task.getException());
                            itemQtd.setText("0");
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }




    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView itemName;
        public TextView itemQtd;


        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            itemName = (TextView) itemView.findViewById(R.id.category_name);
            itemQtd = (TextView) itemView.findViewById(R.id.category_qtd);


        }


        @Override
        public void onClick(View v) {
            String name = itemName.getText().toString();
            Toast.makeText(v.getContext(), "Você selecionou: " + name, Toast.LENGTH_SHORT).show();
        }

    }
}

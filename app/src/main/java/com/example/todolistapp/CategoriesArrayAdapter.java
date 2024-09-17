package com.example.todolistapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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
    private ArrayList<String> itemIds;
    private FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();;
    private String userIDAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();;

    public CategoriesArrayAdapter(int layoutId, ArrayList<CategoryModel> itemList, ArrayList<String> itemIds){
        this.listItemLayout = layoutId;
        this.itemList = itemList;
        this.itemIds = itemIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        CategoriesArrayAdapter.ViewHolder myViewHolder = new CategoriesArrayAdapter.ViewHolder(view, this);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView itemName = holder.itemName;
        TextView itemQtd = holder.itemQtd;
        TextView itemId = holder.itemId;

        itemId.setText(itemIds.get(position));
        itemName.setText(itemList.get(position).getName());


        firestoreDB.collection("Users").document(userIDAuth).collection("Tasks")
                .whereEqualTo("category", itemIds.get(position))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
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

    private void editCategory(String categoryName, String id){
        firestoreDB.collection("Users").document(userIDAuth).collection("Categories").document(id).
                update("name", categoryName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            for (int i = 0; i < itemList.size(); i++) {
                                if (itemIds.get(i).equals(id)) {
                                    itemList.get(i).setName(categoryName);
                                    notifyItemChanged(i);
                                    break;
                                }
                            }
                            Log.d("Sucesso", "categoria atualizada");
                        }else{
                            Log.d("Error", "categoria nao foi atualizada");
                        }
                    }
                });
    }

    private void deleteCategory(String id){
        firestoreDB.collection("Users").document(userIDAuth).collection("Categories").document(id).
                delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            int position = -1;
                            for (int i = 0; i < itemIds.size(); i++) {
                                if (itemIds.get(i).equals(id)) {
                                    position = i;
                                    break;
                                }
                            }

                            if (position != -1) {
                                // Remove o item da lista de IDs
                                itemIds.remove(position);
                                // Remove o item da lista de modelos
                                itemList.remove(position);

                                // Notifica o adaptador que o item foi removido
                                notifyItemRemoved(position);
                            }
                                Log.d("Sucesso", "Categoria deletada e tela recarregada");


                        }else{
                                Log.d("Erro", "Erro ao deletar categoria");
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }




    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{
        public TextView itemName;
        public TextView itemQtd;
        public TextView itemId;
        public ImageButton dotsCategory;

        private CategoriesArrayAdapter adapter;


        public ViewHolder(View itemView, CategoriesArrayAdapter adapter){
            super(itemView);
            this.adapter = adapter;

            itemView.setOnClickListener(this);
            itemName = (TextView) itemView.findViewById(R.id.category_name);
            itemQtd = (TextView) itemView.findViewById(R.id.category_qtd);
            itemId = (TextView) itemView.findViewById(R.id.category_id);
            dotsCategory = (ImageButton) itemView.findViewById(R.id.dots_category);



            dotsCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), dotsCategory);
                    popupMenu.inflate(R.menu.popup_category);
                    popupMenu.setOnMenuItemClickListener(ViewHolder.this);
                    popupMenu.show();
                }

            });
        }


        @Override
        public void onClick(View v) {
            String name = itemName.getText().toString();
            Toast.makeText(v.getContext(), "Você selecionou: " + name, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.edit_category) {
                LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

                View dialogView = inflater.inflate(R.layout.dialog_edit_category, null);
                EditText editCategoryName = dialogView.findViewById(R.id.edit_category_name);
                editCategoryName.setText(itemName.getText().toString());

                AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Editar Categoria")
                        .setView(dialogView)
                        .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String categoryName = editCategoryName.getText().toString().trim();
                                adapter.editCategory(categoryName, itemId.getText().toString());
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
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(itemView.getContext().getResources().getColor(R.color.primary));

                        // Alterar a cor do botão "Cancelar" (negativo)
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(itemView.getContext().getResources().getColor(R.color.secondary));
                    }
                });

                dialog.show();

                return true;
            } else if (item.getItemId() == R.id.delete_category) {
                    AlertDialog.Builder confirmDelete = new AlertDialog.Builder(itemView.getContext());
                    confirmDelete.setTitle("Atenção!");
                    confirmDelete.setMessage("Tem certeza que deseja excluir essa categoria?");
                    confirmDelete.setCancelable(false);
                    confirmDelete.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.deleteCategory(itemId.getText().toString());
                        }
                    });

                    confirmDelete.setNegativeButton("Não", null);
                    confirmDelete.create().show();

                return true;
            } else {
                return false;
            }
        }
    }
}

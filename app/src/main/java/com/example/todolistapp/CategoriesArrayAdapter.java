package com.example.todolistapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.models.CategoryModel;

import java.util.ArrayList;

public class CategoriesArrayAdapter extends RecyclerView.Adapter<CategoriesArrayAdapter.ViewHolder>{
    private int listItemLayout;
    private ArrayList<CategoryModel> itemList;


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
        TextView itemName = holder.itemName;

        itemName.setText(itemList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }




    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView itemName;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            itemName = (TextView) itemView.findViewById(R.id.category_name);


        }


        @Override
        public void onClick(View v) {
            String name = itemName.getText().toString();
            Toast.makeText(v.getContext(), "Você selecionou: " + name, Toast.LENGTH_SHORT).show();
        }

    }
}

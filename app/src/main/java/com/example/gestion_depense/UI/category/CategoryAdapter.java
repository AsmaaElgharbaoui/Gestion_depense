package com.example.gestion_depense.UI.category;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestion_depense.Data.Model.Category;
import com.example.gestion_depense.R;

import java.util.Arrays;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> list;
    private CategoryFragment fragment;

    public CategoryAdapter(List<Category> list, CategoryFragment fragment) {
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category c = list.get(position);
        holder.txtName.setText(c.getName());

        // DEBUG : Affiche dans les logs
        android.util.Log.d("DEBUG_CATEGORY",
                "Nom: " + c.getName() +
                        ", isDefault: " + c.isDefault() +
                        ", ID: " + c.getId());

        if (c.isDefault()) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            android.util.Log.d("DEBUG_CATEGORY", "-> Boutons CACHÉS");
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            android.util.Log.d("DEBUG_CATEGORY", "-> Boutons VISIBLES");

            holder.btnEdit.setOnClickListener(v ->
                    fragment.openAddEditDialog(c));
            holder.btnDelete.setOnClickListener(v ->
                    fragment.deleteCategory(c));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

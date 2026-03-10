package com.example.gestion_depense.UI.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestion_depense.Data.Model.Category;
import com.example.gestion_depense.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> list;
    private CategoryFragment fragment;
    private int openPosition = -1;

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

        //  GESTION SIMPLE DE LA FLÈCHE
        if (!c.isDefault() && openPosition != position) {
            holder.txtSwipeArrow.setVisibility(View.VISIBLE);
        } else {
            holder.txtSwipeArrow.setVisibility(View.GONE);
        }

        // Gestion des boutons (inchangée)
        if (position == openPosition && !c.isDefault()) {
            holder.layoutActions.setVisibility(View.VISIBLE);
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {

            new android.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Modifier")
                    .setMessage("Voulez-vous modifier cette catégorie ?")
                    .setPositiveButton("Oui", (dialog, which) -> {

                        fragment.openAddEditDialog(c);

                    })
                    .setNegativeButton("Non", null)
                    .show();

            closeOpenItem();
        });

        holder.btnDelete.setOnClickListener(v -> {
            fragment.deleteCategory(c);
            closeOpenItem();
        });

        holder.itemView.setOnClickListener(v -> {
            if (openPosition == position) {
                closeOpenItem();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void toggleItem(int position) {
        Category c = list.get(position);
        if (c.isDefault()) {
            fragment.showDefaultCategoryMessage();
            return;
        }
        openPosition = (openPosition == position) ? -1 : position;
        notifyDataSetChanged();
    }

    public void closeOpenItem() {
        openPosition = -1;
        notifyDataSetChanged();
    }

    public Category getItem(int position) {
        return list.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtSwipeArrow;
        Button btnEdit, btnDelete;
        View layoutActions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtSwipeArrow = itemView.findViewById(R.id.txtSwipeArrow); // ✨ NOUVEAU
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            layoutActions = itemView.findViewById(R.id.layoutActions);
        }
    }
}
package com.example.gestion_depense.UI.depense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestion_depense.R;
import com.example.gestion_depense.Data.Model.Depense;
import java.util.ArrayList;
import java.util.List;

public class DepenseAdapter extends RecyclerView.Adapter<DepenseAdapter.ViewHolder> {

    private List<Depense> depenses = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(Depense depense);
        void onDelete(Depense depense);
        void onItemClick(Depense depense); // Pour aller vers le détail
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setDepenses(List<Depense> depenses) {
        this.depenses = depenses != null ? depenses : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_depense, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Depense d = depenses.get(position);

        holder.amount.setText(d.getMontant() + " DH");

        if (d.getCategoryIds() != null && !d.getCategoryIds().isEmpty()) {
            holder.category.setText(String.join(", ", d.getCategoryIds()));
        } else {
            holder.category.setText("Sans catégorie");
        }

        if (d.getDescription() != null && !d.getDescription().trim().isEmpty()) {
            holder.description.setText(d.getDescription());
            holder.description.setVisibility(View.VISIBLE);
        } else {
            holder.description.setVisibility(View.GONE);
        }

        // Clic sur l'item → détail
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(d);
        });
    }

    @Override
    public int getItemCount() {
        return depenses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category, amount, description;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.txtCategory);
            amount = itemView.findViewById(R.id.txtAmount);
            description = itemView.findViewById(R.id.txtDescription);
        }
    }
}

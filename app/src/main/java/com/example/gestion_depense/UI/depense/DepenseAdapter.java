package com.example.gestion_depense.UI.depense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestion_depense.R;
import com.example.gestion_depense.Data.Model.Depense;
import com.example.gestion_depense.Data.Model.DepenseGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DepenseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Depense depense);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // 🔥 IMPORTANT : méthode demandée
    public void setGroupedDepenses(List<DepenseGroup> groups) {
        items.clear();

        for (DepenseGroup group : groups) {
            items.add(group); // header
            items.addAll(group.getDepenses()); // items
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof DepenseGroup)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_depense_header, parent, false);
            return new HeaderHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_depense, parent, false);
            return new ItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderHolder) {

            DepenseGroup group = (DepenseGroup) items.get(position);

            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

            ((HeaderHolder) holder).txtDate.setText(
                    sdf.format(group.getDate())
            );

            ((HeaderHolder) holder).txtTotal.setText(
                    group.getTotal() + " DH"
            );

        } else {

            Depense d = (Depense) items.get(position);
            ItemHolder h = (ItemHolder) holder;

            h.amount.setText(d.getMontant() + " DH");

            if (d.getCategoryIds() != null && !d.getCategoryIds().isEmpty()) {
                h.category.setText(String.join(", ", d.getCategoryIds()));
            } else {
                h.category.setText("Sans catégorie");
            }

            if (d.getDescription() != null && !d.getDescription().trim().isEmpty()) {
                h.description.setText(d.getDescription());
                h.description.setVisibility(View.VISIBLE);
            } else {
                h.description.setVisibility(View.GONE);
            }

            h.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(d);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtTotal;

        HeaderHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtHeaderDate);
            txtTotal = itemView.findViewById(R.id.txtHeaderTotal);
        }
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        TextView category, amount, description;

        ItemHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.txtCategory);
            amount = itemView.findViewById(R.id.txtAmount);
            description = itemView.findViewById(R.id.txtDescription);
        }
    }
}
package com.example.gestion_depense.UI.statistique;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestion_depense.Data.Model.CategoryStat;
import com.example.gestion_depense.R;

import java.util.List;
import java.util.Locale;

public class CategoryStatAdapter extends RecyclerView.Adapter<CategoryStatAdapter.ViewHolder> {

    private List<CategoryStat> stats;

    public CategoryStatAdapter(List<CategoryStat> stats) {
        this.stats = stats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_stat, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CategoryStat stat = stats.get(position);

        holder.txtName.setText(stat.getName());
        holder.txtPercent.setText(
                String.format(Locale.getDefault(), "%.2f %%", stat.getPercentage())
        );
        holder.txtTotal.setText(
                String.format(Locale.getDefault(), "%.2f DH", stat.getTotal())
        );
        holder.progressBar.setProgress((int) stat.getPercentage());
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtPercent, txtTotal;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPercent = itemView.findViewById(R.id.txtPercent);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}

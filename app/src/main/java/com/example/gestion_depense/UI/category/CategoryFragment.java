package com.example.gestion_depense.UI.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestion_depense.Data.Model.Category;
import com.example.gestion_depense.R;
import com.example.gestion_depense.ViewModel.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private CategoryViewModel viewModel;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = v.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CategoryAdapter(list, this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            list.clear();
            list.addAll(categories);
            adapter.notifyDataSetChanged();
        });

        return v;
    }

    public void openAddEditDialog(Category c) {
        // VÉRIFICATION RENFORCÉE : pas de modification pour les catégories par défaut
        if (c != null && c.isDefault()) {
            Toast.makeText(getContext(),
                    "Cette catégorie par défaut ne peut pas être modifiée",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        AddEditCategoryFragment dialog = AddEditCategoryFragment.newInstance(c);
        dialog.show(getChildFragmentManager(), "AddEditCategory");
    }

    public void deleteCategory(Category c) {
        // VÉRIFICATION RENFORCÉE : pas de suppression pour les catégories par défaut
        if (c.isDefault()) {
            Toast.makeText(getContext(),
                    "Cette catégorie par défaut ne peut pas être supprimée",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.deleteCategory(c);
        Toast.makeText(getContext(),
                "Catégorie supprimée",
                Toast.LENGTH_SHORT).show();
    }
}
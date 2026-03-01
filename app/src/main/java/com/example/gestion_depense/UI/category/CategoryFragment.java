package com.example.gestion_depense.UI.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
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

        // Configuration du swipe
        setupSwipe();

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            list.clear();
            list.addAll(categories);
            adapter.closeOpenItem(); // Fermer les boutons ouverts
            adapter.notifyDataSetChanged();
        });

        return v;
    }

    private void setupSwipe() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    adapter.toggleItem(position);
                }
                adapter.notifyItemChanged(position);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.3f; // Seuil à 30% pour déclencher
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void openAddEditDialog(Category c) {
        AddEditCategoryFragment dialog = AddEditCategoryFragment.newInstance(c);
        dialog.show(getChildFragmentManager(), "AddEditCategory");
        adapter.closeOpenItem(); // Fermer les boutons
    }

    public void deleteCategory(Category c) {
        viewModel.deleteCategory(c);
        Toast.makeText(getContext(),
                "Catégorie supprimée",
                Toast.LENGTH_SHORT).show();
        adapter.closeOpenItem(); // Fermer les boutons
    }

    public void showDefaultCategoryMessage() {
        Toast.makeText(getContext(),
                "Les catégories par défaut ne peuvent pas être modifiées",
                Toast.LENGTH_SHORT).show();
    }
}
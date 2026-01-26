// CategoryFragment.java
package com.example.gestion_depense.UI.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.example.gestion_depense.Data.Model.Category;
import com.example.gestion_depense.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();
    private FirebaseManager firebaseManager = new FirebaseManager();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = v.findViewById(R.id.recyclerCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                showAddEditDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                deleteCategory(category);
            }
        });

        recyclerView.setAdapter(adapter);
        loadCategories();

        return v;
    }

    private void loadCategories() {
        firebaseManager.getCategoriesRef()
                .addSnapshotListener((value, error) -> {
                    categoryList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Category c = doc.toObject(Category.class);
                        c.setId(doc.getId());
                        categoryList.add(c);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void deleteCategory(Category category) {
        firebaseManager.getCategoriesRef()
                .document(category.getId())
                .delete();
    }

    public void showAddEditDialog(Category category) {
        // on le fera à l’étape suivante
    }
}

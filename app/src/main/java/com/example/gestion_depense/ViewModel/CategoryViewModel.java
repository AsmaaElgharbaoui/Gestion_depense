package com.example.gestion_depense.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.example.gestion_depense.Data.Model.Category;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel extends ViewModel {

    private final FirebaseManager firebaseManager = new FirebaseManager();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    public MutableLiveData<List<Category>> getCategories() {

        if (categories.getValue() == null) {
            loadCategories();
        }

        return categories;
    }

    private void loadCategories() {

        firebaseManager.listenToCategories((snapshots, error) -> {

            if (error != null) return;

            List<Category> list = new ArrayList<>();

            for (QueryDocumentSnapshot doc : snapshots) {

                Category c = doc.toObject(Category.class);
                c.setId(doc.getId());

                list.add(c);
            }

            categories.setValue(list);
        });
    }

    public void addCategory(Category c) {
        firebaseManager.addCategory(c, documentReference -> {
            c.setId(documentReference.getId());
        });
    }

    public void updateCategory(Category c) {
        firebaseManager.updateCategory(c);
    }

    public void deleteCategory(Category c) {

        if (!c.isDefault() && c.getId() != null) {
            firebaseManager.deleteCategory(c.getId());
        }
    }
}
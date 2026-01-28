package com.example.gestion_depense.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.gestion_depense.Data.Model.Category;
import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.example.gestion_depense.Data.Model.Category;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryViewModel extends ViewModel {

    private final FirebaseManager firebaseManager = new FirebaseManager();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
///
///
private final List<String> defaultCategoryNames = Arrays.asList(
         "Loisirs", "Factures", "Santé", "Éducation", "Shopping", "Autres"
);
    public MutableLiveData<List<Category>> getCategories() {
        if (categories.getValue() == null) {
            loadCategories();
        }
        return categories;
    }


    private void loadCategories() {
        firebaseManager.getCategoriesRef().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Category> list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Category c = doc.toObject(Category.class);
                    c.setId(doc.getId());
                    if (!doc.contains("isDefault")) {
                        // Si le nom est dans notre liste de noms par défaut
                        boolean isDefault = defaultCategoryNames.contains(c.getName());
                        c.setDefault(isDefault);
                    }
                    list.add(c);
                }
                categories.setValue(list);
            }
        });
    }

    public void addCategory(Category c) {
        firebaseManager.addCategory(c, documentReference -> {
            // L'ID est automatiquement généré par Firebase
            c.setId(documentReference.getId());
            loadCategories(); // Recharger la liste
        });
    }

    public void updateCategory(Category c) {
        firebaseManager.updateCategory(c);
        loadCategories();
    }

    public void deleteCategory(Category c) {
        if (!c.isDefault() && c.getId() != null) {
            firebaseManager.deleteCategory(c.getId());
            loadCategories();
        }
    }
}
package com.example.gestion_depense.Data.Firebase;

import com.example.gestion_depense.Data.Model.Category;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CollectionReference getCategoriesRef() {
        return db.collection("categories");
    }

    // Firebase génère l'ID automatiquement
    public void addCategory(Category category, OnSuccessListener<DocumentReference> listener) {
        getCategoriesRef().add(category).addOnSuccessListener(listener);
    }


    public void updateCategory(Category category) {
        if (category.getId() != null) {
            getCategoriesRef().document(category.getId()).set(category);
        }
    }

    public void deleteCategory(String id) {
        getCategoriesRef().document(id).delete();
    }
}
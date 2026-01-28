package com.example.gestion_depense;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseInitializer {

    private static final List<String> DEFAULT_CATEGORIES = Arrays.asList(
            "Nourriture", "Transport", "Loisirs", "Factures"
    );

    public static void initializeDefaultCategories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference categoriesRef = db.collection("categories");

        // D'abord, récupérer les catégories existantes
        categoriesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Créer une liste des noms de catégories existantes
                java.util.Set<String> existingCategories = new java.util.HashSet<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String name = doc.getString("name");
                    if (name != null) {
                        existingCategories.add(name);
                    }
                }

                // Ajouter les catégories manquantes
                for (String categoryName : DEFAULT_CATEGORIES) {
                    if (!existingCategories.contains(categoryName)) {
                        Map<String, Object> category = new HashMap<>();
                        category.put("name", categoryName);
                        category.put("isDefault", true);

                        categoriesRef.add(category);
                    }
                }
            }
        });
    }
}
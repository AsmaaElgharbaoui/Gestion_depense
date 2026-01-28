package com.example.gestion_depense.Data.Firebase;

import androidx.annotation.Nullable;

import com.example.gestion_depense.Data.Model.Category;
import com.example.gestion_depense.Data.Model.Depense;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseManager {

    private static FirebaseFirestore db;

    public static final String COLLECTION_DEPENSES = "expenses";
    public static final String COLLECTION_CATEGORIES = "categories";

    // Récupérer l'instance FirebaseFirestore avec settings
    public static FirebaseFirestore getDB() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true) // mode offline
                    .build();
            db.setFirestoreSettings(settings);
        }
        return db;
    }

    // ========================= DEPENSE =========================
    public static void addDepense(Depense depense) {
        getDB().collection(COLLECTION_DEPENSES).add(depense);
    }

    public static void updateDepense(Depense depense) {
        if (depense.getId() == null) return;
        getDB().collection(COLLECTION_DEPENSES)
                .document(depense.getId())
                .set(depense);
    }

    public static void deleteDepense(String depenseId) {
        getDB().collection(COLLECTION_DEPENSES)
                .document(depenseId)
                .delete();
    }

    public static void getDepense(OnCompleteListener<QuerySnapshot> listener) {
        getDB().collection(COLLECTION_DEPENSES)
                .get()
                .addOnCompleteListener(listener);
    }

    public static ListenerRegistration listenToDepenses(EventListener<QuerySnapshot> listener) {
        return getDB().collection(COLLECTION_DEPENSES)
                .addSnapshotListener(listener);
    }

    // ========================= CATEGORY =========================
    public static void addCategory(Category category, @Nullable OnSuccessListener<DocumentReference> listener) {
        if (listener != null) {
            getDB().collection(COLLECTION_CATEGORIES).add(category).addOnSuccessListener(listener);
        } else {
            getDB().collection(COLLECTION_CATEGORIES).add(category);
        }
    }

    public static void updateCategory(Category category) {
        if (category.getId() == null) return;
        getDB().collection(COLLECTION_CATEGORIES)
                .document(category.getId())
                .set(category);
    }

    public static void deleteCategory(String categoryId) {
        getDB().collection(COLLECTION_CATEGORIES)
                .document(categoryId)
                .delete();
    }

    public static ListenerRegistration listenToCategories(EventListener<QuerySnapshot> listener) {
        return getDB().collection(COLLECTION_CATEGORIES)
                .addSnapshotListener(listener);
    }

    public static CollectionReference getCategoriesRef() {
        return getDB().collection(COLLECTION_CATEGORIES);
    }
}

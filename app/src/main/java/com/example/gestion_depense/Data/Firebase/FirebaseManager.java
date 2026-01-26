package com.example.gestion_depense.Data.Firebase;

import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;


public class FirebaseManager {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Référence à la collection "categories"
    public CollectionReference getCategoriesRef() {
        return db.collection("categories");
    }
    }

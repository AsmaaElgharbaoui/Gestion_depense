package com.example.gestion_depense.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.example.gestion_depense.Data.Model.Depense;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class DepenseViewModel extends ViewModel {

    private final MutableLiveData<List<Depense>> depenses = new MutableLiveData<>();
    private ListenerRegistration listener;

    // 📥 Lire les dépenses depuis Firestore
    public LiveData<List<Depense>> getDepenses() {
        if (depenses.getValue() == null) {
            loadDepenses();
        }
        return depenses;
    }

    private void loadDepenses() {
        listener = FirebaseManager.getDB()
                .collection("expenses")
                .addSnapshotListener((value, error) -> {

                    if (value == null) return;

                    List<Depense> list = new ArrayList<>();
                    value.forEach(doc -> {
                        Depense d = doc.toObject(Depense.class);
                        d.setId(doc.getId());
                        list.add(d);
                    });

                    depenses.setValue(list);
                });
    }

    public void addDepense(Depense depense) {
        FirebaseManager.getDB()
                .collection("expenses")
                .add(depense);
    }

    // ✏ Modifier une dépense
    public void updateDepense(Depense depense) {
        FirebaseManager.getDB()
                .collection("expenses")
                .document(depense.getId())
                .set(depense);
    }

    // 🗑 Supprimer une dépense
    public void deleteDepense(String id) {
        FirebaseManager.getDB()
                .collection("expenses")
                .document(id)
                .delete();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (listener != null) listener.remove();
    }
}

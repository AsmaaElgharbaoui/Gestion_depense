package com.example.gestion_depense.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gestion_depense.Data.Model.Depense;
import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatsViewModel extends ViewModel {

    private final MutableLiveData<List<Depense>> depensesLiveData = new MutableLiveData<>();

    public LiveData<List<Depense>> getDepensesLiveData() {
        return depensesLiveData;
    }

    // Charger toutes les dépenses depuis Firebase
    public void loadDepenses() {
        FirebaseManager.getDepense(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Depense> list = new ArrayList<>();
                for (var doc : task.getResult()) {
                    Depense d = doc.toObject(Depense.class);
                    list.add(d);
                }
                depensesLiveData.setValue(list);
            } else {
                depensesLiveData.setValue(new ArrayList<>());
            }
        });
    }
}

package com.example.gestion_depense.UI.depense;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.example.gestion_depense.Data.Model.Depense;
import com.example.gestion_depense.R;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DepenseDetailFragment extends Fragment {

    private Depense depense;
    private TextView txtMontant, txtDate, txtDescription, txtCategories;
    private Button btnModifier, btnSupprimer;

    // Création instance pour navigation
    public static DepenseDetailFragment newInstance(Depense depense) {
        DepenseDetailFragment fragment = new DepenseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("depense", depense);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_depense_detail, container, false);

        // Initialisation Views
        txtMontant = view.findViewById(R.id.txtMontantDetail);
        txtDate = view.findViewById(R.id.txtDateDetail);
        txtDescription = view.findViewById(R.id.txtDescriptionDetail);
        txtCategories = view.findViewById(R.id.txtCategoriesDetail);
        btnModifier = view.findViewById(R.id.btnModifier);
        btnSupprimer = view.findViewById(R.id.btnSupprimer);

        // Récupérer depense passée
        if (getArguments() != null)
            depense = (Depense) getArguments().getSerializable("depense");

        if (depense != null) populateDetails(depense);

        // Bouton Modifier
        btnModifier.setOnClickListener(v -> {
            if (depense == null) return;

            AddEditDepenseFragment fragment = AddEditDepenseFragment.newInstance(depense);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Bouton Supprimer
        btnSupprimer.setOnClickListener(v -> {
            if (depense == null || depense.getId() == null) return;

            new AlertDialog.Builder(requireContext())
                    .setTitle("Supprimer")
                    .setMessage("Voulez-vous vraiment supprimer cette dépense ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        DocumentReference ref = FirebaseManager.getDB()
                                .collection("depenses")
                                .document(depense.getId());

                        ref.delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Dépense supprimée", Toast.LENGTH_SHORT).show();
                                    // Retour sécurisé à la liste
                                    if (getParentFragmentManager().getBackStackEntryCount() > 0)
                                        getParentFragmentManager().popBackStack();
                                    else
                                        requireActivity().onBackPressed();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(),
                                        "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show());
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        return view;
    }

    private void populateDetails(Depense d) {
        txtMontant.setText(d.getMontant() + " DH");

        if (d.getDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(d.getDate());
            String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(cal.getTime());
            txtDate.setText(dateStr);
        } else {
            txtDate.setText("Date non définie");
        }

        txtDescription.setText(d.getDescription() != null && !d.getDescription().isEmpty()
                ? d.getDescription() : "Aucune description");

        txtCategories.setText(d.getCategoryIds() != null && !d.getCategoryIds().isEmpty()
                ? String.join(", ", d.getCategoryIds()) : "Sans catégorie");
    }
}

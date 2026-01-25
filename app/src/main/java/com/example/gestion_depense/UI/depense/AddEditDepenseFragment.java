package com.example.gestion_depense.UI.depense;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.gestion_depense.R;
import com.example.gestion_depense.Data.Model.Depense;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class AddEditDepenseFragment extends Fragment {

    private EditText edtMontant, edtDate, edtDescription;
    private LinearLayout layoutCategories;
    private Button btnSave, btnAnnuler;

    private FirebaseFirestore firestore;
    private Date selectedDate;

    private Depense depenseToEdit = null;
    private final String[] categories = { "Nourriture", "Transport", "Loisirs", "Autre" };

    // ✅ Création d'une instance avec une dépense existante
    public static AddEditDepenseFragment newInstance(Depense depense) {
        AddEditDepenseFragment fragment = new AddEditDepenseFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_edit_depense, container, false);

        firestore = FirebaseFirestore.getInstance();

        edtMontant = view.findViewById(R.id.edtMontant);
        edtDate = view.findViewById(R.id.edtDate);
        edtDescription = view.findViewById(R.id.edtDescription);
        layoutCategories = view.findViewById(R.id.layoutCategories);
        btnSave = view.findViewById(R.id.btnSave);
        btnAnnuler = view.findViewById(R.id.btnAnnuler);

        setupCategories();
        setupDatePicker();
        setupButtons();

        // 🔹 Vérifier si on est en mode édition
        if (getArguments() != null && getArguments().containsKey("depense")) {
            depenseToEdit = (Depense) getArguments().getSerializable("depense");
            if (depenseToEdit != null) loadDepenseToEdit();
        }

        return view;
    }

    private void setupCategories() {
        layoutCategories.removeAllViews();
        for (String cat : categories) {
            CheckBox cb = new CheckBox(requireContext());
            cb.setText(cat);
            layoutCategories.addView(cb);
        }
    }

    private void setupDatePicker() {
        edtDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            if (selectedDate != null) c.setTime(selectedDate);

            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, dayOfMonth);
                        selectedDate = cal.getTime();
                        edtDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });
    }

    private void setupButtons() {
        btnSave.setOnClickListener(v -> saveDepense());
        btnAnnuler.setOnClickListener(v ->
                requireActivity().onBackPressed()
        );
    }

    private void loadDepenseToEdit() {
        edtMontant.setText(String.valueOf(depenseToEdit.getMontant()));
        edtDescription.setText(depenseToEdit.getDescription());

        selectedDate = depenseToEdit.getDate();
        if (selectedDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            edtDate.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" +
                    (cal.get(Calendar.MONTH) + 1) + "/" +
                    cal.get(Calendar.YEAR));
        }

        if (depenseToEdit.getCategoryIds() != null) {
            for (int i = 0; i < layoutCategories.getChildCount(); i++) {
                CheckBox cb = (CheckBox) layoutCategories.getChildAt(i);
                if (depenseToEdit.getCategoryIds().contains(cb.getText().toString())) {
                    cb.setChecked(true);
                }
            }
        }
    }

    private void saveDepense() {
        String montantStr = edtMontant.getText().toString().trim();
        if (TextUtils.isEmpty(montantStr)) {
            edtMontant.setError("Montant obligatoire");
            return;
        }

        if (selectedDate == null) {
            edtDate.setError("Date obligatoire");
            return;
        }

        double montant = Double.parseDouble(montantStr);
        String description = edtDescription.getText().toString().trim();

        List<String> selectedCategories = new ArrayList<>();
        for (int i = 0; i < layoutCategories.getChildCount(); i++) {
            CheckBox cb = (CheckBox) layoutCategories.getChildAt(i);
            if (cb.isChecked()) selectedCategories.add(cb.getText().toString());
        }

        Depense depense = new Depense(
                montant,
                description,
                selectedDate,
                selectedCategories.isEmpty() ? null : selectedCategories
        );

        if (depenseToEdit != null && depenseToEdit.getId() != null) {
            // 🔹 Mode modification
            firestore.collection("depenses")
                    .document(depenseToEdit.getId())
                    .set(depense)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Dépense modifiée", Toast.LENGTH_SHORT).show();

                        // 🔹 Retour à la page accueil des dépenses
                        requireActivity().getSupportFragmentManager()
                                .popBackStack("depense_accueil", 0); // Nommer le backstack
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(),
                            "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {

            // 🔹 Mode ajout
            firestore.collection("depenses")
                    .add(depense)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(requireContext(), "Dépense ajoutée", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(),
                            "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}

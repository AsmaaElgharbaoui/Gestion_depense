package com.example.gestion_depense.UI.depense;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.example.gestion_depense.Data.Model.Depense;
import com.example.gestion_depense.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.*;

public class DepenseFragment extends Fragment {

    private DepenseAdapter adapter;
    private TextView txtDate;
    private EditText edtSearch;
    private Spinner spinnerDateType;
    private final List<Depense> allDepenses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_depense, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerDepenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        txtDate = view.findViewById(R.id.txtDate);
        edtSearch = view.findViewById(R.id.edtSearch);
        spinnerDateType = view.findViewById(R.id.spinnerDateType);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Jour", "Mois", "Année"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDateType.setAdapter(spinnerAdapter);

        adapter = new DepenseAdapter();
        recyclerView.setAdapter(adapter);

        // 🔹 Clic sur item → détail
        adapter.setOnItemClickListener(new DepenseAdapter.OnItemClickListener() {
            @Override
            public void onEdit(Depense depense) { }

            @Override
            public void onDelete(Depense depense) { }

            @Override
            public void onItemClick(Depense depense) {
                DepenseDetailFragment fragment = DepenseDetailFragment.newInstance(depense);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // 🔹 Filtre texte
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDepenses(s.toString());
            }
        });

        // 🔹 Filtre date
        txtDate.setOnClickListener(v -> openDateFilter());

        // 🔹 Charger les dépenses
        loadDepenses();

        return view;
    }

    private void filterDepenses(String query) {
        List<Depense> filtered = new ArrayList<>();
        for (Depense d : allDepenses) {
            if (d.getDescription() != null &&
                    d.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(d);
            }
        }
        adapter.setDepenses(filtered);
    }

    private void openDateFilter() {
        String type = spinnerDateType.getSelectedItem().toString();
        Calendar calendar = Calendar.getInstance();

        switch (type) {
            case "Jour":
                new DatePickerDialog(requireContext(),
                        (view, year, month, dayOfMonth) -> {
                            calendar.set(year, month, dayOfMonth);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            txtDate.setText("📅 " + sdf.format(calendar.getTime()));
                            filterDepensesByDate(calendar, "day");
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;

            case "Mois":
                LinearLayout layoutMonth = new LinearLayout(requireContext());
                layoutMonth.setOrientation(LinearLayout.HORIZONTAL);
                NumberPicker npMonth = new NumberPicker(requireContext());
                NumberPicker npYear = new NumberPicker(requireContext());

                npMonth.setMinValue(1); npMonth.setMaxValue(12);
                npMonth.setValue(calendar.get(Calendar.MONTH) + 1);
                npYear.setMinValue(2000); npYear.setMaxValue(calendar.get(Calendar.YEAR));
                npYear.setValue(calendar.get(Calendar.YEAR));

                layoutMonth.addView(npMonth);
                layoutMonth.addView(npYear);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Choisir Mois et Année")
                        .setView(layoutMonth)
                        .setPositiveButton("OK", (dialog, which) -> {
                            calendar.set(npYear.getValue(), npMonth.getValue() - 1, 1);
                            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                            txtDate.setText("📅 " + sdf.format(calendar.getTime()));
                            filterDepensesByDate(calendar, "month");
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
                break;

            case "Année":
                NumberPicker np = new NumberPicker(requireContext());
                np.setMinValue(2000); np.setMaxValue(calendar.get(Calendar.YEAR));
                np.setValue(calendar.get(Calendar.YEAR));

                new AlertDialog.Builder(requireContext())
                        .setTitle("Choisir Année")
                        .setView(np)
                        .setPositiveButton("OK", (dialog, which) -> {
                            calendar.set(np.getValue(), 0, 1);
                            txtDate.setText("📅 " + np.getValue());
                            filterDepensesByDate(calendar, "year");
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
                break;
        }
    }

    private void filterDepensesByDate(Calendar calendar, String type) {
        List<Depense> filtered = new ArrayList<>();
        for (Depense d : allDepenses) {
            if (d.getDate() == null) continue;
            Calendar dep = Calendar.getInstance();
            dep.setTime(d.getDate());
            boolean match = false;
            switch (type) {
                case "day":
                    match = dep.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                            dep.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                            dep.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH);
                    break;
                case "month":
                    match = dep.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                            dep.get(Calendar.MONTH) == calendar.get(Calendar.MONTH);
                    break;
                case "year":
                    match = dep.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
                    break;
            }
            if (match) filtered.add(d);
        }
        adapter.setDepenses(filtered);
    }

    private void loadDepenses() {
        FirebaseManager.getDB()
                .collection("depenses")
                .get()
                .addOnSuccessListener(query -> {
                    List<Depense> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        Depense d = doc.toObject(Depense.class);
                        d.setId(doc.getId()); // 🔹 Garder l'ID pour modification/suppression
                        list.add(d);
                    }
                    allDepenses.clear();
                    allDepenses.addAll(list);
                    adapter.setDepenses(list);
                });
    }
}

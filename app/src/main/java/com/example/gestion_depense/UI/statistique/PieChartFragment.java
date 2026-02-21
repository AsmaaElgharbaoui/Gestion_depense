package com.example.gestion_depense.UI.statistique;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestion_depense.Data.Model.CategoryStat;
import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.example.gestion_depense.Data.Model.Depense;
import com.example.gestion_depense.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.*;
public class PieChartFragment extends Fragment {

    private PieChart pieChart;
    private RecyclerView recyclerStats;

    private Button btnWeek, btnMonth, btnYear;
    private TextView txtNoData;

    private List<Depense> allDepenses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        btnWeek = view.findViewById(R.id.btnWeek);
        btnMonth = view.findViewById(R.id.btnMonth);
        btnYear = view.findViewById(R.id.btnYear);
        txtNoData = view.findViewById(R.id.txtNoData);

        setupPieChart();
        loadDepenses();

        btnWeek.setOnClickListener(v -> filterByPeriod(Calendar.WEEK_OF_YEAR));
        btnMonth.setOnClickListener(v -> filterByPeriod(Calendar.MONTH));
        btnYear.setOnClickListener(v -> filterByPeriod(Calendar.YEAR));

        return view;
    }

    private void setupPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(60f);
        pieChart.animateY(800);
    }

    private void loadDepenses() {

        FirebaseManager.getDB()
                .collection("depenses")
                .get()
                .addOnSuccessListener(query -> {

                    allDepenses.clear();

                    for (var doc : query) {
                        Depense d = doc.toObject(Depense.class);
                        if (d != null) allDepenses.add(d);
                    }

                    filterByPeriod(Calendar.MONTH); // affichage par défaut
                });
    }

    private void filterByPeriod(int periodType) {

        Map<String, Float> categoryTotals = new HashMap<>();
        float globalTotal = 0f;

        Calendar now = Calendar.getInstance();

        for (Depense d : allDepenses) {

            if (d.getDate() == null) continue;

            Calendar cal = Calendar.getInstance();
            cal.setTime(d.getDate());

            boolean match = false;

            if (periodType == Calendar.WEEK_OF_YEAR) {
                match = cal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
                        && cal.get(Calendar.YEAR) == now.get(Calendar.YEAR);
            }
            else if (periodType == Calendar.MONTH) {
                match = cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        && cal.get(Calendar.YEAR) == now.get(Calendar.YEAR);
            }
            else if (periodType == Calendar.YEAR) {
                match = cal.get(Calendar.YEAR) == now.get(Calendar.YEAR);
            }

            if (!match) continue;

            float montant = (float) d.getMontant();
            globalTotal += montant;

            if (d.getCategoryIds() == null || d.getCategoryIds().isEmpty()) {

                categoryTotals.put("Sans catégorie",
                        categoryTotals.getOrDefault("Sans catégorie", 0f) + montant);

            } else {

                for (String cat : d.getCategoryIds()) {
                    categoryTotals.put(cat,
                            categoryTotals.getOrDefault(cat, 0f) + montant);
                }
            }
        }

        updatePieChart(categoryTotals, globalTotal);
    }


    private void updatePieChart(Map<String, Float> categoryTotals, float total) {

        recyclerStats = getView().findViewById(R.id.recyclerStats);
        recyclerStats.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<PieEntry> entries = new ArrayList<>();
        List<CategoryStat> statList = new ArrayList<>();

        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {

            float percentage = (entry.getValue() / total) * 100f;

            entries.add(new PieEntry(entry.getValue(), entry.getKey()));

            statList.add(new CategoryStat(
                    entry.getKey(),
                    entry.getValue(),
                    percentage
            ));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors( Color.parseColor("#6A5ACD"),
                Color.parseColor("#FF6F61"),
                Color.parseColor("#4CAF50"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#03A9F4"),
                Color.parseColor("#9E9E9E"));
        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.setCenterText(String.format(Locale.getDefault(),
                "%.2f DH", total));
        pieChart.invalidate();

        recyclerStats.setAdapter(new CategoryStatAdapter(statList));
    }

}

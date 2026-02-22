package com.example.gestion_depense.UI.statistique;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.example.gestion_depense.Data.Model.CategoryStat;
import com.example.gestion_depense.Data.Model.Depense;
import com.example.gestion_depense.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.*;

public class PieChartFragment extends Fragment {

    private PieChart pieChart;
    private RecyclerView recyclerStats;
    private TabLayout tabPeriod;
    private Button btnPrevious, btnNext;
    private TextView txtSelectedPeriod, txtNoData;

    private List<Depense> allDepenses = new ArrayList<>();

    private String currentPeriod = "mois";
    private Date currentDate = new Date();

    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.FRENCH);
    private SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMM", Locale.FRENCH);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        initViews(view);
        setupChart();
        loadDepenses();

        return view;
    }

    private void initViews(View view) {

        pieChart = view.findViewById(R.id.pieChart);
        recyclerStats = view.findViewById(R.id.recyclerStats);
        tabPeriod = view.findViewById(R.id.tabPeriod);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        txtSelectedPeriod = view.findViewById(R.id.txtSelectedPeriod);
        txtNoData = view.findViewById(R.id.txtNoData);

        recyclerStats.setLayoutManager(new LinearLayoutManager(requireContext()));

        tabPeriod.getTabAt(1).select();

        tabPeriod.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentPeriod = "semaine"; break;
                    case 1: currentPeriod = "mois"; break;
                    case 2: currentPeriod = "année"; break;
                }
                updateDisplay();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) { updateDisplay(); }
        });

        btnPrevious.setOnClickListener(v -> changePeriod(-1));
        btnNext.setOnClickListener(v -> changePeriod(1));
    }

    private void setupChart() {
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
                    for (QueryDocumentSnapshot doc : query) {
                        Depense d = doc.toObject(Depense.class);
                        if (d != null) allDepenses.add(d);
                    }
                    updateDisplay();
                });
    }

    private void changePeriod(int direction) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);

        if (currentPeriod.equals("mois"))
            cal.add(Calendar.MONTH, direction);
        else if (currentPeriod.equals("année"))
            cal.add(Calendar.YEAR, direction);
        else
            cal.add(Calendar.WEEK_OF_YEAR, direction);

        currentDate = cal.getTime();
        updateDisplay();
    }

    private void updateDisplay() {

        updateDateText();

        Map<String, Float> categoryTotals = new HashMap<>();
        float globalTotal = 0f;

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);

        int targetYear = cal.get(Calendar.YEAR);
        int targetMonth = cal.get(Calendar.MONTH);
        int targetWeek = cal.get(Calendar.WEEK_OF_YEAR);

        for (Depense d : allDepenses) {

            if (d.getDate() == null) continue;

            Calendar depCal = Calendar.getInstance();
            depCal.setTime(d.getDate());

            boolean match = false;

            if (currentPeriod.equals("semaine")) {
                match = depCal.get(Calendar.YEAR) == targetYear &&
                        depCal.get(Calendar.WEEK_OF_YEAR) == targetWeek;
            }
            else if (currentPeriod.equals("mois")) {
                match = depCal.get(Calendar.YEAR) == targetYear &&
                        depCal.get(Calendar.MONTH) == targetMonth;
            }
            else if (currentPeriod.equals("année")) {
                match = depCal.get(Calendar.YEAR) == targetYear;
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

        if (categoryTotals.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
            recyclerStats.setAdapter(null);
            return;
        }

        txtNoData.setVisibility(View.GONE);
        pieChart.setVisibility(View.VISIBLE);

        updatePieChart(categoryTotals, globalTotal);
    }

    private void updateDateText() {

        String text;

        if (currentPeriod.equals("mois"))
            text = monthFormat.format(currentDate);
        else if (currentPeriod.equals("année"))
            text = yearFormat.format(currentDate);
        else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date start = cal.getTime();
            cal.add(Calendar.DAY_OF_WEEK, 6);
            Date end = cal.getTime();
            text = dayMonthFormat.format(start) + " - " + dayMonthFormat.format(end);
        }

        txtSelectedPeriod.setText(text);
    }

    private void updatePieChart(Map<String, Float> categoryTotals, float total) {

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
        dataSet.setColors(
                Color.parseColor("#6A5ACD"),
                Color.parseColor("#FF6F61"),
                Color.parseColor("#4CAF50"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#03A9F4"),
                Color.parseColor("#9E9E9E")
        );

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.setCenterText(String.format(Locale.FRENCH, "%.2f DH", total));
        pieChart.invalidate();

        recyclerStats.setAdapter(new CategoryStatAdapter(statList));
    }
}
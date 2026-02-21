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

import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.example.gestion_depense.Data.Model.Depense;
import com.example.gestion_depense.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class BarChartFragment extends Fragment {

    private BarChart barChart;
    private TabLayout tabPeriod;
    private TextView txtSelectedPeriod, txtTotal, txtAverage, txtNoData;
    private Button btnPrevious, btnNext;

    private List<Depense> allDepenses = new ArrayList<>();
    private String currentPeriod = "mois"; // semaine, mois, année
    private Date currentDate = new Date();

    // Formats de date
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.FRENCH);
    private SimpleDateFormat weekFormat = new SimpleDateFormat("'Semaine' w", Locale.FRENCH);
    private SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMM", Locale.FRENCH);
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.FRENCH);
    private SimpleDateFormat monthShortFormat = new SimpleDateFormat("MMM", Locale.FRENCH);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        initViews(view);
        setupChart();
        loadDepenses();

        return view;
    }

    private void initViews(View view) {
        barChart = view.findViewById(R.id.barChart);
        tabPeriod = view.findViewById(R.id.tabPeriod);
        txtSelectedPeriod = view.findViewById(R.id.txtSelectedPeriod);
        txtTotal = view.findViewById(R.id.txtTotal);
        txtAverage = view.findViewById(R.id.txtAverage);
        txtNoData = view.findViewById(R.id.txtNoData);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);

        // Sélectionner Mois par défaut
        tabPeriod.getTabAt(1).select();

        tabPeriod.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentPeriod = "semaine";
                        break;
                    case 1:
                        currentPeriod = "mois";
                        break;
                    case 2:
                        currentPeriod = "année";
                        break;
                }
                updateDisplay();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) { updateDisplay(); }
        });

        btnPrevious.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);

            if (currentPeriod.equals("mois")) {
                cal.add(Calendar.MONTH, -1);
            } else if (currentPeriod.equals("année")) {
                cal.add(Calendar.YEAR, -1);
            } else {
                cal.add(Calendar.WEEK_OF_YEAR, -1);
            }

            currentDate = cal.getTime();
            updateDisplay();
        });

        btnNext.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);

            if (currentPeriod.equals("mois")) {
                cal.add(Calendar.MONTH, 1);
            } else if (currentPeriod.equals("année")) {
                cal.add(Calendar.YEAR, 1);
            } else {
                cal.add(Calendar.WEEK_OF_YEAR, 1);
            }

            currentDate = cal.getTime();
            updateDisplay();
        });
    }

    private void setupChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.animateY(1000);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
    }

    private void loadDepenses() {
        FirebaseManager.getDB()
                .collection("depenses")
                .get()
                .addOnSuccessListener(query -> {
                    allDepenses.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Depense d = doc.toObject(Depense.class);
                        if (d != null) {
                            d.setId(doc.getId());
                            allDepenses.add(d);
                        }
                    }
                    updateDisplay();
                })
                .addOnFailureListener(e -> {
                    txtNoData.setVisibility(View.VISIBLE);
                    txtNoData.setText("Erreur: " + e.getMessage());
                });
    }

    private void updateDisplay() {
        updateDateText();

        // Récupérer les données filtrées pour la période
        Map<String, Float> data = getFilteredData();

        if (data.isEmpty()) {
            // Pas de dépenses pour cette période
            barChart.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
            txtNoData.setText("Aucune dépense pour " + txtSelectedPeriod.getText());

            // Remettre les totaux à zéro
            txtTotal.setText("0.00");
            txtAverage.setText("0.00");
        } else {
            // Des dépenses existent
            barChart.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);

            // Mettre à jour le graphique
            updateChart(data);

            // Calculer et afficher le total et la moyenne
            updateTotals(data);
        }
    }

    /**
     * Met à jour le texte de la période sélectionnée
     */
    private void updateDateText() {
        String text = "";

        if (currentPeriod.equals("mois")) {
            text = monthFormat.format(currentDate);
        }
        else if (currentPeriod.equals("année")) {
            text = yearFormat.format(currentDate);
        }
        else { // semaine
            int weekNumber = getWeekNumber(currentDate);
            String weekRange = getWeekDateRange(currentDate);
            text = "Semaine " + weekNumber + " (" + weekRange + ")";
        }

        txtSelectedPeriod.setText(text);
    }

    /**
     * Récupère le numéro de la semaine dans l'année
     */
    private int getWeekNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Calcule la plage de dates de la semaine (Lundi au Dimanche)
     */
    private String getWeekDateRange(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // Aller au Lundi de la semaine
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = cal.getTime();

        // Aller au Dimanche de la semaine
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date endDate = cal.getTime();

        return dayMonthFormat.format(startDate) + " - " + dayMonthFormat.format(endDate);
    }

    /**
     * Calcule le numéro de la semaine DANS LE MOIS (1-5)
     */
    private int getWeekOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * Filtre les dépenses selon la période et les groupe
     */
    private Map<String, Float> getFilteredData() {
        Map<String, Float> data = new TreeMap<>(); // TreeMap pour garder l'ordre
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);

        int targetYear = cal.get(Calendar.YEAR);
        int targetMonth = cal.get(Calendar.MONTH);
        int targetWeek = cal.get(Calendar.WEEK_OF_YEAR);

        for (Depense d : allDepenses) {
            if (d.getDate() == null) continue;

            Calendar depCal = Calendar.getInstance();
            depCal.setTime(d.getDate());

            boolean include = false;
            String key = "";

            if (currentPeriod.equals("semaine")) {
                // Filtrer par semaine
                if (depCal.get(Calendar.YEAR) == targetYear &&
                        depCal.get(Calendar.WEEK_OF_YEAR) == targetWeek) {
                    include = true;
                    // Grouper par jour
                    key = dayFormat.format(d.getDate());
                }
            }
            else if (currentPeriod.equals("mois")) {
                // Filtrer par mois
                if (depCal.get(Calendar.YEAR) == targetYear &&
                        depCal.get(Calendar.MONTH) == targetMonth) {
                    include = true;
                    // Grouper par semaine DANS LE MOIS (S1, S2, S3, S4, S5)
                    int weekOfMonth = getWeekOfMonth(d.getDate());
                    key = "S" + weekOfMonth;
                }
            }
            else if (currentPeriod.equals("année")) {
                // Filtrer par année
                if (depCal.get(Calendar.YEAR) == targetYear) {
                    include = true;
                    // Grouper par mois
                    key = monthShortFormat.format(d.getDate());
                }
            }

            if (include) {
                float current = data.getOrDefault(key, 0f);
                data.put(key, current + (float) d.getMontant());
            }
        }

        return data;
    }

    /**
     * Met à jour le graphique avec les données
     */
    private void updateChart(Map<String, Float> data) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Float> entry : data.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Dépenses");
        dataSet.setColors(Color.parseColor("#2196F3"));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size());

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.invalidate();
    }

    /**
     * Calcule et affiche le total et la moyenne
     */
    private void updateTotals(Map<String, Float> data) {
        float total = 0;
        for (float value : data.values()) {
            total += value;
        }

        float average = total / data.size(); // Moyenne = total / nombre de périodes

        txtTotal.setText(String.format(Locale.FRENCH, "%.2f", total));
        txtAverage.setText(String.format(Locale.FRENCH, "%.2f", average));
    }

    /**
     * Récupère le nombre de semaines dans le mois courant
     */
    private int getWeeksInMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        return cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }
}
package com.example.gestion_depense.UI.statistique;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gestion_depense.Data.Firebase.FirebaseManager;
import com.example.gestion_depense.Data.Model.Depense;
import com.example.gestion_depense.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private BarChart barChart; // Changé pour BarChart (plus simple)
    private Spinner spinnerPeriod;
    private TextView txtTotal, txtAverage, txtPeriodTitle, txtNoData;
    private final List<Depense> allDepenses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        barChart = view.findViewById(R.id.barChart);
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod);
        txtTotal = view.findViewById(R.id.txtTotal);
        txtAverage = view.findViewById(R.id.txtAverage);
        txtPeriodTitle = view.findViewById(R.id.txtPeriodTitle);
        txtNoData = view.findViewById(R.id.txtNoData);

        setupChart();
        setupSpinner();
        loadDepenses();

        return view;
    }

    private void setupChart() {
        // Configuration simple du graphique
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);

        // Axe X simple
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7, true);

        // Axe Y simple
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#EEEEEE"));
        leftAxis.setAxisMinimum(0f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setFitBars(true); // Pour bien ajuster les barres
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Cette semaine", "Ce mois-ci", "Cette année"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(adapter);

        spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showWeeklyStats();
                        txtPeriodTitle.setText("Évolution cette semaine");
                        break;
                    case 1:
                        showMonthlyStats();
                        txtPeriodTitle.setText("Évolution ce mois-ci");
                        break;
                    case 2:
                        showYearlyStats();
                        txtPeriodTitle.setText("Évolution cette année");
                        break;
                }
            }
        });
    }

    private void loadDepenses() {
        txtNoData.setVisibility(View.VISIBLE);
        txtNoData.setText("Chargement des dépenses...");

        FirebaseManager.getDB()
                .collection("depenses")
                .get()
                .addOnSuccessListener(query -> {
                    allDepenses.clear();
                    for (var doc : query) {
                        Depense d = doc.toObject(Depense.class);
                        allDepenses.add(d);
                    }

                    if (allDepenses.isEmpty()) {
                        txtNoData.setText("Aucune dépense enregistrée");
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.GONE);
                        showWeeklyStats(); // Par défaut
                    }
                })
                .addOnFailureListener(e -> {
                    txtNoData.setText("Erreur de chargement");
                });
    }

    private void showWeeklyStats() {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);

        // Initialiser un tableau pour 7 jours
        float[] dailyTotals = new float[7];

        for (Depense d : allDepenses) {
            if (d.getDate() == null) continue;

            Calendar depCal = Calendar.getInstance();
            depCal.setTime(d.getDate());

            int depWeek = depCal.get(Calendar.WEEK_OF_YEAR);
            int depYear = depCal.get(Calendar.YEAR);

            // Vérifier si c'est la même semaine
            if (depWeek == currentWeek && depYear == currentYear) {
                int dayOfWeek = depCal.get(Calendar.DAY_OF_WEEK) - 1; // 0=Dimanche
                // Convertir: Dimanche=0 -> Lundi=0
                if (dayOfWeek == 0) dayOfWeek = 6;
                else dayOfWeek = dayOfWeek - 1;

                dailyTotals[dayOfWeek] += d.getMontant();
            }
        }

        drawBarChart(dailyTotals, "week");
        updateStats(dailyTotals);
    }

    private void showMonthlyStats() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Initialiser un tableau pour tous les jours du mois
        float[] dailyTotals = new float[daysInMonth];

        for (Depense d : allDepenses) {
            if (d.getDate() == null) continue;

            Calendar depCal = Calendar.getInstance();
            depCal.setTime(d.getDate());

            int depMonth = depCal.get(Calendar.MONTH);
            int depYear = depCal.get(Calendar.YEAR);

            // Vérifier si c'est le même mois
            if (depMonth == currentMonth && depYear == currentYear) {
                int dayOfMonth = depCal.get(Calendar.DAY_OF_MONTH) - 1; // 0-based
                dailyTotals[dayOfMonth] += d.getMontant();
            }
        }

        drawBarChart(dailyTotals, "month");
        updateStats(dailyTotals);
    }

    private void showYearlyStats() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        // Initialiser un tableau pour 12 mois
        float[] monthlyTotals = new float[12];

        for (Depense d : allDepenses) {
            if (d.getDate() == null) continue;

            Calendar depCal = Calendar.getInstance();
            depCal.setTime(d.getDate());

            int depYear = depCal.get(Calendar.YEAR);

            // Vérifier si c'est la même année
            if (depYear == currentYear) {
                int month = depCal.get(Calendar.MONTH); // 0-11
                monthlyTotals[month] += d.getMontant();
            }
        }

        drawBarChart(monthlyTotals, "year");
        updateStats(monthlyTotals);
    }

    private void drawBarChart(float[] data, String periodType) {
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            entries.add(new BarEntry(i, data[i]));
        }

        if (entries.isEmpty()) {
            barChart.clear();
            txtNoData.setVisibility(View.VISIBLE);
            txtNoData.setText("Aucune donnée pour cette période");
            return;
        }

        txtNoData.setVisibility(View.GONE);

        BarDataSet dataSet = new BarDataSet(entries, "Dépenses");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.parseColor("#666666"));

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f); // Largeur des barres

        // Configurer les labels selon la période
        final String[] labels;
        switch (periodType) {
            case "week":
                labels = new String[]{"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
                break;
            case "month":
                labels = generateDayLabels(data.length);
                break;
            case "year":
                labels = new String[]{"Jan", "Fév", "Mar", "Avr", "Mai", "Jun",
                        "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"};
                break;
            default:
                labels = null;
        }

        if (labels != null && labels.length == data.length) {
            barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    int index = (int) value;
                    if (index >= 0 && index < labels.length) {
                        return labels[index];
                    }
                    return "";
                }
            });
            barChart.getXAxis().setLabelCount(labels.length);
        }

        barChart.setData(barData);
        barChart.invalidate();
        barChart.animateY(1000);
    }

    private void updateStats(float[] data) {
        float total = 0;
        float max = 0;
        int daysWithData = 0;

        for (float value : data) {
            total += value;
            if (value > max) {
                max = value;
            }
            if (value > 0) {
                daysWithData++;
            }
        }

        txtTotal.setText(String.format(Locale.getDefault(), "Total: %.2f DH", total));

        if (daysWithData > 0) {
            float average = total / daysWithData;
            txtAverage.setText(String.format(Locale.getDefault(), "Moyenne: %.2f DH", average));
        } else {
            txtAverage.setText("Moyenne: 0 DH");
        }
    }

    private String[] generateDayLabels(int days) {
        String[] labels = new String[days];
        for (int i = 0; i < days; i++) {
            // Afficher seulement les jours 1, 5, 10, 15, 20, 25, dernier jour
            if (i == 0 || (i+1) % 5 == 0 || i == days-1) {
                labels[i] = String.valueOf(i + 1);
            } else {
                labels[i] = "";
            }
        }
        return labels;
    }
}
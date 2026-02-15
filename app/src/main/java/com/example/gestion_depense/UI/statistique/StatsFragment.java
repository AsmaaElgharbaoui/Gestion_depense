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
import androidx.lifecycle.ViewModelProvider;

import com.example.gestion_depense.Data.Model.Depense;
import com.example.gestion_depense.R;
import com.example.gestion_depense.ViewModel.StatsViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private LineChart lineChart;
    private Button btnDay, btnMonth, btnYear;
    private TextView txtTotal, txtAverage, txtPeriodTitle, txtNoData;

    private StatsViewModel viewModel;
    private List<Depense> allDepenses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        // --- Liens XML ---
        lineChart = view.findViewById(R.id.lineChart);
        btnDay = view.findViewById(R.id.btnWeek);
        btnMonth = view.findViewById(R.id.btnMonth);
        btnYear = view.findViewById(R.id.btnYear);
        txtTotal = view.findViewById(R.id.txtTotal);
        txtAverage = view.findViewById(R.id.txtAverage);
        txtPeriodTitle = view.findViewById(R.id.txtPeriodTitle);
        txtNoData = view.findViewById(R.id.txtNoData);

        setupChart();

        // --- ViewModel ---
        viewModel = new ViewModelProvider(this).get(StatsViewModel.class);
        viewModel.getDepensesLiveData().observe(getViewLifecycleOwner(), depenses -> {
            allDepenses = depenses;
            if (allDepenses.isEmpty()) {
                txtNoData.setVisibility(View.VISIBLE);
                txtNoData.setText("Aucune dépense enregistrée");
            } else {
                txtNoData.setVisibility(View.GONE);
                showDayStats(); // affichage par défaut
            }
        });
        viewModel.loadDepenses();

        // --- Boutons ---
        btnDay.setOnClickListener(v -> showDayStats());
        btnMonth.setOnClickListener(v -> showMonthStats());
        btnYear.setOnClickListener(v -> showYearStats());

        return view;
    }

    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
    }

    // --- Jour ---
    private void showDayStats() {
        txtPeriodTitle.setText("Évolution par jour");
        if (allDepenses.isEmpty()) return;

        // Date de la première dépense
        Date firstDate = null;
        for (Depense d : allDepenses) {
            if (d.getDate() == null) continue;
            if (firstDate == null || d.getDate().before(firstDate)) {
                firstDate = d.getDate();
            }
        }
        if (firstDate == null) firstDate = new Date();

        Date today = new Date();
        Calendar start = Calendar.getInstance();
        start.setTime(firstDate);
        Calendar end = Calendar.getInstance();
        end.setTime(today);

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        while (!start.after(end)) {
            double total = 0;
            for (Depense d : allDepenses) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(d.getDate());
                if (cal.get(Calendar.YEAR) == start.get(Calendar.YEAR)
                        && cal.get(Calendar.MONTH) == start.get(Calendar.MONTH)
                        && cal.get(Calendar.DAY_OF_MONTH) == start.get(Calendar.DAY_OF_MONTH)) {
                    total += d.getMontant();
                }
            }
            entries.add(new Entry(index, (float) total));
            labels.add(new SimpleDateFormat("dd/MM", Locale.getDefault()).format(start.getTime()));
            start.add(Calendar.DAY_OF_MONTH, 1);
            index++;
        }

        drawLineChart(entries, labels);
    }

    // --- Mois ---
    private void showMonthStats() {
        txtPeriodTitle.setText("Évolution par mois");
        if (allDepenses.isEmpty()) return;

        Date firstDate = Collections.min(allDepenses, (a, b) -> a.getDate().compareTo(b.getDate())).getDate();
        Date today = new Date();

        Calendar start = Calendar.getInstance();
        start.setTime(firstDate);
        start.set(Calendar.DAY_OF_MONTH, 1);

        Calendar end = Calendar.getInstance();
        end.setTime(today);
        end.set(Calendar.DAY_OF_MONTH, 1);

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        while (!start.after(end)) {
            double total = 0;
            for (Depense d : allDepenses) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(d.getDate());
                if (cal.get(Calendar.YEAR) == start.get(Calendar.YEAR)
                        && cal.get(Calendar.MONTH) == start.get(Calendar.MONTH)) {
                    total += d.getMontant();
                }
            }
            entries.add(new Entry(index, (float) total));
            labels.add(new SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(start.getTime()));
            start.add(Calendar.MONTH, 1);
            index++;
        }

        drawLineChart(entries, labels);
    }

    // --- Année ---
    private void showYearStats() {
        txtPeriodTitle.setText("Évolution par année");
        if (allDepenses.isEmpty()) return;

        Date firstDate = Collections.min(allDepenses, (a, b) -> a.getDate().compareTo(b.getDate())).getDate();
        Date today = new Date();

        Calendar start = Calendar.getInstance();
        start.setTime(firstDate);
        start.set(Calendar.MONTH, 0);
        start.set(Calendar.DAY_OF_MONTH, 1);

        Calendar end = Calendar.getInstance();
        end.setTime(today);
        end.set(Calendar.MONTH, 0);
        end.set(Calendar.DAY_OF_MONTH, 1);

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        while (!start.after(end)) {
            double total = 0;
            for (Depense d : allDepenses) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(d.getDate());
                if (cal.get(Calendar.YEAR) == start.get(Calendar.YEAR)) {
                    total += d.getMontant();
                }
            }
            entries.add(new Entry(index, (float) total));
            labels.add(String.valueOf(start.get(Calendar.YEAR)));
            start.add(Calendar.YEAR, 1);
            index++;
        }

        drawLineChart(entries, labels);
    }

    // --- Affichage du graphique ---
    private void drawLineChart(List<Entry> entries, List<String> labels) {
        if (entries.isEmpty()) {
            lineChart.clear();
            txtNoData.setVisibility(View.VISIBLE);
            txtNoData.setText("Aucune donnée");
            return;
        }

        txtNoData.setVisibility(View.GONE);

        LineDataSet dataSet = new LineDataSet(entries, "Dépenses");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setCircleColor(Color.parseColor("#2196F3"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, com.github.mikephil.charting.components.AxisBase axis) {
                int i = (int) value;
                if (i >= 0 && i < labels.size()) return labels.get(i);
                return "";
            }
        });

        lineChart.invalidate();

        // Total et moyenne
        float total = 0;
        int countWithData = 0;
        for (Entry e : entries) {
            total += e.getY();
            if (e.getY() > 0) countWithData++;
        }
        txtTotal.setText(String.format(Locale.getDefault(), "Total: %.2f DH", total));
        txtAverage.setText(String.format(Locale.getDefault(), "Moyenne: %.2f DH",
                countWithData > 0 ? total / countWithData : 0));
    }
}

package com.example.gestion_depense;

import static org.junit.Assert.assertEquals;

import com.example.gestion_depense.Data.Model.Depense;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

public class DepenseFilterTest {

    @Test
    public void testFiltreCategorie() {

        List<Depense> allDepenses = new ArrayList<>();

        allDepenses.add(new Depense(100, "", new Date(),
                Arrays.asList("food")));

        allDepenses.add(new Depense(200, "", new Date(),
                Arrays.asList("transport")));

        String currentCategoryQuery = "food";

        List<Depense> result = new ArrayList<>();

        for (Depense d : allDepenses) {

            boolean matchCategory = false;

            if (d.getCategoryIds() != null) {
                for (String cat : d.getCategoryIds()) {
                    if (cat.toLowerCase().contains(currentCategoryQuery)) {
                        matchCategory = true;
                        break;
                    }
                }
            }

            if (matchCategory) {
                result.add(d);
            }
        }

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getMontant(), 0.001);
    }

    @Test
    public void testFiltreAnnee() {

        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1);
        Date date2024 = cal.getTime();

        cal.set(2023, Calendar.JANUARY, 1);
        Date date2023 = cal.getTime();

        List<Depense> allDepenses = new ArrayList<>();
        allDepenses.add(new Depense(100, "", date2024, null));
        allDepenses.add(new Depense(200, "", date2023, null));

        Calendar currentDateFilter = Calendar.getInstance();
        currentDateFilter.set(2024, Calendar.JANUARY, 1);

        List<Depense> result = new ArrayList<>();

        for (Depense d : allDepenses) {

            Calendar dep = Calendar.getInstance();
            dep.setTime(d.getDate());

            boolean matchDate =
                    dep.get(Calendar.YEAR) == currentDateFilter.get(Calendar.YEAR);

            if (matchDate) {
                result.add(d);
            }
        }

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getMontant(), 0.001);
    }
}
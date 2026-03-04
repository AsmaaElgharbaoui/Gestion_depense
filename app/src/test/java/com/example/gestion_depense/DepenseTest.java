package com.example.gestion_depense;

import static org.junit.Assert.*;

import com.example.gestion_depense.Data.Model.Depense;
import com.example.gestion_depense.Data.Model.DepenseGroup;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

public class DepenseTest {

    @Test
    public void testCreationDepense() {

        Depense depense = new Depense(
                150.0,
                "Courses",
                new Date(),
                Arrays.asList("cat1", "cat2")
        );

        assertEquals(150.0, depense.getMontant(), 0.001);
        assertEquals("Courses", depense.getDescription());
        assertEquals(2, depense.getCategoryIds().size());
    }

    @Test
    public void testMontantInvalide() {

        Depense depense = new Depense(
                -50,
                "Erreur",
                new Date(),
                Arrays.asList("cat1")
        );

        assertFalse(depense.isValid());
    }

    @Test
    public void testTotalDepenseGroup() {

        Depense d1 = new Depense(100, "", new Date(), null);
        Depense d2 = new Depense(50, "", new Date(), null);

        DepenseGroup group = new DepenseGroup(new Date());
        group.addDepense(d1);
        group.addDepense(d2);

        assertEquals(150, group.getTotal(), 0.001);
    }
}
package com.example.gestion_depense;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.gestion_depense.Data.Model.Depense;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Date;


    @RunWith(AndroidJUnit4.class)
    public class DepenseIntegrationTest {

        @Test
        public void testAjoutDepenseFirebase() {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Depense depense = new Depense(
                    200,
                    "Test Firebase",
                    new Date(),
                    Arrays.asList("cat1")
            );

            db.collection("depenses")
                    .add(depense)
                    .addOnSuccessListener(documentReference -> {
                        assertNotNull(documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        fail("Insertion Firebase échouée");
                    });
        }
    }


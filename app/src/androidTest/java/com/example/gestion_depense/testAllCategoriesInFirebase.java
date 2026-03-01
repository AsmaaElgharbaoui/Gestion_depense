package com.example.gestion_depense;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class testAllCategoriesInFirebase {

    @Test
    public void testAfficherToutesLesCategories() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        System.out.println("CONNEXION A FIREBASE...");

        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        System.out.println("CONNEXION REUSSIE !");
                        System.out.println("LISTE DES CATEGORIES");
                        System.out.println("======================");

                        int compteur = 0;
                        List<String> erreurs = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            compteur++;

                            String id = doc.getId();
                            String name = doc.getString("name");
                            Boolean isDefault = doc.getBoolean("isDefault");

                            System.out.println("\nCategorie " + compteur + ":");
                            System.out.println("   ID: " + id);
                            System.out.println("   Nom: " + name);
                            System.out.println("   isDefault: " + isDefault);

                            // Vérifications sans arrêter le test
                            if (name == null) {
                                erreurs.add("ERREUR: Nom manquant pour la categorie " + compteur + " (ID: " + id + ")");
                            }
                            if (isDefault == null) {
                                erreurs.add("ERREUR: isDefault manquant pour la categorie " + compteur + " (ID: " + id + ")");
                            }
                        }

                        System.out.println("\n======================");
                        System.out.println("RESUME");
                        System.out.println("======================");
                        System.out.println("Total categories trouvees: " + compteur);

                        if (erreurs.isEmpty()) {
                            System.out.println("Toutes les categories sont valides !");
                            assertTrue("Aucune categorie trouvee", compteur > 0);
                        } else {
                            System.out.println("\nPROBLEMES DETECTES:");
                            for (String erreur : erreurs) {
                                System.out.println(erreur);
                            }
                            fail(erreurs.size() + " erreur(s) detectee(s) dans les donnees");
                        }

                    } else {
                        System.err.println("ERREUR: " + task.getException().getMessage());
                        fail("Impossible de se connecter a Firebase");
                    }
                    latch.countDown();
                });

        latch.await(10, TimeUnit.SECONDS);
    }
}
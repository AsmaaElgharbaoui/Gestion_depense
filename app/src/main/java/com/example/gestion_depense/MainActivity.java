package com.example.gestion_depense;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gestion_depense.UI.depense.DepenseFragment;
import com.example.gestion_depense.UI.depense.AddEditDepenseFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        // Charger le fragment par défaut
        loadFragment(new DepenseFragment());

        // Gestion du menu du bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.nav_depenses) {
                fragment = new DepenseFragment();
            } /*else if (item.getItemId() == R.id.nav_category) {
                fragment = new com.example.gestion_depense.UI.category.CategoryFragment();
            } else if (item.getItemId() == R.id.nav_stats) {
                fragment = new com.example.gestion_depense.UI.statistique.StatsFragment();
            }
*/
            if (fragment != null) {
                loadFragment(fragment);
            }

            return true;
        });

        // Navigation vers AddEditDepenseFragment depuis le FAB
        fab.setOnClickListener(v -> loadFragment(new AddEditDepenseFragment()));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("depense_accueil")
                .commit();
    }
}

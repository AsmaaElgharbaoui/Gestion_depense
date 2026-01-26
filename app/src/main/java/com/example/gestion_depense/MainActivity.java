package com.example.gestion_depense;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gestion_depense.UI.depense.DepenseFragment;
import com.example.gestion_depense.UI.statistique.StatsFragment;
import com.example.gestion_depense.UI.category.CategoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.nav_categories)
                fragment = new CategoryFragment();

            loadFragment(fragment);
            return true;
        });

        fab.setOnClickListener(v -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
            if (current instanceof CategoryFragment) {
                ((CategoryFragment) current).showAddEditDialog(null);
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
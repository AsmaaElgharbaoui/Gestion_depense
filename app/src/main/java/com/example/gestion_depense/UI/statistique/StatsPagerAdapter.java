package com.example.gestion_depense.UI.statistique;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StatsPagerAdapter extends FragmentStateAdapter {

    public StatsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 0) {
            return new BarChartFragment();
        } else {
            return new PieChartFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

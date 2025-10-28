package com.ml.mlreal_estate.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardPagerAdapter extends FragmentStateAdapter {
    private static final int TAB_COUNT = 4;

    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SellerListingsFragment();
            case 1:
                return new SellerProfileFragment();
            case 2:
                return new SellerProfileFragment();
            case 3:
                return new SellerProfileFragment();
            default:
                return new InquiriesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
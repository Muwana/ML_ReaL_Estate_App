package com.ml.ml_real_estate.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ml.ml_real_estate.fragments.SellerStatisticsFragment;

public class SellerPagerAdapter extends FragmentStateAdapter {
    private static final int TAB_COUNT = 4;

    public SellerPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new com.ml.ml_real_estate.adapters.SellerListingsFragment();
            case 1:
                return new SellerStatisticsFragment();
            case 2:
                return new com.ml.ml_real_estate.adapters.InquiriesFragment();
            case 3:
                return new com.ml.ml_real_estate.adapters.SellerProfileFragment();
            default:
                return new com.ml.ml_real_estate.adapters.SellerListingsFragment();
        }
    }

    private void ListingsFragment() {

    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
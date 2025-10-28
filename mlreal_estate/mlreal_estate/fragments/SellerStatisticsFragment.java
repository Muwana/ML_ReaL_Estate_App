package com.ml.mlreal_estate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ml.mlreal_estate.R;

public class SellerStatisticsFragment extends Fragment {

    private TextView tvTotalProperties, tvSoldProperties, tvPendingProperties, tvTotalViews;
    private TextView tvEarnings, tvResponseRate, tvAvgPrice;

    public SellerStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seller_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        loadStatisticsData();
    }

    private void initializeViews(View view) {
        tvTotalProperties = view.findViewById(R.id.tvTotalProperties);
        tvSoldProperties = view.findViewById(R.id.tvSoldProperties);
        tvPendingProperties = view.findViewById(R.id.tvPendingProperties);
        tvTotalViews = view.findViewById(R.id.tvTotalViews);
        tvEarnings = view.findViewById(R.id.tvEarnings);
        tvResponseRate = view.findViewById(R.id.tvResponseRate);
        tvAvgPrice = view.findViewById(R.id.tvAvgPrice);
    }

    private void loadStatisticsData() {
        // TODO: Replace with actual API calls to fetch statistics
        // For now, using mock data

        // Mock data
        tvTotalProperties.setText("12");
        tvSoldProperties.setText("8");
        tvPendingProperties.setText("4");
        tvTotalViews.setText("1,247");
        tvEarnings.setText("$350,000");
        tvResponseRate.setText("95%");
        tvAvgPrice.setText("$287,500");
    }

    // Method to refresh statistics (can be called from activity)
    public void refreshStatistics() {
        loadStatisticsData();
    }
}
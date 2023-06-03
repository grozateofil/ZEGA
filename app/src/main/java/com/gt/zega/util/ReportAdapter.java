package com.gt.zega.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gt.zega.fragment.AllReportsFragment;
import com.gt.zega.fragment.MyReportsFragment;

public class ReportAdapter extends FragmentStateAdapter {


    public ReportAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MyReportsFragment();

            case 1:
                return new AllReportsFragment("brokenDeviceReport");

            case 2:
                return new AllReportsFragment("suppliesReport");

            default:
                return new MyReportsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

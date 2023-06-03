package com.gt.zega.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gt.zega.fragment.BrokenDeviceReportFragment;
import com.gt.zega.fragment.SuppliesReportsFragment;

public class PersonalReportsAdapter extends FragmentStateAdapter {

    public PersonalReportsAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BrokenDeviceReportFragment();

            case 1:
                return new SuppliesReportsFragment();

            default:
                return new BrokenDeviceReportFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

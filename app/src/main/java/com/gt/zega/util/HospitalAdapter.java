package com.gt.zega.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gt.zega.fragment.NewHospitalFragment;
import com.gt.zega.fragment.NewHospitalSectionFragment;

public class HospitalAdapter extends FragmentStateAdapter {
    public HospitalAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NewHospitalFragment();

            case 1:
                return new NewHospitalSectionFragment();

            default:
                return new NewHospitalFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

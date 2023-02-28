package com.gt.zega.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.gt.zega.R;

public class NotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

//        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);

        return view;
    }
}
package com.gt.zega.fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.gt.zega.R;

public class StatisticsFragment extends Fragment implements View.OnClickListener {

    private Button graphButton1;
    private Button graphButton2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        graphButton1 = view.findViewById(R.id.graph1);
        graphButton2 = view.findViewById(R.id.graph2);

        graphButton1.setOnClickListener(this);
        graphButton2.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.graph1):
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new GraphFragment()).addToBackStack(TAG).commit();
                break;

            case (R.id.graph2):
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new GraphFragment()).addToBackStack(TAG).commit();
                break;
        }

    }
}
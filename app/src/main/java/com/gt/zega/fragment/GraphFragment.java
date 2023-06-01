package com.gt.zega.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gt.zega.R;
import com.gt.zega.entity.BrokenMedicalDevicesMonthly;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class GraphFragment extends Fragment implements MonthSelectedFragment.OnDataSelectedListener {

    private ImageButton rotateFragment;
    private ImageButton calendar;
    private ImageButton closeFragment;
    private BarChart barChart;

    private DatabaseReference firebaseDatabase;

    private HashMap<Integer, BrokenMedicalDevicesMonthly> pointsList = new HashMap<>();
    private ArrayList<BarEntry> entries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        rotateFragment = view.findViewById(R.id.rotateFragment);
        calendar = view.findViewById(R.id.calendar);
        closeFragment = view.findViewById(R.id.closeFragment);
        barChart = view.findViewById(R.id.barChart);


        firebaseDatabase = FirebaseDatabase.getInstance().getReference("brokenMedicalDevices");

        getArrayListOfPoints(String.valueOf(Year.now().getValue()), new SimpleDateFormat("MMMM").format(new Date(System.currentTimeMillis())).toUpperCase(), new DataCallback() {
            @Override
            public void onDataLoaded(ArrayList<BrokenMedicalDevicesMonthly> data) {
                entries = new ArrayList<>();
                int i = 1;
                for (BrokenMedicalDevicesMonthly item : data) {
                    entries.add(new BarEntry(i, item.getNumberOfBrokenDevices()));
                    i++;
                }
                extracted();
            }
        });


        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFragment();
            }
        });

        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void extracted() {
        System.out.println(entries);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(Month.valueOf(new SimpleDateFormat("MMMM").format(new Date(System.currentTimeMillis())).toUpperCase()).length(false));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index % 2 == 0)
                    return "";
                return String.valueOf(index);
            }
        });

        YAxis yAxisR = barChart.getAxisRight();
        YAxis yAxisL = barChart.getAxisLeft();
        yAxisR.setAxisMinimum(0f);
        yAxisL.setAxisMinimum(0f);

        BarDataSet barDataSet = new BarDataSet(entries, "Numar de defectiuni");

        BarData barData = new BarData(barDataSet);

        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index == 0)
                    return "";
                return String.valueOf(index);
            }
        });
        barChart.setData(barData);
        barChart.invalidate(); // Refresh the chart
    }


    public ArrayList<BrokenMedicalDevicesMonthly> getArrayListOfPoints(String year, String month, final DataCallback callback) {

        ArrayList<BrokenMedicalDevicesMonthly> brokenMedicalDevicesMonthlyArrayList = new ArrayList<>();

        DatabaseReference yearRef = firebaseDatabase.child("years").child(String.valueOf(year));

        DatabaseReference monthRef = yearRef.child("months").child(String.valueOf(month));

        DatabaseReference dayRef = monthRef.child("days");

        for (int monthDay = 1; monthDay <= Month.valueOf(month.toUpperCase()).length(false); monthDay++) {
            String date = monthDay + "." + month + "." + year;
            dayRef.child(String.valueOf(monthDay)).child("errorsName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<BrokenMedicalDevicesMonthly> dailyItems = new ArrayList<>();

                    if (snapshot.exists()) {
                        for (DataSnapshot errorSnapshot : snapshot.getChildren()) {
                            String errorCode = errorSnapshot.getKey();
                            Integer numberOfBrokenDevices = errorSnapshot.child("numberOfBrokenDevices").getValue(Integer.class);

                            ArrayList<String> arrayListOfDevicesCodes = new ArrayList<>();

                            for (DataSnapshot deviceSnapshot : errorSnapshot.child("arrayListOfDevicesCodes").getChildren()) {
                                String deviceCode = deviceSnapshot.getValue(String.class);
                                arrayListOfDevicesCodes.add(deviceCode);

                            }

                            dailyItems.add(new BrokenMedicalDevicesMonthly(date, errorCode, numberOfBrokenDevices, arrayListOfDevicesCodes));
                        }
                    } else {
                        dailyItems.add(new BrokenMedicalDevicesMonthly(date, "", 0, new ArrayList<>()));
                    }

                    brokenMedicalDevicesMonthlyArrayList.addAll(dailyItems);

                    if (brokenMedicalDevicesMonthlyArrayList.size() == Month.valueOf(month.toUpperCase()).length(false)) {

                        callback.onDataLoaded(brokenMedicalDevicesMonthlyArrayList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        return brokenMedicalDevicesMonthlyArrayList;

    }

    private void showDialogFragment() {
        MonthSelectedFragment monthSelectedFragment = new MonthSelectedFragment();
        monthSelectedFragment.setOnDataSelectedListener(this);
        monthSelectedFragment.show(getParentFragmentManager(), "tag");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
    }

    @Override
    public void onDataSelected(int selectedValue1, int selectedValue2) {
        String month = Month.of(selectedValue1).name();
        String year = String.valueOf(selectedValue2);
        entries = new ArrayList<>();

        getArrayListOfPoints(year, month, new DataCallback() {
            @Override
            public void onDataLoaded(ArrayList<BrokenMedicalDevicesMonthly> data) {
                entries = new ArrayList<>();
                int i = 1;
                for (BrokenMedicalDevicesMonthly item : data) {
                    entries.add(new BarEntry(i, item.getNumberOfBrokenDevices()));
                    i++;
                }
                extracted();
                Toast.makeText(requireContext(), "Data: " + month + ", " + year, Toast.LENGTH_LONG).show();
            }
        });


    }

    interface DataCallback {
        void onDataLoaded(ArrayList<BrokenMedicalDevicesMonthly> data);
    }
}
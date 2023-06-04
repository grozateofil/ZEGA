package com.gt.zega.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gt.zega.R;
import com.gt.zega.entity.BrokenMedicalDevicesMonthly;
import com.gt.zega.htmlToPdf.HtmlComponents;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class GraphFragment extends Fragment implements MonthSelectedFragment.OnDataSelectedListener {

    private ImageButton rotateFragment;
    private ImageButton calendar;
    private ImageButton generateHtmlFileButton;

    private ImageButton closeFragment;
    private BarChart barChart;

    private DatabaseReference firebaseDatabase;

    private ArrayList<BarEntry> entries;
    private ArrayList<BrokenMedicalDevicesMonthly> brokenMedicalDevicesMonthlyArrayList;
    private Locale englishLocale;
    private Locale romanianLocale;

    private String[] arrayOfMonths = new String[]{"Ianuarie", "Februarie", "Martie", "Aprilie", "Mai", "Iunie", "Iulie", "August", "Septembrie", "Octombrie", "Noiembrie", "Decembrie"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        rotateFragment = view.findViewById(R.id.rotateFragment);
        calendar = view.findViewById(R.id.calendar);
        generateHtmlFileButton = view.findViewById(R.id.htmlFile);
        closeFragment = view.findViewById(R.id.closeFragment);
        barChart = view.findViewById(R.id.barChart);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference("brokenMedicalDevices");

        englishLocale = new Locale("en");
        romanianLocale = new Locale("ro");

        getArrayListOfPoints(String.valueOf(Year.now().getValue()), new SimpleDateFormat("MMMM", englishLocale).format(new Date(System.currentTimeMillis())).toUpperCase(), new DataCallback() {
            @Override
            public void onDataLoaded(ArrayList<BrokenMedicalDevicesMonthly> data) {
                entries = new ArrayList<>();
                int i = 1;
                for (BrokenMedicalDevicesMonthly item : data) {
                    entries.add(new BarEntry(i, item.getNumberOfBrokenDevices()));
                    i++;
                }
                displayBarChart();
            }
        });


        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFragment();
            }
        });

        generateHtmlFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateHtmlFile();
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

    private void displayBarChart() {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.getDescription().setEnabled(false);

        barChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {


            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                Highlight highlight = barChart.getHighlightByTouchPoint(me.getX(), me.getY());
                int dataIndex = (int) highlight.getX() - 1;

                new AlertDialog.Builder(getContext())
                        .setMessage(brokenMedicalDevicesMonthlyArrayList.get(dataIndex).toString())
                        .setNegativeButton("Închide", null)
                        .show();
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(Month.valueOf(new SimpleDateFormat("MMMM", englishLocale).format(new Date(System.currentTimeMillis())).toUpperCase()).length(false));
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

        BarDataSet barDataSet = new BarDataSet(entries, "Număr de defecțiuni");

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

        brokenMedicalDevicesMonthlyArrayList = new ArrayList<>();

        DatabaseReference yearRef = firebaseDatabase.child("years").child(String.valueOf(year));

        DatabaseReference monthRef = yearRef.child("months").child(String.valueOf(month));

        DatabaseReference dayRef = monthRef.child("days");

        for (int monthDay = 1; monthDay <= Month.valueOf(month).length(false); monthDay++) {
            String date = monthDay + " " + arrayOfMonths[Month.valueOf(month).getValue() - 1].toUpperCase() + " " + year;
            dayRef.child(String.valueOf(monthDay)).child("errorsName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<BrokenMedicalDevicesMonthly> dailyItems = new ArrayList<>();

                    BrokenMedicalDevicesMonthly dayData = null;
                    if (snapshot.exists()) {
                        ArrayList<String> arrayListOfDevicesCodes = new ArrayList<>();
                        ArrayList<String> errorCodesArrayList = new ArrayList<>();

                        for (DataSnapshot errorSnapshot : snapshot.getChildren()) {
                            String errorCode = errorSnapshot.getKey();
                            Integer numberOfBrokenDevices = errorSnapshot.child("numberOfBrokenDevices").getValue(Integer.class);


                            for (DataSnapshot deviceSnapshot : errorSnapshot.child("arrayListOfDevicesCodes").getChildren()) {
                                String deviceCode = deviceSnapshot.getValue(String.class);
                                arrayListOfDevicesCodes.add(deviceCode);

                            }
                            errorCodesArrayList.add(errorCode);
                        }

                        dayData = new BrokenMedicalDevicesMonthly(date, errorCodesArrayList, arrayListOfDevicesCodes.size(), new ArrayList<>(arrayListOfDevicesCodes));

                    } else {
                        dayData = new BrokenMedicalDevicesMonthly(date, new ArrayList<>(), 0, new ArrayList<>());
                    }

                    brokenMedicalDevicesMonthlyArrayList.add(dayData);


                    if (brokenMedicalDevicesMonthlyArrayList.size() == Month.valueOf(month).length(false)) {
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
                displayBarChart();
                Toast.makeText(requireContext(), "Data selectată: " + arrayOfMonths[Month.valueOf(month).getValue() - 1].toUpperCase() + " " + year, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Aceasta metoda creaza un fisier HTML cu datele din luna selectata
     */
    public void generateHtmlFile() {
        String fileName = "test";
        File directory = getContext().getExternalFilesDir("Reports");
        System.out.println("-------------------------------------->" + directory.getAbsolutePath());
        if (!directory.exists()) {
            if (!directory.mkdirs())
                System.out.println("---------------------------------->failed to create directory");

        }

        File file = null;

        try {
            Document document = Jsoup.parse(HtmlComponents.monthlyReportWithBrokenDevices(brokenMedicalDevicesMonthlyArrayList), "UTF-8");
            System.out.println(document.outerHtml());
            if (!fileName.endsWith(".html")) {
                String htmlFileName = fileName + ".html";

                file = new File(directory + File.separator + htmlFileName);

            }
            if (file != null) {
                if (file.createNewFile()) {
                    FileUtils.writeStringToFile(file, document.outerHtml(), StandardCharsets.UTF_8);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    interface DataCallback {
        void onDataLoaded(ArrayList<BrokenMedicalDevicesMonthly> data);
    }
}
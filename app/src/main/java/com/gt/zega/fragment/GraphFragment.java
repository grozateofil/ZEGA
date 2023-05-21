package com.gt.zega.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gt.zega.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class GraphFragment extends Fragment implements OnChartGestureListener, MonthSelectedFragment.OnDataSelectedListener {

    private ImageButton rotateFragment;
    private ImageButton calendar;
    private ImageButton closeFragment;

    private BarChart barChart;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ArrayList<BarEntry> entries;
    private ArrayList<String> daysOfMonth;

    //    private String day;
    private int year = Calendar.getInstance().getActualMaximum(Calendar.YEAR);
    private int month = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        rotateFragment = view.findViewById(R.id.rotateFragment);
        calendar = view.findViewById(R.id.calendar);
        closeFragment = view.findViewById(R.id.closeFragment);

        barChart = view.findViewById(R.id.chart);

        entries = new ArrayList<>();
        daysOfMonth = new ArrayList<>();
//        for (int i = 1; i <= Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
        for (int i = 1; i <= month; i++) {
            entries.add(new BarEntry(i, i));
            daysOfMonth.add(String.valueOf(i));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("users");

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(daysOfMonth.size());
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index % 2 == 0)
                    return "";
                return String.valueOf(index);


//                return "";
            }

        });

        YAxis yAxisR = barChart.getAxisRight();
        YAxis yAxisL = barChart.getAxisLeft();
        yAxisR.setAxisMinimum(0f);
        yAxisL.setAxisMinimum(0f);


        BarDataSet barDataSet = new BarDataSet(entries, "Data");
        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.invalidate(); // Refresh the chart


//        rotateFragment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
//        });

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

//        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//            @Override
//            public void onSuccess(ListResult listResult) {
//                for (StorageReference item : listResult.getItems()) {
//                    item.getStream().addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
//                            InputStream inputStream = taskSnapshot.getStream();
////
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                            String line;
//                            try {
//                                while ((line = reader.readLine()) != null) {
//                                    System.out.println("---------> "+line);
//
//                                }
//                                reader.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }
//        });
//
//        storageReference.listAll().addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//            }
//        });
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    String uid = userSnapshot.getKey();
//                    User name = userSnapshot.getValue(User.class);
//
//                    StorageReference userFilesRef = storageReference.child(uid);
//
//                    userFilesRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                        @Override
//                        public void onSuccess(ListResult listResult) {
//                            ArrayList<String> files = new ArrayList<>();
//                            for (StorageReference item : listResult.getItems()) {
//                                String filename = item.getName();
//                                files.add(filename);
//                            }
//
//                            UserFiles userFiles = new UserFiles(uid, name, files);
//
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });

        return view;
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

    private void showDialogFragment() {
        MonthSelectedFragment monthSelectedFragment = new MonthSelectedFragment();
        monthSelectedFragment.setOnDataSelectedListener(this);
        monthSelectedFragment.show(getParentFragmentManager(), "tag");
    }

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

    @Override
    public void onDataSelected(int selectedValue1, int selectedValue2) {
        month = selectedValue1;
        year = selectedValue2;
        entries = new ArrayList<>();
        daysOfMonth = new ArrayList<>();

        for (int i = 1; i <= LocalDate.of(year, month, 1).lengthOfMonth(); i++) {
            entries.add(new BarEntry(i, i));
            daysOfMonth.add(String.valueOf(i));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Data");
        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.invalidate();
        Toast.makeText(requireContext(), "Set date: " + month + ", " + year, Toast.LENGTH_LONG).show();
    }
}
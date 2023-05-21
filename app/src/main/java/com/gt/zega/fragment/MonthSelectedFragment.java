package com.gt.zega.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gt.zega.R;

import java.util.Calendar;

public class MonthSelectedFragment extends DialogFragment {

    private NumberPicker pickerMonth;
    private NumberPicker pickerYear;

    private OnDataSelectedListener onDataSelectedListener;

    private String[] arrayOfMonths = new String[]{"Ianuarie", "Februarie", "Martie", "Aprilie", "Mai", "Iunie", "Iulie", "August", "Septembrie", "Octombrie", "Noiembrie", "Decembrie"};

    public interface OnDataSelectedListener {
        void onDataSelected(int selectedValue1, int selectedValue2);
    }

    public void setOnDataSelectedListener(OnDataSelectedListener onDataSelectedListener) {
        this.onDataSelectedListener = onDataSelectedListener;
    }


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_month_selected, null);

        pickerMonth = view.findViewById(R.id.pickerMonth);
        pickerYear = view.findViewById(R.id.pickerYear);

        pickerMonth.setMinValue(0);
        pickerMonth.setMaxValue(11);
        pickerMonth.setDisplayedValues(arrayOfMonths);
        pickerMonth.setValue(Calendar.getInstance().get(Calendar.MONTH));

        pickerYear.setMaxValue(Calendar.getInstance().get(Calendar.YEAR));
        pickerYear.setMinValue(2000);
        pickerYear.setValue(Calendar.getInstance().get(Calendar.YEAR));


        return new AlertDialog.Builder(getContext()).setTitle("Selectati luna si anul").setView(view).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int monthSelected = pickerMonth.getValue() + 1;
                int yearSelected = pickerYear.getValue();

                onDataSelectedListener.onDataSelected(monthSelected, yearSelected);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).create();
    }
}
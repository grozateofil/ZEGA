package com.gt.zega.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.gt.zega.R;

import java.util.ArrayList;

public class HospitalDepartmentAdapter extends ArrayAdapter<String> {

    private ArrayList<String> oldArrayList;
    private ArrayList<String> filteredArrayList;
    private Context context;

    public HospitalDepartmentAdapter(@NonNull Context context, ArrayList<String> oldArrayList) {
        super(context, android.R.layout.simple_list_item_activated_1);
        this.oldArrayList = oldArrayList;
        this.filteredArrayList = new ArrayList<>(oldArrayList);
        this.context = context;
    }

    @Override
    public int getCount() {
        return filteredArrayList.size();
    }

    @Override
    public String getItem(int position) {
        return filteredArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_activated_1, parent, false);
        }

        TextView sectionName = convertView.findViewById(android.R.id.text1);
        sectionName.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
        sectionName.setText(filteredArrayList.get(position));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<String> filteredList = new ArrayList<>();

                String query = constraint.toString().toUpperCase();
                for (String section : oldArrayList) {
                    if (section.toUpperCase().contains(query)) {
                        filteredList.add(section);
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredArrayList = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}

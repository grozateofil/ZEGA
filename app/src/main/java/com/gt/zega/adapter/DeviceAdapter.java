package com.gt.zega.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.gt.zega.R;
import com.gt.zega.entity.MedicalDevice;

import java.util.ArrayList;

public class DeviceAdapter extends ArrayAdapter<MedicalDevice> {
    private ArrayList<MedicalDevice> oldArrayList;
    private ArrayList<MedicalDevice> filteredArrayList;
    private Context context;

    public DeviceAdapter(@NonNull Context context, ArrayList<MedicalDevice> oldArrayList) {
        super(context, R.layout.list_item_layout_user_name);
        this.context = context;
        this.oldArrayList = oldArrayList;
        this.filteredArrayList = new ArrayList<>(oldArrayList);
    }

    @Override
    public int getCount() {
        return filteredArrayList.size();
    }

    @Override
    public MedicalDevice getItem(int position) {
        return filteredArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout_user_name, parent, false);
        }

        TextView name = convertView.findViewById(R.id.text1);
        TextView companyName = convertView.findViewById(R.id.text2);
        ImageView expandCollapseArrow = convertView.findViewById(R.id.arrow);
        expandCollapseArrow.setVisibility(View.GONE);

        name.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
        companyName.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

        name.setText(getItem(position).getNameAndCode());
        companyName.setText(filteredArrayList.get(position).getDeviceCompanyName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<MedicalDevice> filteredList = new ArrayList<>();

                String query = constraint.toString().toLowerCase();
                for (MedicalDevice medicalDevice : oldArrayList) {
                    if (medicalDevice.getNameAndCode().toLowerCase().contains(query)) {
                        filteredList.add(medicalDevice);
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredArrayList = (ArrayList<MedicalDevice>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}

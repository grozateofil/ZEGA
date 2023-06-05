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
import com.gt.zega.entity.Supply;

import java.util.ArrayList;

public class SuppliesAdapter extends ArrayAdapter<Supply> {

    private ArrayList<Supply> oldArrayList;
    private ArrayList<Supply> filteredArrayList;
    private Context context;

    public SuppliesAdapter(@NonNull Context context, ArrayList<Supply> oldArrayList) {
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
    public Supply getItem(int position) {
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

        TextView type = convertView.findViewById(R.id.text1);
        TextView companyName = convertView.findViewById(R.id.text2);
        ImageView expandCollapseArrow = convertView.findViewById(R.id.arrow);
        expandCollapseArrow.setVisibility(View.GONE);

        type.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
        companyName.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

        type.setText(getItem(position).getNameAndCode());
        companyName.setText(filteredArrayList.get(position).getBrand());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Supply> filteredList = new ArrayList<>();

                String query = constraint.toString().toLowerCase();
                for (Supply supply : oldArrayList) {
                    if (supply.getNameAndCode().toLowerCase().contains(query)) {
                        filteredList.add(supply);
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredArrayList = (ArrayList<Supply>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}

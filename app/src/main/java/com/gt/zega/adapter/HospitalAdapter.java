package com.gt.zega.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.gt.zega.R;
import com.gt.zega.entity.Hospital;

import java.util.ArrayList;

public class HospitalAdapter extends ArrayAdapter<Hospital> {

    private ArrayList<Hospital> oldArrayList;
    private ArrayList<Hospital> filteredArrayList;
    private Context context;

    public HospitalAdapter(Context context, ArrayList<Hospital> oldArrayList) {
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
    public Hospital getItem(int position) {
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

        TextView hospitalName = convertView.findViewById(R.id.text1);
        TextView hospitalAddress = convertView.findViewById(R.id.text2);
        ImageView expandCollapseArrow = convertView.findViewById(R.id.arrow);
        expandCollapseArrow.setVisibility(View.GONE);

        hospitalName.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
        hospitalAddress.setTextColor(ContextCompat.getColor(getContext(), R.color.lightGray));


        hospitalName.setText(getItem(position).getHospitalName());
        hospitalAddress.setText(filteredArrayList.get(position).getHospitalAddress().toString());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Hospital> filteredList = new ArrayList<>();

                String query = constraint.toString().toLowerCase();
                for (Hospital item : oldArrayList) {
                    if (item.getHospitalName().toLowerCase().contains(query)) {
                        filteredList.add(item);
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredArrayList = (ArrayList<Hospital>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}

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
import com.gt.zega.entity.FaultCode;

import java.util.ArrayList;

public class FaultCodeAdapter extends ArrayAdapter<FaultCode> {

    private ArrayList<FaultCode> oldArrayList;
    private ArrayList<FaultCode> filteredArrayList;
    private Context context;

    public FaultCodeAdapter(@NonNull Context context, ArrayList<FaultCode> oldArrayList) {
        super(context, R.layout.list_item_layout_user_name);
        this.oldArrayList = oldArrayList;
        this.filteredArrayList = new ArrayList<>(oldArrayList);
        this.context = context;
    }

    @Override
    public int getCount() {
        return filteredArrayList.size();
    }

    @Override
    public FaultCode getItem(int position) {
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

        TextView errorCode = convertView.findViewById(R.id.text1);
        TextView errorDescription = convertView.findViewById(R.id.text2);
        ImageView expandCollapseArrow = convertView.findViewById(R.id.arrow);
        expandCollapseArrow.setVisibility(View.GONE);

        errorCode.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
        errorDescription.setTextColor(ContextCompat.getColor(getContext(), R.color.lightGray));

        errorCode.setText(getItem(position).getCode());
        errorDescription.setText(filteredArrayList.get(position).getDescription());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<FaultCode> filteredList = new ArrayList<>();

                String query = constraint.toString().toLowerCase();
                for (FaultCode faultCode : oldArrayList) {
                    if (faultCode.getCode().toLowerCase().contains(query)) {
                        filteredList.add(faultCode);
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredArrayList = (ArrayList<FaultCode>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}

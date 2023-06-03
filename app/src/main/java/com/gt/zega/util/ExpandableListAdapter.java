package com.gt.zega.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.gt.zega.R;
import com.gt.zega.entity.UserFiles;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private ArrayList<UserFiles> listOfUserFiles;
    private ArrayList<UserFiles> listAfterFiltered;
    private Context context;
    private AdapterForPersonalReports customAdapter;

    private String searchedUser;
    private String type;

    public ExpandableListAdapter(Context context, ArrayList<UserFiles> userList, String type) {
        this.listOfUserFiles = userList;
        this.context = context;
        this.searchedUser = searchedUser;
        this.listAfterFiltered = new ArrayList<>(userList);
        this.type = type;
    }

    @Override
    public int getGroupCount() {
        return listAfterFiltered.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listAfterFiltered.get(groupPosition).getListOfFiles().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listAfterFiltered.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listAfterFiltered.get(groupPosition).getListOfFiles().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout_user_name, parent, false);
        }

        TextView userName = convertView.findViewById(R.id.text1);
        TextView userRole = convertView.findViewById(R.id.text2);
        ImageView expandCollapseArrow = convertView.findViewById(R.id.arrow);

        userName.setText(listAfterFiltered.get(groupPosition).getUser().getFirstName() + " " + listAfterFiltered.get(groupPosition).getUser().getLastName());
        userRole.setText(listAfterFiltered.get(groupPosition).getUser().getRole());

        userName.setTypeface(null, Typeface.BOLD);
        userName.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.black_russian));

        userRole.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.lightGray));


        if (isExpanded && listAfterFiltered.get(groupPosition).getListOfFiles().size() > 0) {
            expandCollapseArrow.setRotation(180);
        } else {
            expandCollapseArrow.setRotation(0);
        }


        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_view_with_user_files, parent, false);
        }

        ListView childListView = convertView.findViewById(R.id.listView);

        ArrayList<String> arrayListOfUserNames = listAfterFiltered.get(groupPosition).getListOfFiles();
        String childUid = listAfterFiltered.get(groupPosition).getUid();


        customAdapter = new AdapterForPersonalReports(childPosition, childUid, arrayListOfUserNames, context, type);

        childListView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filterListByName(String searchedWord) {
        listAfterFiltered.clear();

        if (searchedWord.isEmpty()) {
            listAfterFiltered.addAll(listOfUserFiles);
        } else {
            for (UserFiles group : listOfUserFiles) {
                String userName = group.getUser().getFirstName() + " " + group.getUser().getLastName();
                String userRole = group.getUser().getRole();
                if (userName.toLowerCase().contains(searchedWord.toLowerCase()) || userRole.contains(searchedWord.toLowerCase())) {
                    listAfterFiltered.add(group);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void filterListByDate(String startDate, String endDate) {
        listAfterFiltered.clear();

        if (startDate.isEmpty() && endDate.isEmpty()) {
            listAfterFiltered.addAll(listOfUserFiles);
        } else {
            DateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ro"));
            try {
                Date strDate = dateTimeFormatter.parse(startDate);
                Date edDate = dateTimeFormatter.parse(endDate);

                for (UserFiles group : listOfUserFiles) {
                    ArrayList<String> listOfFiles = group.getListOfFiles();

                    for (String file : listOfFiles) {
                        Date dateFromFileName = dateTimeFormatter.parse(file.substring(0, file.indexOf('_')));
                        if (dateFromFileName.compareTo(strDate) >= 0 && dateFromFileName.compareTo(edDate) <= 0) {
                            listAfterFiltered.add(group);
                        }

                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        notifyDataSetChanged();
    }

}

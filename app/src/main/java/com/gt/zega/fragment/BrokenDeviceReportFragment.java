package com.gt.zega.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.gt.zega.R;
import com.gt.zega.entity.User;
import com.gt.zega.util.AdapterForPersonalReports;

import java.util.ArrayList;

public class BrokenDeviceReportFragment extends Fragment {

    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String userId;
    private User use;

    private ListView listView;
    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private ArrayList<String> arrayList;

    private AdapterForPersonalReports customAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_report, container, false);
        message = view.findViewById(R.id.message);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("users/" + userId + "/brokenDeviceReport");
        arrayList = new ArrayList<>();
        listView = view.findViewById(R.id.listOfFiles);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progress_bar_broken_device_report);

        getUserFromDB();

        progressBar.setVisibility(View.VISIBLE);
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    arrayList.add(item.getName());
                }

                customAdapter = new AdapterForPersonalReports(userId, use, arrayList, getContext(), "brokenDeviceReport");

//                if (arrayList.isEmpty()) {
//                    message.setText("Nu exista niciun raport");
//                }
                checkIfListIsEmpty();


                listView.setAdapter(customAdapter);
                customAdapter.notifyDataSetChanged();

//                listView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
//                    @Override
//                    public void onChildViewAdded(View view, View view1) {
//                        if (customAdapter.getCount() == 0) {
//                            message.setText("Nu exista niciun raport");
//                        }else {
//                            message.setText(null);
//                        }
//                    }
//
//                    @Override
//                    public void onChildViewRemoved(View view, View view1) {
//                        if (customAdapter.getCount() == 0) {
//                            message.setText("Nu exista niciun raport");
//                        }else {
//                            message.setText(null);
//                        }
//                    }
//                });
                progressBar.setVisibility(View.GONE);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                customAdapter.notifyDataSetChanged();
                storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ListResult> task) {
                        if (task.isSuccessful()) {
                            for (StorageReference item : task.getResult().getItems()) {
                                arrayList.add(item.getName());
                            }
                            customAdapter.notifyDataSetChanged();
                            checkIfListIsEmpty();
                        }

                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void getUserFromDB() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                use = dataSnapshot.getValue(User.class);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void checkIfListIsEmpty() {
        if (arrayList.isEmpty()) {
            message.setVisibility(View.VISIBLE);
            message.setText("Nu exista niciun raport");
            listView.setVisibility(View.GONE);

        } else {
            message.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
    }

}
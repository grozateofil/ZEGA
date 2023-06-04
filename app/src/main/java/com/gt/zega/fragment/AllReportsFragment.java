package com.gt.zega.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
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
import com.gt.zega.entity.UserFiles;
import com.gt.zega.util.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class AllReportsFragment extends Fragment {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ArrayList<UserFiles> userFilesArrayList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageButton filterButton;
    private TextInputLayout search;

    private TextInputLayout fromTIL;
    private TextInputLayout untilTIL;

    private Dialog dialog;

    private ProgressBar progressBar;

    private String type;

    public AllReportsFragment(String type) {
        this.type = type;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_reports, container, false);

        expandableListView = view.findViewById(R.id.expandableListView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLay);

        filterButton = view.findViewById(R.id.filtersButton);
        search = view.findViewById(R.id.search);

        progressBar = view.findViewById(R.id.progress_bar1);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("users");

        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String uid = userSnapshot.getKey();
                    User name = userSnapshot.getValue(User.class);

                    StorageReference userFilesRef = storageReference.child(uid).child(type);

                    userFilesRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            ArrayList<String> files = new ArrayList<>();
                            for (StorageReference item : listResult.getItems()) {
                                String filename = item.getName();
                                files.add(filename);
                            }

                            UserFiles userFiles = new UserFiles(uid, name, files);
                            userFilesArrayList.add(userFiles);

                            Collections.sort(userFilesArrayList, new Comparator<UserFiles>() {
                                @Override
                                public int compare(UserFiles userFiles1, UserFiles userFiles2) {
                                    return userFiles1.getUser().getFirstName().toLowerCase(Locale.ROOT).compareTo(userFiles2.getUser().getFirstName().toLowerCase(Locale.ROOT));
                                }
                            });

                            expandableListAdapter = new ExpandableListAdapter(getContext(), userFilesArrayList, type);
                            expandableListView.setAdapter(expandableListAdapter);
                            expandableListView.setGroupIndicator(null);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userFilesArrayList.clear();
                expandableListAdapter.notifyDataSetChanged();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String uid = userSnapshot.getKey();
                            User name = userSnapshot.getValue(User.class);

                            StorageReference userFilesRef = storageReference.child(uid);

                            userFilesRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                @Override
                                public void onSuccess(ListResult listResult) {
                                    ArrayList<String> files = new ArrayList<>();
                                    for (StorageReference item : listResult.getItems()) {
                                        String filename = item.getName();
                                        files.add(filename);
                                    }

                                    UserFiles userFiles = new UserFiles(uid, name, files);
                                    userFilesArrayList.add(userFiles);

                                    Collections.sort(userFilesArrayList, new Comparator<UserFiles>() {
                                        @Override
                                        public int compare(UserFiles userFiles1, UserFiles userFiles2) {
                                            return userFiles1.getUser().getFirstName().toLowerCase(Locale.ROOT).compareTo(userFiles2.getUser().getFirstName().toLowerCase(Locale.ROOT));
                                        }
                                    });

                                    expandableListAdapter.notifyDataSetChanged();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                expandableListAdapter.filterListByName(search.getEditText().getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.filter_dialog);

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.show();

                ImageButton closeDialog = dialog.findViewById(R.id.closeDialog);

                fromTIL = dialog.findViewById(R.id.from);
                untilTIL = dialog.findViewById(R.id.until);

                Button applyFilter = dialog.findViewById(R.id.applyFilter);

                fromTIL.getEditText().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        fromTIL.getEditText().setText(dayOfMonth + "." + (month + 1) + "." + year);
                                    }
                                },
                                year, month, day);
                        datePickerDialog.show();


                    }
                });

                untilTIL.getEditText().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        untilTIL.getEditText().setText(dayOfMonth + "." + (month + 1) + "." + year);
                                    }
                                },
                                year, month, day);
                        datePickerDialog.show();

                    }
                });

                closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                applyFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        expandableListAdapter.filterListByDate(fromTIL.getEditText().getText().toString(), untilTIL.getEditText().getText().toString());
                    }
                });

            }
        });

        return view;
    }

}
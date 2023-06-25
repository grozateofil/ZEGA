package com.gt.zega.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gt.zega.R;
import com.gt.zega.adapter.HospitalAdapter;
import com.gt.zega.adapter.HospitalDepartmentAdapter;
import com.gt.zega.entity.Hospital;
import com.gt.zega.entity.MedicalDevice;
import com.gt.zega.entity.User;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

import java.util.ArrayList;
import java.util.Arrays;

public class AddNewMedicalDeviceFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private TextView deviceHospital;
    private TextView deviceSection;
    private TextInputLayout deviceCompany;
    private TextInputLayout deviceName;
    private TextInputLayout deviceCode;
    private Button addButton;
    private ProgressBar progressBar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private Validations validations;

    private ArrayList<String> usersList;

    private MedicalDevice medicalDevice;
    private User user;
    private ArrayList<String> hospitalSectionArrayList;
    private ArrayList<Hospital> hospitalsArrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_medical_device, container, false);

        usersList = new ArrayList<>(Arrays.asList(getText(R.string.super_user).toString().split(",")));

        deviceHospital = view.findViewById(R.id.deviceHospital);
        deviceSection = view.findViewById(R.id.deviceSection);
        deviceCompany = view.findViewById(R.id.deviceCompany);
        deviceName = view.findViewById(R.id.deviceName);
        deviceCode = view.findViewById(R.id.deviceCode);
        addButton = view.findViewById(R.id.addNewMedicalDeviceButton);
        progressBar = view.findViewById(R.id.progress_bar_create_medical_device);

        hospitalSectionArrayList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();

        validations = new ValidationsImpl();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userKey = firebaseUser.getUid();
            getUserFromDB(userKey);
        }

        deviceHospital.setOnClickListener(this);
        deviceSection.setOnClickListener(this);
        addButton.setOnClickListener(this);

        deviceHospital.setOnLongClickListener(this);
        deviceSection.setOnLongClickListener(this);


        return view;
    }


    private void getUserFromDB(String userKey) {
        enabled(false);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String userRole = user.getRole();
                    if (!userRole.equalsIgnoreCase("admin")) {
                        deviceHospital.setText(user.getHospitalName());
                    }
                    if (!userRole.equalsIgnoreCase("admin") && !userRole.equalsIgnoreCase("inginer")) {
                        deviceHospital.setEnabled(false);
                        deviceSection.setText(user.getHospitalDepartmentsNames().get(0));
                    } else if (userRole.equalsIgnoreCase("admin")) {
                        deviceHospital.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_icon, 0);
                        deviceSection.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_icon, 0);
                        getHospitalFromDB();
                    } else if (userRole.equalsIgnoreCase("inginer")) {
                        deviceSection.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_icon, 0);
                        getHospitalSectionsFromDB();
                    }
                }
                progressBar.setVisibility(View.GONE);
                enabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                enabled(false);
            }
        });
    }

    public void getHospitalFromDB() {
        hospitalsArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("hospitals");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    Hospital hospital = objectSnapshot.getValue(Hospital.class);
                    if (!hospitalsArrayList.stream().anyMatch(f -> f.getHospitalName().equalsIgnoreCase(hospital.getHospitalName())))
                        hospitalsArrayList.add(hospital);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getHospitalSectionsFromDB() {
        hospitalSectionArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("hospitals").child(deviceHospital.getText().toString()).child("hospitalSections");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    String arrayList = objectSnapshot.getValue(String.class);
//                    Hospital hospital = objectSnapshot.getValue(Hospital.class);
//                    if (!hospitalSectionsArrayList.stream().anyMatch(f -> f.getHospitalName().equalsIgnoreCase(hospital.getHospitalName())))
                    hospitalSectionArrayList.add(arrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openDialogWithHospitals(ArrayList<Hospital> arrayListWithHospitals) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        HospitalAdapter hospitalAdapter = new HospitalAdapter(getContext(), arrayListWithHospitals);
        listView.setAdapter(hospitalAdapter);

        closeFragButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hospitalAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                deviceHospital.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                deviceHospital.setText(hospitalAdapter.getItem(position).getHospitalName());
                deviceHospital.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        deviceSection.setText(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                try {
                    hospitalSectionArrayList = new ArrayList<>();
                    hospitalSectionArrayList.addAll(hospitalAdapter.getItem(position).getHospitalDepartments());
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(), getString(R.string.without_sections, hospitalAdapter.getItem(position).getHospitalName()), Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });
    }

    private void openDialogWithHospitalSections(ArrayList<String> arrayListWithDevices) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        HospitalDepartmentAdapter sectionAdapter = new HospitalDepartmentAdapter(getContext(), arrayListWithDevices);
        listView.setAdapter(sectionAdapter);

        closeFragButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sectionAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                deviceSection.setText(sectionAdapter.getItem(position));
                deviceSection.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                dialog.dismiss();
            }
        });
    }


    private boolean validation() {
        return validations.textViewValidation(deviceHospital) &
                validations.textViewValidation(deviceSection) &
                validations.textInputLayoutValidation(deviceCompany) &
                validations.textInputLayoutValidation(deviceName) &
                validations.textInputLayoutValidation(deviceCode);
    }

    public void enabled(boolean type) {
        deviceHospital.setEnabled(type);
        deviceSection.setEnabled(type);
        deviceCompany.setEnabled(type);
        deviceName.setEnabled(type);
        deviceCode.setEnabled(type);
        addButton.setEnabled(type);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case (R.id.deviceHospital):
                if (user.getRole().equalsIgnoreCase("admin") && hospitalsArrayList.size() > 0)
                    openDialogWithHospitals(hospitalsArrayList);
                break;

            case (R.id.deviceSection):
                if ((user.getRole().equalsIgnoreCase("admin") || user.getRole().equalsIgnoreCase("inginer")) && hospitalSectionArrayList.size() > 0)
                    openDialogWithHospitalSections(hospitalSectionArrayList);
                break;

            case (R.id.addNewMedicalDeviceButton):
                if (validation()) {
                    progressBar.setVisibility(View.VISIBLE);
                    medicalDevice = new MedicalDevice(deviceCompany.getEditText().getText().toString(), deviceName.getEditText().getText().toString(), deviceCode.getEditText().getText().toString(), deviceHospital.getText().toString(), deviceSection.getText().toString());

                    databaseReference = firebaseDatabase.getReference("devices");
                    databaseReference.child(medicalDevice.getDeviceCode()).setValue(medicalDevice).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (user.getRole().equalsIgnoreCase("admin"))
                                    deviceHospital.setText(null);
                                if (user.getRole().equalsIgnoreCase("admin") || user.getRole().equalsIgnoreCase("inginer"))
                                    deviceSection.setText(null);

                                deviceCompany.getEditText().setText(null);
                                deviceName.getEditText().setText(null);
                                deviceCode.getEditText().setText(null);

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity().getApplicationContext(), "Aparat adaugat cu succes", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity().getApplicationContext(), "Eroare la adaugare", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;

        }

    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case (R.id.deviceHospital):
                if (user.getRole().equalsIgnoreCase("admin")) {
                    deviceHospital.setText(null);
                    deviceSection.setText(null);
                    hospitalSectionArrayList.clear();
                }
                break;
            case (R.id.deviceSection):
                if (user.getRole().equalsIgnoreCase("admin") || user.getRole().equalsIgnoreCase("inginer")) {
                    deviceSection.setText(null);
                }
                break;
        }
        return false;
    }
}
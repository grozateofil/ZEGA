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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gt.zega.R;
import com.gt.zega.adapter.DeviceAdapter;
import com.gt.zega.adapter.HospitalAdapter;
import com.gt.zega.adapter.HospitalDepartmentAdapter;
import com.gt.zega.adapter.SuppliesAdapter;
import com.gt.zega.entity.ConsumablesOfMedicalDevice;
import com.gt.zega.entity.Hospital;
import com.gt.zega.entity.MedicalDevice;
import com.gt.zega.entity.User;
import com.gt.zega.htmlToPdf.Html;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

import java.util.ArrayList;

public class SupplyFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    // stoc minim si maxim pe un consumabil
    private TextView selectSupplies;
    private TextView selectMedicalDevice;
    private TextView selectHospital;
    private TextView selectHospitalSection;
    private TextInputLayout devLocation;
    private Button sendButton;

    private Validations validations;

    private Dialog dialog;

    private ArrayList<ConsumablesOfMedicalDevice> suppliesArrayList;
    private ArrayList<MedicalDevice> medicalDevicesArrayList;
    private ArrayList<String> hospitalSectionsArrayList;
    private ArrayList<Hospital> hospitalsArrayList;

    private User user;
    private String userKey;

    private DatabaseReference databaseRef;
    private FirebaseUser firebaseUser;
//    private Address currentAddress;

    private Html html;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_supply, container, false);

        selectSupplies = view.findViewById(R.id.selectSupplies);
        selectMedicalDevice = view.findViewById(R.id.selectMedicalDevice);
        selectHospital = view.findViewById(R.id.selectHospital);
        selectHospitalSection = view.findViewById(R.id.selectSection);
        devLocation = view.findViewById(R.id.devLocation);
        sendButton = view.findViewById(R.id.sendButton);

        validations = new ValidationsImpl();

        hospitalSectionsArrayList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userKey = firebaseUser.getUid();
            getUserData(userKey);
        }

        getSupplies();

        selectHospital.setOnClickListener(this);
        selectHospitalSection.setOnClickListener(this);
        selectMedicalDevice.setOnClickListener(this);
        selectSupplies.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        selectHospital.setOnLongClickListener(this);
        selectHospitalSection.setOnLongClickListener(this);
        selectMedicalDevice.setOnLongClickListener(this);
        selectSupplies.setOnLongClickListener(this);

        return view;
    }

    private void getDevices(String selectedSection) {
        medicalDevicesArrayList = new ArrayList<>();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("devices");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    MedicalDevice medicalDevice = objectSnapshot.getValue(MedicalDevice.class);
                    if (medicalDevice.getSection().equalsIgnoreCase(selectedSection)) {
                        medicalDevicesArrayList.add(medicalDevice);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        hospitalSectionsArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("hospitals").child(selectHospital.getText().toString()).child("hospitalSections");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    String arrayList = objectSnapshot.getValue(String.class);
//                    Hospital hospital = objectSnapshot.getValue(Hospital.class);
//                    if (!hospitalSectionsArrayList.stream().anyMatch(f -> f.getHospitalName().equalsIgnoreCase(hospital.getHospitalName())))
                    hospitalSectionsArrayList.add(arrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSupplies() {
        suppliesArrayList = new ArrayList<>();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("supplies");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    ConsumablesOfMedicalDevice consumablesOfMedicalDevice = objectSnapshot.getValue(ConsumablesOfMedicalDevice.class);
                    suppliesArrayList.add(consumablesOfMedicalDevice);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData(String userKey) {
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        databaseRef.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (!user.getRole().equalsIgnoreCase("admin"))
                    selectHospital.setText(user.getHospitalName());
                if (!user.getRole().equalsIgnoreCase("admin") && !user.getRole().equalsIgnoreCase("inginer")) {
                    selectHospital.setEnabled(false);
                    selectHospitalSection.setText(user.getHospitalDepartmentsNames().get(0));
                    getDevices(user.getHospitalDepartmentsNames().get(0));
                } else if (user.getRole().equalsIgnoreCase("admin")) {
                    selectHospital.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_icon, 0);
                    selectHospitalSection.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_icon, 0);
                    getHospitalFromDB();
                } else if (user.getRole().equalsIgnoreCase("inginer")) {
                    selectHospitalSection.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_icon, 0);
                    getHospitalSectionsFromDB();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void openDialogWithSupplies(ArrayList<ConsumablesOfMedicalDevice> arrayListWithDevices) {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        SuppliesAdapter adapter = new SuppliesAdapter(getContext(), arrayListWithDevices);
        listView.setAdapter(adapter);

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
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectSupplies.setText(adapter.getItem(position).toString());
                selectSupplies.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                dialog.dismiss();
            }
        });
    }

    private void openDialogWithHospitals(ArrayList<Hospital> arrayListWithDevices) {

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        HospitalAdapter adapter = new HospitalAdapter(getContext(), arrayListWithDevices);
        listView.setAdapter(adapter);

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
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectHospital.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                selectHospital.setText(adapter.getItem(position).getHospitalName());


                try {
                    hospitalSectionsArrayList = new ArrayList<>();
                    hospitalSectionsArrayList.addAll(adapter.getItem(position).getHospitalDepartments());
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(), getString(R.string.without_sections, adapter.getItem(position).getHospitalName()), Toast.LENGTH_SHORT).show();
                }
//                currentAddress = adapter.getItem(position).getHospitalAddress();

                dialog.dismiss();
            }
        });
    }


    private void openDialogWithHospitalSections(ArrayList<String> arrayListWithDevices) {
        dialog = new Dialog(getContext());
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

                selectHospitalSection.setText(sectionAdapter.getItem(position).toString());
                selectHospitalSection.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                selectHospitalSection.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        selectMedicalDevice.setText(null);
                        medicalDevicesArrayList.clear();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                getDevices(sectionAdapter.getItem(position));
                dialog.dismiss();
            }
        });
    }

    private void openDialogWithMedicalDevices(ArrayList<MedicalDevice> arrayListWithMedicalDevices) {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        DeviceAdapter deviceAdapter = new DeviceAdapter(getContext(), arrayListWithMedicalDevices);

        listView.setAdapter(deviceAdapter);

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
                deviceAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectMedicalDevice.setText(deviceAdapter.getItem(position).toString());
                selectMedicalDevice.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                dialog.dismiss();
            }
        });
    }


    private boolean validation() {
        return validations.textViewValidation(selectSupplies) &
                validations.textViewValidation(selectMedicalDevice) &
                validations.textViewValidation(selectHospital) &
                validations.textViewValidation(selectHospitalSection) &
                validations.textInputLayoutValidation(devLocation);
    }

    public void enabled(boolean type) {
        selectHospital.setEnabled(type);
        selectHospitalSection.setEnabled(type);
        selectMedicalDevice.setEnabled(type);
        selectSupplies.setEnabled(type);
        devLocation.setEnabled(type);
        sendButton.setEnabled(type);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.selectHospital):
                if (user.getRole().equalsIgnoreCase("admin") && hospitalsArrayList.size() > 0)
                    openDialogWithHospitals(hospitalsArrayList);
                break;

            case (R.id.selectSection):
                if ((user.getRole().equalsIgnoreCase("admin") || user.getRole().equalsIgnoreCase("inginer")) && hospitalSectionsArrayList.size() > 0)
                    openDialogWithHospitalSections(hospitalSectionsArrayList);
                break;

            case (R.id.selectMedicalDevice):
                if (!selectHospitalSection.getText().toString().isEmpty()) {
                    if (!selectHospitalSection.getText().toString().isEmpty() && medicalDevicesArrayList.size() > 0) {
                        openDialogWithMedicalDevices(medicalDevicesArrayList);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Nu exista niciun aparat in sectia  \"" + selectHospitalSection.getText().toString() + "\"", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Nu ati selectat o sectie", Toast.LENGTH_SHORT).show();
                }
                break;

            case (R.id.selectSupplies):
                openDialogWithSupplies(suppliesArrayList);
                break;

            case (R.id.sendButton):
                if (validation()) {
                    enabled(false);
                    html = new Html(getActivity(), getContext(), user, selectSupplies.getText().toString(), selectMedicalDevice.getText().toString(), selectHospital.getText().toString(), selectHospitalSection.getText().toString(), devLocation.getEditText().getText().toString(), "suppliesReport");
                    if (html.writeHTML()) {
                        if (user.getRole().equalsIgnoreCase("admin"))
                            selectHospital.setText(null);
                        if (user.getRole().equalsIgnoreCase("admin") || user.getRole().equalsIgnoreCase("inginer"))
                            selectHospitalSection.setText(null);

                        selectMedicalDevice.setText(null);
                        selectSupplies.setText(null);
                        devLocation.getEditText().setText(null);
                        enabled(true);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case (R.id.selectHospital):
                if (user.getRole().equalsIgnoreCase("admin")) {
                    selectMedicalDevice.setText(null);
                    selectHospital.setText(null);
                    selectHospitalSection.setText(null);
                    hospitalSectionsArrayList.clear();
                    medicalDevicesArrayList.clear();
                }
                break;

            case (R.id.selectSection):
                if (user.getRole().equalsIgnoreCase("admin") || user.getRole().equalsIgnoreCase("inginer")) {
                    selectHospitalSection.setText(null);
                    selectMedicalDevice.setText(null);
                    medicalDevicesArrayList.clear();
                }
                break;

            case (R.id.selectSupplies):
                selectSupplies.setText(null);
                break;
            case (R.id.selectMedicalDevice):
                selectMedicalDevice.setText(null);
                break;

        }
        return true;
    }
}
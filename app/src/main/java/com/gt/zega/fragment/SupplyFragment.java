package com.gt.zega.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.gt.zega.entity.Address;
import com.gt.zega.entity.Device;
import com.gt.zega.entity.Hospital;
import com.gt.zega.entity.Supply;
import com.gt.zega.entity.User;
import com.gt.zega.htmlToPdf.HtmlToPdf;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

import java.util.ArrayList;

public class SupplyFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private TextView selectSupplies;
    private TextView selectMedicalDevice;
    private TextView selectHospital;
    private TextView selectHospitalSection;
    private TextInputLayout devLocation;
    private Button sendButton;

    private Validations validations;

    private Dialog dialog;

    private ArrayList<Supply> suppliesArrayList;
    private ArrayList<Device> medicalDevicesArrayList;
    private ArrayList<String> hospitalSectionsArrayList;
    private ArrayList<Hospital> hospitalsArrayList;

    private User user;
    private String userKey;

    private DatabaseReference databaseRef;
    private FirebaseUser firebaseUser;
    private Address currentAddress;

    private HtmlToPdf htmlToPdf;

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

        getDevices();
        getHospitalFromDB();
        getSupplies();

        selectSupplies.setOnClickListener(this);
        selectMedicalDevice.setOnClickListener(this);
        selectHospital.setOnClickListener(this);
        selectHospitalSection.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        selectSupplies.setOnLongClickListener(this);
        selectMedicalDevice.setOnLongClickListener(this);
        selectHospital.setOnLongClickListener(this);
        selectHospitalSection.setOnLongClickListener(this);
        sendButton.setOnLongClickListener(this);

        return view;
    }

    private void getDevices() {
        medicalDevicesArrayList = new ArrayList<>();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("devices");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    Device device = objectSnapshot.getValue(Device.class);
                    medicalDevicesArrayList.add(device);
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

    private void getSupplies() {
        suppliesArrayList = new ArrayList<>();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("supplies");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    Supply supply = objectSnapshot.getValue(Supply.class);
                    suppliesArrayList.add(supply);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void openDialog(ArrayList<Supply> arrayListWithDevices) {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        ArrayAdapter<Supply> adapter = new ArrayAdapter<Supply>(getContext(), R.layout.list_item_layout_user_name, arrayListWithDevices) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout_user_name, parent, false);
                TextView nameAndCode = convertView.findViewById(R.id.text1);
                TextView brand = convertView.findViewById(R.id.text2);

                ImageView expandCollapseArrow = convertView.findViewById(R.id.arrow);
                expandCollapseArrow.setVisibility(View.GONE);

                nameAndCode.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                brand.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                nameAndCode.setText(arrayListWithDevices.get(position).getName().concat(", ").concat(arrayListWithDevices.get(position).getCode()));
                brand.setText(arrayListWithDevices.get(position).getBrand());


                return convertView;
            }
        };

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

        ArrayAdapter<Hospital> adapter = new ArrayAdapter<Hospital>(getContext(), R.layout.list_item_layout_user_name, arrayListWithDevices) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout_user_name, parent, false);
                TextView hospitalName = convertView.findViewById(R.id.text1);
                TextView hospitalAddress = convertView.findViewById(R.id.text2);

                ImageView expandCollapseArrow = convertView.findViewById(R.id.arrow);
                expandCollapseArrow.setVisibility(View.GONE);

                hospitalName.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                hospitalAddress.setTextColor(ContextCompat.getColor(getContext(), R.color.lightGray));

                hospitalName.setText(arrayListWithDevices.get(position).getHospitalName());
                hospitalAddress.setText(arrayListWithDevices.get(position).getHospitalAddress().toString());


                return convertView;
            }
        };

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
                selectHospital.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        selectHospitalSection.setText(null);

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                hospitalSectionsArrayList = new ArrayList<>();
                hospitalSectionsArrayList.addAll(adapter.getItem(position).getHospitalSections());

                currentAddress = adapter.getItem(position).getHospitalAddress();

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayListWithDevices) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                return view;
            }
        };

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

                selectHospitalSection.setText(adapter.getItem(position).toString());
                selectHospitalSection.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                dialog.dismiss();
            }
        });
    }

    private void openDialogWithMedicalDevices(ArrayList<Device> arrayListWithDevices) {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        ArrayAdapter<Device> adapter = new ArrayAdapter<Device>(getContext(), R.layout.list_item_layout_user_name, arrayListWithDevices) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout_user_name, parent, false);
                TextView tv = convertView.findViewById(R.id.text1);
                TextView description = convertView.findViewById(R.id.text2);

                ImageView expandCollapseArrow = convertView.findViewById(R.id.arrow);
                expandCollapseArrow.setVisibility(View.GONE);

                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                description.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                tv.setText(arrayListWithDevices.get(position).getDeviceName() + ", " + arrayListWithDevices.get(position).getDeviceCode());
                description.setText(arrayListWithDevices.get(position).getDeviceCompanyName());
                return convertView;
            }
        };

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

                selectMedicalDevice.setText(adapter.getItem(position).toString());
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.selectSupplies):
                openDialog(suppliesArrayList);
                break;
            case (R.id.selectMedicalDevice):
                openDialogWithMedicalDevices(medicalDevicesArrayList);
                break;
            case (R.id.selectHospital):
                openDialogWithHospitals(hospitalsArrayList);
                break;
            case (R.id.selectSection):
                if (hospitalSectionsArrayList.size() > 0)
                    openDialogWithHospitalSections(hospitalSectionsArrayList);
                break;
            case (R.id.sendButton):
                if (validation()) {
                    htmlToPdf = new HtmlToPdf(getActivity(), getContext(), user, selectSupplies.getText().toString(), selectMedicalDevice.getText().toString(), selectHospital.getText().toString(), currentAddress.toString(), selectHospitalSection.getText().toString(), devLocation.getEditText().getText().toString(), "suppliesReport");
                    if (htmlToPdf.writeHTML()) {
                        selectSupplies.setText(null);
                        selectMedicalDevice.setText(null);
                        selectHospital.setText(null);
                        selectHospitalSection.setText(null);
                        devLocation.getEditText().setText(null);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case (R.id.selectSupplies):
                selectSupplies.setText(null);
                break;
            case (R.id.selectMedicalDevice):
                selectMedicalDevice.setText(null);
                break;
            case (R.id.selectHospital):
                selectHospital.setText(null);
                hospitalSectionsArrayList.clear();
                break;
            case (R.id.selectSection):
                selectHospitalSection.setText(null);
                break;

        }
        return true;
    }
}
package com.gt.zega.fragment;

import android.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gt.zega.R;
import com.gt.zega.adapter.HospitalAdapter;
import com.gt.zega.entity.Address;
import com.gt.zega.entity.Hospital;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

import java.util.ArrayList;

public class NewHospitalSectionFragment extends Fragment {

    private TextView selectHospital;
    private TextView description;
    private Button hospitalSectionsButton;
    private TextInputLayout hospitalSection;
    private Button saveHospitalSectionButton;

    private Validations validations;

    private ArrayList<Hospital> hospitalArrayList;
    private ArrayList<String> hospitalSectionsArrayList;

    private DatabaseReference databaseRef;

    private Address currentAddress;
    private Hospital currentHospital;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_hospital_section, container, false);

        selectHospital = view.findViewById(R.id.selectHospitalTV);
        description = view.findViewById(R.id.descrip);
        hospitalSectionsButton = view.findViewById(R.id.hospitalSections);
        hospitalSection = view.findViewById(R.id.hospitalSectionName);
        saveHospitalSectionButton = view.findViewById(R.id.saveHospitalSection);

        validations = new ValidationsImpl();
        hospitalSectionsButton.setVisibility(View.GONE);
        description.setVisibility(View.GONE);

        databaseRef = FirebaseDatabase.getInstance().getReference().child("hospitals");

        getHospitalFromDB();

        selectHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogWithHospitals(hospitalArrayList);
            }
        });

        selectHospital.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                selectHospital.setText(null);
                return true;
            }
        });

        selectHospital.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!selectHospital.getText().toString().isEmpty()) {
                    hospitalSectionsButton.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                } else {
                    hospitalSectionsButton.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        hospitalSectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogWithHospitalSections(hospitalSectionsArrayList);
            }
        });

        saveHospitalSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    Address address = currentAddress;
                    ArrayList<String> sections = currentHospital.getHospitalSections() == null ? new ArrayList<>() : currentHospital.getHospitalSections();
                    if (!sections.stream().anyMatch(s -> s.equalsIgnoreCase(hospitalSection.getEditText().getText().toString().toUpperCase())))
                        sections.add(hospitalSection.getEditText().getText().toString().toUpperCase());
                    else {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Atentie")
                                .setMessage("Sectia " + hospitalSection.getEditText().getText().toString().toUpperCase() + " exista")

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                        return;
                    }
                    Hospital hospital = new Hospital(currentHospital.getHospitalName(), address, sections);
                    databaseRef.child(hospital.getHospitalName()).setValue(hospital).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                selectHospital.setText("");
                                hospitalSection.getEditText().setText("");
                                Toast.makeText(getActivity().getApplicationContext(), "Sectie adaugata cu succes", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Eroare la adaugare", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    private void openDialogWithHospitals(ArrayList<Hospital> arrayListWithDevices) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        HospitalAdapter hospitalAdapter = new HospitalAdapter(getContext(), arrayListWithDevices);

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
                selectHospital.setText(hospitalAdapter.getItem(position).getHospitalName());
                selectHospital.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                try {
                    hospitalSectionsArrayList = new ArrayList<>();
                    hospitalSectionsArrayList.addAll(hospitalAdapter.getItem(position).getHospitalSections());
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(), getString(R.string.without_sections, hospitalAdapter.getItem(position).getHospitalName()), Toast.LENGTH_SHORT).show();
                }

                currentAddress = hospitalAdapter.getItem(position).getHospitalAddress();
                currentHospital = hospitalAdapter.getItem(position);

                dialog.dismiss();
            }
        });
    }

    private void openDialogWithHospitalSections(ArrayList<String> arrayListOfHospitalSections) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, arrayListOfHospitalSections) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView hospitalSection = view.findViewById(android.R.id.text1);

                hospitalSection.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

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
    }

    public void getHospitalFromDB() {
        hospitalArrayList = new ArrayList<>();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    Hospital hospital = objectSnapshot.getValue(Hospital.class);
                    if (!hospitalArrayList.stream().anyMatch(f -> f.getHospitalName().equalsIgnoreCase(hospital.getHospitalName())))
                        hospitalArrayList.add(hospital);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean validation() {
        return validations.textViewValidation(selectHospital) & validations.textInputLayoutValidation(hospitalSection);
    }
}
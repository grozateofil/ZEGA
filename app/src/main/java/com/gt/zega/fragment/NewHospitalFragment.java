package com.gt.zega.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.gt.zega.entity.Address;
import com.gt.zega.entity.Hospital;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

import java.util.ArrayList;

public class NewHospitalFragment extends Fragment {

    private TextInputLayout hospitalName;
    private TextInputLayout city;
    private TextInputLayout street;
    private TextInputLayout number;
    private Button saveHospitalButton;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ArrayList<Hospital> hospitalArrayList;

    private Validations validations;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_hospital, container, false);

        hospitalName = view.findViewById(R.id.hospitalName);
        city = view.findViewById(R.id.city);
        street = view.findViewById(R.id.street);
        number = view.findViewById(R.id.number);
        saveHospitalButton = view.findViewById(R.id.saveHospital);

        validations = new ValidationsImpl();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("hospitals");

        getHospitalFromDB();

        saveHospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    Address address = new Address(city.getEditText().getText().toString(), street.getEditText().getText().toString(), number.getEditText().getText().toString());
                    ArrayList<String> hospitalSections = new ArrayList<>();
                    Hospital hospital = new Hospital(hospitalName.getEditText().getText().toString(), address, hospitalSections);

                    if (!hospitalArrayList.stream().anyMatch(f -> f.getHospitalName().equalsIgnoreCase(hospital.getHospitalName()))) {

                        databaseReference.child(hospitalName.getEditText().getText().toString()).setValue(hospital).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    hospitalName.getEditText().setText("");
                                    city.getEditText().setText("");
                                    street.getEditText().setText("");
                                    number.getEditText().setText("");
                                    Toast.makeText(getActivity().getApplicationContext(), "Spital adaugat cu succes", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "Eroare la adaugare", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Atentie")
                                .setMessage("Spitalul " + hospital.getHospitalName().toUpperCase() + " exista")
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                        return;
                    }
                }

            }
        });

        return view;
    }

    public void getHospitalFromDB() {
        hospitalArrayList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    Hospital hospital = objectSnapshot.getValue(Hospital.class);
                    if (!hospitalArrayList.stream().anyMatch(f -> f.getHospitalName().equals(hospital.getHospitalName())))
                        hospitalArrayList.add(hospital);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean validation() {
        return validations.textInputLayoutValidation(hospitalName) & validations.textInputLayoutValidation(city) & validations.textInputLayoutValidation(street) & validations.textInputLayoutValidation(number);
    }

}
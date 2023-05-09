package com.gt.zega.fragment;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gt.zega.R;
import com.gt.zega.entity.Device;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

public class NewMedicalDeviceFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout deviceCompany;
    private TextInputLayout deviceName;
    private TextInputLayout deviceCode;
    private Button addButton;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Validations validations;

    private Device device;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_medical_device, container, false);

        deviceCompany = view.findViewById(R.id.deviceCompany);
        deviceName = view.findViewById(R.id.deviceName);
        deviceCode = view.findViewById(R.id.deviceCode);
        addButton = view.findViewById(R.id.addNewMedicalDeviceButton);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("devices");

        validations = new ValidationsImpl();

        addButton.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case (R.id.addNewMedicalDeviceButton):
                if (validation()) {
                    device = new Device(deviceCompany.getEditText().getText().toString(), deviceName.getEditText().getText().toString(), deviceCode.getEditText().getText().toString());

                    databaseReference.child(deviceCode.getEditText().getText().toString()).setValue(device).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                deviceCompany.getEditText().setText("");
                                deviceName.getEditText().setText("");
                                deviceCode.getEditText().setText("");
                                Toast.makeText(getActivity().getApplicationContext(), "Aparat adaugat cu succes", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Eroare la adaugare", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }

    }

    private boolean validation() {
        return validations.textInputLayoutValidation(deviceCompany) & validations.textInputLayoutValidation(deviceName) & validations.textInputLayoutValidation(deviceCode);
    }
}
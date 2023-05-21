package com.gt.zega.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gt.zega.R;
import com.gt.zega.entity.FaultCode;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

public class NewFaultCodeFragment extends Fragment {

    private TextView searchFaultCode;
    private TextInputLayout faultCode;
    private TextInputLayout faultCodeDescription;
    private Button addButton;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Validations validations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_fault_code, container, false);

        searchFaultCode = view.findViewById(R.id.searchFaultCode);
        faultCode = view.findViewById(R.id.newFaultCode);
        faultCodeDescription = view.findViewById(R.id.faultCodeDescription);
        addButton = view.findViewById(R.id.addNewFaultCodeButton);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("faultCodes");

        validations = new ValidationsImpl();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {

                    FaultCode code = new FaultCode(faultCode.getEditText().getText().toString(), faultCodeDescription.getEditText().getText().toString());

                    databaseReference.child(faultCode.getEditText().getText().toString()).setValue(code).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                faultCode.getEditText().setText("");
                                faultCodeDescription.getEditText().setText("");

                                Toast.makeText(getActivity().getApplicationContext(), "Cod adaugat cu succes", Toast.LENGTH_SHORT).show();
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

    private boolean validation() {
        return validations.textInputLayoutValidation(faultCode);
    }
}
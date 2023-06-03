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
import com.gt.zega.entity.Supply;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;


public class NewSuppliesFragment extends Fragment {

    private TextInputLayout supplieName;
    private TextInputLayout supplieCode;
    private TextInputLayout supplieBrand;

    private Button sendButton;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Validations validations;

    private Supply supply;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_supplies, container, false);

        supplieName = view.findViewById(R.id.supplieName);
        supplieCode = view.findViewById(R.id.supplieCode);
        supplieBrand = view.findViewById(R.id.supplieBrand);

        sendButton = view.findViewById(R.id.sendButton);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("supplies");

        validations = new ValidationsImpl();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    supply = new Supply(supplieName.getEditText().getText().toString(), supplieCode.getEditText().getText().toString(), supplieBrand.getEditText().getText().toString());
                    databaseReference.child(supplieCode.getEditText().getText().toString()).setValue(supply).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                supplieCode.getEditText().setText("");
                                supplieName.getEditText().setText("");
                                supplieBrand.getEditText().setText("");
                                Toast.makeText(getActivity().getApplicationContext(), "Consumabil adaugat cu succes", Toast.LENGTH_SHORT).show();
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
        return validations.textInputLayoutValidation(supplieName) &
                validations.textInputLayoutValidation(supplieCode) &
                validations.textInputLayoutValidation(supplieBrand);
    }

}
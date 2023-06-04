package com.gt.zega.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.gt.zega.entity.FaultCode;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

import java.util.ArrayList;

public class NewFaultCodeFragment extends Fragment {

    private TextView searchFaultCode;
    private TextInputLayout faultCode;
    private TextInputLayout faultCodeDescription;
    private Button addButton;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Validations validations;

    private ArrayList<FaultCode> faultCodeArrayList;

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

        faultCodeArrayList = new ArrayList<>();

        getFaultCodes();

        searchFaultCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogWithFaultCode(faultCodeArrayList);
            }
        });

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

    private void getFaultCodes() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("faultCodes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    FaultCode device = objectSnapshot.getValue(FaultCode.class);
                    faultCodeArrayList.add(device);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openDialogWithFaultCode(ArrayList<FaultCode> arrayListOfHospitalSections) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        ArrayAdapter<FaultCode> adapter = new ArrayAdapter<FaultCode>(getContext(), R.layout.list_item_layout_user_name, arrayListOfHospitalSections) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout_user_name, parent, false);
                TextView code = convertView.findViewById(R.id.text1);
                TextView description = convertView.findViewById(R.id.text2);

                ImageView expandCollapseArrow = convertView.findViewById(R.id.arrow);
                expandCollapseArrow.setVisibility(View.GONE);

                code.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                description.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                code.setText(arrayListOfHospitalSections.get(position).getCode());
                description.setText(arrayListOfHospitalSections.get(position).getDescription());

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
    }

    private boolean validation() {
        return validations.textInputLayoutValidation(faultCode) & validations.textInputLayoutValidation(faultCodeDescription);
    }
}
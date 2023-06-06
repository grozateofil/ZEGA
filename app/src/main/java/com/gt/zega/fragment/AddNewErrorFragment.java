package com.gt.zega.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.gt.zega.adapter.FaultCodeAdapter;
import com.gt.zega.adapter.HospitalAdapter;
import com.gt.zega.adapter.HospitalSectionAdapter;
import com.gt.zega.entity.Address;
import com.gt.zega.entity.BrokenMedicalDevices;
import com.gt.zega.entity.Device;
import com.gt.zega.entity.FaultCode;
import com.gt.zega.entity.Hospital;
import com.gt.zega.entity.User;
import com.gt.zega.htmlToPdf.HtmlToPdf;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;


public class AddNewErrorFragment extends Fragment {

    private final int MAX_NUMBER_OF_PHOTOS = 3;
    private final ArrayList<Uri> listOfImages = new ArrayList<>();

    private TextView selectDevice;
    private TextView errorCode;
    private TextView errorCodeDescription;
    private TextView hospital;
    private TextView hospitalSection;
    private TextInputLayout sectionRoom;
    private TextInputLayout description;
    private ProgressBar progressBar;

    private Dialog dialog;
    private ArrayList<Device> deviceArrayList;
    private ArrayList<FaultCode> errorCodeArrayList;
    private ArrayList<Hospital> hospitalArrayList;

    private ArrayList<String> hospitalSectionsArrayList;
    private LinearLayout linearLayout;

    private ImageButton addPictureButton;
    private Button addButton;

    private DatabaseReference databaseReference;
    private DatabaseReference databaseRef;
    private FirebaseUser firebaseUser;
    private Validations validations;
    private User user;
    private String userKey;
    private HtmlToPdf htmlToPdf;

    private Address currentAddress;

    ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri uri = data.getData();

                if (uri != null) {
                    ImageView imageView = new ImageView(getActivity().getApplicationContext());
                    imageView.setImageURI(data.getData());
                    listOfImages.add(data.getData());

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getActivity().getApplicationContext(), "Pentru ștergere apăsați lung pe o imagine", Toast.LENGTH_SHORT).show();
                        }
                    });

                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            ViewGroup parentView = (ViewGroup) view.getParent();

                            listOfImages.remove(parentView.indexOfChild(view));
                            parentView.removeView(view);
                            return true;
                        }
                    });
                    adView(imageView, linearLayout.getWidth() / 3, linearLayout.getHeight());
                }
            }
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_error, container, false);

        selectDevice = view.findViewById(R.id.selectDevice);
        errorCode = view.findViewById(R.id.errorCode);
        errorCodeDescription = view.findViewById(R.id.description);
        hospital = view.findViewById(R.id.hospital);
        hospitalSection = view.findViewById(R.id.section);
        sectionRoom = view.findViewById(R.id.sectionRoom);
        description = view.findViewById(R.id.optionalDescription);

        linearLayout = view.findViewById(R.id.photosLinearLayout);
        addPictureButton = view.findViewById(R.id.addPicture);
        addButton = view.findViewById(R.id.addNewDeviceButton);
        progressBar = view.findViewById(R.id.progress_bar_create_broken_device_report);

        description.getEditText().setMovementMethod(new ScrollingMovementMethod());

        validations = new ValidationsImpl();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("devices");

        deviceArrayList = new ArrayList<>();
        errorCodeArrayList = new ArrayList<>();
        hospitalArrayList = new ArrayList<>();
        hospitalSectionsArrayList = new ArrayList<>();

        getDevices();
        getFaultCodes();
        getHospitalFromDB();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userKey = firebaseUser.getUid();
            getUserData(userKey);
        }

        selectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogWithMedicalDevices(deviceArrayList);
            }
        });

        selectDevice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                selectDevice.setText(null);
                return true;
            }
        });

        errorCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogWithErrorCodes(errorCodeArrayList);
            }
        });

        errorCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                errorCode.setText(null);
                errorCodeDescription.setText(null);
                return true;
            }
        });

        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogWithHospitals(hospitalArrayList);
            }
        });

        hospital.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                hospital.setText(null);
                hospitalSection.setText(null);
                hospitalSectionsArrayList.clear();
                return true;
            }
        });

        hospitalSection.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                hospitalSection.setText(null);
                return true;
            }
        });

        hospitalSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hospitalSectionsArrayList.size() > 0)
                    openDialogWithHospitalSections(hospitalSectionsArrayList);
            }
        });

        addPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayout.getChildCount() < MAX_NUMBER_OF_PHOTOS)
                    choosePhotoFromGallery();
                else
                    Toast.makeText(getActivity().getApplicationContext(), "Maxim " + MAX_NUMBER_OF_PHOTOS + " imagini", Toast.LENGTH_SHORT).show();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    enabled(false);
                    htmlToPdf = new HtmlToPdf(getActivity(), getContext(), user, selectDevice.getText().toString(), errorCode.getText().toString(), errorCodeDescription.getText().toString(), hospital.getText().toString(), currentAddress.toString(), hospitalSection.getText().toString(), sectionRoom.getEditText().getText().toString(), description.getEditText().getText().toString(), listOfImages, "brokenDeviceReport");
                    saveProblemForGraphFragment();
                    if (htmlToPdf.writeHTML()) {
                        selectDevice.setText(null);
                        errorCode.setText(null);
                        errorCodeDescription.setText(null);
                        hospital.setText(null);
                        hospitalSection.setText(null);
                        sectionRoom.getEditText().setText(null);
                        description.getEditText().setText(null);

                        linearLayout.removeAllViews();
                        listOfImages.clear();
                        enabled(true);

                    }
                }
            }
        });

        return view;
    }


    public void choosePhotoFromGallery() {
        Intent photo = new Intent();
        photo.setType("image/*");

        photo.setAction(Intent.ACTION_GET_CONTENT);
        pickImage.launch(photo);
    }

    private void adView(ImageView imageView, int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMarginEnd(10);
        imageView.setLayoutParams(params);

        linearLayout.addView(imageView);
    }

    private void getDevices() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    Device device = objectSnapshot.getValue(Device.class);
                    deviceArrayList.clear();
                    deviceArrayList.add(device);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFaultCodes() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("faultCodes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    FaultCode device = objectSnapshot.getValue(FaultCode.class);
                    errorCodeArrayList.add(device);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getHospitalFromDB() {
        hospitalArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("hospitals");

        databaseReference.addValueEventListener(new ValueEventListener() {
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

    private void getUserData(String userKey) {
        databaseReference.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void checkMonthExistence(DatabaseReference dayRef, BrokenMedicalDevices brokenMD) {
        dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Month exists, update data
                    addDeviceCode(dayRef, brokenMD.getDeviceCode());
                } else {
                    // Month does not exist, create it
                    dayRef.child("numberOfBrokenDevices").setValue(1);
                    dayRef.child("arrayListOfDevicesCodes").setValue(new ArrayList<String>(Collections.singletonList(brokenMD.getDeviceCode())));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    private void addDeviceCode(DatabaseReference monthRef, String deviceCode) {
        DatabaseReference deviceCodesRef = monthRef.child("arrayListOfDevicesCodes");
        deviceCodesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> deviceCodes = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot codeSnapshot : dataSnapshot.getChildren()) {
                        String code = codeSnapshot.getValue(String.class);
                        deviceCodes.add(code);
                    }
                }

                deviceCodes.add(deviceCode);

//                Actualizeaza valoarea nodului "arrayListOfDevicesCodes"
                deviceCodesRef.setValue(deviceCodes)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                            }
                        });

                getSizeOfArrayListOfDevicesCodes(deviceCodes, monthRef);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    private static void getSizeOfArrayListOfDevicesCodes(ArrayList<String> deviceCodes, DatabaseReference monthRef) {
        DatabaseReference numberOfBrokenDevicesRef = monthRef.child("numberOfBrokenDevices");
        numberOfBrokenDevicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long size = deviceCodes.size();
                    numberOfBrokenDevicesRef.setValue(size);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void openDialogWithErrorCodes(ArrayList<FaultCode> arrayListWithDevices) {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        FaultCodeAdapter faultCodeAdapter = new FaultCodeAdapter(getContext(), arrayListWithDevices);

        listView.setAdapter(faultCodeAdapter);

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
                faultCodeAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                errorCode.setText(faultCodeAdapter.getItem(position).getCode());
                errorCode.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                errorCodeDescription.setText(faultCodeAdapter.getItem(position).getDescription());

                dialog.dismiss();
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

                hospital.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                hospital.setText(hospitalAdapter.getItem(position).getHospitalName());
                hospital.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        hospitalSection.setText(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                try {
                    hospitalSectionsArrayList = new ArrayList<>();
                    hospitalSectionsArrayList.addAll(hospitalAdapter.getItem(position).getHospitalSections());
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(), getString(R.string.without_sections, hospitalAdapter.getItem(position).getHospitalName()), Toast.LENGTH_SHORT).show();
                }

                currentAddress = hospitalAdapter.getItem(position).getHospitalAddress();
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

        HospitalSectionAdapter sectionAdapter = new HospitalSectionAdapter(getContext(), arrayListWithDevices);
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

                hospitalSection.setText(sectionAdapter.getItem(position));
                hospitalSection.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

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

        DeviceAdapter deviceAdapter = new DeviceAdapter(getContext(), arrayListWithDevices);
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

                selectDevice.setText(deviceAdapter.getItem(position).toString());
                selectDevice.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                dialog.dismiss();
            }
        });
    }

    public void saveProblemForGraphFragment() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("brokenMedicalDevices");
        BrokenMedicalDevices brokenMedicalDevices = new BrokenMedicalDevices(errorCode.getText().toString(), String.valueOf(Year.now().getValue()), LocalDate.now().getMonth().name(), String.valueOf(LocalDate.now().getDayOfMonth()), selectDevice.getText().toString().split(",")[1].trim());

        DatabaseReference yearsRef = databaseReference.child("years");
        DatabaseReference yearRef = yearsRef.child(brokenMedicalDevices.getYear());

        DatabaseReference monthsRef = yearRef.child("months");
        DatabaseReference monthRef = monthsRef.child(brokenMedicalDevices.getMonth());

        DatabaseReference daysRef = monthRef.child("days");
        DatabaseReference dayRef = daysRef.child(brokenMedicalDevices.getDay());

        DatabaseReference errorsNameRef = dayRef.child("errorsName");
        DatabaseReference problemNameRef = errorsNameRef.child(brokenMedicalDevices.getProblemName());


        errorsNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    checkMonthExistence(problemNameRef, brokenMedicalDevices);

                } else {
                    errorsNameRef.setValue("").addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            checkMonthExistence(problemNameRef, brokenMedicalDevices);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private boolean validation() {
        return (validations.textViewValidation(selectDevice) &
                validations.textViewValidation(errorCode) &
                validations.textViewValidation(hospital) &
                validations.textViewValidation(hospitalSection) &
                validations.textInputLayoutValidation(sectionRoom));
    }

    public void enabled(boolean type) {
        selectDevice.setEnabled(type);
        errorCode.setEnabled(type);
        hospital.setEnabled(type);
        hospitalSection.setEnabled(type);
        sectionRoom.setEnabled(type);
        description.setEnabled(type);
        linearLayout.setEnabled(type);
        addPictureButton.setEnabled(type);
    }

}
package com.gt.zega.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gt.zega.R;
import com.gt.zega.entity.User;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    //    private ImageView profilePicture;
//    private Uri imageUri;
    private TextView role;
    private TextInputLayout firstname;
    private TextInputLayout lastname;
    private CountryCodePicker ccp;
    private TextInputLayout phoneNumber;
    private TextInputLayout emailAddress;

    private Button resetPassword;
    private ToggleButton editSaveButton;

    private String userKey;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private Validations validations;
    private User user;

    private boolean isEditMode = false;

    private ProgressBar progressBar;

    private ArrayList<String> superUsersList;
    private ArrayList<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        View view2 = inflater.inflate(R.layout.loading_dialog, container, false);
        progressBar = view2.findViewById(R.id.progress_bar);

        superUsersList = new ArrayList<>(Arrays.asList(getText(R.string.super_user).toString().split(",")));
        usersList = new ArrayList<>(Arrays.asList(getText(R.string.user).toString().split(",")));
//        profilePicture = view.findViewById(R.id.profilePicture);
        role = view.findViewById(R.id.role);
        firstname = view.findViewById(R.id.newFirstname);
        lastname = view.findViewById(R.id.newLastname);
        ccp = view.findViewById(R.id.newCcp);
        phoneNumber = view.findViewById(R.id.newPhoneNumber);
        emailAddress = view.findViewById(R.id.newEmail);

        resetPassword = view.findViewById(R.id.resetPasswordButton);
        editSaveButton = view.findViewById(R.id.saveChangesButton);

        editSaveButton.setTextOff("Edit");
        editSaveButton.setTextOn("Save");
        editSaveButton.setChecked(isEditMode);

        ccp.registerCarrierNumberEditText(phoneNumber.getEditText());

        firstname.setEnabled(isEditMode);
        lastname.setEnabled(isEditMode);
        phoneNumber.setEnabled(isEditMode);
        ccp.setEnabled(isEditMode);

        validations = new ValidationsImpl();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            userKey = firebaseUser.getUid();
            getUserData(userKey);
        }

        firstname.getEditText().addTextChangedListener(textWatcher);
        lastname.getEditText().addTextChangedListener(textWatcher);
        phoneNumber.getEditText().addTextChangedListener(textWatcher);

//        profilePicture.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
//        editSaveButton.setOnClickListener(this);
        editSaveButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isEditMode = b;

                if (isEditMode) {

                    firstname.setEnabled(true);
                    lastname.setEnabled(true);
                    phoneNumber.setEnabled(true);
                    ccp.setEnabled(true);
                    editSaveButton.setTextOn("Save");

                } else {

                    firstname.setEnabled(false);
                    lastname.setEnabled(false);
                    phoneNumber.setEnabled(false);
                    ccp.setEnabled(false);
                    editSaveButton.setTextOff("Edit");


                    updateData(firstname, lastname, phoneNumber, ccp, emailAddress);
                }
            }
        });

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case (R.id.profilePicture):
//                openGallery();
//                break;
            case (R.id.resetPasswordButton):
                resetPassword();
                break;
        }

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(firstname.getEditText().getText().toString()) ||
                    TextUtils.isEmpty(lastname.getEditText().getText().toString()) ||
                    TextUtils.isEmpty(phoneNumber.getEditText().getText().toString())) {
                editSaveButton.setEnabled(false);
            } else {
                editSaveButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            validation(firstname, lastname, phoneNumber, ccp);
        }
    };

//    public void openGallery() {
//        Intent photo = new Intent(Intent.ACTION_PICK);
//        photo.setType("image/*");
//        activityResultLaunch.launch(photo);
//    }
//
//    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Intent data = result.getData();
//                        if (data != null) {
//                            imageUri = data.getData();
//                            profilePicture.setImageURI(imageUri);
//                        } else {
//                            Toast.makeText(getActivity().getApplicationContext(), "Nicio imagine selectată", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                }
//            });

    private boolean validation(TextInputLayout firstname, TextInputLayout lastname, TextInputLayout phoneNumber, CountryCodePicker ccp) {
        return (validations.textInputLayoutValidation(firstname) &
                validations.textInputLayoutValidation(lastname) &
                validations.phoneNumberValidation(phoneNumber, ccp));
    }

    private void getUserData(String userKey) {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    String userRole = user.getRole();
                    if (superUsersList.contains(userRole) || userRole.equals(getText(R.string.admin).toString())) {
                        role.setText(userRole);
                        firstname.getEditText().setText(user.getFirstName());
                        lastname.getEditText().setText(user.getLastName());
                        ccp.setFullNumber(user.getPhoneNumber());
                        emailAddress.getEditText().setText(firebaseAuth.getCurrentUser().getEmail());
                    } else if (usersList.contains(userRole)) {
                        role.setText(userRole);
                        firstname.getEditText().setText(user.getFirstName());
                        lastname.getEditText().setText(user.getLastName());
                        ccp.setFullNumber(user.getPhoneNumber());
                        emailAddress.getEditText().setText(firebaseAuth.getCurrentUser().getEmail());
                    }

//                    Picasso.get().load(user.getImageUrl()).into(profilePicture);

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void resetPassword() {
        firebaseAuth.sendPasswordResetEmail(firebaseUser.getEmail())
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity().getApplicationContext(), "În căteva momente vei primi un email pentru resetarea parolei", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Eroare la trimitere", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void updateData(TextInputLayout firstname, TextInputLayout lastname, TextInputLayout phoneNumber, CountryCodePicker ccp, TextInputLayout email) {
        if (validation(firstname, lastname, phoneNumber, ccp)) {
            HashMap<String, Object> hashMap = new HashMap();
            if (!user.getFirstName().equals(firstname.getEditText().getText().toString())) {
                hashMap.put("firstName", firstname.getEditText().getText().toString());
            }
            if (!user.getLastName().equals(lastname.getEditText().getText().toString())) {
                hashMap.put("lastName", lastname.getEditText().getText().toString());
            }
            if (!user.getPhoneNumber().equals(ccp.getFullNumberWithPlus())) {
                hashMap.put("phoneNumber", ccp.getFullNumberWithPlus());
            }
//            if (!emailAddress.getEditText().getText().toString().equals(firebaseAuth.getCurrentUser().getEmail())) {
//                changedElementsList.add(email.getHint().toString());
////                hashMap.put("phoneNumber", ccp.getFullNumberWithPlus());
//            }


            if (hashMap.size() > 0) {
                databaseReference.child(userKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

//                        saveProfilePicture();
                            if (hashMap.size() == 1) {
                                Toast.makeText(getActivity().getApplicationContext(), getCollect(hashMap) + " actualizat cu succes", Toast.LENGTH_SHORT).show();
                            } else if (hashMap.size() == 2) {
                                Toast.makeText(getActivity().getApplicationContext(), getCollect(hashMap) + " actualizate cu succes", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Date actualizate cu succes", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Actualizarea datelor a eșuat", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
//            if (hashMap.containsKey("Email")) {
//                firebaseUser.updateEmail(email.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(getActivity().getApplicationContext(), "Email actualizat cu succes", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getActivity().getApplicationContext(), "Date actualizate cu succes - nu", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });
//            }

        }


    }

    @NonNull
    private String getCollect(HashMap<String, Object> hashMap) {
        return hashMap.keySet().stream().map(Object::toString)
                .collect(Collectors.joining(", "));
    }


//    private void saveProfilePicture() {
//        firebaseStorage = FirebaseStorage.getInstance();
//        storageReference = firebaseStorage.getReference("usersProfilePictures/"+firebaseUser.getEmail()+"_profile_picture."+getPhotoExtension(imageUri));
//        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//            }
//        });
//    }

//    private String getPhotoExtension(Uri uri) {
//        ContentResolver contentResolver = getContext().getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }

}
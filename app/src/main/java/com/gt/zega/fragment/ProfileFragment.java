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
import java.util.HashMap;
import java.util.Locale;


public class ProfileFragment extends Fragment implements View.OnClickListener {

//    private ImageView profilePicture;
//    private Uri imageUri;

    private TextInputLayout firstname;
    private TextInputLayout lastname;
    private CountryCodePicker ccp;
    private TextInputLayout phoneNumber;
    private TextInputLayout emailAddress;

    private Button resetPassword;
    private Button save;

    private String userKey;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private Validations validations;
    private User user;
    private ArrayList<String> changedElementsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

//        profilePicture = view.findViewById(R.id.profilePicture);

        firstname = view.findViewById(R.id.newFirstname);
        lastname = view.findViewById(R.id.newLastname);
        ccp = view.findViewById(R.id.newCcp);
        phoneNumber = view.findViewById(R.id.newPhoneNumber);
        emailAddress = view.findViewById(R.id.newEmail);

        resetPassword = view.findViewById(R.id.resetPasswordButton);
        save = view.findViewById(R.id.saveChangesButton);

        ccp.registerCarrierNumberEditText(phoneNumber.getEditText());

        validations = new ValidationsImpl();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            userKey = firebaseUser.getUid();
            getUserData(userKey);
        }

//        profilePicture.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        save.setOnClickListener(this);

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
            case (R.id.saveChangesButton):
                updateData(firstname, lastname, phoneNumber, ccp, emailAddress);
                break;
        }

    }

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

    private boolean validation(TextInputLayout firstname, TextInputLayout lastname, TextInputLayout phoneNumber, CountryCodePicker ccp, TextInputLayout email) {
        return (validations.textInputLayoutValidation(firstname) &
                validations.textInputLayoutValidation(lastname) &
                validations.phoneNumberValidation(phoneNumber, ccp) &
                validations.emailValidation(email));
    }

    private void getUserData(String userKey) {
        databaseReference.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
//                    Picasso.get().load(user.getImageUrl()).into(profilePicture);
                    firstname.getEditText().setText(user.getFirstName());
                    lastname.getEditText().setText(user.getLastName());
                    ccp.setFullNumber(user.getPhoneNumber());
                    emailAddress.getEditText().setText(firebaseAuth.getCurrentUser().getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        if (validation(firstname, lastname, phoneNumber, ccp, email)) {
            HashMap<String, Object> hashMap = new HashMap();
            changedElementsList = new ArrayList<>();
            if (!user.getFirstName().equals(firstname.getEditText().getText().toString())) {
                changedElementsList.add(firstname.getHint().toString());
                hashMap.put("firstName", firstname.getEditText().getText().toString());
            }
            if (!user.getLastName().equals(lastname.getEditText().getText().toString())) {
                changedElementsList.add(lastname.getHint().toString());
                hashMap.put("lastName", lastname.getEditText().getText().toString());
            }
            if (!user.getPhoneNumber().equals(ccp.getFullNumberWithPlus())) {
                changedElementsList.add(phoneNumber.getHint().toString());
                hashMap.put("phoneNumber", ccp.getFullNumberWithPlus());
            }
            if (!emailAddress.getEditText().getText().toString().equals(firebaseAuth.getCurrentUser().getEmail())) {
                changedElementsList.add(email.getHint().toString());
//                hashMap.put("phoneNumber", ccp.getFullNumberWithPlus());
            }


            if (hashMap.size() > 0) {
                databaseReference.child(userKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

//                        saveProfilePicture();
                            if (changedElementsList.size() == 1) {
                                Toast.makeText(getActivity().getApplicationContext(), changedElementsList.get(0) + " actualizat cu succes", Toast.LENGTH_SHORT).show();
                            } else if (changedElementsList.size() == 2) {
                                Toast.makeText(getActivity().getApplicationContext(), changedElementsList.get(0) + ", " + changedElementsList.get(1).toLowerCase(Locale.ROOT) + " actualizate cu succes", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Date actualizate cu succes", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Actualizarea datelor a eșuat", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
            if (changedElementsList.contains("Email")) {
                firebaseUser.updateEmail(email.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity().getApplicationContext(), "Email actualizat cu succes", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Date actualizate cu succes - nu", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

        }


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
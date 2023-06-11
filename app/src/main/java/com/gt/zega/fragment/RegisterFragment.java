package com.gt.zega.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
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
import com.gt.zega.adapter.HospitalAdapter;
import com.gt.zega.adapter.HospitalSectionAdapter;
import com.gt.zega.entity.Hospital;
import com.gt.zega.entity.User;
import com.gt.zega.entity.UserAccount;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class RegisterFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

//    private ImageView imageView;
//    private Uri imageUri;

    private TextInputLayout firstname;
    private TextInputLayout lastname;
    private CountryCodePicker ccp;
    private TextInputLayout phoneNumber;
    private TextInputLayout email;
    private TextInputLayout password;
    private TextView role;
    private TextView hospital;
    private TextView hospitalSection;
    private CheckBox checkBox;
    private TextView terms;
    private Button createButton;
    private Button backToLoginButton;
    private ProgressBar progressBar;

    private FirebaseAuth fAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference dbReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private Validations validations;
    private String userUid;
    private User user;
    private Hospital selectedHospital;

    private ArrayList<Hospital> hospitalArrayList;
    private ArrayList<String> hospitalSectionsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

//        imageView = view.findViewById(R.id.selectPhotoFromGalery);

        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        ccp = view.findViewById(R.id.ccpRegistration);
        phoneNumber = view.findViewById(R.id.phoneNumberRegistration);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        role = view.findViewById(R.id.role);
        hospital = view.findViewById(R.id.hsp);
        hospitalSection = view.findViewById(R.id.sct);
        terms = view.findViewById(R.id.terms);
        checkBox = view.findViewById(R.id.checkboxTerms);
        createButton = view.findViewById(R.id.createButton);
        backToLoginButton = view.findViewById(R.id.backToLoginButton);
        progressBar = view.findViewById(R.id.progress_bar2);

        ccp.registerCarrierNumberEditText(phoneNumber.getEditText());
        ccp.setCustomMasterCountries(getText(R.string.europeanCountries).toString());
        ccp.setDialogEventsListener(dialogEventsListener());

        terms.setPaintFlags(terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        fAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        dbReference = FirebaseDatabase.getInstance().getReference("hospitals");
        firebaseStorage = FirebaseStorage.getInstance();

        hospitalArrayList = new ArrayList<>();
        validations = new ValidationsImpl();
        hospitalSectionsArrayList = new ArrayList<>();
        getHospitalFromDB();

        hospital.setOnLongClickListener(this);
        hospitalSection.setOnLongClickListener(this);

        role.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hospital.setText(null);
                hospitalSection.setText(null);
                hospitalSectionsArrayList.clear();
                if (!role.getText().toString().isEmpty()) {
                    hospital.setVisibility(View.VISIBLE);
                } else {
                    hospital.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        hospital.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hospitalSection.setText(null);
                hospitalSection.setVisibility(View.GONE);
                hospitalSectionsArrayList.clear();
                if (!hospital.getText().toString().isEmpty() && !role.getText().toString().equalsIgnoreCase("inginer")) {
                    hospitalSection.setVisibility(View.VISIBLE);
                } else {
                    hospitalSection.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


//        storageReference = firebaseStorage.getReference("usersProfilePictures");


        firstname.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());
        lastname.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());
        phoneNumber.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());
        email.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());
        password.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());


//        imageView.setOnClickListener(this);
        createButton.setOnClickListener(this);
        backToLoginButton.setOnClickListener(this);
        role.setOnClickListener(this);
        hospital.setOnClickListener(this);
        hospitalSection.setOnClickListener(this);
        terms.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

//            case (R.id.selectPhotoFromGalery):
//                openGallery();
//                break;
            case (R.id.role):
                openDialogWithUserRoles();
                break;

            case (R.id.hsp):
                if (hospitalArrayList.size() > 0)
                    openDialogWithHospitals(hospitalArrayList);
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Nu s-a incarcat lista cu spitale", Toast.LENGTH_SHORT).show();
                }

                break;

            case (R.id.sct):
                if (hospitalSectionsArrayList.size() > 0)
                    openDialogWithHospitalSections(hospitalSectionsArrayList);
                break;

            case (R.id.terms):
                openDialogWithTerms();
                break;

            case (R.id.createButton):
                if (validation())
//                    if (imageView.getDrawable() != null) {
                    firebaseRegistration();
//                    } else {
//                        Toast.makeText(getActivity().getApplicationContext(), "Nicio imagine selectata pt profil", Toast.LENGTH_SHORT).show();
//                    }
                break;
            case (R.id.backToLoginButton):
                getActivity().onBackPressed();
                break;

        }
    }

//    public void openGallery() {
//        Intent photo = new Intent(Intent.ACTION_PICK);
//        photo.setType("image/*");
//        activityResultLaunch.launch(photo);
//    }

//    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//        @Override
//        public void onActivityResult(ActivityResult result) {
//            if (result.getResultCode() == Activity.RESULT_OK) {
//                Intent data = result.getData();
//                if (data != null) {
//                    imageUri = data.getData();
//                    imageView.setImageURI(imageUri);
//                } else {
//                    Toast.makeText(getActivity().getApplicationContext(), "Nicio imagine selectata", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        }
//    });


    private CountryCodePicker.DialogEventsListener dialogEventsListener() {
        return new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialog) {
                TextView title = dialog.findViewById(com.hbb20.R.id.textView_title);
                title.setText(R.string.selectACountry);

                EditText search = dialog.findViewById(com.hbb20.R.id.editText_search);
                search.setHint(R.string.search);
            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {
            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {
            }
        };
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
                Toast.makeText(getActivity().getApplicationContext(), "Eroare la înregistrare", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validation() {
        return (validations.textInputLayoutValidation(firstname) &
                validations.textInputLayoutValidation(lastname) &
                validations.phoneNumberValidation(phoneNumber, ccp) &
                validations.emailValidation(email) &
                validations.createPassword(password) &
                validations.textViewValidation(role) &
                validations.textViewValidation(hospital) &
                validations.checkBoxValidation(checkBox, getContext()));
    }

    private void firebaseRegistration() {
        String firstName = firstname.getEditText().getText().toString().replaceFirst("\\s++$", "").replaceFirst("^\\s*", "");
        String lastName = lastname.getEditText().getText().toString().replaceFirst("\\s++$", "").replaceFirst("^\\s*", "");
        String phoneNumber = ccp.getFullNumberWithPlus();
        String emailAddress = email.getEditText().getText().toString();
        String pass = password.getEditText().getText().toString();
        String userHospital = hospital.getText().toString();
        ArrayList<String> userSections = new ArrayList<>(Arrays.asList(hospitalSection.getText().toString()));
        String role = this.role.getText().toString();
        boolean blockedAccount = false;


        UserAccount userAccount = new UserAccount(emailAddress, pass);
        user = new User(firstName, lastName, phoneNumber, userHospital, userSections, role, blockedAccount);
//        user = new User(firstName, lastName, phoneNumber, selectedHospital, role, blockedAccount);


//        storageReference = firebaseStorage.getReference("usersProfilePictures/" + emailAddress + "_profile_picture." + getPhotoExtension(imageUri));
//        storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if (task.isSuccessful()) {
//                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            String imageUrl = uri.toString();
//                            user = new User(firstName, lastName, phoneNumber, imageUrl);
        progressBar.setVisibility(View.VISIBLE);
        enabled(false);
        fAuth.createUserWithEmailAndPassword(userAccount.getEmail(), userAccount.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    email.setError(null);
                    email.setErrorEnabled(false);

                    FirebaseUser firebaseUser = fAuth.getCurrentUser();
                    userUid = firebaseUser.getUid();
                    databaseReference.child(userUid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

//                                fAuth.setLanguageCode("ro");
//                                firebaseUser.sendEmailVerification();
//                                Toast.makeText(getActivity().getApplicationContext(), "Ți-a fost trimis un email pentru a valida adresa de email", Toast.LENGTH_LONG).show();

                                getActivity().onBackPressed();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Eroare la înregistrare", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    enabled(true);
                    email.setError("Există deja un cont asociat acestei adrese de email");
                }
            }
        });
//                        }
//                    });
//                }
//            }
//        });


    }

    private void openDialogWithUserRoles() {
        ArrayList<String> roles = new ArrayList<>();
        roles.addAll(Arrays.asList(getText(R.string.super_user).toString().split(",")));
        roles.addAll(Arrays.asList(getText(R.string.user).toString().split(",")));
        Collections.sort(roles);

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(900, 600);
        dialog.show();

        EditText searchEditText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ImageButton closeFragButton = dialog.findViewById(R.id.closeFrag);

        searchEditText.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, roles) {
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
        listView.setScrollBarFadeDuration(0);

        closeFragButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                role.setText(adapter.getItem(position));
                role.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                dialog.dismiss();
            }
        });
    }

    private void openDialogWithHospitals(ArrayList<Hospital> arrayListWithHospitals) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.devices_list_view);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                hospital.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));
                hospital.setText(hospitalAdapter.getItem(position).getHospitalName());
                selectedHospital = hospitalAdapter.getItem(position);
                hospital.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                        hospitalSection.setText(null);
                        if (!hospital.getText().toString().isEmpty() && !role.getText().toString().equalsIgnoreCase("inginer")) {
                            hospitalSection.setVisibility(View.VISIBLE);
                        } else {
                            hospitalSection.setVisibility(View.GONE);
                        }
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

                dialog.dismiss();
            }
        });
    }

    private void openDialogWithHospitalSections(ArrayList<String> arrayListWithDevices) {
        Dialog dialog = new Dialog(getContext());
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

    private void openDialogWithTerms() {
        new AlertDialog.Builder(getContext())
                .setTitle("Termeni și condiții")
                .setMessage("Ești de acord cu acest termen?")
                .setPositiveButton("De acord", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkBox.setChecked(true);
                    }
                }).setNegativeButton("Nu sunt de acord", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                        }
                    }
                })
                .show();
    }


//    private String getPhotoExtension(Uri uri) {
//        ContentResolver contentResolver = getContext().getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }

    @NonNull
    private ActionMode.Callback getActionModeCallback() {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        };
    }

    public void enabled(boolean type) {
        firstname.setEnabled(type);
        lastname.setEnabled(type);
        phoneNumber.setEnabled(type);
        ccp.setEnabled(type);
        email.setEnabled(type);
        password.setEnabled(type);
        role.setEnabled(type);
        hospital.setEnabled(type);
        hospitalSection.setEnabled(type);
        checkBox.setEnabled(type);
        terms.setEnabled(type);
        createButton.setEnabled(type);
        backToLoginButton.setEnabled(type);
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case (R.id.role):

                break;

            case (R.id.hsp):
                hospitalSectionsArrayList.clear();
                hospital.setText(null);
                hospitalSection.setText(null);

                break;

            case (R.id.sct):
                hospitalSection.setText(null);
                break;
        }
        return false;
    }
}


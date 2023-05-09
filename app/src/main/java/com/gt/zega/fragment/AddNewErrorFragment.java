package com.gt.zega.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.gt.zega.database.HideAndShow;
import com.gt.zega.entity.Device;
import com.gt.zega.entity.User;
import com.gt.zega.htmlToPdf.HtmlToPdf;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;

import java.io.File;
import java.util.ArrayList;


public class AddNewErrorFragment extends Fragment implements HideAndShow, LoginFragment.OnUserRoleSelectedListener {

    private int MAX_NUMBER_OF_PHOTOS = 3;
    private File photoFile;

    private TextView selectDevice;
    private TextInputLayout description;
    private TextInputLayout deviceLocation;
    private TextInputLayout hospital;
    private Dialog dialog;

    private ArrayList<Device> deviceArrayList;

    private LinearLayout linearLayout;
    private ImageButton addPictureButton;
    private Button addButton;
    private Uri imageUri;
    private ArrayList<Uri> listOfImages = new ArrayList<>();

    private DatabaseReference databaseReference;
    private DatabaseReference databaseRef;
    private FirebaseUser firebaseUser;

    private Validations validations;

    private User user;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String userKey;

    private HtmlToPdf htmlToPdf;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_error, container, false);

        selectDevice = view.findViewById(R.id.selectDevice);
        description = view.findViewById(R.id.errorDescription);
        deviceLocation = view.findViewById(R.id.deviceLocation);
        hospital = view.findViewById(R.id.hospital);

        linearLayout = view.findViewById(R.id.photosLinearLayout);
        addPictureButton = view.findViewById(R.id.addPicture);
        addButton = view.findViewById(R.id.addNewDeviceButton);

        description.getEditText().setMovementMethod(new ScrollingMovementMethod());

        validations = new ValidationsImpl();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("devices");

        deviceArrayList = new ArrayList<>();

        getDevices();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userKey = firebaseUser.getUid();
            getUserData(userKey);
        }

        selectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.devices_list_view);

                // set custom height and width
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 1500);
                dialog.show();

                // Initialize and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                ArrayAdapter<Device> adapter = new ArrayAdapter<Device>(getContext(), android.R.layout.simple_list_item_1, deviceArrayList) {
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
                editText.addTextChangedListener(new TextWatcher() {
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

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        selectDevice.setText(adapter.getItem(position).toString());
                        selectDevice.setTextColor(ContextCompat.getColor(getContext(), R.color.black_russian));

                        dialog.dismiss();
                    }
                });
            }
        });

        description.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    description.getEditText().setHintTextColor(ContextCompat.getColor(getContext(), R.color.lightGray));
                    description.getEditText().setHint("Cât mai multe detalii");
                } else {
                    description.getEditText().setHint("");
                }
            }
        });

        deviceLocation.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    deviceLocation.getEditText().setHintTextColor(ContextCompat.getColor(getContext(), R.color.lightGray));
                    deviceLocation.getEditText().setHint("Etaj, salon, etc.");
                } else {
                    deviceLocation.getEditText().setHint("");
                }
            }
        });

        hospital.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    hospital.getEditText().setHintTextColor(ContextCompat.getColor(getContext(), R.color.lightGray));
                    hospital.getEditText().setHint("Numele spitalului/clinicii");
                } else {
                    hospital.getEditText().setHint("");
                }
            }
        });

        addPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayout.getChildCount() < MAX_NUMBER_OF_PHOTOS)
//                    showPictureDialog();
                    choosePhotoFromGallary();
                else
                    Toast.makeText(getActivity().getApplicationContext(), "Maxim " + MAX_NUMBER_OF_PHOTOS + " imagini", Toast.LENGTH_SHORT).show();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {

                    htmlToPdf = new HtmlToPdf(getActivity(), getContext(), user, selectDevice.getText().toString(), description.getEditText().getText().toString(), hospital.getEditText().getText().toString(), deviceLocation.getEditText().getText().toString(), listOfImages);

                    if (htmlToPdf.writeHTML()) {
                        selectDevice.setText(null);
                        description.getEditText().setText(null);
                        deviceLocation.getEditText().setText(null);
                        hospital.getEditText().setText(null);
                        linearLayout.removeAllViews();
                        listOfImages.clear();
                    }
                }
            }
        });

        return view;
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Selectați imagine din galerie", "Deschide-ți camera"};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        choosePhotoFromGallary();
                        break;
                    case 1:

                        takePhotoFromCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent photo = new Intent();
        photo.setType("image/*");

        photo.setAction(Intent.ACTION_GET_CONTENT);
        pickImage.launch(photo);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "dir/pic.jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        takePicture.launch(imageUri);
    }

    ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri uri = data.getData();

//                if (data.getClipData() != null) {
//                    int count = data.getClipData().getItemCount();
//
//                    for (int i = 0; i < count; i++) {
//                        listOfImages.add(data.getClipData().getItemAt(i).getUri());
//                    }
//
//                    for (Uri uri : listOfImages) {
//                        ImageView imageView = new ImageView(getActivity().getApplicationContext());
//                        imageView.setImageURI(uri);
//                        addvieW(imageView, 130, linearLayout.getHeight());
//                        System.out.println("-----------------------------------------------------" + linearLayout.getHeight());
//                    }
//
//                } else
                if (uri != null) {
                    ImageView imageView = new ImageView(getActivity().getApplicationContext());
                    imageView.setImageURI(data.getData());
                    listOfImages.add(data.getData());
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            ViewGroup parentView = (ViewGroup) view.getParent();

                            listOfImages.remove(parentView.indexOfChild(view));
                            parentView.removeView(view);
                            return true;
                        }
                    });
//                    setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            ViewGroup parentView = (ViewGroup) view.getParent();
//                            parentView.removeView(view);
//                        }
//                    });
                    addview(imageView, linearLayout.getWidth() / 3, linearLayout.getHeight());
                }
            }
        }
    });

    ActivityResultLauncher<Uri> takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
        if (success) {
            ImageView imageView = new ImageView(getActivity().getApplicationContext());
            imageView.setImageURI(imageUri);
            addview(imageView, linearLayout.getWidth() / 3, linearLayout.getHeight());

        }

    });


    private void addview(ImageView imageView, int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMarginEnd(10);
        imageView.setLayoutParams(params);

        linearLayout.addView(imageView);
    }

    private boolean validation() {
        return (validations.textInputLayoutValidation(description) &
                validations.textInputLayoutValidation(hospital) &
                validations.textInputLayoutValidation(deviceLocation) &
                validations.textViewValidation(selectDevice));
    }

    private void getUserData(String userKey) {
        databaseReference.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    firstname = user.getFirstName();
                    lastname = user.getLastName();
                    phoneNumber = user.getPhoneNumber();
//                    hideTextView(getView().findViewById(R.id.titleNewError),user.getRole().equals("admin"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getDevices() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objectSnapshot : snapshot.getChildren()) {
                    Device device = objectSnapshot.getValue(Device.class);
                    deviceArrayList.add(device);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void hideTextView(TextView textView, boolean isAdmin) {
        if (!isAdmin) {
            textView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserRoleSelected(String userRole) {
        this.userRole = userRole;

    }
}
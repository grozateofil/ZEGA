package com.gt.zega.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.gt.zega.R;

import java.util.ArrayList;
import java.util.Arrays;


public class AddNewErrorFragment extends Fragment {

    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextView selectDevice;
    private TextInputLayout description;
    private TextInputLayout deviceLocation;
    private ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("Aparat 1", "Aparat 2", "Aparat 3", "Aparat 4", "Aparat 5", "Aparat 6", "Aparat 7", "Aparat 8", "Aparat 9", "Aparat 10"));
    private Dialog dialog;

    private LinearLayout linearLayout;
    private ImageButton takePictureButton;
    private Button addButton;
    private int GALLERY = 1, CAMERA = 2;
    private Uri imageUri;
    private ArrayList<Uri> listOfImages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_error, container, false);

//        firstName = view.findViewById(R.id.currentUserFirstname);
//        lastName = view.findViewById(R.id.currentUserLastname);
        selectDevice = view.findViewById(R.id.selectDevice);
        description = view.findViewById(R.id.errorDescription);
        deviceLocation = view.findViewById(R.id.deviceLocation);
        linearLayout = view.findViewById(R.id.photosLinearLayout);
        takePictureButton = view.findViewById(R.id.takePicture);

        description.getEditText().setMovementMethod(new ScrollingMovementMethod());

        selectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_searchable_spinner);

                // set custom height and width
                dialog.getWindow().setLayout(950, 1000);
                dialog.show();

                // Initialize and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList) {
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
                        selectDevice.setText(adapter.getItem(position));
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

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayout.getChildCount() < 4)
//                    showPictureDialog();
                    choosePhotoFromGallary();
                else
                    Toast.makeText(getActivity().getApplicationContext(), "Numarul maxim de poze a fost atins", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Selectați imagine din galerie",
                "Deschide-ți camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
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
        activityResultLaunch.launch(photo);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLaunch.launch(intent);
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
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
                if (data.getData() != null) {
                    ImageView imageView = new ImageView(getActivity().getApplicationContext());
                    imageView.setImageURI(data.getData());
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            ViewGroup parentView = (ViewGroup) view.getParent();
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
                    addview(imageView, 150, linearLayout.getHeight());
                }
            }
        }
    });

    private void addview(ImageView imageView, int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMarginEnd(10);
        imageView.setLayoutParams(params);

        linearLayout.addView(imageView);
    }
}
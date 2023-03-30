package com.gt.zega.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.gt.zega.R;

public class HomeFragment extends Fragment implements View.OnClickListener {

//    private ImageView imageView;
//    private Button button;
//    private Button addFilter1Button;
//    private CheckBox blurCheckBox;
//    private CheckBox sepiaCheckBox;
//    private Uri imageUri;
//
//    private EditText brightness;
//    private EditText contrast;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

//        imageView = view.findViewById(R.id.selectedPhotoFromGalery);
//        button = view.findViewById(R.id.addImageButton);
//        addFilter1Button = view.findViewById(R.id.applyFilterButton);
//        blurCheckBox = view.findViewById(R.id.blurCheckBox);
//        sepiaCheckBox = view.findViewById(R.id.sepiaCheckBox);
//
//        brightness = view.findViewById(R.id.brightnessEditText);
//        contrast = view.findViewById(R.id.contrastEditText);
//
//        button.setOnClickListener(this);
//        addFilter1Button.setOnClickListener(this);
//        blurCheckBox.setOnClickListener(this);
//        sepiaCheckBox.setOnClickListener(this);

        return view;

    }

//    public void openGallery() {
//        Intent photo = new Intent(Intent.ACTION_PICK);
//        photo.setType("image/*");
//        activityResultLaunch.launch(photo);
//    }
//
//
//    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//        @Override
//        public void onActivityResult(ActivityResult result) {
//            if (result.getResultCode() == Activity.RESULT_OK) {
//                Intent data = result.getData();
//                if (data != null) {
//                    imageUri = data.getData();
//                    imageView.setImageURI(imageUri);
//                }
//            }
//        }
//    });

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case (R.id.addImageButton):
//                openGallery();
//                break;
//            case (R.id.applyFilterButton):
//                if (!brightness.getText().toString().equals("") && !contrast.getText().toString().equals("")) {
//                    if (blurCheckBox.isChecked()) {
//
//                    }
//                }
//                break;
//            case (R.id.blurCheckBox):
//                blurTransformation = new BlurTransformation(getActivity());
//                if (blurCheckBox.isChecked())
//                    Picasso.get().load(imageUri).transform(blurTransformation).into(imageView);
//                else
//                    Picasso.get().load(imageUri).into(imageView);
//                break;
//
//            case (R.id.sepiaCheckBox):
//
//                sepiaFilterTransformation = new SepiaFilterTransformation(getActivity().getApplicationContext());
//                if (sepiaCheckBox.isChecked())
//                    Picasso.get().load(imageUri).transform(sepiaFilterTransformation).into(imageView);
//                else
//                    Picasso.get().load(imageUri).into(imageView);
//                break;
        }

    }
}
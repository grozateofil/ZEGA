package com.gt.zega.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.gt.zega.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.ContrastFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.SepiaFilterTransformation;

public class EditPhotoFragment extends Fragment implements View.OnClickListener {

    private ImageView imageView;
    private Button addImageButton;

    private Uri imageUri;

    private SeekBar contrastSeekBar;
    private SeekBar brightnessSeekBar;

    private Button sepiaButton;
    private Button grayButton;
    private Button invertButton;
    private Button blurButton;
    private Button savePhotoButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_photo, container, false);

        imageView = view.findViewById(R.id.selectedPhotoFromGalery);
        addImageButton = view.findViewById(R.id.addImageButton);


        brightnessSeekBar = view.findViewById(R.id.brightnessBar);
        contrastSeekBar = view.findViewById(R.id.contrastBar);

        sepiaButton = view.findViewById(R.id.sepiaFilter);
        grayButton = view.findViewById(R.id.grayFilter);
        invertButton = view.findViewById(R.id.invertFilter);
        blurButton = view.findViewById(R.id.blurFilter);
        savePhotoButton = view.findViewById(R.id.saveImage);

        addImageButton.setOnClickListener(this);

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float brightnessValue = (float) progress / 100;

                Picasso.get().load(imageUri)
                        .transform(new BrightnessFilterTransformation(getContext(), brightnessValue)).into(imageView);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float contrast = (progress + 10) / 100f;
                Picasso.get().load(imageUri)
                        .transform(new ContrastFilterTransformation(getContext(), contrast)).into(imageView);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sepiaButton.setOnClickListener(this);
        grayButton.setOnClickListener(this);
        invertButton.setOnClickListener(this);
        blurButton.setOnClickListener(this);
        savePhotoButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.addImageButton):
                openGallery();
                break;

            case (R.id.sepiaFilter):
                Picasso.get().load(imageUri)
                        .transform(new SepiaFilterTransformation(getContext())).into(imageView);
                break;

            case (R.id.grayFilter):
                Picasso.get().load(imageUri)
                        .transform(new GrayscaleTransformation()).into(imageView);
                break;

            case (R.id.invertFilter):
                Picasso.get().load(imageUri)
                        .transform(new InvertFilterTransformation(getContext())).into(imageView);
                break;

            case (R.id.blurFilter):
                Picasso.get().load(imageUri)
                        .transform(new BlurTransformation(getContext(), 25, 5)).into(imageView);
                break;

            case (R.id.saveImage):
                saveImageToGallery();
                break;
        }
    }

    public void openGallery() {
        Intent photo = new Intent(Intent.ACTION_PICK);
        photo.setType("image/*");
        activityResultLaunch.launch(photo);
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    imageUri = data.getData();
                    imageView.setImageURI(imageUri);
                }
            }
        }
    });

    private void saveImageToGallery() {
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ro")).format(new Date());
        String fileName = new SimpleDateFormat(date + "_HH.mm.ss", Locale.forLanguageTag("ro")).format(new Date());

        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);

        // Save the bitmap to a file in the Pictures directory
        String filename = fileName + ".jpg";
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(picturesDir, filename);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.getFD().sync();
            Toast.makeText(getContext(), "Imaginea a fost salvata in galerie", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Imaginea nu a fost salvata", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // Tell the gallery app to scan for new images
        MediaScannerConnection.scanFile(getContext(),
                new String[]{imageFile.getAbsolutePath()},
                new String[]{"image/jpeg"},
                null);
    }

}
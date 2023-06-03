package com.gt.zega.htmlToPdf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gt.zega.R;
import com.gt.zega.entity.User;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HtmlToPdf {
    private final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 777;

    private String DIRECTORY = "ZEGA";
    private String PDF_DIRECTORY = "Pdf";
    private String htmlFileName;
    private String pdfFileName;


    private Activity activity;
    private Context context;
    private User user;
    private String deviceName;
    private String faultCode;
    private String defaultDescription;
    private String optionalDescription;
    private String hospitalName;
    private String hospitalLocation;
    private String hospitalSection;
    private String roomSection;
    private ArrayList<Uri> listOfImages;

    private String suppliesName;

    private String reportType;


    public HtmlToPdf(Activity activity, Context context, User user, String deviceName, String faultCode, String defaultDescription, String hospitalName, String hospitalLocation, String hospitalSection, String roomSection, String optionalDescription, ArrayList<Uri> listOfImages, String reportType) {
        this.activity = activity;
        this.context = context;
        this.user = user;
        this.deviceName = deviceName;
        this.faultCode = faultCode;
        this.defaultDescription = defaultDescription;
        this.hospitalName = hospitalName;
        this.hospitalLocation = hospitalLocation;
        this.hospitalSection = hospitalSection;
        this.roomSection = roomSection;
        this.optionalDescription = optionalDescription;
        this.listOfImages = listOfImages;

        this.reportType = reportType;
    }

    public HtmlToPdf(Activity activity, Context context, User user, String suppliesName, String deviceName, String hospitalName, String hospitalLocation, String hospitalSection, String roomSection, String reportType) {
        this.activity = activity;
        this.context = context;
        this.user = user;
        this.suppliesName = suppliesName;
        this.deviceName = deviceName;
        this.hospitalName = hospitalName;
        this.hospitalLocation = hospitalLocation;
        this.hospitalSection = hospitalSection;
        this.roomSection = roomSection;

        this.reportType = reportType;
    }

    public boolean writeHTML() {
        boolean wasCreated = false;
        String time = new SimpleDateFormat("HH:mm:ss", Locale.forLanguageTag("ro")).format(new Date());
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ro")).format(new Date());
        String fileName = new SimpleDateFormat(date + "_HH.mm.ss", Locale.forLanguageTag("ro")).format(new Date());
//        File directory=context.getExternalFilesDir("MYDIR17"); // works
//        File directory=new File(context.getExternalFilesDir("2023"),"2024"); // works
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (Environment.isExternalStorageManager()) {
//                File directory = new File(Environment.getExternalStoragePublicDirectory("").getPath() + File.separator + DIRECTORY + File.separator + PDF_DIRECTORY); // works
        File directory = context.getExternalFilesDir(PDF_DIRECTORY);
        System.out.println("-------------------------------------->" + directory.getAbsolutePath());
        if (!directory.exists()) {
            if (!directory.mkdirs())
                System.out.println("---------------------------------->failed to create directory");

        }
        File file = null;

        try {
            Document document = null;
            if (reportType.equalsIgnoreCase("brokenDeviceReport")) {
                document = Jsoup.parse(HtmlComponents.createHtml(context, date, time, user, deviceName, faultCode, defaultDescription, hospitalName, hospitalLocation, hospitalSection, roomSection, optionalDescription, listOfImages), "UTF-8");
            } else if (reportType.equalsIgnoreCase("suppliesReport")) {
                document = Jsoup.parse(HtmlComponents.createHtml1(context, date, time, user, suppliesName, deviceName, hospitalName, hospitalLocation, hospitalSection, roomSection), "UTF-8");
            }
            System.out.println(document.outerHtml());
            if (!fileName.endsWith(".html")) {
                htmlFileName = fileName + ".html";

                file = new File(directory + File.separator + htmlFileName);

            }
            if (file != null) {
                if (file.createNewFile()) {
                    FileUtils.writeStringToFile(file, document.outerHtml(), StandardCharsets.UTF_8);

                    uploadUserFileToFirebaseStorage(file, context, activity);

                    wasCreated = true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//            } else {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                context.startActivity(intent);
//            }
//        }
        return wasCreated;
    }

    private void AllFilesAccessPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.all_files_access_permission, null);
        builder.setView(view);

        Button cancelButton = view.findViewById(R.id.denyAccessButton);
        Button okButton = view.findViewById(R.id.confirmAccessButton);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        cancelButton.setOnClickListener(view1 -> dialog.cancel());

        okButton.setOnClickListener(view2 -> {
            dialog.cancel();
        });
    }

    private void uploadUserFileToFirebaseStorage(File file, Context context, Activity activity) {

//        View customProgressDialogView = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
//        ProgressBar progressBar = customProgressDialogView.findViewById(R.id.progress_bar);
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(0);
//        TextView progressTextView = customProgressDialogView.findViewById(R.id.loading_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(progressBar);
        builder.setCancelable(false);
        AlertDialog progressDialog = builder.create();
        progressDialog.show();

        // Show the progress dialog
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                progressDialog.show();
//            }
//        });

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference fileRef = storage.getReference().child("users/" + userId + "/" + reportType + "/" + file.getName());

        fileRef.putFile(Uri.fromFile(file)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(@NonNull UploadTask.TaskSnapshot snapshot) {
                System.out.println("Upload is paused!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                System.out.println("Error uploading file: " + e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                file.delete();
                progressDialog.dismiss();
                Toast.makeText(context, htmlFileName + " a fost salvat", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private boolean requestAllFilesAccessPermission(Context context) {
//        // Check if the permission is already granted
//        boolean bool = false;
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted, request it
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
//                    REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);
//
//        } else {
//            bool = true;
//            // Permission is already granted, access the external storage
//            // ...
//        }
//        return bool;
//    }


}

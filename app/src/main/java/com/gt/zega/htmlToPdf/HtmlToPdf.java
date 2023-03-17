package com.gt.zega.htmlToPdf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gt.zega.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HtmlToPdf {
    private static String DIRECTORY = "ZEGA";
    private static String PDF_DIRECTORY = "Pdf";

    public static void writeHTML(Context context, String userName, String deviceName, String errorDescription, String deviceLocation) {

        String htmlName = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss", Locale.forLanguageTag("ro")).format(new Date());
//        File directory=context.getExternalFilesDir("MYDIR17"); // works
//        File directory=new File(context.getExternalFilesDir("2023"),"2024"); // works
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                File directory = new File(Environment.getExternalStoragePublicDirectory("").getPath() + File.separator + DIRECTORY + File.separator + PDF_DIRECTORY); // works
                if (!directory.exists()) {
                    if (!directory.mkdirs())
                        System.out.println("---------------------------------->failed to create directory");

                }
                File file = null;
                try {
                    Document document = Jsoup.parse(HtmlComponents.createHtml(userName, deviceName, errorDescription, deviceLocation), "UTF-8");
                    if (!htmlName.endsWith(".html"))
                        file = new File(directory + File.separator + htmlName + ".html");
                    if (file != null) {
                        if (file.createNewFile()) {
//                    FileUtils.writeStringToFile(file, document.outerHtml(), StandardCharsets.UTF_8);
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(document.outerHtml().getBytes(StandardCharsets.UTF_8));
                            Toast.makeText(context, file.getName() + " a fost salvat", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                context.startActivity(intent);
            }
        }
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


}

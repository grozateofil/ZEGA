package com.gt.zega.htmlToPdf;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.gt.zega.entity.BrokenMedicalDevicesMonthly;
import com.gt.zega.entity.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

public class HtmlComponents {

    public static String createHtml(Context context, String date, String time, User user, String deviceName, String faultCode, String defaultDescription, String hospitalName, String hospitalSection, String roomSection, String optionalDescription, ArrayList<Uri> list) throws IOException {
        String userName = user.getFirstName() + " " + user.getLastName();
        String phoneNumber = user.getPhoneNumber();
        String htmlHead = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>" + "<style>" + ".center {\n" +
                "  margin-left: auto;\n" +
                "  margin-right: auto;\n" +
                "}\n" +
                "\n" +
                "table{\n" +
                "  width:100%;\n" +
                "}\n" +
                "\n" +
                "th{\n" +
                "  width:20%;\n" +
                "  background-color:#c2f4fc;\n" +
                "}\n" +
                "\n" +
                "td{\n" +
                "  width:80%;\n" +
                "  word-wrap: break-word;\n" +
                "}\n" +
                "\n" +
                "tbody tr:nth-child(even) td{\n" +
                "    background-color:#cdd0d1;\n" +
                "}\n" +
                "tbody tr:nth-child(odd) td{\n" +
                "}\n" +
                "\n" +
                "table, th, td {\n" +
                "  border: 1px solid black;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "\n" +
                "th {\n" +
                "  text-align: left;\n" +
                "}" + "</style>" + "</head>";

        StringBuilder htmlBody = new StringBuilder("\n<body>\n" +
                "<table class=\"center\" border=\"1\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<th>Data</th>\n" +
                "<td>" + date + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Ora</th>\n" +
                "<td>" + time + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Nume utilizator</th>\n" +
                "<td>" + userName + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Telefon</th>\n" +
                "<td>" + phoneNumber + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Aparat</th>\n" +
                "<td>" + deviceName + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Cod defectiune</th>\n" +
                "<td>" + faultCode + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Descriere generala defectiune</th>\n" +
                "<td>" + defaultDescription + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Spital</th>\n" +
                "<td>" + hospitalName + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Sectia</th>\n" +
                "<td>" + hospitalSection + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Salon</th>\n" +
                "<td>" + roomSection + "</td>\n" +
                "</tr>\n");

        if (!optionalDescription.isEmpty()) {
            htmlBody.append("<tr>\n" +
                    "<th>Descriere optionala</th>\n" +
                    "<td>" + optionalDescription + "</td>\n" +
                    "</tr>\n");
        }

        if (list.size() > 0) {
            htmlBody.append(
                    "<tr>\n" +
                            "<th>Poza</th>\n" + "<td>");
            for (Uri uri : list) {

                byte[] fileBytes = Files.readAllBytes(getImageFilePath(context, uri));
                htmlBody.append("<img src=\"data:image/").append(getImageExtension(getImageFilePath(context, uri))).append(";base64,").append(Base64.getEncoder().encodeToString(fileBytes)).append("\" width=\"400\" height=\"300\"/><br>");
            }
            htmlBody.append("</td>\n");
            htmlBody.append("</tr>\n");
        }
        htmlBody.append("</tbody>\n" + "</table>\n" + "</body>\n" + "</html>");
        return htmlHead + htmlBody;

    }

    public static String getImageExtension(Path path) {
        String extension;
        String fileName = path.getFileName().toString();
        extension = fileName.substring(fileName.indexOf(".") + 1);
        return extension;
    }

    public static Path getImageFilePath(Context context, Uri uri) {
        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];
        Path imagePath = null;

        Cursor cursor = context.getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            if (columnIndex >= 0) {
                cursor.moveToFirst();
                imagePath = Paths.get(cursor.getString(columnIndex));
            } else {
                System.out.println("Column " + columnIndex + " does not exist in the cursor");
            }
            cursor.close();
            return imagePath;
        }
        return null;
    }

    public static String createHtml1(Context context, String date, String time, User user, String suppliesName, String deviceName, String hospitalName, String hospitalSection, String roomSection) throws IOException {
        String userName = user.getFirstName() + " " + user.getLastName();
        String phoneNumber = user.getPhoneNumber();
        String htmlHead = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>" + "<style>" + ".center {\n" +
                "  margin-left: auto;\n" +
                "  margin-right: auto;\n" +
                "}\n" +
                "\n" +
                "table{\n" +
                "  width:100%;\n" +
                "}\n" +
                "\n" +
                "th{\n" +
                "  width:20%;\n" +
                "  background-color:#c2f4fc;\n" +
                "}\n" +
                "\n" +
                "td{\n" +
                "  width:80%;\n" +
                "  word-wrap: break-word;\n" +
                "}\n" +
                "\n" +
                "tbody tr:nth-child(even) td{\n" +
                "    background-color:#cdd0d1;\n" +
                "}\n" +
                "tbody tr:nth-child(odd) td{\n" +
                "}\n" +
                "\n" +
                "table, th, td {\n" +
                "  border: 1px solid black;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "\n" +
                "th {\n" +
                "  text-align: left;\n" +
                "}" + "</style>" + "</head>";

        StringBuilder htmlBody = new StringBuilder("\n<body>\n" +
                "<table class=\"center\" border=\"1\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<th>Data</th>\n" +
                "<td>" + date + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Ora</th>\n" +
                "<td>" + time + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Nume utilizator</th>\n" +
                "<td>" + userName + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Telefon</th>\n" +
                "<td>" + phoneNumber + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Consumabil</th>\n" +
                "<td>" + suppliesName + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Aparat</th>\n" +
                "<td>" + deviceName + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Spital</th>\n" +
                "<td>" + hospitalName + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Sectia</th>\n" +
                "<td>" + hospitalSection + "</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<th>Salon</th>\n" +
                "<td>" + roomSection + "</td>\n" +
                "</tr>\n");

        htmlBody.append("</tbody>\n" + "</table>\n" + "</body>\n" + "</html>");
        return htmlHead + htmlBody;

    }

    public static String monthlyReportWithBrokenDevices(ArrayList<BrokenMedicalDevicesMonthly> brokenMedicalDevicesMonthlyArrayList) {
        String localTime = new SimpleDateFormat("HH:mm:ss", Locale.forLanguageTag("ro")).format(new Date());
        String localDate = new SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ro")).format(new Date());
        String htmlHead = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>" + "<style>" + ".center {\n" +
                "  margin-left: auto;\n" +
                "  margin-right: auto;\n" +
                "}\n" +
                "\n" +
                "table{\n" +
                "  width:100%;\n" +
                "}\n" +
                "\n" +
                "tbody tr:nth-child(even) td{\n" +
                "    background-color:#cdd0d1;\n" +
                "}\n" +
                "tbody tr:nth-child(odd) td{\n" +
                "}\n" +
                "\n" +
                "table, th, td {\n" +
                "  border: 1px solid black;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "\n" +
                "th {\n" +
                "  text-align: left;\n" +
                "}" + "</style>" + "</head>";
        StringBuilder htmlBody = new StringBuilder("\n<body>\n" +
                "<p>" + localTime + "</p>\n" +
                "<p>" + localDate + "</p>\n" +
                "<table class=\"center\" border=\"1\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<th>Data</th>" +
                "<th>Coduri de eroare</th>" +
                "<th>Numar de dispozitive defecte</th>" +
                "<th>Codurile dispozitivelor defecte</th>" +
                "</tr>");
        for (BrokenMedicalDevicesMonthly brokenMedicalDevicesMonthly : brokenMedicalDevicesMonthlyArrayList) {
            String date = brokenMedicalDevicesMonthly.getDate();
            ArrayList<String> errorCodeArrayList = brokenMedicalDevicesMonthly.getErrorCode();
            int numberOfBrokenDevices = brokenMedicalDevicesMonthly.getNumberOfBrokenDevices();
            ArrayList<String> arrayListOfDevicesCodes = brokenMedicalDevicesMonthly.getArrayListOfDevicesCodes();
            if (numberOfBrokenDevices != 0) {
                htmlBody.append("<tr><td>" + date + "</td>\n" +
                        "<td>" + errorCodeArrayList.toString() + "</td>\n" +
                        "<td>" + numberOfBrokenDevices + "</td>\n" +
                        "<td>" + arrayListOfDevicesCodes.toString() + "</td></tr>\n");
            }
        }
        htmlBody.append("</tbody>\n" + "</table>\n" + "</body>\n" + "</html>");


        return htmlHead + htmlBody;
    }

}

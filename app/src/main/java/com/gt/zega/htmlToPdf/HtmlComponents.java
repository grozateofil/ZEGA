package com.gt.zega.htmlToPdf;

public class HtmlComponents {

    public static String createHtml(String userName, String deviceName, String errorDescription, String deviceLocation) {
        String htmlHead = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>" + "<link href=\"htmlToPdf//style.css\" rel=\"stylesheet\" type=\"text/css\">" + "</head>";

        String htmlBody = "\n<body>\n" + "<p>Nume utilizator: " + userName + "</p>\n" +
                "<p>Aparat: " + deviceName + "</p>\n" +
                "<p>Descriere problemă: " + errorDescription + "</p>\n" +
                "<p>Locația aparatului: " + deviceLocation + "</p>\n" + "</body>\n" + "</html>";

        return htmlHead + htmlBody;

    }

}

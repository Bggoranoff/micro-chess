package com.github.bggoranoff.qchess.util;

public class TextFormatter {

    public static String formatDeviceName(String nameToFormat) {
        String[] deviceDetails = nameToFormat.split("\\s+");
        StringBuilder deviceName = new StringBuilder();
        for(int i = 1; i < deviceDetails.length - 1; i++) {
            deviceName.append(deviceDetails[i]).append(" ");
        }
        deviceName.append(deviceDetails[deviceDetails.length - 1]);
        return deviceName.toString();
    }

    public static String formatDeviceUsername(String formattedName) {
        String[] deviceDetails = formattedName.split("\\|");
        return deviceDetails[1];
    }

    public static String formatDeviceIconName(String formattedName) {
        String[] deviceDetails = formattedName.split("\\|");
        return deviceDetails[0];
    }

    public static String formatDeviceIp(String formattedName) {
        String[] deviceDetails = formattedName.split("\\|");
        return deviceDetails[2];
    }
}

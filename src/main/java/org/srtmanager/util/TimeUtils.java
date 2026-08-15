package org.srtmanager.util;

public class TimeUtils {
    public static String toSrtTimestamp(double secondsTime) {
        int hours = (int) (secondsTime / 3600);
        int minutes = (int) ((secondsTime - hours * 3600) / 60);
        double seconds = secondsTime - hours * 3600 - minutes * 60;
        double milliseconds = (seconds - (long) seconds) * 1000;

        return (String.format("%02d:%02d:%02d,%03d", hours, minutes, (int) seconds, (int) milliseconds));
    }

    public static double fromSrtTimestamp(String srtTimestamp) {

            String[] timestampArray = srtTimestamp.split("[:,]");
            int arrayLength = timestampArray.length;
            if (arrayLength == 4){
                    double hours = Double.parseDouble(timestampArray[0]) * 3600;
                    double minutes = Double.parseDouble(timestampArray[1]) * 60;
                    double seconds = Double.parseDouble(timestampArray[2]);
                    double milliseconds = Double.parseDouble(timestampArray[3]) / 1000.0;
                    return (hours + minutes + seconds + milliseconds);

            } else{throw new IllegalArgumentException("The timestamp is invalid");}




    }
}

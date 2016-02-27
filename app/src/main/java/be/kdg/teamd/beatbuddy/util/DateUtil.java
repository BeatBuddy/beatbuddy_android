package be.kdg.teamd.beatbuddy.util;

public class DateUtil {

    public static String secondsToFormattedString(int durationInSeconds){
        int hours = durationInSeconds / 3600;
        int minutes = (durationInSeconds % 3600) / 60;
        int seconds = durationInSeconds % 60;

        if(hours == 0){
            return String.format("%02d:%02d", minutes, seconds);
        }

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}

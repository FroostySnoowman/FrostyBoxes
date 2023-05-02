package net.frostyservices.frostyboxes.util;

public class TimeUtils {
    public static int[] formatToMinutesAndSeconds(int millis) {
        int minutes = millis / (1000 * 60);
        int nMillis = millis - (minutes * 1000 * 60);
        int seconds = nMillis / 1000;
        return new int[]{minutes, seconds};
    }
}

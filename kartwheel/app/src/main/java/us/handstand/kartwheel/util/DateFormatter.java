package us.handstand.kartwheel.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd\'T\'HH:mm:ss\'.\'SSSZ";
    private static final String BACKUP_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss\'.\'SSS";

    public static Date get(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.ENGLISH);
        return format.format(date);
    }
}

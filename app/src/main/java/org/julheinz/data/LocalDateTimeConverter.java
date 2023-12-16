package org.julheinz.data;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;

public class LocalDateTimeConverter {

    @TypeConverter
    public static LocalDateTime stringToDate(String string) {
        if (string == null) {
            return null;
        } else {
            return LocalDateTime.parse(string);
        }
    }

    @TypeConverter
    public static String dateToString(LocalDateTime date) {
        if (date == null) {
            return null;
        } else {
            return date.toString();
        }

    }

}

package org.julheinz.data;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.HashSet;

public class HashSetConverter {
    public static final String SEPARATOR = ";;;";

    @TypeConverter
    public static HashSet<String> fromString(String string) {
        if (string == null || string.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(string.split(SEPARATOR)));
    }

    @TypeConverter
    public static String fromSet(HashSet<String> set) {
        if (set == null) {
            return "";
        }
        return String.join(SEPARATOR, set);
    }
}

package org.julheinz.data;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Translate a HashSet to a String with separators between elements and vice versa.
 * Used as a Type converter so that the contents of the Hashset can be saved in databases that can't store Hashsets directly.
 * When the data is retrieved from the database, the contents are structured as a HashSet again.
 */
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

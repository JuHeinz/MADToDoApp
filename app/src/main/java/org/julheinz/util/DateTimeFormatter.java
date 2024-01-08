package org.julheinz.util;

import java.time.LocalDateTime;

public class DateTimeFormatter {

    public static String format(LocalDateTime date){
        java.time.format.DateTimeFormatter formatter =  java.time.format.DateTimeFormatter.ofPattern("dd.M | hh:ss a");
        return formatter.format(date);
    }
}

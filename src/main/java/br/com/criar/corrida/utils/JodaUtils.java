package br.com.criar.corrida.utils;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public final class JodaUtils {

    public static final String PATTERM_DDMMYYYY_HH24MMSS = "ddMMyyyy_HHmmss";
    public static final String PATTERM_BR_DDMMYYYY_HH24MMSS = "dd/MM/yyyy_HH:mm:ss";
    public static final String PATTERM_BR_DDMMYYYY = "dd/MM/yyyy_HH:mm:ss";
    public static final String PATTERM_BR_15_DDMMYYYY_HH24MMSS = "ddMMyyyy HHmmss";
    public static final String PATTERM_BR_15_DDMMYYYY_HH24MMSSSSS = "ddMMyyyy HHmmss.SSS";
    public static final String PATTERM_BR_DD_MM_YYYY = "dd/MM/yyyy";

    private JodaUtils(){}

    public static String formatarnow(String pattern){
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return DateTime.now().toString(fmt);
    }

    public static DateTime parseStringToDateTime(String dtString, String pattern){
        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
            return fmt.parseDateTime(dtString);
        }catch (Exception e){
            return null;
        }

    }

    public static DateTime parseStringToDate(String dtString, String pattern){
        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
            DateTime  dt = fmt.parseDateTime(dtString);
            dt.withHourOfDay(0);
            dt.withMinuteOfHour(0);
            dt.withSecondOfMinute(0);
            return dt;
        }catch (Exception e){
            return null;
        }
    }

    public static Date parseStringToJavaDate(String dtString, String pattern){
        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
            DateTime  dt = fmt.parseDateTime(dtString);
            dt.withHourOfDay(0);
            dt.withMinuteOfHour(0);
            dt.withSecondOfMinute(0);
            return dt.toDate();
        }catch (Exception e){
            return null;
        }
    }


}

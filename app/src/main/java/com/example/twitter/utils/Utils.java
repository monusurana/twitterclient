package com.example.twitter.utils;

/**
 * Created by monusurana on 8/3/16.
 */

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    public static String getRelativeTimeAgo(String rawJsonDate) {
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";

        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(
                    dateMillis,
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS
            ).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parseDate(relativeDate);
    }

    public static Date getDateFromString(String date) {
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER_DATE_FORMAT);
        sf.setLenient(true);
        try {
            return sf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Convert date into Twitter's date format
     *
     * @param date
     * @return
     */
    private static String parseDate(String date) {
        if (date.contains("ago")) {
            String dateArray[] = date.split(" ");

            return dateArray[0] + dateArray[1].charAt(0);
        } else if (date.contains(",")) {
            String dateArray[] = date.split(" ");

            return dateArray[1].substring(0, dateArray[1].length() - 1) + " " + dateArray[0] + " " + dateArray[2].substring(0, 2);
        }

        return date;
    }
}


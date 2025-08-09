package com.simple.Bookstore.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PostedDateFormatter {
    public static String formatTimeAgo(LocalDateTime dateTime, boolean edited) {
        if (dateTime == null) {
            return "Unknown";
        }
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        String formattedDate;
        if (ChronoUnit.DAYS.between(dateTime, now) == 0) {
            formattedDate = "Today";
        } else if (ChronoUnit.DAYS.between(dateTime, now) == 1) {
            formattedDate = "Yesterday";
        } else if (duration.toDays() < 7) {
            formattedDate = duration.toDays() + " days ago";
        } else if (duration.toDays() < 30) {
            long weeks = duration.toDays() / 7;
            formattedDate = weeks + " weeks ago";
        } else if (duration.toDays() < 365) {
            long months = duration.toDays() / 30;
            formattedDate = months + " months ago";
        } else {
            long years = duration.toDays() / 365;
            formattedDate = years + " years ago";
        }
        if (edited) {
            return formattedDate + "*";
        } else {
            return formattedDate;
        }
    }
}

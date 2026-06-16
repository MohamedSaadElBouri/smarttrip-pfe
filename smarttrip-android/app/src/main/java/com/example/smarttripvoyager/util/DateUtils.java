package com.example.smarttripvoyager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    private DateUtils() {}

    /** Convertit une date ISO renvoyee par le backend (ex. "2026-06-15T10:30:00") en texte relatif (ex. "Il y a 2h"). */
    public static String relativeDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            String cleaned = isoDate.length() > 19 ? isoDate.substring(0, 19) : isoDate;
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE);
            Date date = parser.parse(cleaned);
            if (date == null) return "";

            long diffMs = System.currentTimeMillis() - date.getTime();
            if (diffMs < 0) diffMs = 0;
            long minutes = diffMs / 60000;
            long hours = minutes / 60;
            long days = hours / 24;

            if (minutes < 1) return "À l'instant";
            if (minutes < 60) return "Il y a " + minutes + " min";
            if (hours < 24) return "Il y a " + hours + "h";
            if (days < 7) return "Il y a " + days + " j";
            return new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(date);
        } catch (ParseException e) {
            return "";
        }
    }
}

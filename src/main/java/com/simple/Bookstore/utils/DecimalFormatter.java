package com.simple.Bookstore.utils;

import java.text.DecimalFormat;

public class DecimalFormatter {
    public static String formatDecimal(double value) {
        return new DecimalFormat("#.##").format(value);
    }
}

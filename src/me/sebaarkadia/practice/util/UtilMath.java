package me.sebaarkadia.practice.util;

import java.text.DecimalFormat;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.Random;

public class UtilMath
{
    private static Random random;
    
    static {
        UtilMath.random = new Random();
    }
    
    public static Random getRandom() {
        return UtilMath.random;
    }
    
    public static double getFraction(final double value) {
        return value % 1.0;
    }
    
    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static double trim(final int degree, final double d) {
        String format = "#.#";
        for (int i = 1; i < degree; ++i) {
            format = String.valueOf(format) + "#";
        }
        final DecimalFormat twoDForm = new DecimalFormat(format);
        return Double.valueOf(twoDForm.format(d));
    }
    
    public static String convertTicksToMinutes(final int ticks) {
        final long minute = ticks / 1200;
        final long second = ticks / 20 - minute * 60L;
        String secondString = new StringBuilder().append(Math.round(second)).toString();
        if (second < 10L) {
            secondString = "0" + secondString;
        }
        String minuteString = new StringBuilder().append(Math.round(minute)).toString();
        if (minute == 0L) {
            minuteString = "0";
        }
        return String.valueOf(minuteString) + ":" + secondString;
    }
}

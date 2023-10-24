package com.dy.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static int randomInt(int min, int max) {
        return (int)(Math.random() * (max - min + 1) + min);
    }

    public static String toLocalCapitalize(String str){
        final String parts[] = str.split(" ");
        String result = "";

        for(String part : parts){
            result += part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase() + " ";
        }

        return result;
    }
}

package com.dy.app.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public static byte[] serialize(Object obj){
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object deserialize(byte[] bytes){
        try {
            return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static int long2int(long longValue){
        int intValue;

        try{
            intValue = Math.toIntExact(longValue);
        }catch (ArithmeticException e){
            throw new RuntimeException("long value is too large to convert to int");
        }

        return intValue;

    }
}

package com.shojabon.mcutils.Utils;

import org.bukkit.Material;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class BaseUtils {

    public static boolean isInt(String testing){
        try{
            Integer.parseInt(testing);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean isFloat(String testing){
        try{
            Float.valueOf(testing);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean isDouble(String testing){
        try{
            Double.valueOf(testing);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean isLong(String testing){
        try{
            Long.valueOf(testing);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean isBoolean(String testing){
        return "true".equalsIgnoreCase(testing) || "false".equalsIgnoreCase(testing);
    }

    public static boolean isMaterial(String testing){
        try{
            Material.valueOf(testing);
            return true;
        }catch (IllegalArgumentException  e){
            return false;
        }
    }

    public static boolean isUUID(String testing){
        try{
            UUID.fromString(testing);
            return true;
        }catch (IllegalArgumentException  e){
            return false;
        }
    }



    public static double roundValue(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String priceString(int price){
        return String.format("%,d", price);
    }

    public static String booleanToJapaneseText(boolean bool){
        if(bool){
            return "有効";
        }
        return "無効";
    }

    public static String weekToString(int week){
        return new String[]{"日曜日", "月曜日", "火曜日", "水曜日","木曜日", "金曜日", "土曜日"}[week];
    }

    public static String unixTimeToString(long unixTime){
        if(unixTime == 0) return null;
        Date date = new java.util.Date(unixTime*1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

}

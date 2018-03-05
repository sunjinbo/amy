package com.sun.amy.utils;

public class CapacityUtil
{
    public static String DEFAULT_VALUE = "0K";

    public static String ConvertByteToString(double capacity){
        String capacityStr = DEFAULT_VALUE;
        
        if (capacity < 1024) // B
        {
            capacityStr = String.format("%.1fB", capacity);
        }
        else if (capacity >= 1024 && capacity < 1024 * 1024) // KB
        {
            capacityStr = String.format("%.1fK", capacity /1024);
        }
        else if (capacity >= 1024 * 1024 && capacity < 1024 * 1024 * 1024) // MB
        {
            capacityStr = String.format("%.1fM", capacity / (1024 * 1024));
        }
        else if (capacity >= 1024 * 1024 * 1024) // G
        {
            capacityStr = String.format("%.1fG", capacity / (1024 * 1024 * 1024));
        }else {
            capacityStr = DEFAULT_VALUE;
        }
        return capacityStr;
    }

    public static String ConvertKByteToString(double capacity){
        String capacityStr = DEFAULT_VALUE;

        if (capacity < 1024) // KB
        {
            capacityStr = String.format("%.1f", capacity);
            capacityStr = removeUselessZero(capacityStr) + "KB";
        }
        else if (capacity >= 1024 && capacity < 1024 * 1024) // MB
        {
            capacityStr = String.format("%.1f", capacity /1024);
            capacityStr = removeUselessZero(capacityStr) + "MB";
        }
        else if (capacity >= 1024 * 1024 && capacity < 1024 * 1024 * 1024) // GB
        {
            capacityStr = String.format("%.1f", capacity / (1024 * 1024));
            capacityStr = removeUselessZero(capacityStr) + "GB";
        }else {
            capacityStr = DEFAULT_VALUE;
        }
        return capacityStr;
    }

    public static String removeUselessZero(String s) {
        if(s.indexOf(".") > 0){
            //regex
            s = s.replaceAll("0+?$", "");//remove useless zeros in the end
            s = s.replaceAll("[.]$", "");// if data after decimal point all are zeros, remove the decimal point
        }
        return s;
    }
}

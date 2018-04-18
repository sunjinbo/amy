package com.sun.amy.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class KidBean {
    public String chinese_name = "";
    public String english_name = "";
    public int age = 1;

    public KidBean() {
    }

    public static KidBean parseConfig(String path) {
        KidBean bean = null;

        if (!new File(path).exists()) {
            return null;
        }

        // step 1: read the config string
        String jsonData = "";
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(path), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.contains("start_time") || line.contains("end_time") || line.contains("word_time")) {
                    line = line.replace(" ", "");
                }
                jsonData += line;
            }
            bufferedReader.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            return bean;
        }

        // step 2: parse json string to AdsBean object.
        try {
            bean = new KidBean();
            JSONObject rootObj = new JSONObject(jsonData);

            if (rootObj.has("chinese_name")) {
                bean.chinese_name = rootObj.optString("chinese_name");
            }

            if (rootObj.has("english_name")) {
                bean.english_name = rootObj.optString("english_name");
            }

            if (rootObj.has("age")) {
                bean.age = rootObj.optInt("age");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            return bean;
        }

        return bean;
    }
}

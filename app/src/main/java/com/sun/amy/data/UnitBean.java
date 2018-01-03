package com.sun.amy.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * UnitBean.
 */
public class UnitBean implements Serializable {
    public List<WordBean> words = new ArrayList<>();
    public List<SongBean> songs = new ArrayList<>();
    public List<StoryBean> storys = new ArrayList<>();
    public String name;
    public String type;
    public String level;

    public static UnitBean parseConfig(String path) {
        UnitBean bean = null;

        String configName = path + File.separator + "config.ini";

        // step 1: read the config string
        String jsonData = "";
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(configName));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
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
            bean = new UnitBean();
            JSONObject rootObj = new JSONObject(jsonData);

            if (rootObj.has("name")) {
                bean.name = rootObj.optString("name");
            }

            if (rootObj.has("level")) {
                bean.level = rootObj.optString("level");
            }

            if (rootObj.has("type")) {
                bean.type = rootObj.optString("type");
            }

            if (rootObj.has("words")) {
                JSONArray array = rootObj.getJSONArray("words");
                for (int i = 0; i < array.length(); ++i) {
                    JSONObject object = (JSONObject) array.get(i);
                    WordBean wordBean = new WordBean();
                    wordBean.isKeyWord = (object.optInt("is_key_word") == 1);
                    wordBean.english = object.optString("english");
                    wordBean.chinese = object.optString("chinese");
                    wordBean.img = path + object.optString("img");
                    bean.words.add(wordBean);
                }
            }

            if (rootObj.has("songs")) {
                JSONArray array = rootObj.getJSONArray("songs");
                for (int i = 0; i < array.length(); ++i) {
                    JSONObject object = (JSONObject) array.get(i);
                    SongBean songBean = new SongBean();
                    songBean.type = object.optInt("type");
                    songBean.name = object.optString("name");
                    songBean.file = path + object.optString("file");
                    bean.songs.add(songBean);
                }
            }

            if (rootObj.has("storys")) {
                JSONArray array = rootObj.getJSONArray("storys");
                for (int i = 0; i < array.length(); ++i) {
                    JSONObject object = (JSONObject) array.get(i);
                    StoryBean storyBean = new StoryBean();
                    storyBean.type = object.optInt("type");
                    storyBean.name = object.optString("name");
                    storyBean.file = path + object.optString("file");
                    bean.storys.add(storyBean);
                }
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

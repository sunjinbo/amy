package com.sun.amy.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.sun.amy.data.WordBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * DictUtils class.
 */
public class DictUtils {

    public static Map<String, WordBean> readWordFromStore() {
        Map<String, WordBean> words = new HashMap();

        File amy = new File(Environment.getExternalStorageDirectory(), "amy");
        File dict = new File(amy, "dict.ini");

        if (dict.exists()) {
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;

            try {
                fis = new FileInputStream(dict);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);

                String temp;
                while ((temp = br.readLine()) != null) {
                    WordBean wordBean = WordBean.parse(temp);
                    if (wordBean != null && !TextUtils.isEmpty(wordBean.img)) {
                        words.put(wordBean.img, wordBean);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }

                    if (isr != null) {
                        isr.close();
                    }

                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return words;
    }

    public static void writeWordToStore(Map<String, WordBean> words) {
        File amy = new File(Environment.getExternalStorageDirectory(), "amy");
        File dict = new File(amy, "dict.ini");
        FileOutputStream fos  = null;
        PrintWriter pw = null;
        try {
            if (dict.exists()) {
                dict.delete();
            }

            fos = new FileOutputStream(dict);
            pw = new PrintWriter(fos);
            for (WordBean wordBean : words.values()) {
                if (wordBean.score < 100) {
                    pw.write(wordBean.toString() + '\n');
                }
            }
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }

                if (pw != null) {
                    pw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

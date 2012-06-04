package org.ling0322.danci;


import android.app.Activity;
import android.os.Environment;

public class Config {
    public final static String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public final static String LIA_PATH = SDCARD_PATH + "/单词喵喵喵";
    public final static String LIA_PATH_OLD = SDCARD_PATH + "/lia";
    public final static String SPEECH_PATH = LIA_PATH + "/speech";
    public final static String WL_KAOYAN_PATH = LIA_PATH + "/wl-kaoyan";
    public final static String WL_CET4_PATH = LIA_PATH + "/wl-cet-4";
    public final static String WL_CET6_PATH = LIA_PATH + "/wl-cet-6";
    public final static String WL_IELTS_PATH = LIA_PATH + "/wl-ielts";
    public final static String WL_GRE_PATH = LIA_PATH + "/wl-gre";
    public final static String WL_TOFEL_PATH = LIA_PATH + "/wl-tofel";
    public final static String DICT_12_PATH = LIA_PATH + "/dict-12-v2";
    public final static String DICT_FULL_PATH = LIA_PATH + "/dict-full";
    public final static String DICT_EC_PATH = LIA_PATH + "/dict-ec";
    public final static int DICT_12_PARTS = 16;
    public final static int REVIEWLIST_WORDS_PER_PAGE = 50;
    public static Activity mainInstance;
    
}

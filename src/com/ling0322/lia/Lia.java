package com.ling0322.lia;

import java.io.*;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

public class Lia {
	public final static String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public final static String LIA_PATH =  SDCARD_PATH + "/lia";
    public final static String WL_KAOYAN_PATH = LIA_PATH + "/wl-kaoyan";
    public final static String WL_CET4_PATH = LIA_PATH + "/wl-cet-4";
    public final static String WL_CET6_PATH = LIA_PATH + "/wl-cet-6";
    public final static String WL_IELTS_PATH = LIA_PATH + "/wl-ielts";
    public final static String PRON_PATH = LIA_PATH + "/wl-pron";
    public final static String DICT_12_PATH = LIA_PATH + "/dict-12";
    public final static long DICT_12_SIZE = 35069952;
    public final static long WORD_LIST_SIZE = 505856;
    
    public static Activity mainInstance;
    
}

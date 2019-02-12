package com.example.nioto.emojigame.api;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.utils.Constants;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LifeTimerHelper {

    private static long memoryDateMillis;
    private static long currentDateMillis;
    private static long timeLeftInMillis;
    private static CountDownTimer countDownTimer;
    private TextView countDownTextView;



    private static long getCurrentDate() {
        Date date = new Date(System.currentTimeMillis());
        //converting it back to a milliseconds representation:
        return date.getTime();
    }

    private static void putCurrentDateInSharedPreferences(SharedPreferences sharedPreferences){
    }

    public static void saveLifeDate(SharedPreferences sharedPreferences, int userLife){




    }

}

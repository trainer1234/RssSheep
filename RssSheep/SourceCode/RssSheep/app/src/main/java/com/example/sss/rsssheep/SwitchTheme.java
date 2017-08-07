package com.example.sss.rsssheep;

import android.app.Activity;

/**
 * Created by SSS on 24/04/2017.
 */

public class SwitchTheme {
    private static int sTheme;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_DARK = 1;

    // Set the theme of the Activity, and restart it by creating a new Activity of the same type.
    public static void changeTheme(Activity activity, int theme){
        sTheme = theme;
        //activity.recreate();
        //activity.finish();
        //activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static int getsTheme() {
        return sTheme;
    }

    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity){
        switch (sTheme){
            case THEME_DEFAULT:
                activity.setTheme(R.style.MyMaterialTheme_Base);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.MyMaterialTheme_Dark);
                break;
            default:
                break;
        }
    }
}

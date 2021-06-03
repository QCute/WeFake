package com.github.qcute.wefake;

import java.io.File;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class Settings {

    public static final String name = "settings";
    public static final String path = "/data/user_de/0/" + BuildConfig.APPLICATION_ID + "/shared_prefs";
    public static final String file = path + "/" + name + ".xml";
    private static XSharedPreferences preferences;
    public static XSharedPreferences getModuleSharedPreferences() {
        if (preferences == null) {
            try {
                if (XposedBridge.getXposedVersion() >= 93) {
                    preferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, name);
                } else {
                    preferences = new XSharedPreferences(new File(file));
                }
                preferences.makeWorldReadable();
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        } else {
            preferences.reload();
        }

        return preferences;
    }

    public static String getSSID() {
        return getModuleSharedPreferences().getString("SSID", "");
    }

    public static String getBSSID() {
        return getModuleSharedPreferences().getString("BSSID", "");
    }

    public static String getIP() {
        return getModuleSharedPreferences().getString("IP", "");
    }

    public static String getGateway() {
        return getModuleSharedPreferences().getString("gateway", "");
    }

    public static String getNetmask() {
        return getModuleSharedPreferences().getString("netmask", "");
    }

    public static boolean isEnabled() {
        return false;
    }
}
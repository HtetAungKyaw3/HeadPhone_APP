package com.airoha.utapp.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PreferenceUtility {
    public static final String AIROHA_UUID_NAME = "Airoha_UUID";
    public static final String COMMON_UUID_NAME = "Common_UUID";

    private static final String UUID_Setting = "uuid_setting";
    private static final String UUID_NAME_SELECT = "uuid_name_select";
    private static final String UUID_INFO_LIST = "uuid_info_list";

    public static class UUID_INFO {
        public String mName;
        public String mUUID;
    }

    public static void saveUUIDList(Context context, ArrayList<UUID_INFO> pdInfoList) {
        SharedPreferences settings = context.getSharedPreferences(UUID_Setting, 0);
        SharedPreferences.Editor editor = settings.edit();

        Type listType = TypeToken.getParameterized(ArrayList.class, UUID_INFO.class).getType();
        Gson gson = new Gson();
        String json = gson.toJson(pdInfoList, listType);
        editor.putString(UUID_INFO_LIST, json);

        editor.commit();
    }

    public static ArrayList<UUID_INFO> getUUIDList(Context context) {
        ArrayList<UUID_INFO> ret;
        SharedPreferences settings = context.getSharedPreferences(UUID_Setting, 0);
        String json = settings.getString(UUID_INFO_LIST, null);
        if (json == null) {
            ret = new ArrayList<>();
        } else {
            Type listType = TypeToken.getParameterized(ArrayList.class, UUID_INFO.class).getType();
            Gson gson = new Gson();
            ret = gson.fromJson(json, listType);
        }
        return ret;
    }

    public static void saveUUIDSelectedName(Context context, String UUID_selected_name) {
        SharedPreferences settings = context.getSharedPreferences(UUID_Setting, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(UUID_NAME_SELECT, UUID_selected_name);
        editor.commit();
    }

    public static String getUUIDSelectedName(Context context) {
        SharedPreferences settings = context.getSharedPreferences(UUID_Setting, 0);
        return settings.getString(UUID_NAME_SELECT, AIROHA_UUID_NAME);
    }
}

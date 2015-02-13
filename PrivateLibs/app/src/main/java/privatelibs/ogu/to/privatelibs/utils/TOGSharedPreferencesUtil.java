package privatelibs.ogu.to.privatelibs.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import privatelibs.ogu.to.privatelibs.json.TOGJSONUtil;

public class TOGSharedPreferencesUtil {

    // プリファレンスから指定キーの情報を削除する
    static <K, V> void remove(Context context, String prefName, String key) {
        Editor editor = context.getApplicationContext()
                .getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();
        editor.remove(key).apply();
    }

    // プリファレンスにHashMapを保存する
    static <K, V> void setHashMap(Context context,
                                  HashMap<K, V> notifiedCvList, String prefName, String key) {
        JSONObject json = new JSONObject(notifiedCvList);
        Editor editor = context.getApplicationContext()
                .getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();
        editor.putString(key, json.toString());
        editor.apply();
    }

    // プリファレンスからArrayListを取得する
    @SuppressWarnings("unchecked")
    static <K, V> HashMap<K, V> getHashMap(Context context, String prefName,
                                           String Key) throws JSONException {

        SharedPreferences pref = context.getApplicationContext()
                .getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String stringList = pref.getString(Key, "");

        if (TextUtils.isEmpty(stringList)) {
            return null;
        }

        JSONObject json = new JSONObject(stringList);
        HashMap<K, V> map = (HashMap<K, V>) TOGJSONUtil.toMap(json);
        return map;
    }

    // プリファレンスにArrayListを保存する
    static <T> void setArrayList(Context context, ArrayList<T> list,
                                 String prefName, String key) {
        JSONArray array = new JSONArray();
        for (int i = 0, length = list.size(); i < length; i++) {
            try {
                array.put(i, list.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Editor editor = context.getApplicationContext()
                .getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();
        editor.putString(key, array.toString());
        editor.apply();
    }

    // プリファレンスからArrayListを取得する
    @SuppressWarnings("unchecked")
    static ArrayList<String> getArrayList(Context context, String prefName,
                                          String Key) throws JSONException {

        SharedPreferences pref = context.getApplicationContext()
                .getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String stringList = pref.getString(Key, "");

        if (TextUtils.isEmpty(stringList)) {
            return null;
        }

        return TOGJSONUtil.toList(new JSONArray(stringList));
    }
}

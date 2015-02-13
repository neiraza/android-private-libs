package privatelibs.ogu.to.privatelibs.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSONから各オブジェクトへ変換するUtil.
 */
class TOGJSONUtil {

    // JSONObjectからHashMapを作成する
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static HashMap<String, Object> toMap(JSONObject object)
            throws JSONException {
        if (object == null) {
            return null;
        }
        HashMap<String, Object> map = new HashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }

    // JSONArrayからArrayListを作成する
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static ArrayList toList(JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }
        ArrayList list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }
}

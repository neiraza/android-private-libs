package privatelibs.ogu.to.privatelibs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import privatelibs.ogu.to.privatelibs.utils.TOGArrayUtil;
import privatelibs.ogu.to.privatelibs.utils.TOGCipherUtil;

/**
 * サーバ専用クライアントクラス.
 * <p/>
 * <p>通信</p>
 * HTTP POSTメソッドおよびHTTP GETメソッドに対応している
 * <p/>
 * <p>暗号化</p>
 * 通信のパラメータについてAESによる暗号化および復号が可能
 */
public class TOGServerClient {

    /* HTTP Method */
    private static final String HTTP_POST = "POST";
    private static final String HTTP_GET = "GET";
    private static final String HTTPS = "HTTPS";

    // APIクライアント用のインスタンスをシングルトンで取得する.
    static TOGServerClient getInstance() {
        return InstanceHolder.sInstance;
    }

    // 遅延初期化
    private static class InstanceHolder {
        private static final TOGServerClient sInstance = new TOGServerClient();
    }

    /**
     * HTTP POST (Async, AES).
     */
    JSONObject sendPostRequestAsync(Context context, String endpointBaseUrl,
                                    HashMap<String, Object> params) {
        return post(context, endpointBaseUrl, params, isSSL(endpointBaseUrl), true);
    }


    /**
     * HTTP GET（Async）.
     */
    JSONObject sendGetRequestAsync(Context context, String endpointBaseUrl,
                                   HashMap<String, String> params) {
        return get(context, endpointBaseUrl, params, isSSL(endpointBaseUrl));
    }

    // HTTP POST
    private JSONObject post(Context context, String endpoint,
                            HashMap<String, Object> params, boolean ssl, boolean aes) {

        // POST用パラメータ生成
        HashMap<String, Object> reqParams = createCommonReqParams(context);
        if (params != null && !params.isEmpty()) {
            reqParams.putAll(params);
        }

        try {
            URL url = new URL(endpoint);
            return new HttpPostTask(url, new JSONObject(reqParams).toString(),
                    ssl, aes).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // HTTP GET
    private JSONObject get(Context context, String endpointBaseUrl,
                           HashMap<String, String> params, boolean ssl) {

        String endpoint = "";
        HashMap<String, Object> reqParams = createCommonReqParams(context);
        if (params != null && !params.isEmpty()) {
            reqParams.putAll(params);
        }
        StringBuilder paramsSb = new StringBuilder("?");
        paramsSb.append(buildParam(buildParam(reqParams)));
        paramsSb.insert(0, endpointBaseUrl);
        endpoint = paramsSb.toString();

        URL url;
        try {
            url = new URL(endpoint);
            return new HttpGetTask(url, ssl).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // params生成に便利
    private static String buildParam(ArrayList<String> params) {
        return TOGArrayUtil.join("&", params);
    }

    // params生成に便利
    private static <K, V> ArrayList<String> buildParam(HashMap<K, V> params) {
        ArrayList<String> result = new ArrayList<String>();
        for (K key : params.keySet()) {
            result.add(String.format("%s=%s", key,
                    encodeUrl((String) params.get(key))));
        }
        return result;
    }

    // SSLチェック
    private static boolean isSSL(String endpointBaseUrl) {
        return endpointBaseUrl.startsWith(HTTPS);
    }

    // HTTPS POST（非同期）.
    private class HttpPostTask extends AsyncTask<Void, Void, JSONObject> {
        URL mUrl;
        String mParams;
        boolean mSsl;
        boolean mAes;

        HttpPostTask(URL url, String params, boolean ssl, boolean aes) {
            mUrl = url;
            mParams = params;
            mSsl = ssl;
            mAes = aes;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            HttpURLConnection conn;
            try {

                // SSL
                if (mSsl) {
                    conn = (HttpsURLConnection) mUrl.openConnection();
                } else {
                    conn = (HttpURLConnection) mUrl.openConnection();
                }

                conn.setRequestMethod(HTTP_POST);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setChunkedStreamingMode(0);
                conn.setConnectTimeout(10000);
                conn.setRequestProperty("Content-Type",
                        "application/json; charset=utf-8");

                // AES暗号化
                String paramsString;
                if (mAes) {
                    paramsString = TOGCipherUtil.encryptByAes(mParams);
                } else {
                    paramsString = mParams;
                }

                return convertJSON(connection(conn, paramsString), mAes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // HTTPS GET（非同期）.
    private class HttpGetTask extends AsyncTask<Void, Void, JSONObject> {
        URL mUrl;
        boolean mSsl;

        HttpGetTask(URL url, boolean ssl) {
            mUrl = url;
            mSsl = ssl;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            HttpURLConnection conn;
            try {

                // SSL
                if (mSsl) {
                    conn = (HttpsURLConnection) mUrl.openConnection();
                } else {
                    conn = (HttpURLConnection) mUrl.openConnection();
                }

                conn.setRequestMethod(HTTP_GET);
                conn.setDoOutput(false);
                conn.setDoInput(true);
                conn.setConnectTimeout(10000);
                conn.setRequestProperty("Content-Type",
                        "application/json; charset=utf-8");

                return convertJSON(connection(conn, null), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // 共通コネクション処理.
    private static StringBuilder connection(HttpURLConnection conn,
                                            String postParams) {
        System.out.println("connection:" + conn.getClass() + " " + postParams);
        InputStream is = null;
        StringBuilder resSb = null;
        try {
            conn.connect();

            // POST パラメータ処理
            if (postParams != null) {
                OutputStreamWriter osw = new OutputStreamWriter(
                        conn.getOutputStream());
                osw.write(postParams);
                osw.flush();
                osw.close();

                int networkStatus = conn.getResponseCode();
                if (networkStatus != HttpsURLConnection.HTTP_OK) {
                    return null;
                }
            }

            is = conn.getInputStream();
            resSb = convertStringBuilder(is);
        } catch (Exception e) {
        } finally {
            finishInputStream(is);
        }

        return resSb;
    }

    // resをStringBuilderで書き出し
    private static StringBuilder convertStringBuilder(InputStream is)
            throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is,
                "UTF-8"));

        StringBuilder resSb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            resSb.append(line);
        }
        return resSb;
    }

    // connectionを閉じるときはInputStreamをcloseすればok
    private static void finishInputStream(InputStream is) {

        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        // cf.http://developer.android.com/training/basics/network-ops/connecting.html#download
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }

    // GET, POST完了後の処理
    private static JSONObject convertJSON(StringBuilder res, boolean aes) {

        // resがnullの場合は諦めてもらう
        if (res == null) {
            return null;
        }

        JSONObject object = null;
        try {

            // AES復号
            String decrypted;
            if (aes) {
                decrypted = TOGCipherUtil.decryptByAes(res.toString());
            } else {
                decrypted = res.toString();
            }

            // 復号に失敗している場合はresをそのまま返してあげる
            if (decrypted == null) {
                object = new JSONObject(res.toString());
            } else {
                object = new JSONObject(decrypted);
            }
        } catch (JSONException e) {
        }
        return object;
    }

    // 共通パラメータ
    static HashMap<String, Object> createCommonReqParams(Context context) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        return params;
    }

    // URLデコード
    static String decodeUrl(String var) {
        try {
            return URLDecoder.decode(var, "UTF-8");
        } catch (Exception e) {
            return var;
        }
    }

    // URLエンコード
    static String encodeUrl(String var) {
        try {
            return URLEncoder.encode(var, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return var;
        }
    }

}

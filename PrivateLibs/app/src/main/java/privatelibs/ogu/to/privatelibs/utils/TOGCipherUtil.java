package privatelibs.ogu.to.privatelibs.utils;

import android.text.TextUtils;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TOGCipherUtil {
    private static String AP_VIDEO_AES_KEY;
    private static String AP_VIDEO_AES_IV;
    private static final String UTF8 = "UTF-8";

    static {
        AP_VIDEO_AES_KEY = "";
        AP_VIDEO_AES_IV = "";
    }

    // (NativeLib) 指定KeyとIVでAES暗号化
    public static String encryptByAes(String text) {
        return encryptBase64(text, AP_VIDEO_AES_KEY, AP_VIDEO_AES_IV);
    }

    // (NativeLib) 指定KeyとIVでAES復号
    public static String decryptByAes(String base64EncodedCryptedText) {
        return decryptBase64(base64EncodedCryptedText,
                AP_VIDEO_AES_KEY, AP_VIDEO_AES_IV);
    }

    static String encryptBase64(String text, String encryptKey, String encryptIv) {
        byte[] crypted = TOGCipherUtil.encrypt(text, encryptKey, encryptIv);
        String resultStr = Base64.encodeToString(crypted, Base64.NO_WRAP);
        return resultStr;

    }

    static byte[] encrypt(String text, String encryptKey, String encryptIv) {

        if (TextUtils.isEmpty(text)) {
            return null;
        }
        byte[] byteResult = null;
        try {
            byte[] byteText = text.getBytes(UTF8);
            byte[] byteKey = encryptKey.getBytes(UTF8);
            byte[] byteIv = encryptIv.getBytes(UTF8);
            SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(byteIv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byteResult = cipher.doFinal(byteText);
            return byteResult;
        } catch (Exception e) {
        }

        // 暗号化文字列を返却
        return byteResult;
    }

    static String decryptBase64(String base64EncodedCryptedText, String encryptKey, String encryptIv) {
        byte[] byteArray = Base64.decode(base64EncodedCryptedText, Base64.DEFAULT);
        String ret = decrypt(byteArray, encryptKey, encryptIv);
        return ret;
    }

    static String decrypt(byte[] byteArray, String encryptKey, String encryptIv) {

        if (byteArray == null) {
            return null;
        }
        // 変数初期化
        String strResult = null;

        try {
            byte[] byteKey = encryptKey.getBytes(UTF8);
            byte[] byteIv = encryptIv.getBytes(UTF8);
            SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(byteIv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] byteResult = cipher.doFinal(byteArray);
            // バイト配列を文字列へ変換
            strResult = new String(byteResult, UTF8);

        } catch (Exception e) {
        }

        return strResult;
    }
}

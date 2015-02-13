package privatelibs.ogu.to.privatelibs.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * SQLiteOpenHelper拡張クラス.
 */
final class TOGSQLiteOpenHelper extends TOGSQLiteAsyncHelper {

    static Context mContext;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "dbTog";

    // CREATE & DROP : video_view_history
    private static final String CREATE_TABLE = "CREATE TABLE `hoge` (`id` INTEGER, `key` TEXT, `cid` TEXT, PRIMARY KEY(id));";
    private static final String DROP_TABLE = "DROP TABLE `hoge`;";

    // APIクライアント用のインスタンスをシングルトンで取得する.
    static TOGSQLiteOpenHelper getInstance(Context context) {
        mContext = context;
        return InstanceHolder.sInstance;
    }

    // 遅延初期化
    static class InstanceHolder {
        static final TOGSQLiteOpenHelper sInstance = new TOGSQLiteOpenHelper(
                mContext, DB_NAME, null, DB_VERSION);
    }

    TOGSQLiteOpenHelper(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // スキーマ変更時はDROP-CREATEする、テーブル削除時は存在しない場合も考慮しておく
        try {
            db.execSQL(DROP_TABLE);
        } catch (Exception e) {

        } finally {
            onCreate(db);
        }
    }

    static String createInSQL(int length) {
        return TOGSQLiteCRUDHelper.createInSQL(length);
    }

    // SQLiteのdatetime使用時のN分前/N分後の操作
    static String createDateTimeModifiersMinutes(String min, boolean before) {
        return TOGSQLiteCRUDHelper.createDateTimeModifiersMinutes(min, before);
    }
}

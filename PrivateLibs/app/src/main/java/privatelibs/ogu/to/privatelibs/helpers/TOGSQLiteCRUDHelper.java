package privatelibs.ogu.to.privatelibs.helpers;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * CRUDを記述したヘルパークラス.
 */
abstract class TOGSQLiteCRUDHelper extends SQLiteOpenHelper {

    private SQLiteDatabase mDb;

    public TOGSQLiteCRUDHelper(Context context, String name,
                               CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * INSERT.
     * 
     * @param table
     * @param values
     */
    void insert(String table, ContentValues values) {
        try {
            mDb = getWritableDatabase();
            mDb.beginTransaction();
            mDb.insertOrThrow(table, null, values);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            mDb.endTransaction();
            mDb.close();
        }
    }

    /**
     * INSERT OR REPLACE.
     * 
     * @param table
     * @param values
     */
    void insertOrReplace(String table, ContentValues values) {
        try {
            mDb = getWritableDatabase();
            mDb.beginTransaction();
            mDb.replaceOrThrow(table, null, values);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            mDb.endTransaction();
            mDb.close();
        }
    }

    /**
     * DELETE.
     * 
     * @param table
     *            削除するテーブル名
     * @param whereClause
     *            削除条件
     */
    int delete(String table, String whereClause) {

        int deleteRows = 0;
        try {
            mDb = getWritableDatabase();
            mDb.beginTransaction();
            deleteRows = mDb.delete(table, whereClause, null);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            mDb.endTransaction();
            mDb.close();
        }
        return deleteRows;
    }

    /**
     * UPDATE.
     * 
     * @param table
     *            更新するテーブル名
     * @param values
     * @param whereClause
     */
    void update(String table, ContentValues values, String whereClause) {
        try {
            mDb = getWritableDatabase();
            mDb.beginTransaction();
            mDb.update(table, values, whereClause, null);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            mDb.endTransaction();
            mDb.close();
        }
    }

    /**
     * SELECT.
     * 
     * @param sql
     *            SQL文
     * @param selectionArgs
     *            selectionの「？」を置換する文字列
     * @return 取得したデータを返却
     * @throws Exception
     */
    ArrayList<ArrayList<String>> select(String sql, String[] selectionArgs)
            throws Exception {

        mDb = getReadableDatabase();
        Cursor cursor = mDb.rawQuery(sql, selectionArgs);
        int rowCount = cursor.getCount();
        int columnCount = cursor.getColumnCount();

        cursor.moveToFirst();
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>(
                rowCount);
        for (int i = 0; i < rowCount; i++) {
            ArrayList<String> row = new ArrayList<String>(columnCount);
            for (int j = 0; j < columnCount; j++) {
                row.add(cursor.getString(j));
            }
            list.add(row);
            cursor.moveToNext();
        }
        cursor.close();
        mDb.close();
        return list;
    }

    // IN句のpreparedstatementを生成する
    static String createInSQL(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length;) {
            builder.append("?");
            if (++i < length) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    // SQLiteのdatetime使用時のN分前/N分後の操作
    static String createDateTimeModifiersMinutes(String min, boolean before) {
        StringBuilder sb;
        if (before) {
            sb = new StringBuilder("-");
        } else {
            sb = new StringBuilder("+");
        }
        sb.append(min);
        sb.append(" minutes");
        return sb.toString();
    }
}

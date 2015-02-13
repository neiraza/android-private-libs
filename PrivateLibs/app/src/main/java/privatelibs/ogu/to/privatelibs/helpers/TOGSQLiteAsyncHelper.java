package privatelibs.ogu.to.privatelibs.helpers;

import java.util.concurrent.ExecutionException;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.AsyncTask;

/**
 * CRUDを非同期でやりたい時に使うヘルパークラス.
 */
abstract class TOGSQLiteAsyncHelper extends TOGSQLiteCRUDHelper {

    TOGSQLiteAsyncHelper(Context context, String name, CursorFactory factory,
                         int version) {
        super(context, name, factory, version);
    }

    // INSERT
    void insertAsync(String table, ContentValues values) {
        new SQLiteInsertTask(table, values).execute();
    }

    private class SQLiteInsertTask extends AsyncTask<Void, Void, Void> {

        String mTable;
        ContentValues mValues;

        SQLiteInsertTask(String table, ContentValues values) {
            mTable = table;
            mValues = values;
        }

        @Override
        protected Void doInBackground(Void... params) {
            TOGSQLiteAsyncHelper.this.insert(mTable, mValues);
            return null;
        }
    }

    // INSERT OR REPLACE
    void insertOrReplaceAsync(String table, ContentValues values) {
        new SQLiteInsertOrReplaceTask(table, values).execute();
    }

    private class SQLiteInsertOrReplaceTask extends AsyncTask<Void, Void, Void> {

        String mTable;
        ContentValues mValues;

        SQLiteInsertOrReplaceTask(String table, ContentValues values) {
            mTable = table;
            mValues = values;
        }

        @Override
        protected Void doInBackground(Void... params) {
            TOGSQLiteAsyncHelper.this.insertOrReplace(mTable, mValues);
            return null;
        }
    }

    // DELETE
    Integer deleteAsync(String table, String whereClause) {
        try {
            return new SQLiteDeleteTask(table, whereClause).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class SQLiteDeleteTask extends AsyncTask<Void, Void, Integer> {

        String mTable;
        String mWhereClause;

        SQLiteDeleteTask(String table, String whereClause) {
            mTable = table;
            mWhereClause = whereClause;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int i = TOGSQLiteAsyncHelper.this.delete(mTable, mWhereClause);
            return Integer.valueOf(i);
        }
    }
}

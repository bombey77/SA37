package bombey77.sa37;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    int[] position_id = {1, 2, 3, 4};
    String[] position_name = {"Директор", "Программист", "Бухгалтер", "Охранник"};
    int[] position_salary = {80000, 60000, 40000, 20000};

    String[] people_name = {"Максим", "Сергей", "Руслан", "Наталья", "Иван", "Мария", "Светлана", "Григорий"};
    int[] people_posid = {2, 3, 2, 2, 3, 1, 2, 4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        Cursor cursor;

        Log.d(LOG_TAG, "--- Table position ---");
        cursor = sqLiteDatabase.query("position", null, null, null, null, null, null);
        logCursor(cursor);
        cursor.close();
        Log.d(LOG_TAG, "--- ---");

        Log.d(LOG_TAG, "--- Table people ---");
        cursor = sqLiteDatabase.query("people", null, null, null, null, null, null);
        logCursor(cursor);
        cursor.close();
        Log.d(LOG_TAG, "--- ---");

        Log.d(LOG_TAG, "---INNER JOIN with rawQuery---");
        String sqlQuery = "select PL.name as Name, PS.name as Position, salary as Salary "
                + "from people as PL "
                + "inner join position as PS "
                + "on PL.posid = PS.id "
                + "where salary > ?";
        cursor = sqLiteDatabase.rawQuery(sqlQuery, new String[] {"40000"});
        logCursor(cursor);
        cursor.close();
        Log.d(LOG_TAG, "--- ---");

        Log.d(LOG_TAG, "---INNER JOIN with query---");
        String table = "people as PL inner join position as PS on PL.posid = PS.id";
        String[] columns = {"PL.name as Name", "PS.name as Position", "salary as Salary"};
        String selection = "salary < ?";
        String[] selectionArgs = {"40000"};
        cursor = sqLiteDatabase.query(table, columns, selection, selectionArgs, null, null, null);
        logCursor(cursor);
        cursor.close();
        Log.d(LOG_TAG, "--- ---");

        dbHelper.close();
    }



    void logCursor (Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : cursor.getColumnNames()) {
                        str = str.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                    } Log.d(LOG_TAG, str);
                } while (cursor.moveToNext());
            }
        } else Log.d(LOG_TAG, "Cursor is empty");
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDb", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.d(LOG_TAG, "--- onCreateDataBase ---");

            ContentValues contentValues = new ContentValues();

            db.execSQL("create table position (" + "id integer primary key," + "name text," + "salary integer" + ");");

            for (int i = 0; i < position_id.length; i++) {
                contentValues.clear();
                contentValues.put("id", position_id[i]);
                contentValues.put("name", position_name[i]);
                contentValues.put("salary", position_salary[i]);
                db.insert("position", null, contentValues);
            }

            db.execSQL("create table people (" + "id integer primary key autoincrement," + "name text," + "posid integer" + ");");

            for (int i = 0; i < people_name.length; i++) {
                contentValues.clear();
                contentValues.put("name", people_name[i]);
                contentValues.put("posid", people_posid[i]);
                db.insert("people", null, contentValues);
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

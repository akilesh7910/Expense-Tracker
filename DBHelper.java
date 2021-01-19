package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {

    private Cursor cursor;
    private SQLiteDatabase sdb;
    private static final String table_name_1 = "Lists";
    private static final String table_name_2 = "Tasks";

    private static final String create_table_1 =
            "CREATE TABLE " + table_name_1 + "(" + "list_name TEXT PRIMARY KEY)";
    private static final String create_table_2 =
            "CREATE TABLE " + table_name_2 + "(" +
                    "list_name TEXT, " +
                    "task_name TEXT, " +
                    "due_date TEXT, " +
                    "expense_amount TEXT," +
                    "completed INT, " +
                    "FOREIGN KEY(list_name) REFERENCES " + table_name_1 + "(list_name), " +
                    "PRIMARY KEY(list_name,task_name))";


    private static final String drop_table_1 =
            "DROP TABLE IF EXISTS " + table_name_1;
    private static final String drop_table_2 =
            "DROP TABLE IF EXISTS " + table_name_2;

    DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d("Database operations", "Database created");
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table_1);
        Log.d("Database operation", "Table 1 created");
        db.execSQL(create_table_2);
        Log.d("Database operation", "Table 2 created");
    }


    public void onUpgrade(SQLiteDatabase db, int version_old, int version_new) {
        db.execSQL(drop_table_1);
        db.execSQL(create_table_1);
        db.execSQL(drop_table_2);
        db.execSQL(create_table_2);
    }

    //inserting a new item into the list if there is no duplicates along with a debug message to track it easily
    boolean insertList(DBHelper db, String name) {
        SQLiteDatabase sdb = db.getWritableDatabase();
        cursor = null;
        String check = "SELECT * FROM " + table_name_1 + " WHERE list_name='" + name + "'";
        cursor = sdb.rawQuery(check, null);
        if (cursor.getCount() <= 0) {
            ContentValues cv = new ContentValues();
            cv.put("list_name", name);
            sdb.insertOrThrow(table_name_1, null, cv);
            Log.d("Database operations", "one row inserted");
            cursor.close();
            return true;
        } else {
            Log.d("Database operations", "Insertion failed, duplicate name");
            cursor.close();
            return false;
        }

    }

    boolean insertTask(DBHelper db, taskItem task) {
        SQLiteDatabase sdb = db.getWritableDatabase();
        cursor = null;
        String check = "SELECT * FROM " + table_name_2 + " WHERE list_name = ? AND task_name = ?";
        String[] selectionArgs = {task.getList(), task.getName()};
        cursor = sdb.rawQuery(check, selectionArgs);
        if (cursor.getCount() <= 0) {
            ContentValues cv = new ContentValues();
            cv.put("list_name", task.getList());
            cv.put("task_name", task.getName());
            cv.put("due_date", task.getDate());
            cv.put("expense_amount", task.getAmount());
            cv.put("completed", task.getComplete());
            sdb.insertOrThrow("Tasks", null, cv);
            cursor.close();
            return true;

        } else {
            Log.d("Database operations", "Insertion failed, duplicate name");
            cursor.close();
            return false;
        }

    }


    //Getting the cursor for the adapter to read the list
    Cursor getList(DBHelper db) {
        sdb = db.getReadableDatabase();
        String[] columns = {"list_name"};
        cursor = sdb.query(table_name_1, columns, null, null, null, null, null);
        return cursor;
    }

    Cursor getOtherList(DBHelper db, String list) {
        sdb = db.getReadableDatabase();
        String[] columns = {"list_name"};
        String selection = "list_name != ?";
        String[] selectionArgs = {list};

        cursor = sdb.query(table_name_1, columns, selection, selectionArgs, null, null, null);
        return cursor;
    }



    // cursor for reading the task list?
    Cursor getTask(DBHelper db, String list) {
        sdb = db.getReadableDatabase();
        String[] columns = {"list_name", "task_name", "due_date","expense_amount", "completed"};
        String selection = "list_name =?";
        String[] selectionArgs = {list};
        String orderBy = "completed";
        cursor = sdb.query(table_name_2, columns, selection, selectionArgs, null, null, orderBy);
        return cursor;
    }




    boolean updateList(DBHelper db, String name, String newName) {
        SQLiteDatabase sdb = db.getWritableDatabase();
        cursor = null;
        String check = "SELECT * FROM " + table_name_1 + " WHERE list_name='" + newName + "'";
        cursor = sdb.rawQuery(check, null);
        if (cursor.getCount() <= 0) {
            String whereClause = "list_name = ?";
            String[] whereArgs = {name};
            ContentValues cv = new ContentValues();
            cv.put("list_name", newName);

            if (sdb.update(table_name_1, cv, whereClause, whereArgs) > 0) {
                sdb.update(table_name_2, cv, whereClause, whereArgs);
                Log.d("Database operations", name + "has been changed to " + newName);
            } else
                Log.d("Database operations", "Edit failed!");

            return true;
        } else {
            Log.d("Database operations", "Duplicate list name, edit failed");
            cursor.close();
            return false;
        }
    }




    boolean updateTask(DBHelper db, String list, String name, String newName, String newDate, String newAmount) {
        SQLiteDatabase sdb = db.getWritableDatabase();
        cursor = null;
        String check = "SELECT * FROM " + table_name_2 + " WHERE list_name=? AND task_name=?";
        String[] selectionArgs = {list, newName};
        cursor = sdb.rawQuery(check, selectionArgs);
        if ((cursor.getCount() <= 0) || (name.equals(newName))) {
            String whereClause = "list_name = ? AND task_name = ?";
            String[] whereArgs = {list, name};
            ContentValues cv = new ContentValues();
            cv.put("task_name", newName);
            cv.put("due_date", newDate);
            cv.put("expense_amount", newAmount);


            if (sdb.update(table_name_2, cv, whereClause, whereArgs) > 0) {
                Log.d("Database operations", name + " task name has been changed to " + newName);
            } else
                Log.d("Database operations", "Edit task  failed!");

            return true;
        } else {
            Log.d("Database operations", "Duplicate task name, edit failed");
            cursor.close();
            return false;
        }
    }




    boolean deleteTask(DBHelper db, String list, String task) {

        SQLiteDatabase sdb = db.getWritableDatabase();
        cursor = null;
        String whereClause = "list_name = ? AND task_name = ?";
        String[] whereArgs = {list, task};
        if (sdb.delete(table_name_2, whereClause, whereArgs) > 0) {
            Log.d("Database operations", task + " has been deleted from list " + list);
            return true;
        } else {
            Log.d("Database operations", task + " deletion failed.");
            return false;
        }
    }


    boolean deleteList(DBHelper db, String list) {
        SQLiteDatabase sdb = db.getWritableDatabase();
        cursor = null;
        String whereClause = "list_name = ?";
        String[] whereArgs = {list};
        if (sdb.delete(table_name_1, whereClause, whereArgs) > 0) {
            sdb.delete(table_name_2, whereClause, whereArgs);
            Log.d("Database operations", list + " has been deleted");
            return true;
        } else {
            Log.d("Database operations", list + " deletion failed.");
            return false;
        }
    }




    int completeCount(DBHelper db, String listName) {

        String check = "SELECT * FROM " + table_name_2 + "  WHERE list_name = ? AND completed = ?";
        String[] selectionArgs = {listName, "1"};
        SQLiteDatabase sdb = db.getReadableDatabase();
        cursor = sdb.rawQuery(check, selectionArgs);
        return (cursor.getCount());

    }



    boolean moveTask(DBHelper db, String task, String oldList, String newList) {

        SQLiteDatabase sdb = db.getWritableDatabase();
        cursor = null;


        String check = "SELECT * FROM " + table_name_2 + " WHERE list_name=? AND task_name=?";
        String[] selectionArgs = {newList, task};
        cursor = sdb.rawQuery(check, selectionArgs);
        if (cursor.getCount() <= 0) {
            String whereClause = "list_name = ? AND task_name = ?";
            String[] whereArgs = {oldList, task};
            ContentValues cv = new ContentValues();
            cv.put("list_name", newList);

            if (sdb.update(table_name_2, cv, whereClause, whereArgs) > 0) {
                Log.d("Database operations", task + " task has been moved from " + oldList + " to " + newList);
            } else
                Log.d("Database operations", "Move task  failed!");

            return true;
        } else {
            Log.d("Database operations", "Duplicate task name already in other list, move failed!");
            cursor.close();
            return false;
        }
    }


    String firstDate(String list) {
        String check = "SELECT due_date FROM " + table_name_2 + " WHERE list_name=? AND due_date!=? ORDER BY date(due_date) ASC";
        String[] selectionArgs = {list, ""};
        cursor = sdb.rawQuery(check, selectionArgs);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            return cursor.getString(0);
        } else
            return "";
    }

}

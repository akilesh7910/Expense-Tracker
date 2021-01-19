
package com.example.expensetracker;


import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity implements DialogTask.DialogTaskListener {

    private String listName;
    private String taskName;
    private String taskDate;
    private String taskAmount;
    private String oldTask;
    private String oldDate;
    private String oldAmount;
    private TextView textView;
    private TextView tv_ec;
    private TextView tv_sdc;
    private Button btn_addTask;
    private ListView lv_task;
    private DBHelper dbHelper;
    private Cursor cursor;
    private ArrayList<taskItem> al_items;

    private CustomArrayAdapter caa;

    private int ec;
    private static int completed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        //Database
        dbHelper = new DBHelper(this, "ExTra", null, 1);

        textView = findViewById(R.id.task_list_name);
        tv_ec = findViewById(R.id.expense_count);
        tv_sdc = findViewById(R.id.SD_Count);

        //List name at the top
        listName = getIntent().getStringExtra("ListName");
        textView.setText(listName);

        lv_task = findViewById(R.id.lv_task);



        readTask();


        btn_addTask = findViewById(R.id.btn_add2);

        btn_addTask.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                openDialog("add", "", "", "");

            }
        });


        lv_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Using position of the list item to get its name
                taskItem task = (taskItem) parent.getItemAtPosition(position);
                oldTask = task.getName();
                oldDate = task.getDate();
                Log.d("Old Date", oldDate);
                oldAmount = task.getAmount();
                Log.d("Old Amount",oldAmount);
                openDialog("edit", oldTask, oldDate, oldAmount);
            }

        });






        lv_task.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                taskItem task = (taskItem) parent.getItemAtPosition(position);
                moveAndDeleteBox(task.getName());
                readTask();
                return true;
            }
        });





    }


    public void openDialog(String command, String task, String date, String amount) {

        DialogTask dialogTask = new DialogTask();
        Bundle bundle = new Bundle();
        bundle.putString("TEXT", command);
        bundle.putString("NAME", task);
        bundle.putString("DATE", date);
        bundle.putString("AMOUNT",amount);
        dialogTask.setArguments(bundle);
        dialogTask.show(getSupportFragmentManager(), "Task Dialog");
    }


    public void readTask() {
        al_items = new ArrayList<>();
        cursor = dbHelper.getTask(dbHelper, listName);
        ec = cursor.getCount();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                String list = cursor.getString(0);
                String name = cursor.getString(1);
                String date = cursor.getString(2);
                String amount = cursor.getString(3);
                int check = cursor.getInt(4);
                taskItem task = new taskItem(list, name, date, amount, check);
                al_items.add(task);
            } while (cursor.moveToNext());

        }

        String t = "Surplus/Deficit" + completed;
        String tc = "Total Expense" + ec;
        tv_ec.setText(tc);
        completed = dbHelper.completeCount(dbHelper, listName);
        tv_sdc.setText(t);
        caa = new CustomArrayAdapter(this, al_items, this);
        lv_task.setAdapter(caa);


    }


    @Override
    public void applyTexts(String name, String date, String amount, String option) {

        taskName = name;
        taskDate = date;
        taskAmount = amount;

        if (option.equals("add"))
            addTaskToDB(taskName, taskDate, taskAmount);
        else if (option.equals("edit"))
            editTask(taskName, taskDate, taskAmount);
    }


    public void addTaskToDB(String name, String date, String amount) {

        if (name.isEmpty()) {
            Toast.makeText(getBaseContext(), "Task name is empty", Toast.LENGTH_LONG).show();
        } else {
            Log.d("print name", name);
            Log.d("print date", date);
            Log.d("print amount", amount);
            taskItem newTask = new taskItem(listName, name, date, amount, 0);
            if (dbHelper.insertTask(dbHelper, newTask)) {
                Toast.makeText(getBaseContext(), "New Task added", Toast.LENGTH_LONG).show();
                Log.d("Database n operations", "New Task added");
            } else {
                Toast.makeText(getBaseContext(), "Failed...duplicate name", Toast.LENGTH_LONG).show();
                Log.d("Database operations", "New Task failed");
            }

        }

        readTask();
    }





    public void editTask(String newTask, String newDate, String newAmount) {
        if (newTask.isEmpty()) {
            Toast.makeText(getBaseContext(), "Task name cannot be empty ", Toast.LENGTH_LONG).show();
        } else {
            if (dbHelper.updateTask(dbHelper, listName, oldTask, newTask, newDate, newAmount)) {
                Toast.makeText(getBaseContext(), "Task name has been changed", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getBaseContext(), "Failed...duplicate name", Toast.LENGTH_LONG).show();
            }

        }
        readTask();
    }



    public void moveAndDeleteBox(String taskName) {
        final String task = taskName;


        cursor = dbHelper.getOtherList(dbHelper, listName);
        ArrayList<String> stringList = new ArrayList<>();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                stringList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }


        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringList);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("What do you want to do with " + task + "?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Move",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(TaskActivity.this);
                        alert.setTitle("Move " + task + " to?");

                        alert.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moveToNewList(task, adapter.getItem(which));
                                dialog.cancel();
                            }
                        });

                        alert.show();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(TaskActivity.this);

                        alert.setTitle("Are you sure you want to delete " + task + " ?");

                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                delete(task);
                                Toast.makeText(getBaseContext(), task + " has been deleted.", Toast.LENGTH_LONG).show();
                            }
                        });

                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                        alert.show();

                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public void moveToNewList(String task, String list) {
        if (dbHelper.moveTask(dbHelper, task, listName, list)) {
            Toast.makeText(getBaseContext(), task + " moved from " + listName + " to  " + list, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Task move failed cuz of duplicate", Toast.LENGTH_LONG).show();
        }

        readTask();
    }


    public void delete(String task) {

        if (dbHelper.deleteTask(dbHelper, listName, task)) {
            Toast.makeText(getBaseContext(), task + " task  has been deleted ", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getBaseContext(), "Failed...could not delete task " + task, Toast.LENGTH_LONG).show();
        }


        readTask();
    }





    public void updateCheck(boolean i) {
        if (i) {
            completed++;
        } else {
            completed--;
        }
        String t = "Completed tasks: " + completed;
        tv_sdc.setText(t);
    }



}

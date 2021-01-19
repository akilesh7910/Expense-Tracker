package com.example.expensetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {


    private EditText et_input;
    private ListView lv_list;

    private Button btn_add;
    private String list_name;

    private DBHelper dbHelper;
    private Cursor cursor;
    private ArrayList<String> al_list;
    private ArrayAdapter<String> aa_list;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //pulling text view , edit text and list view from xml

        et_input = findViewById(R.id.et_input);
        lv_list = findViewById(R.id.lv_main);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        year=calendar.get(Calendar.YEAR);


        //Delete database
        //this.deleteDatabase("ToDoList");

        //Database
        dbHelper = new DBHelper(this, "ExTra", null, 1);

        //Read data from database
        readListDB();


        btn_add = findViewById(R.id.btn_add);

        //add button click listener
        btn_add.setOnClickListener(new OnClickListener() {
            public void onClick(View Arg0) {
                addListToDB();
                readListDB();
            }
        });

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Using position of the list item to get its name
                String selectedList = (String) parent.getItemAtPosition(position);
                String[] listName = selectedList.split("\n");

                Intent intent = new Intent(MainActivity.this,MonthPage.class);

                //Adding the name to a variable and sending it as an extra
                intent.putExtra("ListName", listName[0]);

                //Starting the task view activity
                startActivity(intent);

                //Toast notification to see that my clicks on the list items are working
                //Toast.makeText(getBaseContext(), "Click click " + selectedList, Toast.LENGTH_LONG).show();
            }
        });



        lv_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedList = (String) parent.getItemAtPosition(position);
                String[] listName = selectedList.split("\n");
                clickBox(listName[0]);

                // Toast.makeText(getBaseContext(), "Due date  " + dbHelper.firstDate(listName[0]), Toast.LENGTH_LONG).show();
                return true;
            }
        });



    }


    public void addListToDB() {
        list_name = et_input.getText().toString();
        if (list_name.isEmpty()) {
            Toast.makeText(getBaseContext(), "List name is empty", Toast.LENGTH_LONG).show();
        } else {
            if (Integer.parseInt(list_name) > year  || Integer.parseInt(list_name)<=1960 ){
                Toast.makeText(getBaseContext(), "Enter Valid Number", Toast.LENGTH_LONG).show();

            }
            else{

                if (dbHelper.insertList(dbHelper, list_name)) {
                    Toast.makeText(getBaseContext(), "New List added", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getBaseContext(), "Failed...duplicate name", Toast.LENGTH_LONG).show();

                }
                et_input.setText("");
            }}
    }

    //method to read all lists from the database
    public void readListDB() {
        cursor = null;
        al_list = new ArrayList<>();
        cursor = dbHelper.getList(dbHelper);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                String list = cursor.getString(0);
                String date = dbHelper.firstDate(list);
                al_list.add(list + "\n" + date);

            } while (cursor.moveToNext());
            aa_list = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al_list) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    String aList = aa_list.getItem(position);
                    String[] list = aList.split("\n");

                    String d1 = dbHelper.firstDate(list[0]);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String date2 = sdf.format(new Date());

                    if (!d1.isEmpty()) {

                        if (d1.equals(date2))
                            view.setBackgroundColor(Color.YELLOW);

                        else if (d1.compareTo(date2) < 0)
                            view.setBackgroundColor(Color.RED);

                    }
                    return view;
                }
            };
            lv_list.setAdapter(aa_list);
        }
    }


    public void editList(String listName, String newList) {

        if (newList.isEmpty()) {
            Toast.makeText(getBaseContext(), "List name cannot be empty ", Toast.LENGTH_LONG).show();
        } else {
            if (dbHelper.updateList(dbHelper, listName, newList)) {
                Toast.makeText(getBaseContext(), "List name has been changed", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getBaseContext(), "Failed...duplicate name", Toast.LENGTH_LONG).show();
            }

        }
        readListDB();
    }


    public void clickBox(String listName) {
        final String list = listName;
        final EditText edittext = new EditText(this);
        edittext.setSingleLine();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("What do you want to do with " + list + "?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Edit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("Enter the new list name for:  " + list);

                        alert.setView(edittext);

                        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                String newList = edittext.getText().toString();
                                editList(list, newList);
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

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
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                        alert.setTitle("Are you sure you want to delete " + list + " ?");

                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteList(list);
                            }
                        });

                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });

                        alert.show();

                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }


    public void deleteList(String list) {

        if (dbHelper.deleteList(dbHelper, list)) {
            Toast.makeText(getBaseContext(), list + " list  has been deleted ", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getBaseContext(), "Failed...could not delete list " + list, Toast.LENGTH_LONG).show();
        }
        readListDB();
    }


    @Override
    public void onStart() {
        super.onStart();
        readListDB();
    }
}

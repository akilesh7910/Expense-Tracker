package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomArrayAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<taskItem> al_items;
    private TaskActivity taskActivity;

    CustomArrayAdapter(Context c, ArrayList<taskItem> al, TaskActivity taskActivity) {
        context = c;
        al_items = al;
        this.taskActivity = taskActivity;
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_date;
        TextView tv_amount;

        CheckBox checkbox;


    }


    public View getView(int position, View convert_view, ViewGroup parent) {
        final ViewHolder holder;


        if (convert_view == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convert_view = inflater.inflate(R.layout.task, parent, false);

            holder.tv_name = convert_view.findViewById(R.id.entry_name);
            holder.tv_date = convert_view.findViewById(R.id.entry_date);
            holder.tv_amount = convert_view.findViewById(R.id.entry_amount);


            holder.checkbox = convert_view.findViewById(R.id.completed_checkbox);


            holder.checkbox.setOnCheckedChangeListener(null);
            holder.checkbox.setTag(al_items.get(position).getName());
            holder.checkbox.setChecked(al_items.get(position).getComplete());


            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (buttonView.isShown()) {
                        int checkBoxValue;
                        String checkBoxID = buttonView.getTag().toString();

                        for (taskItem i : al_items) {
                            if (i.getName().equals(checkBoxID)) {
                                i.setComplete(isChecked);
                            }
                        }

                        if (isChecked) {
                            checkBoxValue = 1;
                            Log.d("Database operations", "CheckBox ID " + checkBoxID + " is checked");
                        } else {
                            checkBoxValue = 0;
                            Log.d("Database operations", "CheckBox ID " + checkBoxID + " is not checked");
                        }


                        String taskName = "";
                        String listName = "";
                        for (taskItem i : al_items) {
                            if (i.getName().equals(checkBoxID)) {
                                taskName = i.getName();
                                listName = i.getList();
                                Log.d("Database operations", "List: " + listName + "Task: " + taskName);
                                break;
                            }

                        }


                        if (!(taskName.isEmpty() && listName.isEmpty())) {
                            String whereClause = "list_name = ? AND task_name = ?";
                            String[] whereArgs = {listName, taskName};

                            DBHelper dbHelper = new DBHelper(context, "ExTra", null, 1);
                            SQLiteDatabase sdb = dbHelper.getWritableDatabase();


                            ContentValues cv = new ContentValues();
                            cv.put("completed", checkBoxValue);




                            if (sdb.update("Tasks", cv, whereClause, whereArgs) > 0) {
                                Log.d("Database operations", "CheckBox value changed");
                                taskActivity.updateCheck(isChecked);
                                taskActivity.readTask();
                            } else
                                Log.d("Database operations", "Change failed!");


                        } else {
                            Log.d("Database operations", "CheckBox value could not be changed");
                        }

                    }

                }

            });

            convert_view.setTag(holder);

        } else {
            holder = (ViewHolder) convert_view.getTag();
        }

        holder.checkbox.setTag(al_items.get(position).getName());
        holder.tv_name.setText(al_items.get(position).getName());
        holder.tv_date.setText(al_items.get(position).getDate());
        holder.tv_amount.setText(al_items.get(position).getAmount());
        holder.checkbox.setChecked(al_items.get(position).getComplete());

        return convert_view;
    }


    public int getCount() {
        return al_items.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return al_items.get(position);
    }



}



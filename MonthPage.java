package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormatSymbols;

public class MonthPage extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView lvMonth;
    String[] months;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);
        lvMonth= findViewById(R.id.lvMonth);
        months =new DateFormatSymbols().getMonths();
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, months);
        lvMonth.setAdapter(monthAdapter);
        lvMonth.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedList = (String) parent.getItemAtPosition(position);
        String[] listName = selectedList.split("\n");

        Intent intent = new Intent(MonthPage.this,TaskActivity.class);

        //Adding the name to a variable and sending it as an extra
        intent.putExtra("ListName", listName[0]);

        //Starting the task view activity
        startActivity(intent);
    }
}

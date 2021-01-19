package com.example.expensetracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogTask extends DialogFragment {
    private EditText taskName;
    private TextView taskDate;
    private EditText taskAmount;
    private String name;
    private String date;
    private String amount;
    private DialogTaskListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_task, null);

        Bundle bundle = getArguments();
        final String option = bundle.getString("TEXT", "");
        final String n = bundle.getString("NAME", "");
        final String d = bundle.getString("DATE", "");
        final String a = bundle.getString("AMOUNT", "0");

        String title = "";


        name = n;
        date = d;
        amount = a;
        long time= System.currentTimeMillis();

        taskName = view.findViewById(R.id.edit_expense_name);
        taskDate = view.findViewById(R.id.tv_expense_date);
        CalendarView cv = view.findViewById(R.id.cv_expense_date);
        taskAmount = view.findViewById(R.id.edit_amount);
        cv.setMaxDate(time);


        if (option.equals("add")) {
            title = "Create new Expense";
        } else if (option.equals("edit")) {
            title = "Edit Expense details";
            taskName.setHint("Original Expense name: " + n);
            taskDate.setText("Original date: " + d);
            taskAmount.setHint("Original Amount: " + a);

        }

        builder.setView(view)
                .setTitle(title)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = taskName.getText().toString();
                        amount = taskAmount.getText().toString();
                        listener.applyTexts(name, date, amount, option);

                    }
                });


        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String monthValue, dayValue;
                if (month < 9)
                    monthValue = "0" + (month + 1);
                else
                    monthValue = "" + (month + 1);
                if (dayOfMonth < 10)
                    dayValue = "0" + dayOfMonth;
                else
                    dayValue = "" + dayOfMonth;
                date = dayValue;
                taskDate.setText("Expense date" + date);
            }
        });


        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogTaskListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DialogTaskListener");
        }
    }

    public interface DialogTaskListener {
        void applyTexts(String name, String date, String amount, String option);
    }

}

package user.d802p;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class BasicInfoActivity extends Activity {

    private String TAG = "BasicInfoActivity";
    private EditText startDateEdit = null;
    private EditText startWeightEdit = null;
    private EditText endDateEdit = null;
    private EditText endWeightEdit = null;
    private Button selectStartDateButton = null;
    private Button selectEndDateButton = null;
    private Button doneButton = null;
    private Button cancelButton = null;
    private Calendar startDateCalendar = null;
    private Calendar endDateCalendar = null;
    private DatePickerDialog startDataPicker = null;
    private DatePickerDialog endDataPicker = null;

    private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String year_data = String.valueOf(selectedYear);
            String month_data = String.format("%02d", selectedMonth + 1);
            String day_data = String.format("%02d", selectedDay);
            startDateEdit.setText(year_data + "-" + month_data + "-" + day_data);
        }
    };

    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String year_data = String.valueOf(selectedYear);
            String month_data = String.format("%02d", selectedMonth + 1);
            String day_data = String.format("%02d", selectedDay);
            endDateEdit.setText(year_data + "-" + month_data + "-" + day_data);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_basic_info);

        startDateEdit = (EditText) this.findViewById(R.id.start_date_value);
        startWeightEdit = (EditText) this.findViewById(R.id.start_weight_value);
        endDateEdit = (EditText) this.findViewById(R.id.end_date_value);
        endWeightEdit = (EditText) this.findViewById(R.id.end_weight_value);
        selectStartDateButton = (Button) this.findViewById(R.id.select_start_date);
        selectEndDateButton = (Button) this.findViewById(R.id.select_end_date);
        doneButton = (Button) this.findViewById(R.id.edit_info_done);
        cancelButton = (Button) this.findViewById(R.id.edit_info_cancel);

        CalendarUtil calendar_util = new CalendarUtil(BasicInfoActivity.this);
        calendar_util.openDatabase();
        ArrayList<CalendarUtil.struct_basic> basics = calendar_util.queryBasic();
        if (basics.size() == 1)
        {
            startDateEdit.setText(basics.get(0)._start_date);
            startWeightEdit.setText(basics.get(0)._start_weight);
            endDateEdit.setText(basics.get(0)._end_date);
            endWeightEdit.setText(basics.get(0)._end_weight);
        }
        calendar_util.closeDatabase();

        startDateEdit.setGravity(Gravity.CENTER);
        startWeightEdit.setGravity(Gravity.CENTER);
        endDateEdit.setGravity(Gravity.CENTER);
        endWeightEdit.setGravity(Gravity.CENTER);

        startDateEdit.setEnabled(false);
        endDateEdit.setEnabled(false);

        startDateCalendar = Calendar.getInstance(TimeZone.getDefault());
        startDataPicker = new DatePickerDialog(this,
                R.style.AppTheme, startDatePickerListener,
                startDateCalendar.get(Calendar.YEAR),
                startDateCalendar.get(Calendar.MONTH),
                startDateCalendar.get(Calendar.DAY_OF_MONTH));
        startDataPicker.setCancelable(false);
        startDataPicker.setTitle(getResources().getString(R.string.select_date_tip));

        endDateCalendar = Calendar.getInstance(TimeZone.getDefault());
        endDataPicker = new DatePickerDialog(this,
                R.style.AppTheme, endDatePickerListener,
                endDateCalendar.get(Calendar.YEAR),
                endDateCalendar.get(Calendar.MONTH),
                endDateCalendar.get(Calendar.DAY_OF_MONTH));
        endDataPicker.setCancelable(false);
        endDataPicker.setTitle(getResources().getString(R.string.select_date_tip));

        selectStartDateButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startDataPicker.show();
            }
        });
        selectEndDateButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                endDataPicker.show();
            }
        });
        doneButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(BasicInfoActivity.this);
                CalendarUtil calendar_util = new CalendarUtil(BasicInfoActivity.this);
                String _start_date = startDateEdit.getText().toString();
                String _start_weight = startWeightEdit.getText().toString();
                String _end_date = endDateEdit.getText().toString();
                String _end_weight = endWeightEdit.getText().toString();
                if (_start_date.length() == 0 || _start_weight.length() == 0 || _end_date.length() == 0 || _end_weight.length() == 0) {
                    dlgAlert.setMessage(getResources().getString(R.string.basic_info_empty_field));
                    dlgAlert.setTitle(getResources().getString(R.string.message));
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok), null);
                    dlgAlert.create().show();
                    return;
                }

                if (_start_date.compareTo(_end_date) > 0) {
                    dlgAlert.setMessage(getResources().getString(R.string.date_inconsistent));
                    dlgAlert.setTitle(getResources().getString(R.string.message));
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok), null);
                    dlgAlert.create().show();
                    return;
                }

                calendar_util.openDatabase();
                calendar_util.updateBasic(_start_weight, _start_date, _end_weight, _end_date);
                calendar_util.closeDatabase();

                dlgAlert.setMessage(getResources().getString(R.string.add_basic_ok));
                dlgAlert.setTitle(getResources().getString(R.string.message));
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                BasicInfoActivity.this.finish();
                            }
                        });
                dlgAlert.create().show();
            }
        });
        cancelButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                BasicInfoActivity.this.finish();
            }
        });
    }
}

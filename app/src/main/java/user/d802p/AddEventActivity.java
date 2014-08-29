package user.d802p;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import user.d802p.R;
import android.content.Intent;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddEventActivity extends Activity {

    private String TAG = "CalendarAddEvent";
    private EditText dateEdit = null;
    private EditText foodEdit = null;
    private EditText calEdit = null;
    private EditText numEdit = null;
    private EditText clazzEdit = null;
    private Button browseButton = null;
    private Button doneButton = null;
    private Button cancelButton = null;
    private CheckBox addToDBCheck = null;

    @Override
    protected void onResume() {
        super.onResume();

        // Check if we should apply value from database.
        int i;
        String food_selection = BrowseDatabaseActivity.getFoodSelection();
        if (food_selection != null) {
            // Search with db
            CalendarUtil calendar_util = new CalendarUtil(AddEventActivity.this);
            calendar_util.openDatabase();
            ArrayList<CalendarUtil.struct_data> data = calendar_util.queryData();
            for (i = 0 ; i < data.size() ; ++i) {
                if (data.get(i)._name.equalsIgnoreCase(food_selection)) {
                    foodEdit.setText(data.get(i)._name);
                    calEdit.setText(data.get(i)._cal);
                    numEdit.setText("1");
                    clazzEdit.setText(data.get(i)._class);
                    break;
                }
            }
            calendar_util.closeDatabase();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_event);

        Intent intent = getIntent();
        String selected_date = intent.getStringExtra("CurrentDate");

        dateEdit = (EditText) this.findViewById(R.id.date_value);
        foodEdit = (EditText) this.findViewById(R.id.food_value);
        calEdit = (EditText) this.findViewById(R.id.cal_value);
        numEdit = (EditText) this.findViewById(R.id.num_value);
        clazzEdit = (EditText) this.findViewById(R.id.clazz_value);
        addToDBCheck = (CheckBox) this.findViewById(R.id.add_to_db_text);
        browseButton = (Button) this.findViewById(R.id.browse_db);
        doneButton = (Button) this.findViewById(R.id.add_event_done);
        cancelButton = (Button) this.findViewById(R.id.add_event_cancel);

        dateEdit.setEnabled(false);
        dateEdit.setGravity(Gravity.CENTER);
        dateEdit.setText(selected_date);
        foodEdit.setGravity(Gravity.CENTER);
        calEdit.setGravity(Gravity.CENTER);
        numEdit.setGravity(Gravity.CENTER);
        clazzEdit.setEnabled(false);
        clazzEdit.setGravity(Gravity.CENTER);
        addToDBCheck.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    numEdit.setText("1");
                    numEdit.setEnabled(false);
                    clazzEdit.setEnabled(true);
                } else {
                    numEdit.setEnabled(true);
                    clazzEdit.setEnabled(false);
                }
            }
        });
        browseButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(AddEventActivity.this, BrowseDatabaseActivity.class);
                startActivity(intent);
            }
        });
        doneButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(AddEventActivity.this);
                CalendarUtil calendar_util = new CalendarUtil(AddEventActivity.this);
                String _date = dateEdit.getText().toString();
                String _food = foodEdit.getText().toString();
                String _cal = calEdit.getText().toString();
                String _num = numEdit.getText().toString();
                String _clazz = clazzEdit.getText().toString();
                boolean _add_to_db = addToDBCheck.isChecked();
                if (_date.length() == 0 || _food.length() == 0 || _cal.length() == 0 || _num.length() == 0 ||
                      (_add_to_db && _clazz.length() == 0)) {
                    dlgAlert.setMessage(getResources().getString(R.string.add_event_empty_field));
                    dlgAlert.setTitle(getResources().getString(R.string.message));
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok), null);
                    dlgAlert.create().show();
                    return;
                }

                calendar_util.openDatabase();
                calendar_util.createEvent(_date, _food, _cal, _num);
                if (_add_to_db) {
                    calendar_util.createData(_clazz, _food, _cal);
                }
                calendar_util.closeDatabase();

                dlgAlert.setMessage(getResources().getString(R.string.add_event_ok));
                dlgAlert.setTitle(getResources().getString(R.string.message));
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            AddEventActivity.this.finish();
                        }
                });
                dlgAlert.create().show();
            }
        });
        cancelButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AddEventActivity.this.finish();
            }
        });
    }
}

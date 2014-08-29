package user.d802p;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class EditWeightActivity extends Activity {

    private String TAG = "EditWeightActivity";
    private EditText dateEdit = null;
    private EditText weightEdit = null;
    private Button doneButton = null;
    private Button cancelButton = null;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_weight);

        Intent intent = getIntent();
        String selected_date = intent.getStringExtra("CurrentDate");

        dateEdit = (EditText) this.findViewById(R.id.date_value);
        weightEdit = (EditText) this.findViewById(R.id.weight_value);
        doneButton = (Button) this.findViewById(R.id.edit_weight_done);
        cancelButton = (Button) this.findViewById(R.id.edit_weight_cancel);

        dateEdit.setGravity(Gravity.CENTER);
        weightEdit.setGravity(Gravity.CENTER);

        dateEdit.setEnabled(false);
        dateEdit.setText(selected_date);

        doneButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(EditWeightActivity.this);
                CalendarUtil calendar_util = new CalendarUtil(EditWeightActivity.this);
                String _date = dateEdit.getText().toString();
                String _weight = weightEdit.getText().toString();
                if (_date.length() == 0 || _weight.length() == 0) {
                    dlgAlert.setMessage(getResources().getString(R.string.edit_weight_empty_field));
                    dlgAlert.setTitle(getResources().getString(R.string.message));
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok), null);
                    dlgAlert.create().show();
                    return;
                }

                calendar_util.openDatabase();
                calendar_util.updateWeight( _date, _weight);
                calendar_util.closeDatabase();

                dlgAlert.setMessage(getResources().getString(R.string.add_weight_ok));
                dlgAlert.setTitle(getResources().getString(R.string.message));
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditWeightActivity.this.finish();
                            }
                        });
                dlgAlert.create().show();
            }
        });
        cancelButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                EditWeightActivity.this.finish();
            }
        });
    }
}

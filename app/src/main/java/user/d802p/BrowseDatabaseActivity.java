package user.d802p;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import user.d802p.R;

public class BrowseDatabaseActivity extends Activity {

    private static String food_selection = null;

    public static String getFoodSelection() {
        String selection = food_selection;
        food_selection = null;
        return selection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_browse_database);
        createTableView();
    }

    private void createTableView() {
        int i;
        int rowViewId = 1;
        int ctrlViewId = 1001;
        RelativeLayout table = (RelativeLayout) findViewById(R.id.tableLayout);
        for (i = 0 ; i < table.getChildCount() ; i++)
        {
            try
            {
                RelativeLayout child = (RelativeLayout) table.getChildAt(i);
                child.removeAllViews();
            }
            catch (java.lang.ClassCastException e) {}
        }
        table.removeAllViews();
        RelativeLayout rowView;
        RelativeLayout.LayoutParams params;
        TextView rowText;
        Button rowButton;

        CalendarUtil calendar_util = new CalendarUtil(BrowseDatabaseActivity.this);
        calendar_util.openDatabase();
        ArrayList<CalendarUtil.struct_data> data = calendar_util.queryData();
        calendar_util.closeDatabase();

        // add title
        rowView = new RelativeLayout(this);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
        rowView.setLayoutParams(params);
        rowView.setId(rowViewId);
        rowText = new TextView(this);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        rowText.setLayoutParams(params);
        rowText.setTextSize(18);
        rowText.setText(getResources().getString(R.string.database_count) + String.valueOf(data.size()));
        rowView.addView(rowText);
        table.addView(rowView);
        rowViewId++;

        for (i = 0 ; i < data.size() ; ++i) {
            rowView = new RelativeLayout(this);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
            params.topMargin = 20;
            params.addRule(RelativeLayout.BELOW, rowViewId - 1);
            rowView.setLayoutParams(params);
            rowView.setId(rowViewId);

            String item_str = getResources().getString(R.string.clazz) + data.get(i)._class;
            item_str += ", " + getResources().getString(R.string.food) + data.get(i)._name;
            item_str += ", " + getResources().getString(R.string.cal) + data.get(i)._cal;

            rowText = new TextView(this);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.topMargin = 20;
            params.addRule(RelativeLayout.BELOW, rowViewId - 1);
            rowText.setLayoutParams(params);
            rowText.setTextSize(18);
            rowText.setText(item_str);
            rowView.addView(rowText);

            table.addView(rowView);
            rowViewId++;

            //
            rowView = new RelativeLayout(this);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
            params.topMargin = 20;
            params.addRule(RelativeLayout.BELOW, rowViewId - 1);
            rowView.setLayoutParams(params);
            rowView.setId(rowViewId);

            rowButton = new Button(this);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rowButton.setId(ctrlViewId);
            rowButton.setLayoutParams(params);
            rowButton.setText(getResources().getString(R.string.select));
            rowButton.setTag(data.get(i)._name);
            rowButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    food_selection = (String) v.getTag();
                    BrowseDatabaseActivity.this.finish();
                }
            });
            rowView.addView(rowButton);

            rowButton = new Button(this);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.RIGHT_OF, ctrlViewId);
            rowButton.setLayoutParams(params);
            rowButton.setText(getResources().getString(R.string.delete));
            rowButton.setTag(data.get(i)._id);
            rowButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String id_to_remove = (String) v.getTag();
                    CalendarUtil calendar_util = new CalendarUtil(BrowseDatabaseActivity.this);
                    calendar_util.openDatabase();
                    calendar_util.deleteData(id_to_remove);
                    calendar_util.closeDatabase();
                    createTableView();
                }
            });
            rowView.addView(rowButton);

            table.addView(rowView);
            rowViewId++;
            ctrlViewId++;
        }
    }

}

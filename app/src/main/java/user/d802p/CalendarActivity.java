package user.d802p;

import android.app.Activity;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.ValueDependentColor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

public class CalendarActivity extends FragmentActivity implements LinearLayout.OnTouchListener {

    private String TAG = "CalendarActivity";
    private GregorianCalendar month;// calendar instances.
    private CalendarAdapter adapter;// adapter instance
    private Handler handler;// for grabbing some event values for showing the dot
    private String selectedGridDate = null;
    // needs showing the event marker
    private RelativeLayout functionLayout;
    private LinearLayout contentLayout;
    private LinearLayout controlLayout;
    static final int MIN_DISTANCE = 100;// TODO change this runtime based on screen resolution. for 1920x1080 is to small the 100 distance
    private float downX, downY, upX, upY;
    private DotView slideDot;
    private ScrollView scrollView;
    private Button leftButton;
    private Button rightButton;
    private boolean monthMode = true;
    static int currPage = 0;

    public void onRightToLeftSwipe() {
        Log.i(TAG, "RightToLeftSwipe!");
        slideDot.SetNextPage();
        currPage = slideDot.GetCurrPage();
        handler.post(calendarUpdater);
    }

    public void onLeftToRightSwipe() {
        Log.i(TAG, "LeftToRightSwipe!");
        slideDot.SetPrevPage();
        currPage = slideDot.GetCurrPage();
        handler.post(calendarUpdater);
    }

    public void onTopToBottomSwipe() {
        Log.i(TAG, "onTopToBottomSwipe!");
        // activity.doSomething();
    }

    public void onBottomToTopSwipe() {
        Log.i(TAG, "onBottomToTopSwipe!");
        // activity.doSomething();
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true;
                    }
                } else {
                    Log.i(TAG, "Swipe was only " + Math.abs(deltaX) + " long horizontally, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                // swipe vertical?
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe();
                        return true;
                    }
                } else {
                    Log.i(TAG, "Swipe was only " + Math.abs(deltaX) + " long vertically, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                return false; // no swipe horizontally and no swipe vertically
            }// case MotionEvent.ACTION_UP:
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(calendarUpdater);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calendar);
        Locale.setDefault(CalendarUtil.defaultLocal);

        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        functionLayout = (RelativeLayout) findViewById(R.id.function);
        controlLayout = (LinearLayout) findViewById(R.id.control);
        contentLayout = (LinearLayout) findViewById(R.id.text);
        // contentLayout.setOnTouchListener(this);
        scrollView.setOnTouchListener(this);
        scrollView.requestDisallowInterceptTouchEvent(true);
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        slideDot = new DotView(this);

        RelativeLayout.LayoutParams params;
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        slideDot.setLayoutParams(params);
        controlLayout.addView(slideDot);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.arg1) {
                    case CalendarUtil.MESSAGE_SET_SELECTION:
                        GridView gridview = (GridView) findViewById(R.id.gridview);
                        int active_position = msg.arg2;
                        gridview.performItemClick(gridview.getChildAt(active_position),
                                active_position,
                                gridview.getAdapter().getItemId(active_position));
                        break;
                }
            }
        };
        adapter = new CalendarAdapter(this, month, handler);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format(CalendarUtil.titledateFormat, month));

        RelativeLayout previous = (RelativeLayout) findViewById(R.id.previous);

        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });

        RelativeLayout next = (RelativeLayout) findViewById(R.id.next);
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();
            }
        });

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v,
                                    int position, long id) {
                CleanupPage();
                Log.d(TAG, "Gridview selected on position : " + String.valueOf(position));
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                selectedGridDate = CalendarAdapter.dayString.get(position);
                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*","");// taking last part of date. ie; 2 from 2012-12-02.
                int gridvalue = Integer.parseInt(gridvalueString);
                // navigate to next or previous month on clicking offdays.
                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);

                if (slideDot.GetCurrPage() == 0) {
                    DrawFirstPage();
                } else {
                    DrawSecondPage();
                }
            }
        });
    }

    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + 1);
        }

    }

    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }

    }

    public void refreshCalendar() {
        TextView title = (TextView) findViewById(R.id.title);
        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some calendar items
        title.setText(android.text.format.DateFormat.format(CalendarUtil.titledateFormat, month));
    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            CalendarUtil calendar_util = new CalendarUtil(CalendarActivity.this);
            calendar_util.openDatabase();
            ArrayList<CalendarUtil.struct_weight> weights = calendar_util.queryWeight();
            ArrayList<CalendarUtil.struct_event> events = calendar_util.queryEvent();
            ArrayList<CalendarUtil.struct_basic> basics = calendar_util.queryBasic();
            calendar_util.closeDatabase();

            adapter.setBasics(basics);
            adapter.setWeights(weights);
            adapter.setEvents(events);
            adapter.notifyDataSetChanged();
            if (selectedGridDate == null)
                selectedGridDate = adapter.curentDateString;
            Log.d(TAG, "Update calendar done");
        }
    };

    private void DrawFirstPage() {
        TextView rowTextView;
        Button rowButton;
        RelativeLayout.LayoutParams params;

        leftButton.setText(getResources().getString(R.string.basic_info));
        rightButton.setText(getResources().getString(R.string.add));
        leftButton.setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(CalendarActivity.this, BasicInfoActivity.class);
                    startActivity(intent);
                }
            }
        );
        rightButton.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(CalendarActivity.this, EditWeightActivity.class);
                        intent.putExtra("CurrentDate", selectedGridDate);
                        startActivity(intent);
                    }
                }
        );

        int i, j;
        CalendarUtil calendar_util = new CalendarUtil(CalendarActivity.this);
        calendar_util.openDatabase();
        ArrayList<CalendarUtil.struct_weight> weights = calendar_util.queryWeight();
        calendar_util.closeDatabase();
        GraphView.GraphViewData[] graphData = null;
        String[] labels = null;
        int dataPoint = 0;

        if (monthMode) {
            dataPoint = 30;
            graphData = new GraphView.GraphViewData[dataPoint];
            labels = new String[dataPoint];

            for (i = 0 ; i < graphData.length ; ++i) {
                String date_to_search = CalendarUtil.getDate(System.currentTimeMillis() - 86400000L * (long) (graphData.length - i));
                int found_weight = 0;
                for (j = 0 ; j < weights.size() ; ++j) {
                    if (weights.get(j)._date.equalsIgnoreCase(date_to_search)) {
                        found_weight = Integer.parseInt(weights.get(j)._weight);
                        break;
                    }
                }
                graphData[i] = new GraphView.GraphViewData(i, (double) found_weight);
                if (i % 2 == 0)
                    labels[i] = date_to_search.substring(date_to_search.length() - 2);
                else
                    labels[i] = "";
            }
        } else {
            dataPoint = 7;
            graphData = new GraphView.GraphViewData[dataPoint];
            labels = new String[dataPoint];

            for (i = 0 ; i < graphData.length ; ++i) {
                String date_to_search = CalendarUtil.getDate(System.currentTimeMillis() - 86400000L * (long) (graphData.length - i));
                int found_weight = 0;
                for (j = 0 ; j < weights.size() ; ++j) {
                    if (weights.get(j)._date.equalsIgnoreCase(date_to_search)) {
                        found_weight = Integer.parseInt(weights.get(j)._weight);
                        break;
                    }
                }
                graphData[i] = new GraphView.GraphViewData(i, (double) found_weight);
                labels[i] =  date_to_search.substring(date_to_search.length() - 2);
            }
        }

        GraphViewSeries.GraphViewSeriesStyle realWeightSeriesStyle = new GraphViewSeries.GraphViewSeriesStyle();
        realWeightSeriesStyle.setValueDependentColor(new ValueDependentColor() {
            @Override
            public int get(GraphViewDataInterface data) {
                // the higher the more red
                return Color.RED;
            }
        });
        GraphViewSeries realWeightSeries = new GraphViewSeries("realWeight", realWeightSeriesStyle, graphData);
        GraphView graphView = new LineGraphView(
                CalendarActivity.this // context
                , getResources().getString(R.string.statistics) // heading
        );
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 500);
        graphView.setLayoutParams(params);
        graphView.addSeries(realWeightSeries); // data
        graphView.setHorizontalLabels(labels);
        contentLayout.addView(graphView);

        Button switchMonthWeek = new Button(this);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switchMonthWeek.setLayoutParams(params);
        switchMonthWeek.setText(getResources().getString(R.string.week_month));
        switchMonthWeek.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (monthMode)
                            monthMode = false;
                        else
                            monthMode = true;
                        CleanupPage();
                        DrawFirstPage();
                    }
                }
        );
        contentLayout.addView(switchMonthWeek);
    }

    private void CleanupPage() {
        if (((LinearLayout) contentLayout).getChildCount() > 0) {
            ((LinearLayout) contentLayout).removeAllViews();
        }
    }

    private void DrawSecondPage() {
        TextView rowTextView;
        Button rowButton;
        RelativeLayout.LayoutParams params;

        leftButton.setText(getResources().getString(R.string.database));
        rightButton.setText(getResources().getString(R.string.add));
        leftButton.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(CalendarActivity.this, BrowseDatabaseActivity.class);
                        startActivity(intent);
                    }
                }
        );
        rightButton.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(CalendarActivity.this, AddEventActivity.class);
                        intent.putExtra("CurrentDate", selectedGridDate);
                        startActivity(intent);
                    }
                }
        );

        // Can be improved, we don't need to query every times
        CalendarUtil calendar_util = new CalendarUtil(CalendarActivity.this);
        calendar_util.openDatabase();
        ArrayList<CalendarUtil.struct_event> events = calendar_util.queryEvent();
        calendar_util.closeDatabase();

        rowTextView = new TextView(CalendarActivity.this);
        rowTextView.setTextSize(35);
        rowTextView.setText(getResources().getString(R.string.today_event));
        rowTextView.setTextColor(Color.BLACK);
        // contentLayout.addView(rowTextView);

        for (int i = 0; i < events.size(); i++) {
            if (!events.get(i)._date.equals(selectedGridDate)) {
                continue;
            }
            String item_str = getResources().getString(R.string.food) + events.get(i)._name;
            item_str += ", " + getResources().getString(R.string.cal) + events.get(i)._cal;
            item_str += "," + getResources().getString(R.string.num) + events.get(i)._num;

            rowTextView = new TextView(CalendarActivity.this);
            rowTextView.setTextSize(25);
            rowTextView.setText(item_str);
            rowTextView.setTextColor(Color.BLACK);
            contentLayout.addView(rowTextView);
            rowButton = new Button(CalendarActivity.this);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rowButton.setTag(events.get(i)._id);
            rowButton.setLayoutParams(params);
            rowButton.setText(getResources().getString(R.string.delete));
            rowButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String id_to_remove = (String) v.getTag();
                    CalendarUtil calendar_util = new CalendarUtil(CalendarActivity.this);
                    calendar_util.openDatabase();
                    calendar_util.deleteEvent(id_to_remove);
                    calendar_util.closeDatabase();
                    CalendarActivity.this.handler.post(calendarUpdater);
                }
            });
            contentLayout.addView(rowButton);
        }
    }
}

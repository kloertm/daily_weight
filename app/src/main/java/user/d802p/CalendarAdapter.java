package user.d802p;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {
    private Context mContext;
    private String TAG = "CalendarAdapter";
    private java.util.Calendar month;
    public GregorianCalendar pmonth; // calendar instance for previous month
    /**
     * calendar instance for previous month for getting complete view
     */
    public GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDay;
    int maxWeeknumber;
    int maxP;
    int calMaxP;
    int mnthlength;
    String itemvalue, curentDateString;
    DateFormat df;

    ArrayList<CalendarUtil.struct_basic> basics;
    private ArrayList<CalendarUtil.struct_weight> weights;
    private ArrayList<CalendarUtil.struct_event> events;
    public static List<String> dayString;
    private View previousView;
    private Handler handler;

    public CalendarAdapter(Context c, GregorianCalendar monthCalendar, Handler callback) {
        CalendarAdapter.dayString = new ArrayList<String>();
        Locale.setDefault(CalendarUtil.defaultLocal);
        month = monthCalendar;
        handler = callback;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
        mContext = c;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);
        this.basics = new ArrayList<CalendarUtil.struct_basic>();
        this.weights = new ArrayList<CalendarUtil.struct_weight>();
        this.events = new ArrayList<CalendarUtil.struct_event>();
        df = new SimpleDateFormat(CalendarUtil.dateFormat, CalendarUtil.defaultLocal);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();
    }

    public void setBasics(ArrayList<CalendarUtil.struct_basic> basics) {
        this.basics = basics;
    }

    public void setWeights(ArrayList<CalendarUtil.struct_weight> weights) {
        this.weights = weights;
    }

    public void setEvents(ArrayList<CalendarUtil.struct_event> events) {
        this.events = events;
    }

    public int getCount() {
        return dayString.size();
    }

    public Object getItem(int position) {
        return dayString.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);
        }
        dayView = (TextView) v.findViewById(R.id.date);
        // separates daystring into parts.
        String[] separatedTime = dayString.get(position).split("-");
        // taking last part of date. ie; 2 from 2012-12-02
        String gridvalue = separatedTime[2].replaceFirst("^0*", "");
        // checking whether the day is in current month or not.
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            // setting offdays to white color.
            dayView.setTextColor(Color.WHITE);
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            dayView.setTextColor(Color.WHITE);
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            // setting curent month's days in blue color.
            dayView.setTextColor(Color.BLUE);
        }

        if (dayString.get(position).equals(curentDateString)) {
            Log.d(TAG, "Set selected on " + curentDateString);
            if (true) {
                Message message_set_selection = new Message();
                Bundle data_set_selection = new Bundle();
                ArrayList<Object> list_set_selection = new ArrayList<Object>();
                ArrayList bundle_list = new ArrayList();
                bundle_list.add(list_set_selection);
                data_set_selection.putParcelableArrayList("bundle_list", bundle_list);
                message_set_selection.arg1 = CalendarUtil.MESSAGE_SET_SELECTION;
                message_set_selection.arg2 = position;
                message_set_selection.setData(data_set_selection);
                handler.sendMessage(message_set_selection);
            }
            setSelected(v);
            previousView = v;
        } else {
            v.setBackgroundResource(R.drawable.list_item_background);
        }
        dayView.setText(gridvalue);

        // create date string for comparison
        String date = dayString.get(position);

        if (date.length() == 1) {
            date = "0" + date;
        }
        String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        // show icon if date is not empty and it exists in the items array
        TextView iw = (TextView) v.findViewById(R.id.date_icon);
        iw.setVisibility(View.VISIBLE);
        if (CalendarActivity.currPage == 0) {
            int today_weight = 0;
            for (int i = 0 ; i < weights.size() ; ++i) {
                if (weights.get(i)._date.equalsIgnoreCase(date)) {
                    today_weight = Integer.parseInt(weights.get(i)._weight);
                    break;
                }
            }
            double expectedWeight = CalendarUtil.getTodayExpectedWeight(basics, date);
            if (expectedWeight != 0)
                iw.setText(String.valueOf(today_weight) + " / " + String.format("%.1f", expectedWeight));
            else
                iw.setText(String.valueOf(today_weight) + " / " + "NA");
        } else if (CalendarActivity.currPage == 1) {
            int total_cal = 0;
            for (int i = 0 ; i < events.size() ; ++i) {
                if (events.get(i)._date.equalsIgnoreCase(date)) {
                    total_cal += Integer.parseInt(events.get(i)._cal) * Integer.parseInt(events.get(i)._num);
                }
            }
            iw.setText(String.valueOf(total_cal));
        }
        return v;
    }

    public View setSelected(View view) {
        if (previousView != null) {
            previousView.setBackgroundResource(R.drawable.list_item_background);
        }
        previousView = view;
        view.setBackgroundResource(R.drawable.calendar_cel_selectl);
        return view;
    }

    public void refreshDays() {
        // clear items
        // items.clear();
        dayString.clear();
        Locale.setDefault(CalendarUtil.defaultLocal);
        pmonth = (GregorianCalendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        /**
         * Calendar instance for getting a complete gridview including the three
         * month's (previous,current,next) dates.
         */
        pmonthmaxset = (GregorianCalendar) pmonth.clone();
        /**
         * setting the start date as previous month's required date.
         */
        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

        /**
         * filling calendar gridview.
         */
        for (int n = 0; n < mnthlength; n++) {
            itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            dayString.add(itemvalue);
        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }

}
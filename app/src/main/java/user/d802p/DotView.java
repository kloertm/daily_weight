package user.d802p;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

class DotView extends View {

    Paint paint = new Paint();
    int width;
    int page;

    public DotView(Context context) {
        super(context);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        setWillNotDraw(false);

        Display disp = ((WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = disp.getWidth();
        page = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (page == 0) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawCircle(width / 2 - 30, 50, 15, paint);

        if (page == 1) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawCircle(width / 2 + 30, 50, 15, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void SetPrevPage() {
        if (page == 0)
            return;
        page = 0;
        this.invalidate();
    }

    public void SetNextPage() {
        if (page == 1)
            return;
        page = 1;
        this.invalidate();
    }

    public int GetCurrPage() {
        return page;
    }
}

class Point {
    float x, y;
}
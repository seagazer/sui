package com.seagazer.sui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.seagazer.sui.util.TimeStringUtil;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 时钟显示
 */
public class ClockView extends AppCompatTextView {
    private boolean isShowDate = false;
    private Timer mTimer = new Timer();

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startTimer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTimer();
    }

    private void startTimer() {
        mTimer.schedule(mTask, 0, 30 * 1000);
    }

    private void stopTimer() {
        mTimer.cancel();
    }

    private TimerTask mTask = new TimerTask() {
        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            final String strMonth = TimeStringUtil.numberToString(calendar.get(Calendar.MONTH) + 1);
            final String strDay = TimeStringUtil.numberToString(calendar.get(Calendar.DAY_OF_MONTH));
            final String strHour = TimeStringUtil.numberToString(calendar.get(Calendar.HOUR_OF_DAY));
            final String strMinute = TimeStringUtil.numberToString(calendar.get(Calendar.MINUTE));
            post(new Runnable() {
                @Override
                public void run() {
                    if (isShowDate) {
                        setText(String.format("%s-%s   %s:%s", strMonth, strDay, strHour, strMinute));
                    } else {
                        setText(String.format("%s:%s", strHour, strMinute));
                    }
                }
            });
        }
    };

    public void setShowDate(boolean showDate) {
        isShowDate = showDate;
    }

}

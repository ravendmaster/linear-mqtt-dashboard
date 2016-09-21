package com.ravendmaster.linearmqttdashboard.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.ravendmaster.linearmqttdashboard.Log;
import com.ravendmaster.linearmqttdashboard.R;

/**
 * Created by Andrey on 15.06.2016.
 */
public class Meter extends View {

    public int X; // Переменные, доступные снаружи
    public int Y;

    Paint p = new Paint();
    Rect bounds = new Rect();


    boolean mOn = false;
    int mColorLight = -7617718;
    float value;

    final int MODE_SIMPLE = 0;
    final int MODE_VALUE = 1;
    final int MODE_PERCENTAGE = 2;
    final int MODE_LOOP = 3;

    private int mode = MODE_SIMPLE;

    float minimum_value = 0f;
    float maximum_value = 100f;
    float full_range = 101f;

    float alarm_zone_minimum = 0f;
    float alarm_zone_maximum = 0f;

    boolean decimalMode = false;

    public final static String[] modes={"Simple", "Value", "Percentage"};

    public void setDecimalMode(boolean decimalMode) {
        this.decimalMode = decimalMode;
    }

    public void setMin(float min) {
        minimum_value = min;
        full_range = maximum_value - minimum_value;
        if(full_range==0)full_range=1;
    }

    public void setMax(float max) {
        maximum_value = max;
        full_range = maximum_value - minimum_value;
        if(full_range==0)full_range=1;
    }

    public float getMin(){
        return minimum_value;
    }

    public float getMax(){
        return maximum_value;
    }

    public void setAlarmZones(float min, float max) {
        alarm_zone_minimum = min / 100;
        alarm_zone_maximum = max / 100;
    }

    String text;
    public void setText(String text){
        this.text=text;
    }

    public void setValue(float value) {
        this.value = value;
    }
    public float getValue() {
        return this.value;
    }

    public int getColorLight() {
        return mColorLight;
    }

    public void setColorLight(int colorLight) {
        mColorLight = colorLight;
        invalidate();
    }

    public Meter(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RGBLEDView,
                0, 0);

        try {
            mColorLight = a.getInteger(R.styleable.RGBLEDView_colorLight, Color.BLUE);
        } finally {
            a.recycle();
        }

        p.setAntiAlias(true);

        setLayerToHW(this);
    }


    private void setLayerToHW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (!isEnabled()) return true;


        //Log.d(getClass().getName(), event.toString());
        X = (int) event.getX();
        Y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Log.d(getClass().getName(), "DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP:
                Log.d(getClass().getName(), "UP, " + event.getAction());

                    mode++;
                    if (mode >= MODE_LOOP) {
                        mode = MODE_SIMPLE;
                    }
                    invalidate();
                    playSoundEffect(SoundEffectConstants.CLICK);

                return true;
        }


        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float density_multi=getResources().getDisplayMetrics().density;

        try {


            int left = getLeft();
            int right = getRight();
            int top = getTop();
            int bottom = getBottom();

            //int density = canvas.getDensity();
            //if(density==0) {
            //    density = 420;
            //}
            int dev = 8 * (int)density_multi;
            final int numberOfMarks = (right - left) / dev;
            int arrow_width = (int) (dev * 0.9f);

            int button_width = right - left;
            int button_height = bottom - top;

            int meter_height = (int) (button_height / 2);
            int meter_vertical_displace = (int) button_height / 2 - (meter_height / 2);

            float arrow_value;
            arrow_value = value;


            float phys_arrow_value;
            phys_arrow_value = numberOfMarks * (arrow_value - minimum_value) / full_range - 1;
            p.setStyle(Paint.Style.FILL);

            int active_color = getCurrentIndicationColor();


            for (int i = 0; i < numberOfMarks; i++) {

                if (phys_arrow_value >= i) {
                    p.setColor(active_color);
                } else {
                    p.setColor(0xFFDDDDDD);
                }

                int x;
                x = i * (button_width) / (numberOfMarks - 1);

                if (isEnabled()) {
                    p.setAlpha(255);
                } else {
                    p.setAlpha(128);
                }

                canvas.drawRect(x, meter_vertical_displace, x + arrow_width, meter_height + meter_vertical_displace, p);
            }

            p.setTextSize(meter_height * 0.9f);
            //primary_paint.setFakeBoldText(true);
            //primary_paint.setColor(0xff000000);
            String label = null;
            switch (mode) {
                case MODE_SIMPLE:
                    break;
                case MODE_PERCENTAGE:
                    int cur_percentage_value = (int) ((value - minimum_value) * 100 / full_range);
                    label = "" + cur_percentage_value + "%";
                    break;
                case MODE_VALUE:
                    label = text;
                    /*
                    if (decimalMode) {
                        label = String.valueOf(value);
                    } else {
                        label = String.valueOf((int) value);
                    }
                    */
                    break;
                default:
                    label = "n/a";
            }

            if (label != null) {

                p.getTextBounds(label, 0, label.length(), bounds);

                //подложка
                /*
                if(phys_arrow_value>0 && active_color==MyColors.getRed()) {
                    int pan_x = button_width / 2 - (bounds.right - bounds.left) / 2;
                    int pan_y = button_height / 2 - (bounds.bottom - bounds.top) / 2;
                    int over = meter_height / 12;
                    primary_paint.setColor(0x80ffffff);
                    canvas.drawRect(pan_x - over, pan_y - over, bounds.right - bounds.left + pan_x + over, bounds.bottom - bounds.top + pan_y + over, primary_paint);
                }
                */


                p.setColor(0xff000000);

                if (isEnabled()) {
                    p.setAlpha(255);
                } else {
                    p.setAlpha(128);
                }

                int text_x = button_width / 2 - (bounds.right - bounds.left) / 2;
                int text_y = button_height / 2 + (bounds.bottom - bounds.top) / 2;
                canvas.drawText(label, text_x, text_y, p);
            }

        } catch (Exception e) {
            p.setTextSize(30);
            p.setColor(0xffEE0000);
            canvas.drawText("Error!", 0, 30, p);
            canvas.drawText("Please check", 0, 60, p);
            canvas.drawText("the widget settings.", 0, 90, p);
        }

    }

    Rect setBoundsOfThreeDots() {
        p.getTextBounds("...", 0, 3, bounds);
        return bounds;
    }

    private Integer getCurrentIndicationColor() {

        if (value > minimum_value + full_range * alarm_zone_minimum) {

            if (value > maximum_value - full_range * (alarm_zone_maximum / 2)) {
                return MyColors.getRed();
            } else if (value > maximum_value - full_range * alarm_zone_maximum) {
                return MyColors.getYellow();
            } else {
                return MyColors.getGreen();
            }
        } else if (value > minimum_value + full_range * (alarm_zone_minimum / 2)) {
            return MyColors.getYellow();
        } else {
            return MyColors.getRed();
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}

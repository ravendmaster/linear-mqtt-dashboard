package com.ravendmaster.linearmqttdashboard.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.ravendmaster.linearmqttdashboard.Log;
import com.ravendmaster.linearmqttdashboard.R;

public class ButtonsSet extends View {


    public int getColorLight() {
        return colorLight;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public int getMaxButtonsPerRow() {
        return maxButtonsPerRow;
    }

    public void setMaxButtonsPerRow(int maxButtonsPerRow) {
        this.maxButtonsPerRow = maxButtonsPerRow;
        invalidate();
    }

    //public String getLabelOff() {
    //    return labelOff;
    //}

    //public String getLabelOn() {
    //    return labelOn;
    //}

    public interface OnButtonsSetEventListener {
        void OnButtonsSetPressed(ButtonsSet button, int index);
    }

    private OnButtonsSetEventListener mButtonsSetEventListener;

    public void setOnButtonsSetEventListener(OnButtonsSetEventListener l) {
        this.mButtonsSetEventListener = l;
    }

    public int X; // Переменные, доступные снаружи
    public int Y;

    //int total_button_count = 15;

    int button_height;

    Paint p = new Paint();
    Rect bounds = new Rect();

    boolean mPressed = false;

    private boolean retained;

    //String currentValue;
    public void setCurrentValue(String currentValue) {
        //Log.d("pressed_button_index", "setCurrentValue:"+currentValue);
        //currentValue=currentValue;
        pressed_button_index=null;
        for(int index=0;index<values.length;index++){
            if(getPublishValueByButtonIndex(index).equals(currentValue)){
                //Log.d("pressed_button_index", ""+index);
                if(retained) {
                    pressed_button_index = index;
                }
                mPressed = true;
                invalidate();
                break;
            }
        }
    }

    public boolean getPressed() {
        return mPressed;
    }

    boolean mOn = false;
    private int colorLight = -7617718;
    //private String labelOff = "off";
    //private String labelOn = "on";

    private int getValueCount(){
        if(values==null)return 0;

        return values.length;
    }

    private int maxButtonsPerRow;


    private int getColCount(){
        return Math.max(1,Math.min(getValueCount(), maxButtonsPerRow));
    }

    private int getRowCount() {
        int colCount=getColCount();
        if(colCount==0)return 1;

        return 1+(getValueCount()-1) / colCount;
    }

    String publishValues;

    public void setPublishValues(String publishValues){
        this.publishValues=publishValues;
        if(publishValues==null)return;

        //StringBuffer s = new StringBuffer("asd zxc 123 sdf");
        values = publishValues.split(",");
        invalidate();
        requestLayout();
    }

    public String getPublishValues(){
        return this.publishValues;
    }

    String[]values;

    public int isColorLight() {
        return colorLight;
    }

    public void setColorLight(int colorLight) {
        this.colorLight = colorLight;
        invalidate();
        //requestLayout();
    }

    public String getPublishValueByButtonIndex(int index){
        if(index>=values.length)return "";
        String value=values[index];
        String[]val_pres=value.split("\\|");
        return val_pres[0].trim();
    }

    public String getPresentationTextByButtonIndex(int index){
        if(index>=values.length)return "";
        String value=values[index];
        String[]val_pres=value.split("\\|");
        if(val_pres.length>=2){return val_pres[1];};

        return val_pres[0].trim();
    }

    public boolean isOn() {
        return mOn;
    }

    public void setOn(boolean on) {
        mOn = on;
        invalidate();
        //requestLayout();
    }

    /*
    public void setLabelOff(String text) {
        labelOff = text == null ? "" : text;
        invalidate();
        //requestLayout();
    }

    public void setLabelOn(String text) {
        labelOn = text == null ? "" : text;
        invalidate();
        requestLayout();
    }
    */

    public ButtonsSet(Context context, AttributeSet attrs) {
        super(context, attrs);

        float density_multi=getResources().getDisplayMetrics().density;

        button_height=40*(int)density_multi;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RGBLEDView,
                0, 0);

        try {
            mOn = a.getBoolean(R.styleable.RGBLEDView_isOn, false);
            colorLight = a.getInteger(R.styleable.RGBLEDView_colorLight, Color.BLUE);
            //labelOff = a.getString(R.styleable.RGBLEDView_labelOff);
            //labelOn = a.getString(R.styleable.RGBLEDView_labelOn);
        } finally {
            a.recycle();
        }
        //if (labelOff == null) labelOff = "";
        //if (labelOn == null) labelOn = "button";

        p.setAntiAlias(true);

        maxButtonsPerRow = 4;
        //requestLayout();

        if(isInEditMode()){
            setPublishValues("1,2,3,4,5,6");
        }
    }

    Integer pressed_button_index=null;

    Integer getButtonIndexByXY(int x, int y){
        int button_by_x = x / (getMeasuredWidth()/getColCount());
        int button_by_y = y / button_height;
        int button_index = button_by_x + button_by_y * getColCount();
        if(button_index<getValueCount()) {

            if(getPublishValueByButtonIndex(button_index).isEmpty())return null;//нечего отправлять, кнопка нет

            return button_index;
        }
        return null;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (!isEnabled()) return true;

        X = (int) event.getX();
        Y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(getClass().getName(), "DOWN");
/*
                Integer button_index = getButtonIndexByXY(X, Y);
                if(button_index!=null) {
                    pressed_button_index=button_index;
                    mPressed = true;
                    //if (mButtonsSetEventListener != null) mButtonsSetEventListener.OnButtonsSetPressed(this, button_index);
                    invalidate();
                    //playSoundEffect(SoundEffectConstants.CLICK);
                }
*/
                break;
            case MotionEvent.ACTION_CANCEL:
                mPressed = false;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                Log.d(getClass().getName(), "UP, " + event.getAction());
                //mPressed = false;
                //if (mButtonsSetEventListener != null) mButtonsSetEventListener.OnMyButtonUp(this);

                Integer new_button_index = getButtonIndexByXY(X, Y);
                if(new_button_index!=null) {
                    //if(retained) {
                        pressed_button_index = new_button_index;

                    if(!retained) {
                        delayedButtonOffHandler.removeMessages(0);
                        delayedButtonOffHandler.sendEmptyMessageDelayed(0, 100);
                    }
                    //mPressed = true;
                    if (mButtonsSetEventListener != null) mButtonsSetEventListener.OnButtonsSetPressed(this, new_button_index);
                    invalidate();
                    playSoundEffect(SoundEffectConstants.CLICK);
                }

                invalidate();
                //requestLayout();
                break;
        }

        return true;
    }

    Handler delayedButtonOffHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            pressed_button_index=null;
            invalidate();
            return false;
        }
    });

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        int mTextWidth = 200;
        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = MeasureSpec.getSize(w) - (int) mTextWidth + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 1);

        setMeasuredDimension(widthMeasureSpec, button_height * getRowCount());
    }


    @Override
    protected void onDraw(Canvas canvas) {



        //Rect rect = canvas.getClipBounds();
        int buttonColor = colorLight;

        int left = getLeft();
        int right = getRight();

        int top = getTop();
        int bottom = getBottom();


        int totalWidth = right - left;
        int buttonWidth = totalWidth / getColCount();


        int button_count=0;
        for (int row = 0; row < getRowCount(); row++) {
            int y = button_height * row;
            for (int col = 0; col < getColCount(); col++) {

                //if (button_count == getValueCount()) break;

                int x = col * buttonWidth;

                if(!getPublishValueByButtonIndex(button_count).isEmpty()) {
                    DrawButton(canvas, buttonColor, x, y, x + buttonWidth, y + button_height, button_count);
                }

                button_count++;
            }
        }

    }

    private void DrawButton(Canvas canvas, int buttonColor, int left, int top, int right, int bottom, int current_button_index) {



        int strokeWidth = 4;

        int button_width = right - left - strokeWidth;
        int button_height = bottom - top - strokeWidth;

        int button_displace_y = strokeWidth / 2;

        int alpha;
        String button_label;
        int displace;
        int displace_bound = 6;

        String label=null;
        if(current_button_index>=getValueCount()){
            label="";
        }else{
            label=getPresentationTextByButtonIndex(current_button_index);
        }


        if (isEnabled()) {
            if (pressed_button_index!=null && pressed_button_index==current_button_index) {
                alpha = 196;
                //button_label = labelOff.isEmpty() ? labelOn : labelOff;
                button_label=label;
                displace = displace_bound;
            } else {
                alpha = 128;
                //button_label = labelOn;
                button_label=label;
                displace = 0;
            }
        } else {
            alpha = 32;
            displace = 0;
            //button_label = labelOn;
            button_label=label;
        }

        p.setStyle(Paint.Style.FILL);

        p.setColor(buttonColor); // основа кнопки
        p.setAlpha(alpha);
        canvas.drawRect(strokeWidth + left, button_displace_y+top, button_width + left, button_height + button_displace_y+top, p);

        //углубление/тень
        canvas.drawRect(strokeWidth + left, button_displace_y + displace+top, button_width - displace_bound + left, button_height + button_displace_y - displace_bound + displace+top, p);


        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(strokeWidth);
        p.setColor(colorLight); // обводка кнопки
        p.setAlpha(128);
        //canvas.drawRect(strokeWidth, button_displace_y, button_width, button_height + button_displace_y, primary_paint);

        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(1);

        p.setColor(0xffffffff);
        p.setAlpha(255);

        p.setTextSize(button_height * 0.3f);

        setBoundsOfThreeDots();
        int widthOfThreeDots = bounds.right - bounds.left;
        String text = button_label.toUpperCase();
        float[] measuredWidth = new float[100];
        int cnt = p.breakText(text, true, button_width - widthOfThreeDots - 4, measuredWidth);

        if (cnt < text.length()) {
            text = text.substring(0, cnt);
            text += "...";
        }
        p.getTextBounds(text, 0, text.length(), bounds);




        canvas.drawText(text, button_width / 2 - (bounds.right - bounds.left) / 2 + left, displace / 2 + button_height / 2 + (bounds.bottom - bounds.top) / 2+top, p);
    }

    Rect setBoundsOfThreeDots() {
        p.getTextBounds("...", 0, 3, bounds);
        return bounds;
    }


}

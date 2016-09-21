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

public class MyButton extends View {

    public int getColorLight() {
        return colorLight;
    }

    public String getLabelOff() {
        return labelOff;
    }

    public String getLabelOn() {
        return labelOn;
    }

    public interface OnMyButtonEventListener {
        void OnMyButtonDown(MyButton button);

        void OnMyButtonUp(MyButton button);
    }

    private OnMyButtonEventListener mMyButtonEventListener;

    public void setOnMyButtonEventListener(OnMyButtonEventListener l) {
        mMyButtonEventListener = l;
    }

    public int X; // Переменные, доступные снаружи
    public int Y;

    Paint p = new Paint();
    Rect bounds=new Rect();

    boolean mPressed = false;

    public void setPressed(boolean pressed){
        mPressed=pressed;
    }
    public boolean getPressed(){return mPressed;}

    boolean mOn=false;
    private int colorLight = -7617718;
    private String labelOff ="off";
    private String labelOn ="on";

    public int isColorLight() {
        return colorLight;
    }

    public void setColorLight(int colorLight) {
        this.colorLight = colorLight;
        invalidate();
        requestLayout();
    }

    public boolean isOn() {
        return mOn;
    }

    public void setOn(boolean on) {
        mOn = on;
        invalidate();
        requestLayout();
    }

    public void setLabelOff(String text) {
        labelOff = text==null?"":text;
        invalidate();
        requestLayout();
    }

    public void setLabelOn(String text) {
        labelOn = text==null?"":text;
        invalidate();
        requestLayout();
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RGBLEDView,
                0, 0);

        try {
            mOn = a.getBoolean(R.styleable.RGBLEDView_isOn, false);
            colorLight = a.getInteger(R.styleable.RGBLEDView_colorLight, Color.BLUE);
            labelOff = a.getString(R.styleable.RGBLEDView_labelOff);
            labelOn = a.getString(R.styleable.RGBLEDView_labelOn);
        } finally {
            a.recycle();
        }
        if (labelOff == null) labelOff = "";
        if (labelOn == null) labelOn = "button";

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

        X = (int) event.getX();
        Y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(getClass().getName(), "DOWN");
                mPressed = true;
                if (mMyButtonEventListener != null) mMyButtonEventListener.OnMyButtonDown(this);
                invalidate();
                requestLayout();
                playSoundEffect(SoundEffectConstants.CLICK);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(getClass().getName(), "UP, " + event.getAction());
                mPressed = false;
                if (mMyButtonEventListener != null) mMyButtonEventListener.OnMyButtonUp(this);
                invalidate();
                requestLayout();
                break;
        }

        return true;
    }
/*
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


        setMeasuredDimension(100, 100);
    }
*/
    @Override
    protected void onDraw(Canvas canvas) {
        //Rect rect = canvas.getClipBounds();
        int buttonColor = colorLight;

        int left = getLeft();
        int right = getRight();
        int top = getTop();
        int bottom = getBottom();

        //int size = right - left;
        int x_center = (right - left) / 2;
        int y_center = (bottom - top) / 2;

        int strokeWidth = 4;

        //int shadow_displace_y = (int) ((bottom - top) * 0.2f);
        int button_width = right - left - strokeWidth;
        int button_height = bottom - top - strokeWidth;


        int button_displace_y = strokeWidth / 2;

        int alpha;
        String button_label;
        int displace;
        int displace_bound = 6;



        if (isEnabled()) {
            if (mPressed) {
                alpha = 196;
                button_label = labelOff.isEmpty()? labelOn : labelOff;
                displace = displace_bound;
            } else {
                alpha = 128;
                button_label = labelOn;
                displace = 0;
            }
        } else {
            alpha = 32;
            displace = 0;
            button_label = labelOn;
        }

        p.setStyle(Paint.Style.FILL);
        //primary_paint.setColor(0xFFFFFFFF); // тень
        //canvas.drawRect(strokeWidth, button_displace_y, button_width, button_height + button_displace_y, primary_paint);
        //RectF rect=new RectF(strokeWidth,shadow_displace_y,button_width,button_height + shadow_displace_y);
        //canvas.drawRoundRect(rect, primary_paint);

        p.setColor(buttonColor); // основа кнопки
        p.setAlpha(alpha);
        canvas.drawRect(strokeWidth, button_displace_y, button_width, button_height + button_displace_y, p);

        //углубление/тень
        canvas.drawRect(strokeWidth , button_displace_y + displace, button_width - displace_bound, button_height + button_displace_y - displace_bound + displace, p);


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
        int widthOfThreeDots=bounds.right-bounds.left;
        String text = button_label.toUpperCase();
        float[] measuredWidth = new float[100];
        int cnt = p.breakText(text, true, button_width - widthOfThreeDots - 4, measuredWidth);

        if(cnt<text.length()) {
            text=text.substring(0, cnt);
            text+="...";
        }
        p.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, button_width / 2 - (bounds.right-bounds.left) / 2, displace / 2 + button_height / 2 + (bounds.bottom-bounds.top)/2, p);

    }

    Rect setBoundsOfThreeDots(){
        p.getTextBounds("...", 0, 3, bounds);
        return bounds;
    }

}

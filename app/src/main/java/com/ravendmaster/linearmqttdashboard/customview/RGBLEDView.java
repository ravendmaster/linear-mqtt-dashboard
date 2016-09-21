package com.ravendmaster.linearmqttdashboard.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ravendmaster.linearmqttdashboard.R;

public class RGBLEDView extends View {

    public int X; // Переменные, доступные снаружи
    public int Y;

    static final Paint p = new Paint();

    int mSize;
    boolean mOn;
    int mColorLight = -7617718;

    public int isColorLight() {
        return mColorLight;
    }

    public void setColorLight(int colorLight) {
        mColorLight = colorLight;
        invalidate();
        requestLayout();
    }

    public int isSize(){return mSize;}
    public void setmSize(int size){
        mSize=size;
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

    public RGBLEDView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RGBLEDView,
                0, 0);

        try {
            mOn = a.getBoolean(R.styleable.RGBLEDView_isOn, false);
            mColorLight = a.getInteger(R.styleable.RGBLEDView_colorLight, Color.RED);
            mSize = a.getInteger(R.styleable.RGBLEDView_size, 96);
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
        X = (int) event.getX();
        Y = (int) event.getY();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        //Rect rect = canvas.getClipBounds();

        int left=getLeft();
        int right=getRight();
        int top=getTop();
        int bottom=getBottom();

        int size=Math.min(right-left, bottom-top);
        int x_center=(right-left)/2;
        int y_center=(bottom-top)/2;

        float alpha_k=1;
        if(!isEnabled()){
            alpha_k=0.3f;
        }


        if (mOn) {

            int a = Color.alpha(mColorLight);
            int r = Color.red(mColorLight);
            int g = Color.green(mColorLight);
            int b = Color.blue(mColorLight);

            //primary_paint.setARGB( (int)(32*alpha_k), (int) (r * 0.9f), (int) (g * 0.9f), (int) (b * 0.9f));
            //canvas.drawCircle(x_center, y_center, size / 2, primary_paint); //ореол

            p.setARGB( (int)(255*alpha_k), (int) (r * 0.9f), (int) (g * 0.9f), (int) (b * 0.9f));
            canvas.drawCircle(x_center, y_center, size / 3.3f, p);

            p.setARGB((int)(255*alpha_k), r, g, b);
            canvas.drawCircle(x_center, y_center, size / 4f, p);

            p.setARGB((int)(192/2*alpha_k), 255, 255, 255);
            canvas.drawCircle(x_center - size / 12, y_center - size / 12, size / 12f, p);


        } else {

            //primary_paint.setARGB(32, 0, 0, 0);
            //canvas.drawCircle(x_center, y_center+ size*0.05f, size / 3.3f, primary_paint); //тень

            p.setARGB(50, 50, 50, 50);
            canvas.drawCircle(x_center, y_center, size / 3.3f, p);

            p.setARGB(50, 255, 255, 255);
            canvas.drawCircle(x_center, y_center, size / 4f, p);

            p.setARGB(255, 255, 255, 255);
            canvas.drawCircle(x_center - size / 12, y_center - size / 12, size / 12f, p);
        }


    }
}
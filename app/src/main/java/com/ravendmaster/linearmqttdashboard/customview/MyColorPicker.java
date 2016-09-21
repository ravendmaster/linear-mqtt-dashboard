package com.ravendmaster.linearmqttdashboard.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.ravendmaster.linearmqttdashboard.Log;
import com.ravendmaster.linearmqttdashboard.R;

public class MyColorPicker extends View {

    OnClickListener listener;

    public void setOnClickListener(OnClickListener listener){
        this.listener=listener;
    }

    boolean stopAnimation;


    public void setColor(int selectedColor) {

        int minDif = 0xffff;
        int minDifPos = 0;
        int pos = 0;
        for (int color : MyColors.colors) {
            int newDif = Math.abs(Color.red(color) - Color.red(selectedColor))
                    + Math.abs(Color.green(color) - Color.green(selectedColor))
                    + Math.abs(Color.blue(color) - Color.blue(selectedColor));
            if (newDif < minDif) {
                minDif = newDif;
                minDifPos = pos;
            }
            pos++;
        }

        this.currentColor = MyColors.getColorByIndex(minDifPos);
    }

    public int getColor() {
        return currentColor;
    }

    int currentColor = 0;

    final int color_rows = 4;

    public int X; // Переменные, доступные снаружи
    public int Y;

    Paint p = new Paint();

    public MyColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RGBLEDView,
                0, 0);

        try {
            //colorLight = a.getInteger(R.styleable.RGBLEDView_colorLight, Color.BLUE);
        } finally {
            a.recycle();
        }

    }

    public void startAnimation(){
        stopAnimation=false;
        removeCallbacks(animator);
        post(animator);
    }

    private Runnable animator = new Runnable() {
        @Override
        public void run() {
            boolean scheduleNewFrame = false;
            long now = AnimationUtils.currentAnimationTimeMillis();

            boolean newFlashState = (now / 500 % 2 == 0);
            if (flashState != newFlashState) {
                flashState = newFlashState;
            }

            if (!stopAnimation) {
                postDelayed(this, 500);
            }
            invalidate();
            //Log.d(getClass().getName(), "refresh!!!");
        }
    };



    public void stopAnimation(){
        stopAnimation=true;
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
                break;

            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP:
                Log.d(getClass().getName(), "UP, " + event.getAction());

                int left = getLeft();
                int right = getRight();
                int top = getTop();
                int bottom = getBottom();

                int strokeWidth = 0;
                int button_width = right - left - strokeWidth;
                int button_height = bottom - top - strokeWidth;
                int oneBlockWidth = button_width / 5;
                int oneBlockHeight = button_height / color_rows;

                int row = Y / oneBlockHeight;
                int col = X / oneBlockWidth;
                int colorIndex = row * 5 + col;

                Log.d(getClass().getName(), "row=" + row + " col=" + col);
                if(colorIndex>=0 && colorIndex<MyColors.colors.length) {
                    currentColor = MyColors.getColorByIndex(colorIndex);

                    //if (mMyColorPickerEventListener != null) mMyColorPickerEventListener.OnColorSelected(this, MyColors.getColorByIndex(colorIndex));
                    invalidate();
                    requestLayout();
                    playSoundEffect(SoundEffectConstants.CLICK);
                    if(listener!=null){
                        listener.onClick(this);
                    }
                }
                break;
        }

        return true;
    }

    boolean flashState;

    @Override
    protected void onDraw(Canvas canvas) {

        int left = getLeft();
        int right = getRight();
        int top = getTop();
        int bottom = getBottom();

        int strokeWidth = 0;
        int button_width = right - left - strokeWidth;
        int button_height = bottom - top - strokeWidth;
        int oneBlockWidth = button_width / 5;
        int oneBlockHeight = button_height / color_rows;

        int button_space = oneBlockWidth / 16;

        p.setStrokeWidth(button_space);
        for (int row = 0; row < color_rows; row++) {
            for (int col = 0; col < 5; col++) {
                int color = MyColors.getColorByIndex(col + row * 5);
                p.setColor(color);

                int ydisp = row * oneBlockHeight;

                p.setStyle(Paint.Style.FILL);

                canvas.drawRect(col * oneBlockWidth + button_space / 2, ydisp + button_space / 2, col * oneBlockWidth + oneBlockWidth - button_space / 2, ydisp + oneBlockHeight - button_space / 2, p);

                if (color == currentColor) {
                    //primary_paint.setColor(0x80FFFFFF);
                    //int size=50;
                    //canvas.drawCircle(size + col * oneBlockWidth + button_space, size + ydisp + button_space, size, primary_paint);
                    //Rect rect=new Rect(0,0,100,100);

                    p.setStyle(Paint.Style.STROKE);

                    if (flashState) {
                        p.setColor(0xff000000);
                    } else {
                        p.setColor(0xffFFFFFF);
                    }
                    canvas.drawRect(col * oneBlockWidth + button_space - 1, ydisp + button_space - 1, col * oneBlockWidth + oneBlockWidth - button_space, ydisp + oneBlockHeight - button_space, p);
                }
            }
        }
        return;
    }


    public void tick() {
    }

}

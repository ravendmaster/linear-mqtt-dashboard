package com.ravendmaster.linearmqttdashboard.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.ravendmaster.linearmqttdashboard.Log;
import com.ravendmaster.linearmqttdashboard.TabData;
import com.ravendmaster.linearmqttdashboard.TabsCollection;
import com.ravendmaster.linearmqttdashboard.activity.MainActivity;

import java.util.ArrayList;

public class MyTabsController extends View {

    static final Paint p = new Paint();
    Rect bounds = new Rect();

    int x_dispose = 0;

    public class MyTab {
        public String name;
        public int dashboardName;

        public MyTab(String name, int dashboardName) {
            this.name = name;
            this.dashboardName = dashboardName;
        }
    }


    public void refreshState(Context context) {

        tabs.clear();

        if (isInEditMode()) {
            tabs.add(new MyTab("tab0", 0));
            tabs.add(new MyTab("tab1", 1));
            selectedScreenTabIndex = 0;
        } else {
            //Log.d("dashboard orders", "---------------------");
            TabsCollection tabsCollection = MainActivity.presenter.getTabs();
            if (tabsCollection != null) {
                for (TabData tab : tabsCollection.getItems()) {
                    //Log.d("dashboard orders", "" + tab.id + "  " + tab.name);
                    tabs.add(new MyTab(tab.name, tab.id));
                }
            }


            /*
            AppSettings settings = AppSettings.getInstance();
            settings.readFromPrefs(getContext());
            tabs.add(new MyTab(settings.tabs[0], 0));
            if (!settings.tabs[1].equals("")) tabs.add(new MyTab(settings.tabs[1], 1));
            if (!settings.tabs[2].equals("")) tabs.add(new MyTab(settings.tabs[2], 2));
            if (!settings.tabs[3].equals("")) tabs.add(new MyTab(settings.tabs[3], 3));
            */


            selectedScreenTabIndex = MainActivity.presenter.getScreenActiveTabIndex();
        }

        invalidate();
        requestLayout();
    }

    public MyTabsController(Context context, AttributeSet attrs) {
        super(context, attrs);

        refreshState(context);

        p.setAntiAlias(true);
        p.setTextSize(40);

        selectedScreenTabIndex = 0;

        setLayerToHW(this);
    }

    private ArrayList<MyTab> tabs = new ArrayList<>();

    public ArrayList<MyTab> getTabs() {
        return tabs;
    }

    Integer selectedScreenTabIndex;

    int widget_width;

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

        if (tabs.size() <= 1) {
            heightMeasureSpec = 0;
        }

        widget_width = widthMeasureSpec;
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }


    boolean startedMove = false;

    boolean pressed = false;
    int started_x;

    int pressedTabIndex;

    int startMoveXPos;


    final int tabButtonWidth=256;

    void refreshXDispose(){
        x_dispose = Math.min(tabs.size() * tabButtonWidth - (getRight()-getLeft()), x_dispose);
        x_dispose = Math.max(0, x_dispose);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (!isEnabled()) return true;

        int left = getLeft();
        int right = getRight();
        int width = right - left;
        int tabWidth = tabButtonWidth;//width / tabs.size();

        //Log.d(getClass().getName(), event.toString());
        int X = (int) event.getX();
        int Y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                startedMove = true;
                x_dispose += (startMoveXPos - X);

                refreshXDispose();

                startMoveXPos = X;
                invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                started_x = X;
                pressedTabIndex = (X + x_dispose) / tabWidth;
                startedMove = false;
                startMoveXPos = X;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                pressed = false;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                pressed = false;
                if (Math.abs(started_x - X) < 20) {
                    selectedScreenTabIndex = (X + x_dispose) / tabWidth;
                    selectedScreenTabIndex=Math.min(selectedScreenTabIndex, tabs.size()-1);

                    playSoundEffect(SoundEffectConstants.CLICK);
                    //MyTab selectedTab = tabs.get(selectedScreenTabIndex);
                    MainActivity.presenter.onTabPressed(selectedScreenTabIndex);
                    //Log.d("ashboard orders", "tap " + selectedScreenTabIndex);
                    invalidate();
                }
                break;
        }

        return true;
    }

    private void setLayerToHW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (tabs.size() == 0) return;

        refreshXDispose();

        canvas.drawColor(0xffdddddd); //серый фон

        int left = getLeft();
        int right = getRight();
        int top = getTop();
        int bottom = getBottom();

        int width = right - left;
        int tabWidth = tabButtonWidth; //width / tabs.size();
        int tabHeight = bottom - top;

        if (pressed) {
            int pressedTabX = tabWidth * pressedTabIndex - x_dispose;
            p.setColor(MyColors.getYellow());
            canvas.drawRect(pressedTabX, 0, pressedTabX + tabWidth, tabHeight, p);
        }


        p.setColor(MyColors.getBlack());
        for (int i = 0; i < tabs.size(); i++) {
            int tabX = tabWidth * i;
            MyTab tab = tabs.get(i);
            String name = tab.name;
            //primary_paint.getTextBounds(name, 0, name.length(), bounds);
            //canvas.drawText(name, tabX + tabWidth / 2 - (bounds.right - bounds.left) / 2, tabHeight / 2 + (bounds.bottom - bounds.top) / 2, primary_paint);


            setBoundsOfThreeDots();
            int widthOfThreeDots = bounds.right - bounds.left;
            String text = name.toUpperCase();
            float[] measuredWidth = new float[100];
            int cnt = p.breakText(text, true, tabWidth - widthOfThreeDots - 4, measuredWidth);

            if (cnt < text.length()) {
                text = text.substring(0, cnt);
                text += "...";
            }
            p.getTextBounds(text, 0, text.length(), bounds);
            canvas.drawText(text, tabX + tabWidth / 2 - (bounds.right - bounds.left) / 2 - x_dispose, tabHeight / 2 + (bounds.bottom - bounds.top) / 2, p);

        }


        p.setColor(0xffaaaaaa);
        p.setStrokeWidth(2);
        for (int i = 0; i < tabs.size(); i++) {
            int tabX = tabWidth * i;
            canvas.drawLine(tabX - x_dispose + tabButtonWidth, 20, tabX - x_dispose + tabButtonWidth, tabHeight - 20, p);
        }


        int selectedTabX = tabWidth * selectedScreenTabIndex;
        p.setColor(MyColors.getYellow());
        canvas.drawRect(selectedTabX - x_dispose, 0, selectedTabX + tabWidth - x_dispose, 10, p);
    }


    Rect setBoundsOfThreeDots() {
        p.getTextBounds("...", 0, 3, bounds);
        return bounds;
    }
}

package com.ravendmaster.linearmqttdashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ravendmaster.linearmqttdashboard.activity.MainActivity;
import com.ravendmaster.linearmqttdashboard.service.WidgetData;
import com.woxthebox.draglistview.*;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    final String TAG = "ListFragment";

    private ArrayList<Pair<Long, WidgetData>> mItemArray;

    private com.woxthebox.draglistview.DragListView mDragListView;
    //private MySwipeRefreshLayout mRefreshLayout;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public void notifyItemChanged(int widgetIndex) {
        //Log.d("refresh", ""+widgetIndex);
        mDragListView.getAdapter().notifyItemChanged(widgetIndex);
    }

    public void notifyItemChangedAll() {
        mDragListView.getAdapter().notifyItemRangeChanged(0, mItemArray.size());
    }

    public void notifyDataSetChanged() {
        Log.d(TAG, "notifyDataSetChanged()");
        loadDataToDataSet();
        mDragListView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        //mRefreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mDragListView = (com.woxthebox.draglistview.DragListView) view.findViewById(R.id.drag_list_view);
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setDragListListener(new com.woxthebox.draglistview.DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {
                //mRefreshLayout.setEnabled(false);
                //Toast.makeText(mDragListView.getContext(), "Start - position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                //mRefreshLayout.setEnabled(true);
                if (fromPosition != toPosition) {
                    //Toast.makeText(mDragListView.getContext(), "End - position: " + toPosition, Toast.LENGTH_SHORT).show();

                    ArrayList arrayList = MainActivity.presenter.getWidgetsList();

                    WidgetData temp = (WidgetData) arrayList.get(fromPosition);
                    arrayList.remove(fromPosition);
                    arrayList.add(toPosition, temp);
                    MainActivity.presenter.saveActiveDashboard(MainActivity.instance, MainActivity.presenter.getActiveDashboardId());

                }
            }
        });

        mItemArray = new ArrayList<>();
        loadDataToDataSet();


        setupListRecyclerView();
        return view;
    }

    public void loadDataToDataSet() {
        mItemArray.clear();
        ArrayList<WidgetData> widgetDatas=MainActivity.presenter.getWidgetsList();
        if(widgetDatas!=null) {
            for (WidgetData widgetData : widgetDatas) {
                mItemArray.add(new Pair<>(Long.valueOf(index), widgetData));
                index++;
            }
        }
    }
    int index = 0;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("List and Grid");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //menu.findItem(R.id.action_disable_drag).setVisible(mDragListView.isDragEnabled());
        //menu.findItem(R.id.action_enable_drag).setVisible(!mDragListView.isDragEnabled());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_disable_drag:
                mDragListView.setDragEnabled(false);
                getActivity().supportInvalidateOptionsMenu();
                return true;
            case R.id.action_enable_drag:
                mDragListView.setDragEnabled(true);
                getActivity().supportInvalidateOptionsMenu();
                return true;
            case R.id.action_list:
                setupListRecyclerView();
                return true;
            case R.id.action_grid_vertical:
                setupGridVerticalRecyclerView();
                return true;
            case R.id.action_grid_horizontal:
                setupGridHorizontalRecyclerView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupListRecyclerView() {
        mDragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.widget_drag_place, false);
        mDragListView.setAdapter(listAdapter, true);
        mDragListView.setCanDragHorizontally(false);
        mDragListView.setCustomDragItem(new MyDragItem(getContext(), R.layout.list_item));
    }

    private void setupGridVerticalRecyclerView() {
        mDragListView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.grid_item, R.id.item_layout, true);
        mDragListView.setAdapter(listAdapter, true);
        mDragListView.setCanDragHorizontally(true);
        mDragListView.setCustomDragItem(null);

    }

    private void setupGridHorizontalRecyclerView() {
        mDragListView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.HORIZONTAL, false));
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.grid_item, R.id.item_layout, true);
        mDragListView.setAdapter(listAdapter, true);
        mDragListView.setCanDragHorizontally(true);
        mDragListView.setCustomDragItem(null);
    }

    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            dragView.setBackgroundColor(dragView.getResources().getColor(R.color.cardview_light_background));
            Utilites.onBindDragWidgetView(clickedView, dragView);
        }
    }
}

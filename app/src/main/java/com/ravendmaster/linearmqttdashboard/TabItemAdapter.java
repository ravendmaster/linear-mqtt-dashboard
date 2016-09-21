package com.ravendmaster.linearmqttdashboard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ravendmaster.linearmqttdashboard.activity.MainActivity;
import com.ravendmaster.linearmqttdashboard.activity.TabsActivity;
import com.ravendmaster.linearmqttdashboard.customview.ButtonsSet;
import com.ravendmaster.linearmqttdashboard.customview.Graph;
import com.ravendmaster.linearmqttdashboard.customview.Meter;
import com.ravendmaster.linearmqttdashboard.customview.MyButton;
import com.ravendmaster.linearmqttdashboard.customview.RGBLEDView;
import com.ravendmaster.linearmqttdashboard.service.Dashboard;
import com.ravendmaster.linearmqttdashboard.service.Presenter;
import com.ravendmaster.linearmqttdashboard.service.WidgetData;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

public class TabItemAdapter extends DragItemAdapter<Pair<Long, TabData>, TabItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;

    public TabItemAdapter(ArrayList<Pair<Long, TabData>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        super(dragOnLongPress);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder.mTabName==null)return;

        TabData tab = mItemList.get(position).second;

        //Presenter presenter= MainActivity.presenter;
        holder.mTabEditButton.setTag(tab);

        holder.mTabName.setText(tab.name);

    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter<Pair<Long, TabData>, TabItemAdapter.ViewHolder>.ViewHolder {
        public TextView mTabName;
        ImageView mTabEditButton;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            mTabName = (TextView) itemView.findViewById(R.id.tab_name);

            mTabEditButton = (ImageView) itemView.findViewById(R.id.imageView_edit_button);


            mTabEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //MainActivity.instance.showPopupMenuWidgetEditButtonOnClick(view);
                    TabsActivity.instance.showPopupMenuTabEditButtonOnClick(view);
                    //Toast.makeText(view.getContext(), "Item edit pressed", Toast.LENGTH_SHORT).show();
                }
            });

        }


        @Override
        public void onItemClicked(View view) {
            //Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            //Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}

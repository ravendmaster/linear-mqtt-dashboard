package com.ravendmaster.linearmqttdashboard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.ravendmaster.linearmqttdashboard.activity.MainActivity;
import com.ravendmaster.linearmqttdashboard.customview.ButtonsSet;
import com.ravendmaster.linearmqttdashboard.customview.ComboBoxSupport;
import com.ravendmaster.linearmqttdashboard.customview.Graph;
import com.ravendmaster.linearmqttdashboard.customview.Meter;
import com.ravendmaster.linearmqttdashboard.customview.MyButton;
import com.ravendmaster.linearmqttdashboard.customview.RGBLEDView;
import com.ravendmaster.linearmqttdashboard.service.Presenter;
import com.ravendmaster.linearmqttdashboard.service.WidgetData;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

public class ItemAdapter extends DragItemAdapter<Pair<Long, WidgetData>, ItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;

    public ItemAdapter(ArrayList<Pair<Long, WidgetData>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
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
        if (holder.mWidgetName == null) return;

        WidgetData widget = mItemList.get(position).second;

        Presenter presenter = MainActivity.presenter;

        String name = widget.getName(0);
        if (widget.type == WidgetData.WidgetTypes.GRAPH) {
            for (int i = 1; i < 4; i++) {
                String name_part = widget.getName(i);
                if (name_part != null && !name_part.isEmpty()) {
                    name += (" " + name_part);
                }
            }
            name = name.trim();
        }

        String[] names = new String[4];
        for (int i = 0; i < 4; i++) {
            names[i] = widget.getName(i);
        }

        holder.mWidgetName.setText(name);
        if (widget.type == WidgetData.WidgetTypes.HEADER) {
            holder.mWidgetName.setTypeface(Typeface.DEFAULT_BOLD);
            holder.mWidgetName.setTextColor(0xFF3F51B5);
        } else {
            holder.mWidgetName.setTypeface(Typeface.DEFAULT);
            holder.mWidgetName.setTextColor(Color.DKGRAY);
        }

        holder.mRoot.setVisibility(presenter.isEditMode() || (widget.type != WidgetData.WidgetTypes.GRAPH) ? View.VISIBLE : View.GONE);

        //TextView topicView = (TextView) convertView.findViewById(R.id.widget_topic);
        holder.mWidgetTopic.setVisibility(presenter.isEditMode() && (widget.type != WidgetData.WidgetTypes.HEADER) ? View.VISIBLE : View.GONE);
        holder.mWidgetTopic.setText(widget.getSubTopic(0));


        holder.mWidgetMeter.setVisibility(View.GONE);


        TextView[]valueTextView=new TextView[4];
        valueTextView[0]= holder.mWidgetValue;
        valueTextView[1]= holder.mWidgetValue1;
        valueTextView[2]= holder.mWidgetValue2;
        valueTextView[3]= holder.mWidgetValue3;

        for(int i=0;i<4;i++) {
            valueTextView[i].setVisibility(View.GONE);
            valueTextView[i].setTextColor(Color.DKGRAY);
            valueTextView[i].setTag(widget);
        }

        if (!presenter.isEditMode()) {
            holder.mWidgetValue.setOnLongClickListener(MainActivity.instance);
            //holder.mWidgetMeter.setOnLongClickListener(MainActivity.instance);
        } else {
            holder.mWidgetValue.setOnLongClickListener(null);
            //holder.mWidgetMeter.setOnLongClickListener(null);
        }
        holder.mWidgetMeter.setTag(widget);

        String topic_suffix = widget.getTopicSuffix();
        String[] values = new String[4];
        values[0] = presenter.getMQTTCurrentValue(widget.getSubTopic(0) + topic_suffix);
        values[1] = presenter.getMQTTCurrentValue(widget.getSubTopic(1) + topic_suffix);
        values[2] = presenter.getMQTTCurrentValue(widget.getSubTopic(2) + topic_suffix);
        values[3] = presenter.getMQTTCurrentValue(widget.getSubTopic(3) + topic_suffix);


        String[]showValue = new String[4];
        for(int i=0;i<4;i++) {
            showValue[i] = values[0];
            if (!widget.onShowExecute.isEmpty()) {
                showValue[i] = presenter.evalJS(widget, values[i], widget.onShowExecute);
            }
        }

        holder.mWidgetGraph.setVisibility(View.GONE);

        //Switch widget_switch = (Switch) convertView.findViewById(R.id.widget_switch);
        holder.mWidgetSwitch.setTag(widget);
        holder.mWidgetSwitch.setVisibility(View.GONE);

        //MyButton widget_button = (MyButton) convertView.findViewById(R.id.widget_button);
        holder.mWidgetButton.setTag(widget);
        holder.mWidgetButton.setVisibility(View.GONE);
        holder.mWidgetButton.setOnMyButtonEventListener(MainActivity.instance);

        //RGBLEDView widget_RGBLEDView = (RGBLEDView) convertView.findViewById(R.id.widget_RGBLed);
        holder.mWidgetRGBLED.setVisibility(View.GONE);

        //LinearLayout seek_bar_group = (LinearLayout) convertView.findViewById(R.id.seek_bar_group);
        //SeekBar widget_slider = (SeekBar) convertView.findViewById(R.id.widget_seekBar);
        holder.mWidgetSlider.setOnSeekBarChangeListener(null);
        holder.mWidgetSeekBarGroup.setVisibility(View.GONE);

        holder.mWidgetButtonsSet.setVisibility(View.GONE);
        holder.mWidgetButtonsSet.setOnButtonsSetEventListener(MainActivity.instance);
        holder.mWidgetButtonsSet.setTag(widget);

        //ImageView drag_place = (ImageView) convertView.findViewById(R.id.widget_drag_place);
        if (holder.mWidgetDragPlace != null) {
            holder.mWidgetDragPlace.setVisibility(presenter.isEditMode() ? View.VISIBLE : View.GONE);
        }

        //ImageView edit_button = (ImageView) convertView.findViewById(R.id.imageView_edit_button);
        holder.mWidgetEditButton.setTag(widget);

        holder.mWidgetComboBoxSelector.setTag(widget);
        holder.mWidgetComboBoxSelector.setVisibility(View.GONE);

        holder.mWidgetJS.setVisibility( !presenter.isEditMode() || (widget.onReceiveExecute.isEmpty() && widget.onShowExecute.isEmpty()) ? View.GONE:View.VISIBLE);

        holder.mWidgetGraph.setTag(widget);

        if (!presenter.isEditMode()) {
            ViewGroup.LayoutParams params = holder.mWidgetEditButton.getLayoutParams();
            params.width = 0;
            holder.mWidgetEditButton.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = holder.mWidgetEditButton.getLayoutParams();
            params.width = 64;

            holder.mWidgetEditButton.setLayoutParams(params);
        }


        switch (widget.type) {
            case COMBOBOX:
                holder.mWidgetComboBoxSelector.setVisibility(View.VISIBLE);
                holder.mWidgetComboBoxSelector.setEnabled(!presenter.isEditMode());

                holder.mWidgetValue.setVisibility(View.VISIBLE);
                holder.mWidgetValue.setText(ComboBoxSupport.getLabelByValue(values[0], widget.publishValue));
                holder.mWidgetValue.setTextColor(widget.getPrimaryColor(0));

                holder.mWidgetButtonsSet.setPublishValues(widget.publishValue);
                holder.mWidgetButtonsSet.setRetained(widget.retained);
                if (values[0] != null) {
                    holder.mWidgetButtonsSet.setCurrentValue(values[0]);
                }
                holder.mWidgetButtonsSet.setMaxButtonsPerRow(Utilites.parseInt(widget.formatMode, 4));
                break;

            case BUTTONSSET:
                //Log.d(getClass().getName(), "case BUTTONSSET:");
                holder.mWidgetButtonsSet.setVisibility(View.VISIBLE);
                holder.mWidgetButtonsSet.setColorLight(widget.getPrimaryColor(0));
                holder.mWidgetButtonsSet.setEnabled(!presenter.isEditMode());
                holder.mWidgetButtonsSet.setPublishValues(widget.publishValue);
                holder.mWidgetButtonsSet.setRetained(widget.retained);
                if (values[0] != null) {
                    holder.mWidgetButtonsSet.setCurrentValue(values[0]);
                }
                holder.mWidgetButtonsSet.setMaxButtonsPerRow(Utilites.parseInt(widget.formatMode, 4));
                break;
            case GRAPH:
                holder.mWidgetGraph.setVisibility(View.VISIBLE);
                for (int i = 0; i < 4; i++) {
                    holder.mWidgetGraph.setColorLight(i, widget.getPrimaryColor(i));
                    holder.mWidgetGraph.setValue(i, values[i]);
                    holder.mWidgetGraph.setName(i, names[i]);
                }


                holder.mWidgetGraph.setMode(widget.mode);
                holder.mWidgetGraph.setSubmode(widget.submode);

                break;
            case METER:
                holder.mWidgetMeter.setVisibility(View.VISIBLE);
                holder.mWidgetMeter.setColorLight(widget.getPrimaryColor(0));

                holder.mWidgetMeter.setDecimalMode(widget.decimalMode);
                holder.mWidgetMeter.setText(showValue[0]);
                if (values[0] != null) {
                    holder.mWidgetMeter.setValue(Utilites.parseFloat(values[0].replace("*", ""), 0));
                }
                holder.mWidgetMeter.setMode(widget.mode);

                holder.mWidgetMeter.setMin(Utilites.parseFloat(widget.publishValue, 0));
                holder.mWidgetMeter.setMax(Utilites.parseFloat(widget.publishValue2, 0));
                holder.mWidgetMeter.setAlarmZones(Utilites.parseFloat(widget.additionalValue, 0), Utilites.parseFloat(widget.additionalValue2, 0));
                holder.mWidgetMeter.setEnabled(!presenter.isEditMode());
                break;
            case VALUE:
                for(int i=0;i<1;i++) {
                    if(widget.getSubTopic(i).isEmpty())continue;
                    valueTextView[i].setVisibility(View.VISIBLE);
                    valueTextView[i].setText(showValue[i]);
                    valueTextView[i].setTextColor(widget.getPrimaryColor(i));
                }

                break;
            case SWITCH:
                holder.mWidgetSwitch.setVisibility(View.VISIBLE);
                if (values[0] != null) {
                    holder.mWidgetSwitch.setChecked(values[0].equals(widget.publishValue));
                }
                holder.mWidgetSwitch.setEnabled(!presenter.isEditMode());
                holder.mWidgetSwitch.setOnCheckedChangeListener(MainActivity.instance);
                break;
            case BUTTON:
                holder.mWidgetButton.setColorLight(widget.getPrimaryColor(0));
                holder.mWidgetButton.setVisibility(View.VISIBLE);
                holder.mWidgetButton.setEnabled(!presenter.isEditMode());
                holder.mWidgetButton.setLabelOn(widget.label);
                holder.mWidgetButton.setLabelOff(widget.label2);
                if (values[0] != null) {
                    holder.mWidgetButton.setPressed(values[0].equals(widget.publishValue));
                }
                break;
            case RGBLed:
                holder.mWidgetRGBLED.setColorLight(widget.getPrimaryColor(0));
                holder.mWidgetRGBLED.setVisibility(View.VISIBLE);
                if (values[0] != null) {

                    if (values[0].length() == 7 && values[0].charAt(0) == '#') {
                        // режим RGB
                        int newColor = Integer.parseInt(values[0].substring(1), 16);
                        holder.mWidgetRGBLED.setColorLight(newColor);
                        holder.mWidgetRGBLED.setOn(true);
                    } else {
                        // обычный режим вкл/выкл
                        holder.mWidgetRGBLED.setOn(values[0].equals(widget.publishValue));
                    }
                }
                holder.mWidgetRGBLED.setEnabled(!presenter.isEditMode());
                break;
            case SLIDER:
                holder.mWidgetSeekBarGroup.setVisibility(View.VISIBLE);
                Object[] tagData = new Object[2];
                tagData[0] = widget;
                tagData[1] = holder.mWidgetValue;
                holder.mWidgetSlider.setTag(tagData);
                holder.mWidgetValue.setVisibility(View.VISIBLE);
                holder.mWidgetValue.setText(showValue[0]);

                //widget.additionalValue3="10.0f";

                float main_step = Utilites.parseFloat(widget.additionalValue3, 1);
                int minMax = (int) (1f / main_step * (Utilites.parseFloat(widget.publishValue2, 0) - Utilites.parseFloat(widget.publishValue, 0)));

                holder.mWidgetSlider.setMax(minMax);
                holder.mWidgetSlider.setEnabled(!presenter.isEditMode());

                float valueInt = Utilites.parseFloat(values[0].replace("*", ""), 0) - Utilites.parseFloat(widget.publishValue, 0);

                holder.mWidgetSlider.setProgress((int) (valueInt / main_step));

                holder.mWidgetSlider.setOnSeekBarChangeListener(MainActivity.instance);
                break;
            case HEADER:
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter<Pair<Long, WidgetData>, ItemAdapter.ViewHolder>.ViewHolder {
        public View mRoot;
        public TextView mWidgetName;
        public TextView mWidgetTopic;
        public TextView mWidgetValue;
        public TextView mWidgetValue1;
        public TextView mWidgetValue2;
        public TextView mWidgetValue3;

        public Graph mWidgetGraph;
        public Meter mWidgetMeter;
        public Switch mWidgetSwitch;
        MyButton mWidgetButton;
        RGBLEDView mWidgetRGBLED;
        LinearLayout mWidgetSeekBarGroup;
        SeekBar mWidgetSlider;
        ImageView mWidgetDragPlace;
        ImageView mWidgetEditButton;
        ButtonsSet mWidgetButtonsSet;
        ImageView mWidgetComboBoxSelector;
        ImageView mWidgetJS;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            mRoot = (View) itemView.findViewById(R.id.root);
            mWidgetName = (TextView) itemView.findViewById(R.id.widget_name);
            mWidgetTopic = (TextView) itemView.findViewById(R.id.widget_topic);
            mWidgetValue = (TextView) itemView.findViewById(R.id.widget_value);
            mWidgetValue1 = (TextView) itemView.findViewById(R.id.widget_value1);
            mWidgetValue2 = (TextView) itemView.findViewById(R.id.widget_value2);
            mWidgetValue3 = (TextView) itemView.findViewById(R.id.widget_value3);

            mWidgetMeter = (Meter) itemView.findViewById(R.id.widget_meter);
            mWidgetGraph = (Graph) itemView.findViewById(R.id.widget_graph);
            mWidgetSwitch = (Switch) itemView.findViewById(R.id.widget_switch);
            mWidgetButton = (MyButton) itemView.findViewById(R.id.widget_button);
            mWidgetRGBLED = (RGBLEDView) itemView.findViewById(R.id.widget_RGBLed);
            mWidgetSeekBarGroup = (LinearLayout) itemView.findViewById(R.id.seek_bar_group);
            mWidgetSlider = (SeekBar) itemView.findViewById(R.id.widget_seekBar);
            mWidgetDragPlace = (ImageView) itemView.findViewById(R.id.widget_drag_place);
            mWidgetButtonsSet = (ButtonsSet) itemView.findViewById(R.id.widget_buttons_set);
            mWidgetEditButton = (ImageView) itemView.findViewById(R.id.imageView_edit_button);
            mWidgetEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.instance.showPopupMenuWidgetEditButtonOnClick(view);
                    //Toast.makeText(view.getContext(), "Item edit pressed", Toast.LENGTH_SHORT).show();
                }
            });

            mWidgetComboBoxSelector = (ImageView) itemView.findViewById(R.id.imageView_combo_box_selector);
            mWidgetComboBoxSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.instance.showPopupMenuComboBoxSelectorButtonOnClick(view);
                    //Toast.makeText(view.getContext(), "Item edit pressed", Toast.LENGTH_SHORT).show();
                }
            });


            mWidgetJS = (ImageView) itemView.findViewById(R.id.imageView_js);

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

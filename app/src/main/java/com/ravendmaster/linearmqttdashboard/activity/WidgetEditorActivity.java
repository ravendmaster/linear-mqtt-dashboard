package com.ravendmaster.linearmqttdashboard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ravendmaster.linearmqttdashboard.customview.MyColorPicker;
import com.ravendmaster.linearmqttdashboard.service.WidgetData;
import com.ravendmaster.linearmqttdashboard.R;

public class WidgetEditorActivity extends AppCompatActivity implements View.OnClickListener {

    Boolean mCreateNew;
    Boolean mCreateCopy;
    int widget_index;


    static WidgetEditorActivity mWidgetEditorActivity;

    WidgetData.WidgetTypes widgetType = WidgetData.WidgetTypes.VALUE;
    Integer widget_mode = 0;

    LinearLayout sub_topic_group;
    LinearLayout pub_topic_group;

    Spinner spinner_widget_mode;

    Integer[] topic_colors=new Integer[4];

    ImageView color_topic;
    ImageView color_topic1;
    ImageView color_topic2;
    ImageView color_topic3;

    EditText editText_name;
    EditText editText_name1;
    EditText editText_name2;
    EditText editText_name3;

    EditText editText_sub_topic;

    EditText editText_pub_topic;

    View extended_topics_group;

    EditText editText_topic1;
    EditText editText_topic2;
    EditText editText_topic3;



    TextView textView_publish_value;
    EditText editText_publish_value;

    TextView textView_publish_value2;
    EditText editText_publish_value2;

    View labels_group;
    EditText editText_labelOn;
    EditText editText_labelOff;

    View format_mode_group;
    TextView textView_format_mode;
    EditText editText_format_mode;

    //View new_value_topic_group;
    //EditText editText_new_value_topic; //new value

    View retained_group;
    CheckBox checkBox_retained;

    View additional_values_group;
    TextView textView_additional_value;
    EditText editText_additional_value;
    TextView textView_additional_value2;
    EditText editText_additional_value2;

    View additional_value_group;
    View additional_value2_group;
    View additional_value3_group;
    TextView textView_additional_value3;
    EditText editText_additional_value3;

    //View primary_color_group;
    //MyColorPicker primary_color_picker;

    CheckBox checkBox_displayAsDecimal;

    View codes_group;
    EditText editText_codeOnShow;


    View on_receive_codes_group;
    EditText editText_codeOnReceive;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Save:
                WidgetData widget = null;
                if (mCreateNew || mCreateCopy) {
                    widget = new WidgetData();
                    MainActivity.presenter.addWidget(widget);
                } else {
                    widget = MainActivity.presenter.getWidgetByIndex(widget_index);
                }

                widget.type = widgetType;

                widget.setName(0, editText_name.getText().toString());
                widget.setName(1, editText_name1.getText().toString());
                widget.setName(2, editText_name2.getText().toString());
                widget.setName(3, editText_name3.getText().toString());

                widget.setSubTopic(0, editText_sub_topic.getText().toString());
                widget.setSubTopic(1, editText_topic1.getText().toString());
                widget.setSubTopic(2, editText_topic2.getText().toString());
                widget.setSubTopic(3, editText_topic3.getText().toString());

                widget.setPubTopic(0, editText_pub_topic.getText().toString());

                widget.publishValue = editText_publish_value.getText().toString();
                widget.publishValue2 = editText_publish_value2.getText().toString();

                widget.label = editText_labelOn.getText().toString();
                widget.label2 = editText_labelOff.getText().toString();

                //widget.newValueTopic = editText_new_value_topic.getText().toString();

                widget.additionalValue = editText_additional_value.getText().toString();
                widget.additionalValue2 = editText_additional_value2.getText().toString();

                widget.additionalValue3 = editText_additional_value3.getText().toString();

                widget.setPrimaryColor(0, topic_colors[0]);
                widget.setPrimaryColor(1, topic_colors[1]);
                widget.setPrimaryColor(2, topic_colors[2]);
                widget.setPrimaryColor(3, topic_colors[3]);

                widget.retained = checkBox_retained.isChecked();

                widget.decimalMode = checkBox_displayAsDecimal.isChecked();

                widget.mode = spinner_widget_mode.getSelectedItemPosition();

                widget.onShowExecute = editText_codeOnShow.getText().toString();
                widget.onReceiveExecute = editText_codeOnReceive.getText().toString();

                widget.formatMode = editText_format_mode.getText().toString();

                MainActivity.presenter.saveActiveDashboard(getApplicationContext(), MainActivity.presenter.getActiveDashboardId());

                MainActivity.presenter.widgetSettingsChanged(widget);

                finish();

                break;
        }
        return true;
    }

    String[]widgetModes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

        mWidgetEditorActivity = this;

        spinner_widget_mode = ((Spinner) findViewById(R.id.spinner_widget_mode));

        editText_name = ((EditText) findViewById(R.id.editText_name));
        editText_name1 = ((EditText) findViewById(R.id.editText_name1));
        editText_name2 = ((EditText) findViewById(R.id.editText_name2));
        editText_name3 = ((EditText) findViewById(R.id.editText_name3));

        color_topic = ((ImageView) findViewById(R.id.color_topic));
        color_topic.setTag(0);//индекс топика
        color_topic1 = ((ImageView) findViewById(R.id.color_topic1));
        color_topic1.setTag(1);//индекс топика
        color_topic2 = ((ImageView) findViewById(R.id.color_topic2));
        color_topic2.setTag(2);//индекс топика
        color_topic3 = ((ImageView) findViewById(R.id.color_topic3));
        color_topic3.setTag(3);//индекс топика

        sub_topic_group = (LinearLayout) findViewById(R.id.sub_topic_group);
        editText_sub_topic = ((EditText) findViewById(R.id.editText_sub_topic));

        pub_topic_group = (LinearLayout) findViewById(R.id.pub_topic_group);


        editText_pub_topic = ((EditText) findViewById(R.id.editText_pub_topic));

        extended_topics_group = findViewById(R.id.extended_topics_group);
        editText_topic1 = ((EditText) findViewById(R.id.editText_topic1));
        editText_topic2 = ((EditText) findViewById(R.id.editText_topic2));
        editText_topic3 = ((EditText) findViewById(R.id.editText_topic3));

        textView_publish_value = ((TextView) findViewById(R.id.textView_publish_value));
        editText_publish_value = ((EditText) findViewById(R.id.editText_publish_value));

        textView_publish_value2 = ((TextView) findViewById(R.id.textView_publish_value2));
        textView_publish_value2.setVisibility(View.GONE);
        editText_publish_value2 = ((EditText) findViewById(R.id.editText_publish_value2));
        editText_publish_value2.setVisibility(View.GONE);

        labels_group = (View) findViewById(R.id.labels_group);
        editText_labelOn = (EditText) findViewById(R.id.editText_OnLabel);
        editText_labelOff = (EditText) findViewById(R.id.editText_OffLabel);

        format_mode_group = (View) findViewById(R.id.format_mode_group);
        textView_format_mode = ((TextView) findViewById(R.id.textView_format_mode));
        editText_format_mode = (EditText) findViewById(R.id.editText_format_mode);

        //new_value_topic_group = findViewById(R.id.new_value_topic_group);
        //editText_new_value_topic = (EditText) findViewById(R.id.editText_new_value_topic);

        retained_group = findViewById(R.id.retained_group);
        checkBox_retained = (CheckBox) findViewById(R.id.checkBox_retained);

        additional_values_group = findViewById(R.id.addition_values_group);
        textView_additional_value = (TextView) findViewById(R.id.textView_additionalValue);
        editText_additional_value = (EditText) findViewById(R.id.editText_additionalValue);
        textView_additional_value2 = (TextView) findViewById(R.id.textView_additionalValue2);
        editText_additional_value2 = (EditText) findViewById(R.id.editText_additionalValue2);

        additional_value_group = findViewById(R.id.addition_value_group);
        additional_value2_group = findViewById(R.id.addition_value2_group);
        additional_value3_group = findViewById(R.id.addition_value3_group);

        textView_additional_value3 = (TextView) findViewById(R.id.textView_additionalValue3);
        editText_additional_value3 = (EditText) findViewById(R.id.editText_additionalValue3);

        //primary_color_group = ((View) findViewById(R.id.primary_color_group));
        //primary_color_picker = (MyColorPicker) findViewById(R.id.color_picker);

        checkBox_displayAsDecimal = (CheckBox) findViewById(R.id.checkBox_decimalMode);


        codes_group = findViewById(R.id.codes_group);
        editText_codeOnShow = (EditText) findViewById(R.id.editText_codeOnShow);

        on_receive_codes_group = findViewById(R.id.on_receive_codes_group);
        editText_codeOnReceive = (EditText) findViewById(R.id.editText_codeOnReceive);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, WidgetData.WidgetTypes.getNames(getApplicationContext()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner widget_type_spinner = (Spinner) findViewById(R.id.spinner_widget_type);
        widget_type_spinner.setAdapter(adapter);
        // заголовок
        widget_type_spinner.setPrompt("Widget type");
        // выделяем элемент



        // устанавливаем обработчик нажатия
        widget_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                widgetType = WidgetData.WidgetTypes.getWidgetTypeByInt(position);
                widgetModes= WidgetData.getWidgetModes(widgetType);

                //инициализация списков режимов
                if(widgetModes!=null) {
                    ArrayAdapter<String> adapter_modes = new ArrayAdapter<String>(mWidgetEditorActivity, android.R.layout.simple_spinner_item, widgetModes);
                    adapter_modes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_widget_mode.setAdapter(adapter_modes);
                    if((widget_mode+1)>adapter_modes.getCount()){
                        widget_mode=0;
                    }
                    spinner_widget_mode.setSelection(widget_mode);
                    spinner_widget_mode.setPrompt("Widget mode");
                    spinner_widget_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            widget_mode = position;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
                    //spinner_widget_mode.setSelection(wid);
                }


                Integer publishValueVisible = View.GONE;
                String valueFieldName = "";

                Integer publishValue2Visible = View.GONE;
                String value2FieldName = "";

                Integer primaryColorVisible = View.GONE;

                Integer modeVisibility = View.GONE;

                Integer labelsGroupVisible = View.GONE;

                Integer newValueTopicGroupVisible = View.GONE;

                Integer retainedVisible = View.GONE;

                Integer additionalValuesVisible = View.GONE;
                String additionalValueName = "";
                String additionalValue2Name = "";

                Integer additionalValueVisible = View.GONE;
                Integer additionalValue2Visible = View.GONE;
                Integer additionalValue3Visible = View.GONE;

                String additionalValue3Name = "";

                int inputType = EditorInfo.TYPE_CLASS_TEXT;
                int additionalInputType = EditorInfo.TYPE_CLASS_TEXT;

                int additional3InputType = EditorInfo.TYPE_CLASS_TEXT;

                Integer displayAsDecimalVisibleVisible = View.GONE;

                Integer codesGroupVisible = View.GONE;

                Integer formatModeGroupVisible = View.GONE;

                Integer extendedTopicsGroupVisible = View.GONE;;



                switch (widgetType) {
                    case COMBOBOX:
                        additionalValueVisible = View.VISIBLE;
                        primaryColorVisible = View.VISIBLE;
                        retainedVisible = View.VISIBLE;
                        publishValueVisible = View.VISIBLE;
                        valueFieldName = "Values and labels (example: '0,127,255' or '0|OFF,127|50%,255|MAX')";
                        //formatModeGroupVisible = View.VISIBLE;
                        break;
                    case BUTTONSSET:
                        additionalValueVisible = View.VISIBLE;
                        primaryColorVisible = View.VISIBLE;
                        retainedVisible = View.VISIBLE;
                        publishValueVisible = View.VISIBLE;
                        valueFieldName = "Values and labels (example: '0,127,255' or '0|OFF,127|50%,255|MAX')";
                        formatModeGroupVisible = View.VISIBLE;
                        break;
                    case GRAPH:
                        primaryColorVisible = View.VISIBLE;
                        extendedTopicsGroupVisible = View.VISIBLE;
                        modeVisibility = View.VISIBLE;
                        break;
                    case VALUE:
                        additionalValueVisible = View.VISIBLE;
                        primaryColorVisible = View.VISIBLE;
                        newValueTopicGroupVisible = View.VISIBLE;
                        codesGroupVisible = View.VISIBLE;
                        modeVisibility = View.VISIBLE;
                        //extendedTopicsGroupVisible = View.VISIBLE;
                        break;
                    case BUTTON:
                        additionalValueVisible = View.VISIBLE;
                        additionalValue2Visible = View.VISIBLE;
                        primaryColorVisible = View.VISIBLE;
                        publishValueVisible = View.VISIBLE;
                        valueFieldName = getString(R.string.value_on);
                        publishValue2Visible = View.VISIBLE;
                        value2FieldName = getString(R.string.value_off);
                        labelsGroupVisible = View.VISIBLE;
                        retainedVisible = View.VISIBLE;
                        break;
                    case SLIDER:

                        publishValueVisible = View.VISIBLE;
                        valueFieldName = getString(R.string.range_from);
                        publishValue2Visible = View.VISIBLE;
                        value2FieldName = getString(R.string.range_to);

                        inputType = EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL | EditorInfo.TYPE_NUMBER_FLAG_SIGNED;
                        additional3InputType = EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL | EditorInfo.TYPE_NUMBER_FLAG_SIGNED;

                        additionalValueVisible = View.VISIBLE;
                        additionalValue2Visible = View.VISIBLE;
                        additionalValue3Visible = View.VISIBLE;
                        additionalValue3Name = getString(R.string.step);

                        displayAsDecimalVisibleVisible = View.VISIBLE;
                        codesGroupVisible = View.VISIBLE;

                        break;
                    case SWITCH:
                        additionalValueVisible = View.VISIBLE;
                        additionalValue2Visible = View.VISIBLE;

                        publishValueVisible = View.VISIBLE;
                        valueFieldName = getString(R.string.value_on);
                        publishValue2Visible = View.VISIBLE;
                        value2FieldName = getString(R.string.value_off);
                        break;
                    case RGBLed:
                        primaryColorVisible = View.VISIBLE;
                        publishValueVisible = View.VISIBLE;

                        additionalValueVisible = View.VISIBLE;
                        additionalValue2Visible = View.VISIBLE;

                        valueFieldName = getString(R.string.value_on);
                        publishValue2Visible = View.VISIBLE;
                        value2FieldName = getString(R.string.value_off);
                        break;
                    case METER:
                        modeVisibility = View.VISIBLE;

                        additionalValueVisible = View.VISIBLE;
                        additionalValue2Visible = View.VISIBLE;

                        additionalValuesVisible = View.VISIBLE;
                        publishValueVisible = View.VISIBLE;
                        valueFieldName = getString(R.string.range_from);
                        publishValue2Visible = View.VISIBLE;
                        value2FieldName = getString(R.string.range_to);

                        additionalValueName = getString(R.string.alarm_zone_lower);
                        additionalValue2Name = getString(R.string.alarm_zone_upper);

                        inputType = EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL | EditorInfo.TYPE_NUMBER_FLAG_SIGNED;
                        additionalInputType = EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL | EditorInfo.TYPE_NUMBER_FLAG_SIGNED;

                        displayAsDecimalVisibleVisible = View.VISIBLE;
                        codesGroupVisible = View.VISIBLE;

                        //newValueTopicGroupVisible = View.VISIBLE;
                        break;
                }

                extended_topics_group.setVisibility(extendedTopicsGroupVisible);

                format_mode_group.setVisibility(formatModeGroupVisible);

                additional_values_group.setVisibility(additionalValuesVisible);

                additional_value_group.setVisibility(additionalValueVisible);
                additional_value2_group.setVisibility(additionalValue2Visible);
                additional_value3_group.setVisibility(additionalValue3Visible);


                sub_topic_group.setVisibility(widgetType == WidgetData.WidgetTypes.HEADER ? View.GONE : View.VISIBLE);
                pub_topic_group.setVisibility(widgetType == WidgetData.WidgetTypes.HEADER || widgetType == WidgetData.WidgetTypes.GRAPH || widgetType == WidgetData.WidgetTypes.RGBLed || widgetType == WidgetData.WidgetTypes.SLIDER  ? View.GONE : View.VISIBLE);
                on_receive_codes_group.setVisibility(widgetType == WidgetData.WidgetTypes.HEADER ? View.GONE : View.VISIBLE);

                spinner_widget_mode.setVisibility(modeVisibility);

                textView_publish_value.setText(valueFieldName);
                textView_publish_value.setVisibility(publishValueVisible);
                editText_publish_value.setVisibility(publishValueVisible);
                editText_publish_value.setInputType(inputType);
                editText_publish_value.setSingleLine(widgetType != WidgetData.WidgetTypes.BUTTONSSET);


                textView_publish_value2.setText(value2FieldName);
                textView_publish_value2.setVisibility(publishValue2Visible);
                editText_publish_value2.setVisibility(publishValue2Visible);
                editText_publish_value2.setInputType(inputType);

                labels_group.setVisibility(labelsGroupVisible);

                //new_value_topic_group.setVisibility(newValueTopicGroupVisible);

                color_topic.setVisibility(primaryColorVisible);
                color_topic1.setVisibility(primaryColorVisible);
                color_topic2.setVisibility(primaryColorVisible);
                color_topic3.setVisibility(primaryColorVisible);

                retained_group.setVisibility(retainedVisible);

                textView_additional_value.setText(additionalValueName);
                textView_additional_value2.setText(additionalValue2Name);

                editText_additional_value.setInputType(additionalInputType);
                editText_additional_value2.setInputType(additionalInputType);

                textView_additional_value3.setText(additionalValue3Name);
                editText_additional_value3.setInputType(additional3InputType);

                checkBox_displayAsDecimal.setVisibility(displayAsDecimalVisibleVisible);

                codes_group.setVisibility(codesGroupVisible);


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Intent intent = getIntent();

        WidgetData tempWidgetData = null;
        mCreateNew = intent.getBooleanExtra("createNew", false);
        mCreateCopy = intent.getBooleanExtra("createCopy", false);
        widget_index = intent.getIntExtra("widget_index", 0);

        if (mCreateCopy) {
            tempWidgetData = MainActivity.presenter.getWidgetByIndex(widget_index);
        } else if (mCreateNew) {
            tempWidgetData = new WidgetData();
        } else {
            tempWidgetData = MainActivity.presenter.getWidgetByIndex(widget_index);
        }

        widgetType = tempWidgetData.type;

        editText_name.setText(tempWidgetData.getName(0));
        editText_name1.setText(tempWidgetData.getName(1));
        editText_name2.setText(tempWidgetData.getName(2));
        editText_name3.setText(tempWidgetData.getName(3));

        topic_colors[0]=tempWidgetData.getPrimaryColor(0);
        topic_colors[1]=tempWidgetData.getPrimaryColor(1);
        topic_colors[2]=tempWidgetData.getPrimaryColor(2);
        topic_colors[3]=tempWidgetData.getPrimaryColor(3);
        updateScreenColorsOfTopics();

        editText_sub_topic.setText(tempWidgetData.getSubTopic(0));

        editText_pub_topic.setText(tempWidgetData.getPubTopic(0));

        editText_topic1.setText(tempWidgetData.getSubTopic(1));

        editText_topic2.setText(tempWidgetData.getSubTopic(2));

        editText_topic3.setText(tempWidgetData.getSubTopic(3));

        editText_publish_value.setText(tempWidgetData.publishValue);
        editText_publish_value2.setText(tempWidgetData.publishValue2);

        editText_labelOn.setText(tempWidgetData.label);
        editText_labelOff.setText(tempWidgetData.label2);

        //editText_new_value_topic.setText(tempWidgetData.newValueTopic);

        checkBox_retained.setChecked(tempWidgetData.retained);

        editText_additional_value.setText(tempWidgetData.additionalValue);
        editText_additional_value2.setText(tempWidgetData.additionalValue2);

        editText_additional_value3.setText(tempWidgetData.additionalValue3);

        checkBox_displayAsDecimal.setChecked(tempWidgetData.decimalMode);

        editText_codeOnShow.setText(tempWidgetData.onShowExecute);

        /*
        SpannableStringBuilder sb = new SpannableStringBuilder(tempWidgetData.onReceiveExecute);
        int color = MyColors.getBlue();
        ForegroundColorSpan fcs  =new ForegroundColorSpan(color);
        sb.setSpan(fcs, 0, 2,0);
        editText_codeOnReceive.setText(sb);
        */

        editText_codeOnReceive.setText(tempWidgetData.onReceiveExecute);

        editText_format_mode.setText(tempWidgetData.formatMode);

        widget_type_spinner.setSelection(widgetType.getAsInt());
        widget_mode = tempWidgetData.mode;

    }

    void updateScreenColorsOfTopics(){
        color_topic.setColorFilter(topic_colors[0]);
        color_topic1.setColorFilter(topic_colors[1]);
        color_topic2.setColorFilter(topic_colors[2]);
        color_topic3.setColorFilter(topic_colors[3]);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        //primary_color_picker.stopAnimation();
        super.onPause();
    }

    @Override
    protected void onResume() {
        //primary_color_picker.startAnimation();
        super.onResume();
    }

    public void OnClickSelectTopicColor(View view){
        int index=(int)view.getTag();
        showColorPicker(index);

    }

    AlertDialog alertDialog;
    MyColorPicker myColorPicker;
    int current_topic_index_for_select_color;
    void showColorPicker(final int topic_index){
        current_topic_index_for_select_color=topic_index;
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.clolor_picker, null);
        //TextView nameView = (TextView) promptsView.findViewById(R.id.textView_name);
        //nameView.setText(widgetData.getName(0));
        myColorPicker=(MyColorPicker) promptsView.findViewById(R.id.color_picker);
        myColorPicker.setOnClickListener(this);

        myColorPicker.setColor(topic_colors[topic_index]);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set new_value_send_dialogue_send_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        //userInput.setText(presenter.getMQTTCurrentValue(widgetData.getSubTopic(0)).replace("*", ""));
        //userInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL | EditorInfo.TYPE_NUMBER_FLAG_SIGNED);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        topic_colors[current_topic_index_for_select_color]=myColorPicker.getColor();
        updateScreenColorsOfTopics();
        alertDialog.cancel();
    }

    public void OnClickHelp(View view){
        MainActivity.presenter.OnClickHelp(this, view);
    }
}

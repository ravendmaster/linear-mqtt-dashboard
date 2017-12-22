package com.ravendmaster.linearmqttdashboard;

import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.ravendmaster.linearmqttdashboard.customview.ButtonsSet;
import com.ravendmaster.linearmqttdashboard.customview.Graph;
import com.ravendmaster.linearmqttdashboard.customview.Meter;
import com.ravendmaster.linearmqttdashboard.customview.MyButton;
import com.ravendmaster.linearmqttdashboard.customview.RGBLEDView;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;
import java.nio.charset.Charset;

public class Utilites {

    public static boolean isNumeric(String s) throws NumberFormatException {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void onBindDragTabView(View clickedView, View dragView) {
        ((TextView) dragView.findViewById(R.id.tab_name)).setText(((TextView) clickedView.findViewById(R.id.tab_name)).getText());
    }

    public static void onBindDragWidgetView(View clickedView, View dragView){
        dragView.findViewById(R.id.root).setVisibility(clickedView.findViewById(R.id.root).getVisibility());

        ((TextView) dragView.findViewById(R.id.widget_name)).setText(((TextView) clickedView.findViewById(R.id.widget_name)).getText());
        ((TextView) dragView.findViewById(R.id.widget_topic)).setText(((TextView) clickedView.findViewById(R.id.widget_topic)).getText());
        ((TextView) dragView.findViewById(R.id.widget_value)).setText(((TextView) clickedView.findViewById(R.id.widget_value)).getText());
        dragView.findViewById(R.id.widget_value).setVisibility(clickedView.findViewById(R.id.widget_value).getVisibility());
        dragView.findViewById(R.id.widget_value1).setVisibility(clickedView.findViewById(R.id.widget_value1).getVisibility());
        dragView.findViewById(R.id.widget_value2).setVisibility(clickedView.findViewById(R.id.widget_value2).getVisibility());
        dragView.findViewById(R.id.widget_value3).setVisibility(clickedView.findViewById(R.id.widget_value3).getVisibility());


        ((TextView) dragView.findViewById(R.id.widget_value)).setTextColor(((TextView) clickedView.findViewById(R.id.widget_value)).getTextColors());

        ((Meter) dragView.findViewById(R.id.widget_meter)).setMode(((Meter) clickedView.findViewById(R.id.widget_meter)).getMode());
        ((Meter) dragView.findViewById(R.id.widget_meter)).setVisibility(((Meter) clickedView.findViewById(R.id.widget_meter)).getVisibility());
        ((Meter) dragView.findViewById(R.id.widget_meter)).setValue(((Meter) clickedView.findViewById(R.id.widget_meter)).getValue());
        ((Meter) dragView.findViewById(R.id.widget_meter)).setMin(((Meter) clickedView.findViewById(R.id.widget_meter)).getMin());
        ((Meter) dragView.findViewById(R.id.widget_meter)).setMax(((Meter) clickedView.findViewById(R.id.widget_meter)).getMax());

        ((MyButton) dragView.findViewById(R.id.widget_button)).setVisibility((((MyButton) clickedView.findViewById(R.id.widget_button)).getVisibility()));
        ((MyButton) dragView.findViewById(R.id.widget_button)).setColorLight((((MyButton) clickedView.findViewById(R.id.widget_button)).getColorLight()));
        ((MyButton) dragView.findViewById(R.id.widget_button)).setLabelOff((((MyButton) clickedView.findViewById(R.id.widget_button)).getLabelOff()));
        ((MyButton) dragView.findViewById(R.id.widget_button)).setLabelOn((((MyButton) clickedView.findViewById(R.id.widget_button)).getLabelOn()));

        ((ButtonsSet) dragView.findViewById(R.id.widget_buttons_set)).setVisibility(((ButtonsSet) clickedView.findViewById(R.id.widget_buttons_set)).getVisibility());
        ((ButtonsSet) dragView.findViewById(R.id.widget_buttons_set)).setPublishValues(((ButtonsSet) clickedView.findViewById(R.id.widget_buttons_set)).getPublishValues());
        ((ButtonsSet) dragView.findViewById(R.id.widget_buttons_set)).setColorLight((((ButtonsSet) clickedView.findViewById(R.id.widget_buttons_set)).getColorLight()));
        ((ButtonsSet) dragView.findViewById(R.id.widget_buttons_set)).setMaxButtonsPerRow((((ButtonsSet) clickedView.findViewById(R.id.widget_buttons_set)).getMaxButtonsPerRow()));

        ((Switch) dragView.findViewById(R.id.widget_switch)).setVisibility((((Switch) clickedView.findViewById(R.id.widget_switch)).getVisibility()));
        ((Switch) dragView.findViewById(R.id.widget_switch)).setChecked(((((Switch) clickedView.findViewById(R.id.widget_switch)).isChecked())));

        dragView.findViewById(R.id.seek_bar_group).setVisibility((clickedView.findViewById(R.id.seek_bar_group)).getVisibility());
        ((SeekBar) dragView.findViewById(R.id.widget_seekBar)).setProgress((((SeekBar) clickedView.findViewById(R.id.widget_seekBar)).getProgress()));
        ((SeekBar) dragView.findViewById(R.id.widget_seekBar)).setMax((((SeekBar) clickedView.findViewById(R.id.widget_seekBar)).getMax()));
        ((RGBLEDView) dragView.findViewById(R.id.widget_RGBLed)).setVisibility((((RGBLEDView) clickedView.findViewById(R.id.widget_RGBLed)).getVisibility()));

        dragView.findViewById(R.id.imageView_edit_button).setVisibility((clickedView.findViewById(R.id.imageView_edit_button)).getVisibility());

        dragView.findViewById(R.id.widget_graph).setVisibility((clickedView.findViewById(R.id.widget_graph)).getVisibility());

        for(int i=0;i<4;i++) {
            ((Graph) dragView.findViewById(R.id.widget_graph)).setValue(i, ((Graph) clickedView.findViewById(R.id.widget_graph)).getValue(i));
            ((Graph) dragView.findViewById(R.id.widget_graph)).setColorLight(i, ((Graph) clickedView.findViewById(R.id.widget_graph)).getColorLight(i));
            ((Graph) dragView.findViewById(R.id.widget_graph)).setName(i, ((Graph) clickedView.findViewById(R.id.widget_graph)).getName(i));
        }

        ((Graph) dragView.findViewById(R.id.widget_graph)).setMode(((Graph) clickedView.findViewById(R.id.widget_graph)).getMode());

        dragView.findViewById(R.id.imageView_combo_box_selector).setVisibility((clickedView.findViewById(R.id.imageView_combo_box_selector)).getVisibility());

        dragView.findViewById(R.id.imageView_js).setVisibility((clickedView.findViewById(R.id.imageView_js)).getVisibility());
    }

    public static UUID createUUIDByString(String uidString){
        String s2 = uidString.replace("-", "");
        return new UUID(
                new BigInteger(s2.substring(0, 16), 16).longValue(),
                new BigInteger(s2.substring(16), 16).longValue());
    }

    public static int parseInt(String input, int def) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
        }
        return def;
    }

    public static float parseFloat(String input, int def) {
        try {
            return Float.parseFloat(input);
        } catch (Exception e) {
        }
        return def;
    }

    public static float round(float input){
        return Math.round(input*1000f)/1000f;
    }

/*
https://stackoverflow.com/questions/88838/how-to-convert-strings-to-and-from-utf8-byte-arrays-in-java
Convert from String to byte[]:

String s = "some text here";
byte[] b = s.getBytes("UTF-8");

Convert from byte[] to String:

byte[] b = {(byte) 99, (byte)97, (byte)116};
String s = new String(b, "US-ASCII");
 */

    public static byte[] stringToBytesUTFCustom(String str) {
        //return str.getBytes(StandardCharsets.UTF_8);
        return str.getBytes();
    }

    public static String bytesToStringUTFCustom(byte[] bytes, int count) {
        //return new String(bytes, StandardCharsets.UTF_8);
        return new String(bytes);
    }

    /* Old code:
    public static byte[] stringToBytesUTFCustom(String str) {

        char[] buffer = str.toCharArray();

        byte[] b = new byte[buffer.length << 1];

        for(int i = 0; i < buffer.length; i++) {

            int bpos = i << 1;

            b[bpos] = (byte) ((buffer[i]&0xFF00)>>8);

            b[bpos + 1] = (byte) (buffer[i]&0x00FF);

        }

        return b;

    }

    public static String bytesToStringUTFCustom(byte[] bytes, int count) {

        char[] buffer = new char[bytes.length >> 1];

        for(int i = 0; i < count/2; i++) {

            int bpos = i << 1;

            char c = (char)(((bytes[bpos]&0x00FF)<<8) + (bytes[bpos+1]&0x00FF));

            buffer[i] = c;

        }

        return new String(buffer);

    }
    */


    public static void xorBuffer(byte []buff){
        final Random random = new Random(0);
        for(int i=0;i<buff.length;i++){
            buff[i]^=random.nextInt(255);
        }
    }

}

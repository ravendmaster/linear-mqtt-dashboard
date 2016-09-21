package com.ravendmaster.linearmqttdashboard.customview;

public class MyColors {

    public static final int colors[] = {
            0xff1abc9c, -7617718  , 0xff3498db, 0xff9b59b6, 0xff34495e,
            0xff16a085, 0xff27ae60, 0xff2980b9, 0xff8e44ad, 0xff2c3e50,
            0xfff1c40f, 0xffe67e22, 0xffe74c3c, 0xffecf0f1, 0xff95a5a6,
            0xfff39c12, 0xffd35400, 0xffc0392b, 0xffbdc3c7, 0xff7f8c8d};
    //           0          1            2            3           4
    //           5
    //           10
    //           15

    //public static final int RGB_LED_RED = -2937298;
    //public static final int RGB_LED_YELLOW = -5317;
    //public static final int RGB_LED_GREEN = -7617718;

    //public static final int RGB_GRAY = 0xAAAAAA;

    public static int getDark() {return colors[4];}

    public static int getAsBlack() {return colors[9];}

    public static int getBlue() {return colors[7];}

    public static int getGray() {return colors[19];}
    public static int getLtGray() {return colors[14];}
    public static int getVeryLtGray() {return colors[18];}

    public static int getWhite() {return colors[13];}

    public static int getRed() {return colors[12];}
    public static int getYellow() {return colors[10];}
    public static int getGreen() {return colors[1];}

    public static int getBlack() {return 0xff000000;}

    public static Integer getColorByIndex(int index) {
        if (index >= colors.length) return null;
        return colors[index];
    }
}

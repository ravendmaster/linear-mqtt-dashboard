package com.ravendmaster.linearmqttdashboard.customview;

public class ComboBoxSupport {
    public static String getLabelByValue(String valueString, String valuesList) {
        final String[] values = valuesList.split(",");
        for (String value : values) {
            String[] valueData = value.split("\\|");
            if (valueData.length > 0) {
                if (valueData[0].equals(valueString)) {
                    return valueData.length > 1 ? valueData[1] : valueData[0];
                }
            }
        }
        return valueString;
    }
}

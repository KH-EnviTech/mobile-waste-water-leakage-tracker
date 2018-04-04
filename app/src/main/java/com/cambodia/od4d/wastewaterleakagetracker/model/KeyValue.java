package com.cambodia.od4d.wastewaterleakagetracker.model;

/**
 * Created by wandy on 8/8/17.
 */

public class KeyValue {
    private String key;
    private String Value;

    public KeyValue(String key, String value){
        setKey(key);
        setValue(value);
    }

    public String getKey() {
        return key;
    }

    private void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return Value;
    }

    private void setValue(String value) {
        Value = value;
    }
}

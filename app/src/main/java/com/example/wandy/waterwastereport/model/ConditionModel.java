package com.example.wandy.waterwastereport.model;

/**
 * Created by wandy on 2/26/18.
 */

public class ConditionModel {
    private String condition;
    private String color;
    private String id;

    public ConditionModel(String id, String status, String color){
        setColor(color);
        setId(id);
        setCondition(status);
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

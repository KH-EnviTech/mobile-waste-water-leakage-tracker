package com.cambodia.od4d.wastewaterleakagetracker.model;

/**
 * Created by wandy on 2/26/18.
 */

public class AreaModel {
    private String id;
    private String area;

    public AreaModel(String id, String area){
        setId(id);
        setArea(area);
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

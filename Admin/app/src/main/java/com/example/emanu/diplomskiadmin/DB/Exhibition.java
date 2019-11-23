package com.example.emanu.diplomskiadmin.DB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emanu on 9/7/2018.
 */

public class Exhibition {

    private Integer id;

    private String name;

    private boolean isSet;

    private List<Picture> pictures = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

}

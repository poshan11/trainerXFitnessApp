package com.cecs453.trainerx.model;

import com.cecs453.trainerx.ParametersWorkout;

import java.io.Serializable;

public class Exercise implements Serializable {

    private String type;
    private String name;
    private ParametersWorkout paramWrkOt;
    private Boolean isCustom;
    private String creator;

    public Exercise() {
    }

    public Exercise(String type, String name, ParametersWorkout paramWrkOt, Boolean isCustom) {
        this.type = type;
        this.name = name;
        this.paramWrkOt = paramWrkOt;
        this.isCustom=isCustom;
    }

    public Boolean getCustom() {
        return isCustom;
    }

    public void setCustom(Boolean custom) {
        isCustom = custom;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParametersWorkout getParamWrkOt() {
        return paramWrkOt;
    }

    public void setParamWrkOt(ParametersWorkout paramWrkOt) {
        this.paramWrkOt = paramWrkOt;
    }

    public Boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }
}

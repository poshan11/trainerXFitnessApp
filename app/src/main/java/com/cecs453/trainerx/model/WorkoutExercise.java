package com.cecs453.trainerx.model;

import com.cecs453.trainerx.ParametersWorkout;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

public class WorkoutExercise {

    private String type;
    private String name;
    private String ID;
    private long Order;

    public long getOrder() {
        return Order;
    }

    public void setOrder(long order) {
        Order = order;
    }

    public String getID() {

        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    //    private Integer numOfSets;

    public WorkoutExercise() {
    }

    public WorkoutExercise(String type, String name, ParametersWorkout paramWrkOt, Boolean isCustom) {
        this.type = type;
        this.name = name;
//        this.numOfSets = 3;
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

//    public Integer getNumOfSets() {
//        return numOfSets;
//    }
//
//    public void setNumOfSets(Integer numOfSets) {
//        this.numOfSets = numOfSets;
//    }
}

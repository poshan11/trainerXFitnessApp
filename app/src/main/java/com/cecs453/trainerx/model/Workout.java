package com.cecs453.trainerx.model;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

public class Workout {

    private List<WorkoutExercise> exercise;
    private Double date;
    private String trainer;
    private String ID;
    private String customerId;

    public Workout() {

    }

    public void setExerciseList(List<WorkoutExercise> exercise) {
        this.exercise = exercise;
    }

    public List<WorkoutExercise> getExerciseList() {
        return exercise;
    }

    public Double getDate() {
        return date;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDate(Double date) {
        this.date = date;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }
}

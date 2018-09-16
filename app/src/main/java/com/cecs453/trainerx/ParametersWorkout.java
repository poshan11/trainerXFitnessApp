package com.cecs453.trainerx;

public class ParametersWorkout {

    private String reps;
    private String weights;
    private String time;

    public ParametersWorkout() {
    }

    public ParametersWorkout(String reps, String weights, String time) {
        this.reps = reps;
        this.weights = weights;
        this.time = time;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getWeights() {
        return weights;
    }

    public void setWeights(String weights) {
        this.weights = weights;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

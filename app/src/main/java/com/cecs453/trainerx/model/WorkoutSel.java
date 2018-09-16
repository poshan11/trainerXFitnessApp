package com.cecs453.trainerx.model;

import java.util.HashMap;
import java.util.Map;

public class WorkoutSel {
    private String setNumber;
    private String srepetitions;
    private String sweight;
    private String stime;
    private String sspeed;
    private String sdistance;

    public WorkoutSel(String setNumber, String srepetitions, String sweight, String stime, String sspeed, String sdistance) {
        this.setNumber = setNumber;
        this.srepetitions = srepetitions;
        this.sweight = sweight;
        this.stime = stime;
        this.sspeed = sspeed;
        this.sdistance = sdistance;
    }

    public Map<String, String> returnWorkoutSel() {
        Map<String, String> h = new HashMap<>();
        h.put("Repetitions", srepetitions);
        h.put("Weight", sweight);
        h.put("Set", setNumber);
        h.put("Time", stime);
        h.put("Speed", sspeed);
        h.put("Distance", sdistance);
        return h;
    }

    public String getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public String getSrepetitions() {
        return srepetitions;
    }

    public void setSrepetitions(String srepetitions) {
        this.srepetitions = srepetitions;
    }

    public String getSweight() {
        return sweight;
    }

    public void setSweight(String sweight) {
        this.sweight = sweight;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getSspeed() {
        return sspeed;
    }

    public void setSspeed(String sspeed) {
        this.sspeed = sspeed;
    }

    public String getSdistance() {
        return sdistance;
    }

    public void setSdistance(String sdistance) {
        this.sdistance = sdistance;
    }
}

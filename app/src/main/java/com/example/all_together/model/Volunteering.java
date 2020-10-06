package com.example.all_together.model;

import com.google.type.Date;

public class Volunteering implements Comparable<Volunteering> {

    private long id;

    private String nameOld;
    private String locationCity;
    private String locationStreet;
    private String date;
    private String hour;
    private String type;
    private String description;
    private String oldUID;

    // is null at first
    private String volunteerUID;
    private String nameVolunteer;

    public Volunteering() {
    }

    public Volunteering(long id) {
        this.id = id;
    }

    public Volunteering(long id, String nameOld, String locationCity, String locationStreet, String date, String hour, String type, String description, String oldUID) {
        this.nameOld = nameOld;
        this.locationCity = locationCity;
        this.locationStreet = locationStreet;
        this.date = date;
        this.hour = hour;
        this.type = type;
        this.description = description;
        this.oldUID = oldUID;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNameOld() {
        return nameOld;
    }

    public void setNameOld(String nameOld) {
        this.nameOld = nameOld;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getLocationStreet() {
        return locationStreet;
    }

    public void setLocationStreet(String locationStreet) {
        this.locationStreet = locationStreet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOldUID() {
        return oldUID;
    }

    public void setOldUID(String oldUID) {
        this.oldUID = oldUID;
    }

    public String getVolunteerUID() {
        return volunteerUID;
    }

    public void setVolunteerUID(String volunteerUID) {
        this.volunteerUID = volunteerUID;
    }

    public String getNameVolunteer() {
        return nameVolunteer;
    }

    public void setNameVolunteer(String nameVolunteer) {
        this.nameVolunteer = nameVolunteer;
    }

    @Override
    public int compareTo(Volunteering o) {
        return this.nameOld.compareTo(o.nameOld);
    }
}



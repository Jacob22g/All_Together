package com.example.all_together.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Volunteering {

    private long id;

    private String name;
    private String locationCity;
    private String locationStreet;
    private String date;
    private String hour;
    private String type;
    private String description;

    private String oldUID;
    private String volunteerUID;

    public Volunteering() {
    }

    public Volunteering(long id) {
        this.id = id;
    }

    public Volunteering(long id, String name, String locationCity, String locationStreet, String date, String hour, String type, String description, String oldUID) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}



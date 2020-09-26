package com.example.all_together.model;

public class Volunteering {

    private String Name;
    private String Location;
    private String Date;
    private String Hour;
    private String Type;

    private String oldUID;
    private String volunteerUID;

    public Volunteering(String name, String location, String date, String hour, String type, String oldUID) {
        Name = name;
        Location = location;
        Date = date;
        Hour = hour;
        Type = type;
        this.oldUID = oldUID;
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

    public String getName() {
        return Name;
    }

    public String getLocation() {
        return Location;
    }

    public String getDate() {
        return Date;
    }

    public String getHour() {
        return Hour;
    }

    public String getType() {
        return Type;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setHour(String hour) {
        Hour = hour;
    }

    public void setType(String type) {
        Type = type;
    }


}



package com.example.all_together.model;

public class User {

private String last_name;
private String first_name;
private String age;
private String email;
private Boolean isOldUser;
private String image;

    public User(){} // Default constructor required for calls to DataSnapshot.getValue(User.class)

    public User(String last_name, String first_name, String age, String email, Boolean isOldUser, String image) {
        this.last_name = last_name;
        this.first_name = first_name;
        this.age = age;
        this.email = email;
        this.isOldUser = isOldUser;
        this.image = image;
    }

    public User(String email, String last_name, String first_name, Boolean isOldUser) {
        this.email = email;
        this.last_name = last_name;
        this.first_name = first_name;
        this.isOldUser = isOldUser;
    }

    public User(String email, String last_name, String first_name) {
        this.email = email;
        this.last_name = last_name;
        this.first_name = first_name;
        isOldUser = false;
    }

    public Boolean getOldUser() {
        return isOldUser;
    }

    public void setOldUser(Boolean oldUser) {
        isOldUser = oldUser;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}








package com.initezz.ebookshop.model;

public class User {
    private String id;
    private String username;
    private String email;
    private String mobile;
    private String password;
    private String line1;
    private String line2;
    private String city;
    private String postal;

    public User() {
    }

    public User(String id, String username, String email, String mobile) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
    }

    public User(String id, String username, String email, String mobile, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
    }

    public User(String id, String username, String email, String mobile, String password, String line1, String line2, String city, String postal) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.postal = postal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }
}

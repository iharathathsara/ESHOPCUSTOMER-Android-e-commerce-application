package com.initezz.ebookshop.model;

public class Order {
    private String id;
    private String email;
    private String total;
    private String mobile;
    private String address1;
    private String address2;
    private String city;
    private String postal;
    private String latitude;
    private String longitude;
    private String date_time;
    private int deliver_status = 0;

    public Order() {
    }

    public Order(String id, String total, String date_time, int deliver_status) {
        this.id = id;
        this.total = total;
        this.date_time = date_time;
        this.deliver_status = deliver_status;
    }

    public Order(String id, String email, String total, String mobile, String address1, String address2, String city, String postal, String latitude, String longitude, String date_time, int deliver_status) {
        this.id = id;
        this.email = email;
        this.total = total;
        this.mobile = mobile;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.postal = postal;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date_time = date_time;
        this.deliver_status = deliver_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getDeliver_status() {
        return deliver_status;
    }

    public void setDeliver_status(int deliver_status) {
        this.deliver_status = deliver_status;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
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

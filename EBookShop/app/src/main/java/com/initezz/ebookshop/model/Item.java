package com.initezz.ebookshop.model;

public class Item {
    private String id;
    private String title;
    private String description;
    private String price;
    private String qty;
    private String image1Path;
    private String image2Path;
    private String image3Path;
    private String category;
    private String status;

    public Item() {
    }

    public Item(String id, String title, String price, String qty, String image1Path) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.qty = qty;
        this.image1Path = image1Path;
    }

    public Item(String id, String title, String description, String price, String qty, String image1Path, String image2Path, String image3Path, String category, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.qty = qty;
        this.image1Path = image1Path;
        this.image2Path = image2Path;
        this.image3Path = image3Path;
        this.category = category;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage1Path() {
        return image1Path;
    }

    public void setImage1Path(String image1Path) {
        this.image1Path = image1Path;
    }

    public String getImage2Path() {
        return image2Path;
    }

    public void setImage2Path(String image2Path) {
        this.image2Path = image2Path;
    }

    public String getImage3Path() {
        return image3Path;
    }

    public void setImage3Path(String image3Path) {
        this.image3Path = image3Path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String categoty) {
        this.category = categoty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

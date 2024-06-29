package com.example.bumitani;

public class Product {
    private String productId;
    private String productName;
    private String productDesc;
    private double finalProductPrice;
    private String productCategory;

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public double getFinalProductPrice() {
        return finalProductPrice;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public Product(String productId, String productName, String productDesc, double finalProductPrice, String productCategory, String imageUrl) {
    }
}

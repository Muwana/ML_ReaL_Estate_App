package com.ml.ml_real_estate.models;

import com.google.gson.annotations.SerializedName;

public class Property {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("propertyType")
    private String propertyType;

    @SerializedName("listingType")
    private String listingType;

    @SerializedName("bedrooms")
    private int bedrooms;

    @SerializedName("bathrooms")
    private double bathrooms;

    @SerializedName("areaSqft")
    private double areaSqft;

    @SerializedName("yearBuilt")
    private String yearBuilt;

    @SerializedName("status")
    private String status;

    @SerializedName("featured")
    private boolean featured;

    @SerializedName("userId")
    private int userId;

    @SerializedName("sellerName")
    private String sellerName;

    @SerializedName("primaryImage")
    private String primaryImage;

    @SerializedName("imageCount")
    private int imageCount;

    @SerializedName("address")
    private Address address;

    @SerializedName("coordinates")
    private Coordinates coordinates;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

    public String getListingType() { return listingType; }
    public void setListingType(String listingType) { this.listingType = listingType; }

    public int getBedrooms() { return bedrooms; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

    public double getBathrooms() { return bathrooms; }
    public void setBathrooms(double bathrooms) { this.bathrooms = bathrooms; }

    public double getAreaSqft() { return areaSqft; }
    public void setAreaSqft(double areaSqft) { this.areaSqft = areaSqft; }

    public String getYearBuilt() { return yearBuilt; }
    public void setYearBuilt(String yearBuilt) { this.yearBuilt = yearBuilt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getPrimaryImage() { return primaryImage; }
    public void setPrimaryImage(String primaryImage) { this.primaryImage = primaryImage; }

    public int getImageCount() { return imageCount; }
    public void setImageCount(int imageCount) { this.imageCount = imageCount; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Address inner class
    public static class Address {
        @SerializedName("line1")
        private String line1;

        @SerializedName("line2")
        private String line2;

        @SerializedName("city")
        private String city;

        @SerializedName("state")
        private String state;

        @SerializedName("zipCode")
        private String zipCode;

        @SerializedName("country")
        private String country;

        // Getters and Setters
        public String getLine1() { return line1; }
        public void setLine1(String line1) { this.line1 = line1; }

        public String getLine2() { return line2; }
        public void setLine2(String line2) { this.line2 = line2; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getFullAddress() {
            return line1 + (line2 != null ? ", " + line2 : "") + ", " + city + ", " + state + " " + zipCode;
        }
    }

    // Coordinates inner class
    public static class Coordinates {
        @SerializedName("latitude")
        private Double latitude;

        @SerializedName("longitude")
        private Double longitude;

        // Getters and Setters
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }
}
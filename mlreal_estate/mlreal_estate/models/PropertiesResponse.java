package com.ml.mlreal_estate.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PropertiesResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("properties")
    private List<Property> properties;

    @SerializedName("pagination")
    private Pagination pagination;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Property> getProperties() { return properties; }
    public void setProperties(List<Property> properties) { this.properties = properties; }

    public Pagination getPagination() { return pagination; }
    public void setPagination(Pagination pagination) { this.pagination = pagination; }

    // Pagination inner class
    public static class Pagination {
        @SerializedName("total")
        private int total;

        @SerializedName("page")
        private int page;

        @SerializedName("limit")
        private int limit;

        @SerializedName("totalPages")
        private int totalPages;

        // Getters and Setters
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }

        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }

        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}
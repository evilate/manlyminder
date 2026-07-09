package com.manlyminder.app.model;

public class PersonInterest {

    private String category;
    private String value;
    private String notes;

    public PersonInterest() {
    }

    public PersonInterest(String category, String value, String notes) {
        this.category = category;
        this.value = value;
        this.notes = notes;
    }

    public String getCategory() {
        return category;
    }

    public String getValue() {
        return value;
    }

    public String getNotes() {
        return notes;
    }
}
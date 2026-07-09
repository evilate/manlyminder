package com.manlyminder.app.model;

public class RelatedPerson {

    private String name;
    private String relationType;
    private long birthday;
    private String notes;

    public RelatedPerson() {
    }

    public RelatedPerson(String name, String relationType, long birthday, String notes) {
        this.name = name;
        this.relationType = relationType;
        this.birthday = birthday;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public String getRelationType() {
        return relationType;
    }

    public long getBirthday() {
        return birthday;
    }

    public String getNotes() {
        return notes;
    }
}
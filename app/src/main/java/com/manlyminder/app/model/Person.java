package com.manlyminder.app.model;

import java.util.ArrayList;
import java.util.List;

public class Person {

    private String name;
    private String relationType;
    private long birthday;
    private long lastContact;
    private String notes;
    private String contactUri;

    private List<RelatedPerson> relatedPeople;
    private List<PersonEvent> events;
    private List<PersonInterest> interests;

    public Person() {
        initLists();
    }

    public Person(
            String name,
            String relationType,
            long birthday,
            long lastContact,
            String notes
    ) {
        this.name = name;
        this.relationType = relationType;
        this.birthday = birthday;
        this.lastContact = lastContact;
        this.notes = notes;
        this.contactUri = "";
        initLists();
    }

    public void initLists() {
        if (relatedPeople == null) {
            relatedPeople = new ArrayList<>();
        }

        if (events == null) {
            events = new ArrayList<>();
        }

        if (interests == null) {
            interests = new ArrayList<>();
        }
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

    public long getLastContact() {
        return lastContact;
    }

    public String getNotes() {
        return notes;
    }

    public String getContactUri() {
        return contactUri;
    }

    public List<RelatedPerson> getRelatedPeople() {
        initLists();
        return relatedPeople;
    }

    public List<PersonEvent> getEvents() {
        initLists();
        return events;
    }

    public List<PersonInterest> getInterests() {
        initLists();
        return interests;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public void setLastContact(long lastContact) {
        this.lastContact = lastContact;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setContactUri(String contactUri) {
        this.contactUri = contactUri;
    }
}
package com.manlyminder.app.model;

public class DashboardReminder {

    public static final String TYPE_BIRTHDAY = "birthday";
    public static final String TYPE_EVENT = "event";
    public static final String TYPE_NO_CONTACT = "no_contact";
    public static final String TYPE_INFO = "info";

    private String id;
    private String title;
    private String subtitle;
    private String type;
    private int personIndex;

    public DashboardReminder(
            String id,
            String title,
            String subtitle,
            String type,
            int personIndex
    ) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.personIndex = personIndex;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getType() {
        return type;
    }

    public int getPersonIndex() {
        return personIndex;
    }

    public boolean hasPersonTarget() {
        return personIndex >= 0;
    }
}
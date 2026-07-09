package com.manlyminder.app.model;

public class PersonEvent {

    private String title;
    private String type;
    private long date;
    private boolean repeatsYearly;
    private int remindDaysBefore;
    private String notes;

    public PersonEvent() {
    }

    public PersonEvent(
            String title,
            String type,
            long date,
            boolean repeatsYearly,
            int remindDaysBefore,
            String notes
    ) {
        this.title = title;
        this.type = type;
        this.date = date;
        this.repeatsYearly = repeatsYearly;
        this.remindDaysBefore = remindDaysBefore;
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public long getDate() {
        return date;
    }

    public boolean isRepeatsYearly() {
        return repeatsYearly;
    }

    public int getRemindDaysBefore() {
        return remindDaysBefore;
    }

    public String getNotes() {
        return notes;
    }
}
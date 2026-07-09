package com.manlyminder.app.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReminderStateManager {

    private static final String PREFS_NAME = "manlyminder";
    private static final String KEY_HIDDEN_REMINDERS = "hidden_reminders";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public ReminderStateManager(Context context) {
        prefs = context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );
    }

    public boolean isHidden(String reminderId) {
        Map<String, Long> hidden = getHiddenReminders();

        Long hiddenUntil = hidden.get(reminderId);

        if (hiddenUntil == null) {
            return false;
        }

        if (hiddenUntil < System.currentTimeMillis()) {
            hidden.remove(reminderId);
            saveHiddenReminders(hidden);
            return false;
        }

        return true;
    }

    public void markDone(String reminderId, int days) {
        hideForDays(reminderId, days);
    }

    public void snooze(String reminderId) {
        hideForDays(reminderId, 3);
    }

    public void dismiss(String reminderId) {
        hideForDays(reminderId, 365);
    }

    private void hideForDays(String reminderId, int days) {
        long hiddenUntil =
                System.currentTimeMillis()
                        + (days * 24L * 60L * 60L * 1000L);

        Map<String, Long> hidden =
                getHiddenReminders();

        hidden.put(
                reminderId,
                hiddenUntil
        );

        saveHiddenReminders(hidden);
    }

    private Map<String, Long> getHiddenReminders() {
        String json =
                prefs.getString(
                        KEY_HIDDEN_REMINDERS,
                        null
                );

        if (json == null) {
            return new HashMap<>();
        }

        Type type =
                new TypeToken<Map<String, Long>>() {
                }.getType();

        Map<String, Long> hidden =
                gson.fromJson(
                        json,
                        type
                );

        if (hidden == null) {
            return new HashMap<>();
        }

        cleanupExpired(hidden);

        return hidden;
    }

    private void cleanupExpired(Map<String, Long> hidden) {
        long now =
                System.currentTimeMillis();

        Iterator<Map.Entry<String, Long>> iterator =
                hidden.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry =
                    iterator.next();

            if (entry.getValue() < now) {
                iterator.remove();
            }
        }
    }

    private void saveHiddenReminders(
            Map<String, Long> hidden
    ) {
        prefs.edit()
                .putString(
                        KEY_HIDDEN_REMINDERS,
                        gson.toJson(hidden)
                )
                .apply();
    }
}
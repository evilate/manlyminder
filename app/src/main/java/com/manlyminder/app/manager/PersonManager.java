package com.manlyminder.app.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manlyminder.app.model.Person;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class PersonManager {

    private static final String PREFS_NAME =
            "manlyminder";

    private static final String KEY_PERSONS =
            "persons";

    private final SharedPreferences prefs;

    private final Gson gson =
            new Gson();

    public PersonManager(Context context) {

        prefs = context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );

    }

    public void savePersons(
            List<Person> persons
    ) {

        String json =
                gson.toJson(persons);

        prefs.edit()
                .putString(
                        KEY_PERSONS,
                        json
                )
                .apply();
    }

    public List<Person> getPersons() {

        String json =
                prefs.getString(
                        KEY_PERSONS,
                        null
                );

        if (json == null) {

            return new ArrayList<>();
        }

        Type type =
                new TypeToken<
                        List<Person>>() {
                }.getType();

        List<Person> persons =
                gson.fromJson(
                        json,
                        type
                );

        return persons != null
                ? persons
                : new ArrayList<>();
    }
}
package com.manlyminder.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manlyminder.app.fragment.EventsFragment;
import com.manlyminder.app.fragment.HomeFragment;
import com.manlyminder.app.fragment.PeopleFragment;
import com.manlyminder.app.fragment.SettingsFragment;
import com.manlyminder.app.manager.PersonManager;
import com.manlyminder.app.model.Person;
import com.manlyminder.app.model.PersonEvent;
import com.manlyminder.app.model.PersonInterest;
import com.manlyminder.app.model.RelatedPerson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "manlyminder";
    private static final String KEY_SAMPLE_DATA_CREATED = "sample_data_created";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createSampleDataIfNeeded();

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {

                loadFragment(new HomeFragment());

            } else if (item.getItemId() == R.id.nav_people) {

                loadFragment(new PeopleFragment());

            } else if (item.getItemId() == R.id.nav_events) {

                loadFragment(new EventsFragment());

            } else if (item.getItemId() == R.id.nav_settings) {

                loadFragment(new SettingsFragment());
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void createSampleDataIfNeeded() {
        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        boolean alreadyCreated =
                prefs.getBoolean(KEY_SAMPLE_DATA_CREATED, false);

        if (alreadyCreated) {
            return;
        }

        List<Person> samplePeople = new ArrayList<>();

        Person wife = new Person(
                "Emma",
                "Spouse",
                makeDate(1990, Calendar.MAY, 14),
                System.currentTimeMillis(),
                "Likes thoughtful gifts. Usually wants something that needs to be ordered early."
        );

        wife.getRelatedPeople().add(
                new RelatedPerson(
                        "Oliver",
                        "Child",
                        makeDate(2016, Calendar.SEPTEMBER, 3),
                        "Likes dinosaurs and LEGO."
                )
        );

        wife.getEvents().add(
                new PersonEvent(
                        "Wedding anniversary",
                        "Anniversary",
                        makeDate(2021, Calendar.JUNE, 12),
                        true,
                        30,
                        "Remember to order gift at least 3 weeks before."
                )
        );

        wife.getInterests().add(
                new PersonInterest(
                        "Gift idea",
                        "Weekend getaway",
                        "Something quiet, preferably without the kids."
                )
        );

        samplePeople.add(wife);

        Person friend = new Person(
                "Thomas",
                "Friend",
                makeDate(1988, Calendar.NOVEMBER, 22),
                daysAgo(47),
                "Old friend. Has kids. Easy to forget checking in."
        );

        friend.getRelatedPeople().add(
                new RelatedPerson(
                        "Maria",
                        "Spouse",
                        makeDate(1989, Calendar.APRIL, 8),
                        ""
                )
        );

        friend.getRelatedPeople().add(
                new RelatedPerson(
                        "Alma",
                        "Child",
                        makeDate(2020, Calendar.JANUARY, 18),
                        "Likes drawing stuff."
                )
        );

        friend.getEvents().add(
                new PersonEvent(
                        "Housewarming",
                        "Party",
                        makeDate(2025, Calendar.AUGUST, 16),
                        false,
                        14,
                        "Bring wine or something for the house."
                )
        );

        friend.getInterests().add(
                new PersonInterest(
                        "Hobby",
                        "Running",
                        "Ask how training is going."
                )
        );

        samplePeople.add(friend);

        Person acquaintance = new Person(
                "Martin",
                "Other",
                makeDate(1992, Calendar.FEBRUARY, 4),
                daysAgo(120),
                "Acquaintance from work. Good contact to maintain lightly."
        );

        acquaintance.getEvents().add(
                new PersonEvent(
                        "Met at conference",
                        "Other",
                        makeDate(2024, Calendar.OCTOBER, 2),
                        false,
                        0,
                        "Talked about renovation and electrical work."
                )
        );

        acquaintance.getInterests().add(
                new PersonInterest(
                        "Work",
                        "Electrician",
                        "Potentially useful contact later."
                )
        );

        samplePeople.add(acquaintance);

        new PersonManager(this).savePersons(samplePeople);

        prefs.edit()
                .putBoolean(KEY_SAMPLE_DATA_CREATED, true)
                .apply();
    }

    private long makeDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(
                year,
                month,
                day,
                0,
                0,
                0
        );

        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    private long daysAgo(int days) {
        Calendar calendar = Calendar.getInstance();

        calendar.add(
                Calendar.DAY_OF_YEAR,
                -days
        );

        return calendar.getTimeInMillis();
    }
}
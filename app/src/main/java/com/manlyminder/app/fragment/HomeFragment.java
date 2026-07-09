package com.manlyminder.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manlyminder.app.PersonDetailsActivity;
import com.manlyminder.app.R;
import com.manlyminder.app.adapter.ReminderAdapter;
import com.manlyminder.app.manager.PersonManager;
import com.manlyminder.app.manager.ReminderStateManager;
import com.manlyminder.app.model.DashboardReminder;
import com.manlyminder.app.model.Person;
import com.manlyminder.app.model.PersonEvent;
import com.manlyminder.app.model.RelatedPerson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment implements ReminderAdapter.ReminderListener {

    private List<DashboardReminder> reminders;
    private ReminderAdapter adapter;

    private PersonManager personManager;
    private ReminderStateManager reminderStateManager;

    private List<Person> persons;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view =
                inflater.inflate(
                        R.layout.fragment_home,
                        container,
                        false
                );

        personManager =
                new PersonManager(
                        requireContext()
                );

        reminderStateManager =
                new ReminderStateManager(
                        requireContext()
                );

        persons =
                personManager.getPersons();

        reminders =
                buildReminders();

        RecyclerView recyclerView =
                view.findViewById(
                        R.id.recyclerReminders
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        adapter =
                new ReminderAdapter(
                        reminders,
                        this
                );

        recyclerView.setAdapter(
                adapter
        );

        return view;
    }

    private List<DashboardReminder> buildReminders() {
        List<DashboardReminder> result =
                new ArrayList<>();

        for (int i = 0; i < persons.size(); i++) {
            Person person =
                    persons.get(i);

            person.initLists();

            addNoContactReminder(
                    result,
                    person,
                    i
            );

            addBirthdayReminder(
                    result,
                    "birthday_person_" + i,
                    person.getName(),
                    person.getBirthday(),
                    i
            );

            for (RelatedPerson related : person.getRelatedPeople()) {
                addBirthdayReminder(
                        result,
                        "birthday_related_" + i + "_" + related.getName(),
                        related.getName() + " (" + person.getName() + ")",
                        related.getBirthday(),
                        i
                );
            }

            for (PersonEvent event : person.getEvents()) {
                addEventReminder(
                        result,
                        person,
                        event,
                        i
                );
            }
        }

        if (result.isEmpty()) {
            result.add(
                    new DashboardReminder(
                            "info_all_clear",
                            "All clear",
                            "No reminders need your attention right now.",
                            DashboardReminder.TYPE_INFO,
                            -1
                    )
            );
        }

        return result;
    }

    private void addNoContactReminder(
            List<DashboardReminder> result,
            Person person,
            int personIndex
    ) {
        String type =
                person.getRelationType();

        if ("Spouse".equals(type) ||
                "Partner".equals(type) ||
                "Child".equals(type)) {
            return;
        }

        long days =
                TimeUnit.MILLISECONDS.toDays(
                        System.currentTimeMillis()
                                - person.getLastContact()
                );

        if (days < 30) {
            return;
        }

        String reminderId =
                "no_contact_" + personIndex;

        if (reminderStateManager.isHidden(reminderId)) {
            return;
        }

        result.add(
                new DashboardReminder(
                        reminderId,
                        person.getName(),
                        "You have not contacted this person in " + days + " days.",
                        DashboardReminder.TYPE_NO_CONTACT,
                        personIndex
                )
        );
    }

    private void addBirthdayReminder(
            List<DashboardReminder> result,
            String reminderId,
            String name,
            long birthday,
            int personIndex
    ) {
        if (birthday <= 0) {
            return;
        }

        if (reminderStateManager.isHidden(reminderId)) {
            return;
        }

        long daysUntil =
                daysUntilNextAnnualDate(
                        birthday
                );

        if (daysUntil <= 30) {
            result.add(
                    new DashboardReminder(
                            reminderId,
                            name,
                            "Birthday in " + daysUntil + " days.",
                            DashboardReminder.TYPE_BIRTHDAY,
                            personIndex
                    )
            );
        }
    }

    private void addEventReminder(
            List<DashboardReminder> result,
            Person person,
            PersonEvent event,
            int personIndex
    ) {
        if (event.getDate() <= 0) {
            return;
        }

        String reminderId =
                "event_" +
                        personIndex +
                        "_" +
                        event.getTitle() +
                        "_" +
                        event.getDate();

        if (reminderStateManager.isHidden(reminderId)) {
            return;
        }

        long daysUntil;

        if (event.isRepeatsYearly()) {
            daysUntil =
                    daysUntilNextAnnualDate(
                            event.getDate()
                    );
        } else {
            daysUntil =
                    daysBetweenTodayAnd(
                            event.getDate()
                    );
        }

        if (daysUntil < 0) {
            return;
        }

        int remindDays =
                event.getRemindDaysBefore();

        if (remindDays <= 0) {
            remindDays = 14;
        }

        if (daysUntil <= remindDays) {
            result.add(
                    new DashboardReminder(
                            reminderId,
                            event.getTitle(),
                            person.getName() +
                                    " · " +
                                    event.getType() +
                                    " in " +
                                    daysUntil +
                                    " days.",
                            DashboardReminder.TYPE_EVENT,
                            personIndex
                    )
            );
        }
    }

    private long daysBetweenTodayAnd(long date) {
        Calendar today =
                Calendar.getInstance();

        clearTime(today);

        Calendar target =
                Calendar.getInstance();

        target.setTimeInMillis(
                date
        );

        clearTime(target);

        return TimeUnit.MILLISECONDS.toDays(
                target.getTimeInMillis()
                        - today.getTimeInMillis()
        );
    }

    private long daysUntilNextAnnualDate(long date) {
        Calendar today =
                Calendar.getInstance();

        clearTime(today);

        Calendar target =
                Calendar.getInstance();

        target.setTimeInMillis(
                date
        );

        target.set(
                today.get(Calendar.YEAR),
                target.get(Calendar.MONTH),
                target.get(Calendar.DAY_OF_MONTH)
        );

        clearTime(target);

        if (target.before(today)) {
            target.add(
                    Calendar.YEAR,
                    1
            );
        }

        return TimeUnit.MILLISECONDS.toDays(
                target.getTimeInMillis()
                        - today.getTimeInMillis()
        );
    }

    private void clearTime(Calendar calendar) {
        calendar.set(
                Calendar.HOUR_OF_DAY,
                0
        );

        calendar.set(
                Calendar.MINUTE,
                0
        );

        calendar.set(
                Calendar.SECOND,
                0
        );

        calendar.set(
                Calendar.MILLISECOND,
                0
        );
    }

    @Override
    public void onReminderClick(int position) {
        DashboardReminder reminder =
                adapter.getReminderAt(
                        position
                );

        if (reminder == null ||
                !reminder.hasPersonTarget()) {
            return;
        }

        Intent intent =
                new Intent(
                        requireContext(),
                        PersonDetailsActivity.class
                );

        intent.putExtra(
                PersonDetailsActivity.EXTRA_POSITION,
                reminder.getPersonIndex()
        );

        startActivity(
                intent
        );
    }

    @Override
    public void onDone(int position) {
        DashboardReminder reminder =
                adapter.getReminderAt(
                        position
                );

        if (reminder == null) {
            return;
        }

        if (DashboardReminder.TYPE_NO_CONTACT.equals(
                reminder.getType()
        )) {
            int personIndex =
                    reminder.getPersonIndex();

            if (personIndex >= 0 &&
                    personIndex < persons.size()) {

                persons.get(personIndex)
                        .setLastContact(
                                System.currentTimeMillis()
                        );

                personManager.savePersons(
                        persons
                );
            }

            reminderStateManager.markDone(
                    reminder.getId(),
                    30
            );

        } else {
            reminderStateManager.markDone(
                    reminder.getId(),
                    365
            );
        }

        adapter.removeAt(
                position
        );
    }

    @Override
    public void onSnooze(int position) {
        DashboardReminder reminder =
                adapter.getReminderAt(
                        position
                );

        if (reminder == null) {
            return;
        }

        reminderStateManager.snooze(
                reminder.getId()
        );

        adapter.removeAt(
                position
        );
    }

    @Override
    public void onDismiss(int position) {
        DashboardReminder reminder =
                adapter.getReminderAt(
                        position
                );

        if (reminder == null) {
            return;
        }

        reminderStateManager.dismiss(
                reminder.getId()
        );

        adapter.removeAt(
                position
        );
    }
}
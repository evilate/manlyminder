package com.manlyminder.app.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.manlyminder.app.PersonDetailsActivity;
import com.manlyminder.app.R;
import com.manlyminder.app.adapter.PersonAdapter;
import com.manlyminder.app.manager.PersonManager;
import com.manlyminder.app.model.Person;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PeopleFragment extends Fragment implements PersonAdapter.PersonListener {

    private PersonManager personManager;
    private List<Person> persons;
    private PersonAdapter adapter;

    private final String[] relationTypes = {
            "Spouse",
            "Partner",
            "Child",
            "Parent",
            "Sibling",
            "Friend",
            "Coworker",
            "Other"
    };

    public PeopleFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        personManager = new PersonManager(requireContext());
        persons = personManager.getPersons();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerPeople);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PersonAdapter(persons, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddPerson);
        fab.setOnClickListener(v -> showPersonDialog(-1));

        return view;
    }

    private void showPersonDialog(int position) {
        boolean isEdit = position >= 0;
        Person person = isEdit ? persons.get(position) : null;

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_person, null);

        EditText editName = dialogView.findViewById(R.id.editName);
        Spinner spinnerRelation = dialogView.findViewById(R.id.spinnerRelation);
        Button btnBirthday = dialogView.findViewById(R.id.btnBirthday);

        final long[] selectedBirthday = {0};

        setupRelationSpinner(spinnerRelation, isEdit ? person.getRelationType() : null);

        if (isEdit) {
            editName.setText(person.getName());

            selectedBirthday[0] = person.getBirthday();

            if (selectedBirthday[0] > 0) {
                btnBirthday.setText(formatBirthday(selectedBirthday[0]));
            }
        }

        btnBirthday.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            if (selectedBirthday[0] > 0) {
                calendar.setTimeInMillis(selectedBirthday[0]);
            }

            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selected = Calendar.getInstance();

                        selected.set(
                                year,
                                month,
                                dayOfMonth,
                                0,
                                0,
                                0
                        );

                        selected.set(Calendar.MILLISECOND, 0);

                        selectedBirthday[0] = selected.getTimeInMillis();

                        btnBirthday.setText(formatBirthday(selectedBirthday[0]));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });

        new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? "Edit Person" : "Add Person")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = editName.getText().toString().trim();
                    String relationType = spinnerRelation.getSelectedItem().toString();

                    if (name.isEmpty()) {
                        return;
                    }

                    if (isEdit) {
                        person.setName(name);
                        person.setRelationType(relationType);
                        person.setBirthday(selectedBirthday[0]);
                    } else {
                        Person newPerson = new Person(
                                name,
                                relationType,
                                selectedBirthday[0],
                                System.currentTimeMillis(),
                                ""
                        );

                        persons.add(newPerson);
                    }

                    saveAndRefresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupRelationSpinner(Spinner spinner, String selectedRelation) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                relationTypes
        );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(spinnerAdapter);

        if (selectedRelation == null) {
            return;
        }

        for (int i = 0; i < relationTypes.length; i++) {
            if (relationTypes[i].equals(selectedRelation)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void showDeleteDialog(int position) {
        Person person = persons.get(position);

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Person")
                .setMessage("Delete " + person.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    persons.remove(position);
                    saveAndRefresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveAndRefresh() {
        personManager.savePersons(persons);
        adapter.refresh();
    }

    private String formatBirthday(long birthday) {
        SimpleDateFormat format = new SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
        );

        return format.format(new Date(birthday));
    }

    @Override
    public void onPersonClick(int position) {
        Intent intent = new Intent(
                requireContext(),
                PersonDetailsActivity.class
        );

        intent.putExtra(
                PersonDetailsActivity.EXTRA_POSITION,
                position
        );

        startActivity(intent);
    }

    @Override
    public void onPersonLongClick(int position) {
        showDeleteDialog(position);
    }
}
package com.manlyminder.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.manlyminder.app.manager.PersonManager;
import com.manlyminder.app.model.Person;
import com.manlyminder.app.model.PersonEvent;
import com.manlyminder.app.model.PersonInterest;
import com.manlyminder.app.model.RelatedPerson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PersonDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    private static final int REQUEST_PICK_CONTACT = 1001;

    private PersonManager manager;
    private List<Person> persons;
    private Person person;

    private TextView txtName;
    private TextView txtRelation;
    private TextView txtBirthday;
    private TextView txtLastContact;
    private TextView txtLinkedContact;

    private EditText editNotes;

    private Button btnLinkContact;
    private Button btnOpenContact;

    private LinearLayout containerRelatedPeople;
    private LinearLayout containerEvents;
    private LinearLayout containerInterests;

    private final String[] relatedTypes = {
            "Spouse", "Partner", "Child", "Parent", "Sibling", "Other"
    };

    private final String[] eventTypes = {
            "Birthday", "Anniversary", "Wedding", "Party", "Dinner", "Gift", "Other"
    };

    private final String[] interestCategories = {
            "Hobby", "Work", "Study", "Housing", "Location", "Gift idea", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

        txtName = findViewById(R.id.txtName);
        txtRelation = findViewById(R.id.txtRelation);
        txtBirthday = findViewById(R.id.txtBirthday);
        txtLastContact = findViewById(R.id.txtLastContact);
        txtLinkedContact = findViewById(R.id.txtLinkedContact);

        editNotes = findViewById(R.id.editNotes);

        containerRelatedPeople = findViewById(R.id.containerRelatedPeople);
        containerEvents = findViewById(R.id.containerEvents);
        containerInterests = findViewById(R.id.containerInterests);

        Button btnContactedToday = findViewById(R.id.btnContactedToday);
        Button btnSaveNotes = findViewById(R.id.btnSaveNotes);
        Button btnAddRelatedPerson = findViewById(R.id.btnAddRelatedPerson);
        Button btnAddEvent = findViewById(R.id.btnAddEvent);
        Button btnAddInterest = findViewById(R.id.btnAddInterest);

        btnLinkContact = findViewById(R.id.btnLinkContact);
        btnOpenContact = findViewById(R.id.btnOpenContact);

        int position = getIntent().getIntExtra(EXTRA_POSITION, -1);

        manager = new PersonManager(this);
        persons = manager.getPersons();

        if (position < 0 || position >= persons.size()) {
            finish();
            return;
        }

        person = persons.get(position);
        person.initLists();

        updateUi();

        btnContactedToday.setOnClickListener(v -> {
            person.setLastContact(System.currentTimeMillis());
            saveAndRefresh();
        });

        btnSaveNotes.setOnClickListener(v -> {
            person.setNotes(editNotes.getText().toString().trim());
            manager.savePersons(persons);
        });

        btnLinkContact.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI
            );

            startActivityForResult(intent, REQUEST_PICK_CONTACT);
        });

        btnOpenContact.setOnClickListener(v -> {
            if (person.getContactUri() == null || person.getContactUri().isEmpty()) {
                return;
            }

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(person.getContactUri())
            );

            startActivity(intent);
        });

        btnAddRelatedPerson.setOnClickListener(v -> showAddRelatedPersonDialog());
        btnAddEvent.setOnClickListener(v -> showAddEventDialog());
        btnAddInterest.setOnClickListener(v -> showAddInterestDialog());
    }

    private void updateUi() {
        txtName.setText(person.getName());
        txtRelation.setText(person.getRelationType());

        if (person.getBirthday() > 0) {
            txtBirthday.setText("Birthday: " + formatDate(person.getBirthday()));
        } else {
            txtBirthday.setText("Birthday: Not set");
        }

        long days = TimeUnit.MILLISECONDS.toDays(
                System.currentTimeMillis() - person.getLastContact()
        );

        txtLastContact.setText("Last contact: " + days + " days ago");

        if (person.getContactUri() != null && !person.getContactUri().isEmpty()) {
            txtLinkedContact.setText("Contact: Linked");
            btnOpenContact.setEnabled(true);
            btnLinkContact.setText("Change Contact");
        } else {
            txtLinkedContact.setText("Contact: Not linked");
            btnOpenContact.setEnabled(false);
            btnLinkContact.setText("Link Contact");
        }

        editNotes.setText(person.getNotes() == null ? "" : person.getNotes());

        renderRelatedPeople();
        renderEvents();
        renderInterests();
    }

    private void renderRelatedPeople() {
        containerRelatedPeople.removeAllViews();

        if (person.getRelatedPeople().isEmpty()) {
            containerRelatedPeople.addView(makeSmallText("No related people yet"));
            return;
        }

        for (RelatedPerson related : person.getRelatedPeople()) {
            String text = related.getName() + " · " + related.getRelationType();

            if (related.getBirthday() > 0) {
                text += "\nBirthday: " + formatDate(related.getBirthday());
            }

            if (related.getNotes() != null && !related.getNotes().isEmpty()) {
                text += "\n" + related.getNotes();
            }

            containerRelatedPeople.addView(makeCardText(text));
        }
    }

    private void renderEvents() {
        containerEvents.removeAllViews();

        if (person.getEvents().isEmpty()) {
            containerEvents.addView(makeSmallText("No events yet"));
            return;
        }

        for (PersonEvent event : person.getEvents()) {
            String text = event.getTitle() + " · " + event.getType();

            if (event.getDate() > 0) {
                text += "\nDate: " + formatDate(event.getDate());
            }

            if (event.isRepeatsYearly()) {
                text += "\nRepeats yearly";
            }

            if (event.getRemindDaysBefore() > 0) {
                text += "\nReminder: " + event.getRemindDaysBefore() + " days before";
            }

            if (event.getNotes() != null && !event.getNotes().isEmpty()) {
                text += "\n" + event.getNotes();
            }

            containerEvents.addView(makeCardText(text));
        }
    }

    private void renderInterests() {
        containerInterests.removeAllViews();

        if (person.getInterests().isEmpty()) {
            containerInterests.addView(makeSmallText("No interests yet"));
            return;
        }

        for (PersonInterest interest : person.getInterests()) {
            String text = interest.getCategory() + ": " + interest.getValue();

            if (interest.getNotes() != null && !interest.getNotes().isEmpty()) {
                text += "\n" + interest.getNotes();
            }

            containerInterests.addView(makeCardText(text));
        }
    }

    private TextView makeSmallText(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setPadding(0, 8, 0, 8);
        view.setTextSize(13);
        return view;
    }

    private TextView makeCardText(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setPadding(18, 14, 18, 14);
        view.setTextSize(14);
        return view;
    }

    private void showAddRelatedPersonDialog() {
        LinearLayout layout = createDialogLayout();

        EditText editName = createEditText("Name");
        Spinner spinnerType = createSpinner(relatedTypes);
        Button btnBirthday = createButton("Select birthday");
        EditText editNotes = createEditText("Notes");

        final long[] birthday = {0};

        btnBirthday.setOnClickListener(v ->
                showDatePicker(0, selectedDate -> {
                    birthday[0] = selectedDate;
                    btnBirthday.setText(formatDate(selectedDate));
                })
        );

        layout.addView(editName);
        layout.addView(spinnerType);
        layout.addView(btnBirthday);
        layout.addView(editNotes);

        new AlertDialog.Builder(this)
                .setTitle("Add Related Person")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    person.getRelatedPeople().add(
                            new RelatedPerson(
                                    editName.getText().toString().trim(),
                                    spinnerType.getSelectedItem().toString(),
                                    birthday[0],
                                    editNotes.getText().toString().trim()
                            )
                    );

                    saveAndRefresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddEventDialog() {
        LinearLayout layout = createDialogLayout();

        EditText editTitle = createEditText("Title");
        Spinner spinnerType = createSpinner(eventTypes);
        Button btnDate = createButton("Select date");
        CheckBox checkRepeats = new CheckBox(this);
        checkRepeats.setText("Repeats yearly");
        EditText editReminder = createEditText("Remind days before");
        EditText editNotes = createEditText("Notes");

        final long[] date = {0};

        btnDate.setOnClickListener(v ->
                showDatePicker(0, selectedDate -> {
                    date[0] = selectedDate;
                    btnDate.setText(formatDate(selectedDate));
                })
        );

        layout.addView(editTitle);
        layout.addView(spinnerType);
        layout.addView(btnDate);
        layout.addView(checkRepeats);
        layout.addView(editReminder);
        layout.addView(editNotes);

        new AlertDialog.Builder(this)
                .setTitle("Add Event")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    int remindDays = 0;

                    try {
                        remindDays = Integer.parseInt(editReminder.getText().toString().trim());
                    } catch (NumberFormatException ignored) {
                    }

                    person.getEvents().add(
                            new PersonEvent(
                                    editTitle.getText().toString().trim(),
                                    spinnerType.getSelectedItem().toString(),
                                    date[0],
                                    checkRepeats.isChecked(),
                                    remindDays,
                                    editNotes.getText().toString().trim()
                            )
                    );

                    saveAndRefresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddInterestDialog() {
        LinearLayout layout = createDialogLayout();

        Spinner spinnerCategory = createSpinner(interestCategories);
        EditText editValue = createEditText("Value");
        EditText editNotes = createEditText("Notes");

        layout.addView(spinnerCategory);
        layout.addView(editValue);
        layout.addView(editNotes);

        new AlertDialog.Builder(this)
                .setTitle("Add Interest")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    person.getInterests().add(
                            new PersonInterest(
                                    spinnerCategory.getSelectedItem().toString(),
                                    editValue.getText().toString().trim(),
                                    editNotes.getText().toString().trim()
                            )
                    );

                    saveAndRefresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private LinearLayout createDialogLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 0);
        return layout;
    }

    private EditText createEditText(String hint) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        return editText;
    }

    private Button createButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        return button;
    }

    private Spinner createSpinner(String[] items) {
        Spinner spinner = new Spinner(this);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        items
                );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        return spinner;
    }

    private interface DateCallback {
        void onDateSelected(long date);
    }

    private void showDatePicker(long existingDate, DateCallback callback) {
        Calendar calendar = Calendar.getInstance();

        if (existingDate > 0) {
            calendar.setTimeInMillis(existingDate);
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();

                    selected.set(year, month, dayOfMonth, 0, 0, 0);
                    selected.set(Calendar.MILLISECOND, 0);

                    callback.onDateSelected(selected.getTimeInMillis());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private String formatDate(long date) {
        SimpleDateFormat format =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        return format.format(new Date(date));
    }

    private void saveAndRefresh() {
        manager.savePersons(persons);
        updateUi();
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_CONTACT &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            person.setContactUri(data.getData().toString());

            manager.savePersons(persons);

            updateUi();
        }
    }
}
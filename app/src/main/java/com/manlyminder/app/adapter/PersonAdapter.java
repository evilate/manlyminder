package com.manlyminder.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manlyminder.app.R;
import com.manlyminder.app.model.Person;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {

    public interface PersonListener {
        void onPersonClick(int position);
        void onPersonLongClick(int position);
    }

    private final List<Person> persons;
    private final PersonListener listener;

    public PersonAdapter(List<Person> persons, PersonListener listener) {
        this.persons = persons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_person,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        Person person = persons.get(position);

        holder.txtName.setText(person.getName());
        holder.txtRelationship.setText(person.getRelationType());

        holder.itemView.setOnClickListener(v ->
                listener.onPersonClick(holder.getAdapterPosition())
        );

        holder.itemView.setOnLongClickListener(v -> {
            listener.onPersonLongClick(holder.getAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtRelationship;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtRelationship = itemView.findViewById(R.id.txtRelationship);
        }
    }
}
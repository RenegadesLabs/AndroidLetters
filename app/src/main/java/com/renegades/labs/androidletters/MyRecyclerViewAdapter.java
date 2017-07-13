package com.renegades.labs.androidletters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.pinxter.letters.Letters;

/**
 * Created by Виталик on 12.07.2017.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private List<MyContact> contactsList;
    private Letters letters;

    public MyRecyclerViewAdapter(List<MyContact> contactsList, Letters letters) {
        this.contactsList = contactsList;
        this.letters = letters;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phone;
        TextView letter;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.contact_name);
            phone = (TextView) itemView.findViewById(R.id.contact_phone);
            letter = (TextView) itemView.findViewById(R.id.item_users_letter);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(contactsList.get(position).getName());
        holder.phone.setText(contactsList.get(position).getPhone());
        holder.letter.setText(letters.getLetter(position));
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }


}
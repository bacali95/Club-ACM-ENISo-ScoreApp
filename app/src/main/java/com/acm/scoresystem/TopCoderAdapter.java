package com.acm.scoresystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.acm.scoresystem.Model.User;

import java.util.List;

public class TopCoderAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> users;

    public TopCoderAdapter(@NonNull Context context, List<User> users) {
        super(context, 0, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.topcoder_item, parent, false);
        }

        User user = getItem(position);

        TextView rang = listItem.findViewById(R.id.rang);
        rang.setText(String.valueOf(position + 1));

        TextView name = listItem.findViewById(R.id.name);
        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));

        TextView ps = listItem.findViewById(R.id.solved);
        ps.setText(String.valueOf(user.getProblemSolved()));

        TextView score = listItem.findViewById(R.id.score);
        score.setText(String.valueOf(user.getScore()));

        return listItem;
    }
}

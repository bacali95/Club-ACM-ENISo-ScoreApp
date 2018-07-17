package com.acm.scoresystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acm.scoresystem.Model.Notification;
import com.acm.scoresystem.Model.User;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    private Context context;
    private List<Notification> notifications;

    public NotificationAdapter(@NonNull Context context, List<Notification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        }

        Notification notification = getItem(position);
        TextView body = listItem.findViewById(R.id.body);
        body.setText(notification.getBody());

        TextView time = listItem.findViewById(R.id.date);
        time.setText(notification.getSentTime());

        return listItem;
    }
}

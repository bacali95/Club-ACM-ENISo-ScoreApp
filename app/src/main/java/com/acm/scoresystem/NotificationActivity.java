package com.acm.scoresystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.acm.scoresystem.Model.Notification;
import com.acm.scoresystem.Repository.NotificationRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    NotificationRepository repo;
    List<Notification> notifs;
    TableLayout notificationsTable;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("notifications");
    public static List<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        repo = new NotificationRepository(this);
        final int color = getResources().getColor(R.color.index_text_color);
        notificationsTable = findViewById(R.id.notificationsTable);
        notificationsTable.removeAllViews();
        notifs = repo.getAll();
        notifications = new LinkedList<>(notifs);
        notificationsTable.removeAllViews();
        for (Notification notification : notifs) {
            TableRow row = new TableRow(getApplicationContext());
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            TextView column = new TextView(getApplicationContext());
            column.setText(notification.getSentTime());
            column.setTextSize(18);
            column.setTextColor(color);
            column.setGravity(Gravity.END);
            row.addView(column);
            notificationsTable.addView(row, 0);
            row = new TableRow(getApplicationContext());
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            column = new TextView(getApplicationContext());
            column.setText(notification.getBody());
            column.setTextSize(18);
            column.setTextColor(color);
            row.addView(column);
            notificationsTable.addView(row, 0);
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notifs = dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<Notification>>() {
                });
                Log.e("ok", "onDataChange: " + notifs);
                if (notifs != null) {
                    notificationsTable.removeAllViews();
                    long notificationsNbr = repo.countItems();
                    for (int i = 0; i < notifs.size(); i++) {
                        TableRow row = new TableRow(getApplicationContext());
                        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        TextView column = new TextView(getApplicationContext());
                        column.setText(notifs.get(i).getSentTime());
                        column.setTextSize(18);
                        column.setTextColor(color);
                        column.setGravity(Gravity.END);
                        row.addView(column);
                        notificationsTable.addView(row, 0);
                        row = new TableRow(getApplicationContext());
                        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        column = new TextView(getApplicationContext());
                        column.setText(notifs.get(i).getBody());
                        column.setTextSize(18);
                        column.setTextColor(color);
                        row.addView(column);
                        notificationsTable.addView(row, 0);
                        if (i >= notificationsNbr)
                            repo.add(new Notification(notifs.get(i).getBody(), notifs.get(i).getSentTime()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

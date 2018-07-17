package com.acm.scoresystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Adapter;
import android.widget.ListView;
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
    ListView notificationsList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("notifications");
    public static List<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        repo = new NotificationRepository(this);
        final int color = getResources().getColor(R.color.index_text_color);
        notificationsList = findViewById(R.id.notificationsList);
        notifs = repo.getAll();
        notifications = new ArrayList<>(notifs);
        NotificationAdapter adapter = new NotificationAdapter(getApplicationContext(),notifs);
        notificationsList.setAdapter(adapter);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notifs = dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<Notification>>() {
                });
                if (notifs != null) {
                    Notification n = null;
                    while (notifs.contains(n)){
                        notifs.remove(n);
                    }
                    NotificationAdapter adapter = new NotificationAdapter(getApplicationContext(),notifs);
                    notificationsList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

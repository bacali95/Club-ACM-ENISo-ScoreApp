package com.acm.scoresystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acm.scoresystem.DAO.UserDAO;
import com.acm.scoresystem.Model.User;

public class IndexActivity extends AppCompatActivity {

    TextView name;
    Button logout;
    Button notificationsBtn;
    Button topCodersBtn;
    Button statisticsBtn;
    UserDAO myDB;
    User user;
    LinearLayout firstLayout, secondLayout;
    Animation uptodown, downtoup;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        firstLayout = findViewById(R.id.firstL);
        secondLayout = findViewById(R.id.secondL);
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        firstLayout.setAnimation(uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        secondLayout.setAnimation(downtoup);
        myDB = new UserDAO(this);
        user = myDB.getFirst();
        name = findViewById(R.id.coderName);
        name.setText(" ".concat(user.getFirstName()));
        logout = findViewById(R.id.logout);
        logout.setAnimation(downtoup);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDB.removeAll();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        topCodersBtn = findViewById(R.id.topCodersBtn);
        topCodersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TopCodersActivity.class);
                startActivity(intent);
            }
        });
        notificationsBtn = findViewById(R.id.noficationsBtn);
        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });
        statisticsBtn = findViewById(R.id.statisticBtn);
        statisticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}

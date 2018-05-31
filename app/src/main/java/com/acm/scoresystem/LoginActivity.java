package com.acm.scoresystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.acm.scoresystem.Model.User;
import com.acm.scoresystem.Repository.UserRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private UserRepository repo;

    private EditText inputLogin, inputPassword;
    public Map<String, User> users;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        repo = new UserRepository(this);
        TextView version = findViewById(R.id.version);
        version.setText(String.valueOf(getResources().getString(R.string.version)).concat(" ").concat(BuildConfig.VERSION_NAME));
        users = new HashMap<>();
        if (repo.countItems() == 0) {
            inputLogin = findViewById(R.id.login);
            inputPassword = findViewById(R.id.password);
            Button btnLogin = findViewById(R.id.btn_login);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    users = dataSnapshot.getValue(new GenericTypeIndicator<Map<String, User>>() {
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String login = inputLogin.getText().toString();
                    final String password = inputPassword.getText().toString();

                    if (TextUtils.isEmpty(login)) {
                        Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (users == null || users.size() == 0) {
                        Toast.makeText(getApplicationContext(), "No Connection \uD83D\uDE41", Toast.LENGTH_LONG).show();
                    } else {
                        user = users.get(login);
                        if (user == null || !user.getPassword().equals(password)) {
                            Toast.makeText(getApplicationContext(), "Authentication failed, check your login or password !! \uD83D\uDEB7", Toast.LENGTH_LONG).show();
                        } else {
                            repo.add(user);
                            for (User u : users.values()) {
                                if (u.getPhoneNumber() != user.getPhoneNumber()) {
                                    repo.add(u);
                                }
                            }
                            users = null;
                            Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                            Toast.makeText(getApplicationContext(), "Hello " + user.getFirstName() + " \ud83d\udc4b", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }

                }
            });
        } else {
            Intent intent = new Intent(this, IndexActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        users = null;
        super.onDestroy();
    }
}


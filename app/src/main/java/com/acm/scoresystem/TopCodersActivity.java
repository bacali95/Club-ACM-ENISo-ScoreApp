package com.acm.scoresystem;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.acm.scoresystem.DAO.UserDAO;
import com.acm.scoresystem.Model.User;
import com.acm.scoresystem.Repository.UserRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TopCodersActivity extends AppCompatActivity {

    TableLayout top;
    Button refresh;
    Spinner spinner;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    UserRepository repo;
    List<User> users;
    boolean messageFromFirebase = false;
    boolean messageFromSpinner = false;
    Handler handlerError;
    Set<Long> toUp;
    Set<String> schoolYears;
    boolean threadInWork = false;
    int color = 0;

    @SuppressLint({"HandlerLeak", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_coders);

        spinner = findViewById(R.id.spinner);
        top = findViewById(R.id.topCodersTable);
        refresh = findViewById(R.id.refresh);

        repo = new UserRepository(this);
        users = repo.getAll();

        toUp = new HashSet<>();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                messageFromSpinner = true;
                new TopCodersTask().onPostExecute("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!threadInWork) {
                    refresh.setEnabled(false);
                    refresh.setText(R.string.loading_text);
                    toUp.clear();
                    threadInWork = true;
                    new TopCodersTask().execute();
                }
            }
        });

        handlerError = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                refresh.setEnabled(true);
                refresh.setText(R.string.refresh_text);
                Toast.makeText(getApplicationContext(), "No Connection \uD83D\uDE41", Toast.LENGTH_LONG).show();
            }
        };


        if (users != null) {
            schoolYears = new HashSet<>();
            schoolYears.add("All");
            for (User user : users) {
                schoolYears.add(user.getSchoolYear());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    new ArrayList<>(schoolYears));
            spinner.setAdapter(adapter);
            messageFromFirebase = true;
            new TopCodersTask().onPostExecute("");
            refresh.performClick();
        } else {
            handlerError.sendMessage(new Message());
        }

    }


    private class TopCodersTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            for (User user : users) {
                HttpURLConnection urlConnection;
                URL url;
                String jsonString;
                try {
                    url = new URL("http://codeforces.com/api/user.status?handle=" + user.getCodeforcesHandle());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    jsonString = sb.toString();
                    JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("result");
                    Set<String> set = new HashSet<>();
                    Map<String, Integer> problemsCount = new HashMap<>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        if (jsonArray.getJSONObject(j).getString("verdict").equals("OK")) {
                            JSONObject pb = jsonArray.getJSONObject(j).getJSONObject("problem");
                            String contestId = pb.getString("contestId");
                            String pbIndex = pb.getString("index");
                            set.add(contestId + "/" + pbIndex);
                            if (problemsCount.containsKey(pbIndex)) {
                                problemsCount.put(pbIndex, problemsCount.get(pbIndex) + 1);
                            } else {
                                problemsCount.put(pbIndex, 1);
                            }
                        }
                    }
                    int i = set.size();
                    DatabaseReference userReference;
                    if (i != user.getProblemSolved()) {
                        toUp.add(user.getPhoneNumber());
                        user.setProblemSolved(i);
                        user.calculScore(problemsCount);
                        repo.edit(user);
                        userReference = database.getReference("users").child(String.valueOf(user.getPhoneNumber()));
                        userReference.setValue(user);
                    }
                } catch (IOException | JSONException e) {
                    handlerError.sendMessage(new Message());
                    return "";
                }
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String aString) {
            threadInWork = false;
            top.removeAllViews();
            Collections.sort(users, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o2.getScore() - o1.getScore();
                }
            });
            int i = 1;
            for (User user : users) {
                if (!user.getSchoolYear().equals(spinner.getSelectedItem().toString())
                        && !spinner.getSelectedItem().toString().equals("All")) continue;
                String name = user.getFirstName() + " " + user.getLastName();
                TableRow row = new TableRow(getApplicationContext());
                row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                TextView column1 = new TextView(getApplicationContext());
                TextView column2 = new TextView(getApplicationContext());
                TextView column3 = new TextView(getApplicationContext());
                TextView column4 = new TextView(getApplicationContext());
                column1.setText(String.format(Locale.ENGLISH, "%d. %s", i, name));
                i++;
                column1.setTextSize(15);
                column2.setText(String.valueOf(user.getProblemSolved()));
                column2.setGravity(Gravity.END);
                column2.setTextSize(15);
                column2.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                column3.setText("    ");
                column4.setText(String.valueOf(user.getScore()));
                column4.setGravity(Gravity.END);
                column4.setTextSize(15);
                column4.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                if (toUp.contains(user.getPhoneNumber())) {
                    color = getResources().getColor(R.color.green);
                } else {
                    color = getResources().getColor(R.color.index_text_color);
                }
                column1.setTextColor(color);
                column2.setTextColor(color);
                column4.setTextColor(color);
                row.addView(column1);
                row.addView(column2);
                row.addView(column3);
                row.addView(column4);
                top.addView(row);
            }
            if (!messageFromFirebase && !messageFromSpinner) {
                refresh.setEnabled(true);
                refresh.setText(R.string.refresh_text);
            } else if (messageFromFirebase && !messageFromSpinner) {
                messageFromFirebase = false;
            } else if (!messageFromFirebase) {
                messageFromSpinner = false;
            }
        }
    }

}
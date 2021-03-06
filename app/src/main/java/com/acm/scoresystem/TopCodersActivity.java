package com.acm.scoresystem;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.acm.scoresystem.DAO.UserDAO;
import com.acm.scoresystem.Model.Contest;
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
import java.util.TreeMap;

public class TopCodersActivity extends AppCompatActivity {

    ListView usersList;
    Button refresh;
    Spinner spinner;
    ProgressDialog progressDialog;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    UserRepository repo;
    List<User> users;
    Map<Integer, Contest> contests = null;
    boolean messageFromFirebase = false;
    boolean messageFromSpinner = false;
    Handler handlerError;
    Set<String> schoolYears;
    boolean threadInWork = false;
    int color = 0;

    @SuppressLint({"HandlerLeak", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_coders);

        spinner = findViewById(R.id.spinner);
        usersList = findViewById(R.id.usersList);
        refresh = findViewById(R.id.refresh);

        repo = new UserRepository(this);
        users = repo.getAll();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                messageFromSpinner = true;
                refreshListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!threadInWork) {
                    new ContestTask().execute();
                }
            }
        });

        handlerError = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                refresh.setEnabled(true);
                refresh.setText(R.string.refresh_text);
                Toast.makeText(getApplicationContext(), "There is something wrong \uD83D\uDE41, " +
                        "please check your connection or try a gain !!", Toast.LENGTH_LONG).show();
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
            refreshListView();
            //refresh.performClick();
        } else {
            handlerError.sendMessage(new Message());
        }

    }

    public void refreshListView() {
        List<User> localUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.getSchoolYear().equals(spinner.getSelectedItem().toString())
                    && !spinner.getSelectedItem().toString().equals("All")) continue;
            localUsers.add(user);
        }
        Collections.sort(localUsers, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o2.getScore() - o1.getScore();
            }
        });

        TopCoderAdapter adapter = new TopCoderAdapter(getApplicationContext(), localUsers);
        usersList.setAdapter(adapter);

        if (!messageFromFirebase && !messageFromSpinner) {
            refresh.setEnabled(true);
            refresh.setText(R.string.refresh_text);
        } else if (messageFromFirebase && !messageFromSpinner) {
            messageFromFirebase = false;
        } else if (!messageFromFirebase) {
            messageFromSpinner = false;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class TopCodersTask extends AsyncTask<Void, Integer, String> {

        private String TAG = TopCodersTask.class.getSimpleName();

        TopCodersTask() {
            progressDialog = new ProgressDialog(TopCodersActivity.this);
            progressDialog.setMessage("Calculating...");
            progressDialog.setCancelable(false);
            progressDialog.setMax(users.size());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            int p = 0;
            for (User user : users) {
                p++;
                if (!user.getSchoolYear().equals(spinner.getSelectedItem().toString())
                        && !spinner.getSelectedItem().toString().equals("All")) continue;
                publishProgress(p);
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
                    problemsCount.put("A", 0);
                    problemsCount.put("B", 0);
                    problemsCount.put("C", 0);
                    problemsCount.put("D", 0);
                    problemsCount.put("E", 0);
                    problemsCount.put("F", 0);
                    problemsCount.put("G", 0);
                    problemsCount.put("H", 0);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject result = jsonArray.getJSONObject(i);
                        int contestId = result.getInt("contestId");
                        if (contests.containsKey(contestId) && result.getString("verdict").equals("OK")) {
                            JSONObject problem = result.getJSONObject("problem");
                            char pbIndex = problem.getString("index").charAt(0);
                            Contest contest = contests.get(contestId);
                            if (contest.getName().contains("div. 3")) {
                                pbIndex = (char) Math.min('A', pbIndex - 2);
                            } else if (contest.getName().contains("div. 1")) {
                                pbIndex = (char) Math.max('H', pbIndex + 2);
                            }
                            if (!set.contains(problem.getString("name"))) {
                                set.add(problem.getString("name"));
                                String key = String.valueOf(pbIndex);
                                if (problemsCount.containsKey(key)) {
                                    problemsCount.put(key, problemsCount.get(key) + 1);
                                }
                            }
                        }
                    }
                    int i = set.size();
                    if (i != user.getProblemSolved()) {
                        user.setProblemSolved(i);
                        user.calculScore(problemsCount);
                        repo.edit(user);
                        DatabaseReference userReference = database.getReference("users").child(String.valueOf(user.getPhoneNumber()));
                        userReference.setValue(user);
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "doInBackground: " + e.getMessage());
                    handlerError.sendMessage(new Message());
                    progressDialog.dismiss();
                    return "";
                }
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            threadInWork = false;
            refreshListView();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ContestTask extends AsyncTask<Void, Integer, String> {

        private String TAG = ContestTask.class.getSimpleName();

        ContestTask() {
            progressDialog = new ProgressDialog(TopCodersActivity.this);
            progressDialog.setMessage("Loading Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (contests == null) {
                HttpURLConnection urlConnection;
                URL url;
                String jsonString;
                try {
                    url = new URL("http://codeforces.com/api/contest.list?gym=false");
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
                    contests = new TreeMap<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        contests.put(obj.getInt("id"), new Contest(obj.getInt("id"),
                                obj.getString("name"),
                                obj.getString("type"),
                                obj.getString("phase"),
                                obj.getBoolean("frozen"),
                                obj.getLong("durationSeconds"),
                                obj.getLong("startTimeSeconds"),
                                obj.getLong("relativeTimeSeconds")));
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "doInBackground: " + e.getMessage());
                    handlerError.sendMessage(new Message());
                    progressDialog.dismiss();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            refresh.setEnabled(false);
            refresh.setText(R.string.loading_text);
            threadInWork = true;
            new TopCodersTask().execute();
        }
    }

}

package com.acm.scoresystem;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.acm.scoresystem.Model.Contest;
import com.acm.scoresystem.Model.User;
import com.acm.scoresystem.Repository.UserRepository;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class StatisticsActivity extends AppCompatActivity {

    TextView score;
    TextView solvedProblems;
    TextView scoreA;
    TextView scoreB;
    TextView scoreC;
    TextView scoreD;
    TextView scoreE;
    TextView scoreF;
    TextView scoreG;
    TextView scoreH;
    Button refresh;
    ProgressDialog progressDialog;

    UserRepository repo;
    User user;
    Map<Integer, Contest> contests;
    Map<String, Integer> problemsCount;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userReference;
    Handler handlerError;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        problemsCount = new HashMap<>();
        problemsCount.put("A", 0);
        problemsCount.put("B", 0);
        problemsCount.put("C", 0);
        problemsCount.put("D", 0);
        problemsCount.put("E", 0);
        problemsCount.put("F", 0);
        problemsCount.put("G", 0);
        problemsCount.put("H", 0);
        scoreA = findViewById(R.id.scoreA);
        scoreB = findViewById(R.id.scoreB);
        scoreC = findViewById(R.id.scoreC);
        scoreD = findViewById(R.id.scoreD);
        scoreE = findViewById(R.id.scoreE);
        scoreF = findViewById(R.id.scoreF);
        scoreG = findViewById(R.id.scoreG);
        scoreH = findViewById(R.id.scoreH);

        repo = new UserRepository(this);
        user = repo.getFirst();

        solvedProblems = findViewById(R.id.codeforcesScore);
        solvedProblems.setText(String.valueOf(user.getProblemSolved()));

        score = findViewById(R.id.acmScore);
        score.setText(String.valueOf(user.getScore()));

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ContestTask().execute();
            }
        });

        handlerError = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                scoreA.setText("-");
                scoreB.setText("-");
                scoreC.setText("-");
                scoreD.setText("-");
                scoreE.setText("-");
                scoreF.setText("-");
                scoreG.setText("-");
                scoreH.setText("-");
                refresh.setEnabled(true);
                refresh.setText(R.string.refresh_text);
                Toast.makeText(getApplicationContext(), "There is something wrong \uD83D\uDE41, " +
                        "please check your connection or try a gain !!", Toast.LENGTH_LONG).show();
            }
        };

        refresh.performClick();
    }

    @SuppressLint("StaticFieldLeak")
    private class StatisticsTask extends AsyncTask<Void, Integer, String> {

        private String TAG = StatisticsTask.class.getSimpleName();

        StatisticsTask() {
            progressDialog = new ProgressDialog(StatisticsActivity.this);
            progressDialog.setMessage("Calculating...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
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
                problemsCount = new HashMap<>();
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
                    userReference = database.getReference("users").child(String.valueOf(user.getPhoneNumber()));
                    userReference.setValue(user);
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "doInBackground: " + e.getMessage());
                handlerError.sendMessage(new Message());
                progressDialog.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            solvedProblems.setText(String.valueOf(user.getProblemSolved()));
            score.setText(String.valueOf(user.getScore()));
            scoreA.setText(String.valueOf(problemsCount.get("A")));
            scoreB.setText(String.valueOf(problemsCount.get("B")));
            scoreC.setText(String.valueOf(problemsCount.get("C")));
            scoreD.setText(String.valueOf(problemsCount.get("D")));
            scoreE.setText(String.valueOf(problemsCount.get("E")));
            scoreF.setText(String.valueOf(problemsCount.get("F")));
            scoreG.setText(String.valueOf(problemsCount.get("G")));
            scoreH.setText(String.valueOf(problemsCount.get("H")));
            refresh.setEnabled(true);
            refresh.setText(R.string.refresh_text);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ContestTask extends AsyncTask<Void, Integer, String> {

        private String TAG = ContestTask.class.getSimpleName();

        ContestTask() {
            progressDialog = new ProgressDialog(StatisticsActivity.this);
            progressDialog.setMessage("Loading Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
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
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            refresh.setEnabled(false);
            refresh.setText(R.string.loading_text);
            new StatisticsTask().execute();
        }
    }
}

package com.acm.scoresystem;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
                new StatisticsTask().execute();
                refresh.setEnabled(false);
                refresh.setText(R.string.loading_text);
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
                Toast.makeText(getApplicationContext(), "No Connection \uD83D\uDE41", Toast.LENGTH_LONG).show();
            }
        };

        refresh.performClick();
    }

    private class StatisticsTask extends AsyncTask<Void, Integer, String> {

        public StatisticsTask() {
            progressDialog = new ProgressDialog(StatisticsActivity.this);
            progressDialog.setMessage("Please wait");
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
                for (int j = 0; j < jsonArray.length(); j++) {
                    if (jsonArray.getJSONObject(j).getString("verdict").equals("OK")) {
                        JSONObject pb = jsonArray.getJSONObject(j).getJSONObject("problem");
                        String contestId = pb.getString("contestId");
                        String pbIndex = pb.getString("index");
                        set.add(contestId + "/" + pbIndex);
                        if (problemsCount.containsKey(pbIndex)) {
                            problemsCount.put(pbIndex, problemsCount.get(pbIndex) + 1);
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
                handlerError.sendMessage(new Message());
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
}

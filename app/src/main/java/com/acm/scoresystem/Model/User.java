package com.acm.scoresystem.Model;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class User {
    private static final String TAG = User.class.getSimpleName();
    private String firstName;
    private String lastName;
    private String email;
    private long phoneNumber;
    private String password;
    private String schoolYear;
    private String major;
    private int score;
    private int problemSolved;
    private String codeforcesHandle;

    public User() {
    }

    public User(String firstName, String lastName, String email, long phoneNumber, String password, String schoolYear, String major, int score, int problemSolved, String codeforcesHandle) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.schoolYear = schoolYear;
        this.major = major;
        this.score = score;
        this.problemSolved = problemSolved;
        this.codeforcesHandle = codeforcesHandle;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getProblemSolved() {
        return problemSolved;
    }

    public void setProblemSolved(int problemSolved) {
        this.problemSolved = problemSolved;
    }

    public String getCodeforcesHandle() {
        return codeforcesHandle;
    }

    public void setCodeforcesHandle(String codeforcesHandle) {
        this.codeforcesHandle = codeforcesHandle;
    }


    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", password='" + password + '\'' +
                ", schoolYear='" + schoolYear + '\'' +
                ", major='" + major + '\'' +
                ", score=" + score +
                ", problemSolved=" + problemSolved +
                ", codeforcesHandle='" + codeforcesHandle + '\'' +
                '}';
    }

    public void calculScore(Map<String, Integer> problemsCount) {
        Log.e(TAG, "calculScore: "+problemsCount+" "+firstName);
        Map<String, Integer> echelle = new HashMap<>();
        echelle.put("A", 1);
        echelle.put("B", 2);
        echelle.put("C", 3);
        echelle.put("D", 5);
        echelle.put("E", 8);
        echelle.put("F", 13);
        echelle.put("G", 21);
        echelle.put("H", 34);
        score = 0;
        for (String pbIndex : problemsCount.keySet()) {
            if (echelle.containsKey(pbIndex)) {
                score += echelle.get(pbIndex) * problemsCount.get(pbIndex);
            }
        }
    }
}